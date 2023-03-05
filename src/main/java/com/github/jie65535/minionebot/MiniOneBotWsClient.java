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
//package com.github.jie65535.minionebot;
//
//import org.java_websocket.client.WebSocketClient;
//import org.java_websocket.handshake.ServerHandshake;
//import org.slf4j.Logger;
//
//import java.net.URI;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Timer;
//import java.util.TimerTask;
//
//public class MiniOneBotWsClient extends WebSocketClient implements WsStream {
//    private final Logger logger;
//
//    private MiniOneBotWsClient(URI serverUri, Map<String, String> headers, Logger logger) {
//        super(serverUri, headers);
//
//        this.logger = logger;
//    }
//
//    public static MiniOneBotWsClient create(URI serverUri, String token, Logger logger) {
//        var headers = new HashMap<String, String>();
//        headers.put("Authorization", "Bearer " + token);
//        var client = new MiniOneBotWsClient(serverUri, headers, logger);
//        var wsClientDaemon = new Timer("WsClientDaemon", true);
//        wsClientDaemon.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                if (!client.isOpen()) {
//                    logger.debug("Try connect...");
//                    client.connect();
//                }
//            }
//        }, 5_000);
//        return client;
//    }
//
//    private WsMessageHandler callback;
//
//    @Override
//    public void subscribe(WsMessageHandler callback) {
//        this.callback = callback;
//    }
//
//    @Override
//    public void onOpen(ServerHandshake handshakedata) {
//        logger.info("onOpen: statusMessage={}", handshakedata.getHttpStatusMessage());
//    }
//
//    @Override
//    public void onMessage(String message) {
//        logger.info("onMessage: {}", message);
//        callback.onMessage(message);
//    }
//
//    @Override
//    public void onClose(int code, String reason, boolean remote) {
//        logger.info("onClose: code={} reason={} isRemote={}", code, reason, remote);
//    }
//
//    @Override
//    public void onError(Exception ex) {
//        logger.error("onError:", ex);
//    }
//
//    @Override
//    public void send(String message) {
//        if (isOpen()) {
//            super.send(message);
//        }
//    }
//}
