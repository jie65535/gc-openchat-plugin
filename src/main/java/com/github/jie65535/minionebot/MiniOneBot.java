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

import com.google.gson.JsonObject;
import emu.grasscutter.utils.JsonUtils;
import io.javalin.Javalin;
import org.slf4j.Logger;
import com.github.jie65535.minionebot.events.GroupMessage;
import com.github.jie65535.minionebot.events.GroupMessageHandler;
import com.github.jie65535.minionebot.events.GuildChannelMessage;
import com.github.jie65535.minionebot.events.GuildChannelMessageHandler;

import java.io.IOException;
import java.util.Objects;
import java.util.regex.Pattern;

public class MiniOneBot implements WsStream.WsMessageHandler {
    private final Logger logger;
    private final Javalin javalin;
    private final String token;
    private MiniOneBotWsServer server;
//    private MiniOneBotWsClient client;

    public MiniOneBot(Javalin javalin, String token, Logger logger) {
        this.javalin = javalin;
        this.token = token;
        this.logger = logger;
    }

    // region WebSocket

    public void startWsServer(String path) {
        if (server == null) {
            logger.debug("Start MiniOneBot WebSocket Server");
            server = new MiniOneBotWsServer(javalin, path, token, logger);
            server.subscribe(this);
        }
    }

//    public void startWsClient(URI serverUri) {
//        if (client == null) {
//            logger.info("Start MiniOneBot WebSocket Client");
//            client = MiniOneBotWsClient.create(serverUri, token, logger);
//            client.subscribe(this);
//        }
//    }

    public void stop() {
//        if (client != null) {
//            client.close();
//        }
        if (server != null) {
            try {
                server.close();
            } catch (IOException e) {
                logger.error("Stop MiniOneBot WebSocket Server Failed!", e);
            }
        }
    }

    private void sendMessageToAll(String message) {
        logger.debug("Sending... message=\"{}\"", message);
        server.send(message);
//        client.send(message);
    }

    // endregion

    // region OneBot message

    @Override
    public void onMessage(String message) {
        var map = JsonUtils.decode(message, JsonObject.class);
        if (!map.has("post_type")) return;
        var postType = map.get("post_type").getAsString();
        // 消息事件上报
        if (Objects.equals(postType, "message")) {
            var messageType = map.get("message_type").getAsString();
            var subType = map.get("sub_type").getAsString();

            // 发送者信息 https://docs.go-cqhttp.org/reference/data_struct.html#post-message-messagesender
            var sender = map.get("sender").getAsJsonObject();
            var senderId = sender.get("user_id").getAsLong();
            var senderNickname = sender.get("nickname").getAsString();

            // 群消息上报 https://docs.go-cqhttp.org/event/#%E7%BE%A4%E6%B6%88%E6%81%AF
            if (Objects.equals(messageType, "group")
                    && Objects.equals(subType, "normal")) {
                var groupId = map.get("group_id").getAsLong();
//                    var message = (List<Map<?, ?>>)map.get("message");
                var rawMessage = map.get("raw_message").getAsString();

                var senderCard = sender.get("card").getAsString();
                var senderLevel = sender.get("level").getAsString();
                var senderRole = sender.get("role").getAsString();
                var senderCardOrNickname = senderCard == null || senderCard.isEmpty() ? senderNickname : senderCard;
                var senderTitle = sender.get("title").getAsString();
                onGroupMessage(groupId, handleRawMessage(rawMessage), senderId, senderCardOrNickname, senderLevel, senderRole, senderTitle);
            }
            // 频道消息上报 https://docs.go-cqhttp.org/event/guild.html#%E6%94%B6%E5%88%B0%E9%A2%91%E9%81%93%E6%B6%88%E6%81%AF
            else if (Objects.equals(messageType, "guild")
                        && Objects.equals(subType, "channel")) {
                var guildId = map.get("guild_id").getAsString();
                var channelId = map.get("channel_id").getAsString();
                var rawMessage = map.get("message").getAsString(); // 需要 Message 消息链类型处理
                var tinyId = map.get("user_id").getAsString();
                onGuildMessage(guildId, channelId, handleRawMessage(rawMessage), tinyId, senderNickname);
            }
        }
    }

    /**
     * 当收到群消息时触发
     * @param groupId 群号
     * @param message 消息
     * @param senderId 发送者ID
     * @param senderCardOrNickname 发送者群名片或昵称，名片为空时是昵称
     * @param senderLevel 发送者群等级
     * @param senderRole 发送者群身份
     * @param senderTitle 发送者群专属头衔
     */
    private void onGroupMessage(long groupId,
                                String message,
                                long senderId,
                                String senderCardOrNickname,
                                String senderLevel,
                                String senderRole,
                                String senderTitle) {
        logger.debug("groupId={}, message={}, senderId={}, senderCardOrNickname={}, senderLevel={}, senderRole={}, senderTitle={}",
                groupId, message, senderId, senderCardOrNickname, senderLevel, senderRole, senderTitle);
        groupMessageHandler.handleGroupMessage(new GroupMessage(groupId, message, senderId, senderCardOrNickname, senderLevel, senderRole, senderTitle));
    }

    /**
     * 当收到频道消息时触发
     * @param guildId 频道Id
     * @param channelId 子频道Id
     * @param message 消息
     * @param senderId 发送者Id
     * @param senderName 发送者昵称
     */
    private void onGuildMessage(String guildId, String channelId, String message, String senderId, String senderName) {
        logger.debug("guildId={}, channelId={}, message={}, senderId={}, senderNickname={}",
                guildId, channelId, message, senderId, senderName);
        guildChannelMessageHandler.handleGuildChannelMessage(new GuildChannelMessage(guildId, channelId, message, senderId, senderName));
    }

    // endregion

    // region Message API

    // region Models
    private static class Action {
        public String action;
        public Object params;
        public Action(String action, Object params) {
            this.action = action;
            this.params = params;
        }
    }

    private static class SendGroupMsgArgs {
        public long group_id;
        public String message;
        public boolean auto_escape = true;
        public SendGroupMsgArgs(long groupId, String message) {
            this.group_id = groupId;
            this.message = message;
        }
    }

    private static class SendGuildChannelMsgArgs {
        public String guild_id;
        public String channel_id;
        public String message;
        public boolean auto_escape = true;
        public SendGuildChannelMsgArgs(String guildId, String channelId, String message) {
            this.guild_id = guildId;
            this.channel_id = channelId;
            this.message = message;
        }
    }
    // endregion

    /**
     * 发送消息到群
     * @param groupId 群号
     * @param message 消息
     */
    public void sendGroupMessage(long groupId, String message) {
        sendMessageToAll(JsonUtils.encode(new Action("send_group_msg", new SendGroupMsgArgs(groupId, message))));
    }

    /**
     * 发送消息到子频道
     * @param guildId 频道ID
     * @param channelId 子频道ID
     * @param message 消息
     */
    public void sendGuildChannelMessage(String guildId, String channelId, String message) {
        sendMessageToAll(JsonUtils.encode(new Action("send_guild_channel_msg", new SendGuildChannelMsgArgs(guildId, channelId, message))));
    }

    GroupMessageHandler groupMessageHandler;

    GuildChannelMessageHandler guildChannelMessageHandler;

    /**
     * 订阅群消息事件
     * @param handler 群消息处理器
     */
    public void subscribeGroupMessageEvent(GroupMessageHandler handler) {
        groupMessageHandler = handler;
    }

    /**
     * 订阅频道消息事件
     * @param handler 频道消息处理器
     */
    public void subscribeGuildChannelMessageEvent(GuildChannelMessageHandler handler) {
        guildChannelMessageHandler = handler;
    }

    // endregion

    // region Utils

    private static final Pattern cqCodePattern = Pattern.compile("\\[CQ:(\\w+).*?]");

    private static String handleRawMessage(String rawMessage) {
        if (rawMessage.indexOf('[') == -1)
            return unescape(rawMessage);
        var message = new StringBuilder();
        var matcher = cqCodePattern.matcher(rawMessage);
        while (matcher.find()) {
            var type = matcher.group(1);
            var replacement = switch (type) {
                case "image" -> "[图片]";
                case "reply" -> "[回复]";
                case "at" -> "[@]";
                case "record" -> "[语音]";
                case "forward" -> "[合并转发]";
                case "video" -> "[视频]";
                case "music" -> "[音乐]";
                case "redbag" -> "[红包]";
                case "poke" -> "[戳一戳]";
                default -> "";
            };
            matcher.appendReplacement(message, replacement);
        }
        matcher.appendTail(message);
        return unescape(message.toString());
    }

//    private static String escape(String msg) {
//        return msg.replace("&", "&amp;")
//                .replace("[", "&#91;")
//                .replace("]", "&#93;")
//                .replace(",", "&#44;");
//    }

    private static String unescape(String msg) {
        return msg.replace("&amp;", "&")
                .replace("&#91;", "[")
                .replace("&#93;", "]")
                .replace("&#44;", ",");
    }

    // endregion
}
