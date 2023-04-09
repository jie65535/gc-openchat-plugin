/*
 * MiniOneBot
 * Copyright (C) 2023  jie65535
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.github.jie65535.minionebot;

import io.javalin.Javalin;
import io.javalin.websocket.*;
import org.eclipse.jetty.websocket.api.CloseStatus;
import org.slf4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MiniOneBotWsServer implements WsStream, Closeable {

    private String token;
    private final Logger logger;
    private final Map<WsContext, String> connections = new ConcurrentHashMap<>();

    public MiniOneBotWsServer(Javalin javalin, String path, String token, Logger logger) {
        this.token = token;
        this.logger = logger;
        javalin.ws(path, ws -> {
            ws.onConnect(this::onConnect);
            ws.onClose(this::onClose);
            ws.onError(this::onError);
            ws.onMessage(this::onMessage);
        });

        logger.info("WebSocket server started at {}", path);
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void onConnect(WsConnectContext ctx) {
        logger.info("onConnect: address={} headers={}", ctx.session.getRemoteAddress(), ctx.headerMap());
        var author = ctx.header("Authorization");
        // Check access token.
        if (author == null) {
            logger.warn("The connection was closed because the request did not contain an authorization token");
            ctx.session.close(new CloseStatus(401, "Unauthorized"));
        } else if (!author.equals("Bearer " + token) && !author.equals("Token " + token)) {
            logger.warn("Connection closed due to incorrect authorization token in the request");
            ctx.session.close(new CloseStatus(403, "Unauthorized"));
        } else {
            var selfId = ctx.header("X-Self-ID");
            if (selfId != null && !selfId.isEmpty()) {
                logger.info("Bot [{}] WebSocket connected", selfId);
            } else {
                logger.info("[{}] WebSocket connected", ctx.session.getRemoteAddress());
            }
            connections.put(ctx, selfId);
        }
    }

    public void onClose(WsCloseContext ctx) {
        logger.debug("onClose: address={} status={} reason={}", ctx.session.getRemoteAddress(), ctx.status(), ctx.reason());
        var selfId = connections.remove(ctx);
        if (selfId != null && !selfId.isEmpty()) {
            logger.warn("Bot [{}] WebSocket disconnected, status={} reason={}", selfId, ctx.status(), ctx.reason());
        } else {
            logger.warn("[{}] WebSocket disconnected, status={} reason={}", ctx.session.getRemoteAddress(), ctx.status(), ctx.reason());
        }
    }

    public void onError(WsErrorContext ctx) {
        logger.debug("onError: address={}", ctx.session.getRemoteAddress(), ctx.error());
        var selfId = connections.remove(ctx);
        if (selfId != null && !selfId.isEmpty()) {
            logger.warn("Bot [{}] WebSocket disconnected", selfId, ctx.error());
        } else {
            logger.warn("[{}] WebSocket disconnected", ctx.session.getRemoteAddress(), ctx.error());
        }
    }

    public void onMessage(WsMessageContext ctx) {
        logger.debug("onMessage: {}", ctx.message());

        callback.onMessage(ctx.message());
    }

    private WsMessageHandler callback;

    @Override
    public void subscribe(WsMessageHandler callback) {
        this.callback = callback;
    }

    @Override
    public void send(String message) {
        if (connections.isEmpty()) return;
        for (var ctx : connections.keySet()) {
            if (ctx.session.isOpen()) {
                ctx.send(message);
            }
        }
    }

    @Override
    public void close() throws IOException {
        if (connections.isEmpty()) return;
        for (var ctx : connections.keySet()) {
            if (ctx.session.isOpen()) {
                ctx.session.close(1001, "Service stopped");
            }
        }
        connections.clear();
    }
}
