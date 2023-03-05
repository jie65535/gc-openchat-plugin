/*
 * gc-openchat
 * Copyright (C) 2022  jie65535
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
package com.github.jie65535.openchat;

import com.github.jie65535.minionebot.MiniOneBot;
import com.github.jie65535.minionebot.events.GroupMessage;
import emu.grasscutter.GameConstants;
import emu.grasscutter.game.chat.ChatSystem;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.server.event.player.PlayerJoinEvent;
import emu.grasscutter.utils.Crypto;
import emu.grasscutter.utils.Utils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue;
import org.slf4j.Logger;

public class OpenChatSystem extends ChatSystem {
    private final OpenChatPlugin plugin;
    private final Logger logger;
    private final MiniOneBot miniOneBot;

    public OpenChatSystem(OpenChatPlugin plugin) {
        super(plugin.getServer());
        this.plugin = plugin;
        this.logger = plugin.getLogger();

        // 获取HttpServer框架
        var javalin = plugin.getHandle().getHttpServer().getHandle();
        var token = plugin.getConfig().wsToken;
        if (token == null || token.isEmpty()) {
            token = Utils.base64Encode(Crypto.createSessionKey(24));
            plugin.getConfig().wsToken = token;
            plugin.saveConfig();
            logger.warn("Detected that wsToken is empty, automatically generated Token for you as follows: {}", token);
        }
        // 构造MiniOneBot
        miniOneBot = new MiniOneBot(javalin, token, logger);
        // 启动WebSocket服务
        miniOneBot.startWsServer(plugin.getConfig().wsPath);
        // 订阅群消息事件
        miniOneBot.subscribeGroupMessageEvent(this::onGroupMessage);
    }

    /**
     * 玩家进入服务器时触发
     *
     * @param event 事件
     */
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!plugin.getConfig().sendLoginMessageToBot || plugin.getConfig().groupId < 1) return;
        var player = event.getPlayer();
        miniOneBot.sendGroupMessage(plugin.getConfig().groupId, plugin.getConfig().loginMessageFormat
                .replace("{nickName}", player.getNickname())
                .replace("{uid}", String.valueOf(player.getUid())));
    }

    /**
     * 用于标识玩家是否首次获取聊天记录
     */
    IntSet hasHistory = new IntOpenHashSet();

    /**
     * 在登出时清理聊天记录
     *
     * @param player 登出玩家
     */
    @Override
    public void clearHistoryOnLogout(Player player) {
        super.clearHistoryOnLogout(player);
        hasHistory.remove(player.getUid());

        // 发送离线消息
        if (!plugin.getConfig().sendLogoutMessageToBot || plugin.getConfig().groupId < 1) return;
        miniOneBot.sendGroupMessage(plugin.getConfig().groupId, plugin.getConfig().logoutMessageFormat
                .replace("{nickName}", player.getNickname())
                .replace("{uid}", String.valueOf(player.getUid())));
    }

    /**
     * 处理拉取聊天记录请求
     *
     * @param player 拉取聊天记录的玩家
     */
    @Override
    public void handlePullRecentChatReq(Player player) {
        super.handlePullRecentChatReq(player);
        if (!hasHistory.contains(player.getUid())) {
            // 如果是首次拉取，则向玩家发送欢迎消息
            hasHistory.add(player.getUid());
            if (plugin.getConfig().sendJoinMessage && !plugin.getConfig().joinMessage.isEmpty()) {
                logger.debug("send join message to {}({})",
                        player.getNickname(), player.getUid());
                player.dropMessage(plugin.getConfig().joinMessage);
            }
        }
    }

    /**
     * 重载父类发送私聊消息方法
     * 将普通聊天消息接出由本系统处理
     *
     * @param player    发言玩家
     * @param targetUid 目标玩家Uid
     * @param message   消息内容
     */
    @Override
    public void sendPrivateMessage(Player player, int targetUid, String message) {
        logger.debug("onSendPrivateMessage: player={}({}) targetUid={} message={}",
                player.getNickname(), player.getUid(), targetUid, message);
        // Sanity checks.
        if (message == null || message.length() == 0) {
            return;
        }
        // 调用父类发送消息方法
        super.sendPrivateMessage(player, targetUid, message);

        // 如果目标不是服务器，或者消息是命令，则忽略
        if (targetUid != GameConstants.SERVER_CONSOLE_UID || message.charAt(0) == '/' || message.charAt(0) == '!') {
            return;
        }

        // 否则执行玩家任意消息方法
        handlePlayerMessage(player, message);
    }

    /**
     * 处理玩家消息
     *
     * @param player  玩家对象
     * @param message 消息内容
     */
    private void handlePlayerMessage(Player player, String message) {
        // 检查服务器是否启用聊天，或者玩家是否禁用聊天
        if (!plugin.getConfig().serverChatEnabled || plugin.getData().offChatPlayers.contains(player.getUid())) {
            return;
        }

        // 检测是否正在禁言中
        if (checkIsBanning(player)) {
            logger.warn("Message blocked (banning): player={}({}): \"{}\"",
                    player.getNickname(), player.getUid(), message);
            if (!plugin.getConfig().bannedFeedback.isEmpty()) {
                player.dropMessage(plugin.getConfig().bannedFeedback);
            }
            return;
        }

        // 处理发言频率限制
        if (!checkMessageFre(player)) {
            // 可提示也可忽略，忽略可让玩家以为自己发送成功，其实别人看不到
            logger.warn("Message blocked (too often): player={}({}): \"{}\"",
                    player.getNickname(), player.getUid(), message);
            if (!plugin.getConfig().msgTooFrequentFeedback.isEmpty()) {
                player.dropMessage(
                        plugin.getConfig().msgTooFrequentFeedback
                                .replace("{limit}", String.valueOf(plugin.getConfig().messageFreLimitPerMinute)));
            }
            return;
        }

        // 处理发言内容审查
        if (!checkMessageModeration(message)) {
            logger.warn("Message blocked (moderation): player={}({}): \"{}\"",
                    player.getNickname(), player.getUid(), message);
            return;
        }

        // log messages
        if (plugin.getConfig().logChat) {
            logger.info("{}({}): \"{}\"",
                    player.getNickname(), player.getUid(), message);
        }

        // 格式化消息
        var formattedMessage = OpenChatPlugin.getInstance().getConfig().serverChatFormat
                .replace("{nickName}", player.getNickname())
                .replace("{uid}", String.valueOf(player.getUid()))
                .replace("{message}", message);

        // 转发给其它玩家
        for (Player p : getServer().getPlayers().values()) {
            // 将消息发送给除了自己以外所有未关闭聊天的玩家
            if (p != player && !plugin.getData().offChatPlayers.contains(p.getUid())) {
                p.dropMessage(formattedMessage);
            }
        }

        // 转发到机器人
        if (!plugin.getConfig().isSendToBot || plugin.getConfig().groupId < 1) return;
        miniOneBot.sendGroupMessage(plugin.getConfig().groupId, plugin.getConfig().gameToGroupFormat
                .replace("{nickName}", player.getNickname())
                .replace("{uid}", String.valueOf(player.getUid()))
                .replace("{message}", message));
    }

    /**
     * 收到群消息时触发
     *
     * @param event 群消息事件
     */
    private void onGroupMessage(GroupMessage event) {
        if (!plugin.getConfig().isSendToGame
                || plugin.getConfig().groupId < 1
                || event.groupId() != plugin.getConfig().groupId
        ) return;

        // log messages
        if (plugin.getConfig().logChat) {
            logger.info("[MiniOneBot] {}: \"{}\"",
                    event.senderCardOrNickname(), event.message());
        }
        broadcastChatMessage(plugin.getConfig().groupToGameFormat
                .replace("{id}", String.valueOf(event.senderId()))
                .replace("{name}", event.senderCardOrNickname())
                .replace("{message}", event.message()));
    }

    /**
     * 广播聊天消息给所有玩家（未开启聊天玩家除外）
     *
     * @param message 纯文本消息
     */
    public void broadcastChatMessage(String message) {
        for (Player p : getServer().getPlayers().values()) {
            if (!plugin.getData().offChatPlayers.contains(p.getUid())) {
                p.dropMessage(message);
            }
        }
    }

    /**
     * 检查玩家是否正在禁言中
     *
     * @param player 玩家对象
     * @return 是否禁言中
     */
    private boolean checkIsBanning(Player player) {
        var banList = plugin.getData().banList;
        // 检测是否正在禁言中
        if (banList.containsKey(player.getUid())) {
            if (banList.get(player.getUid()) < System.currentTimeMillis()) {
                banList.remove(player.getUid());
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * 消息内容审查
     *
     * @param message 消息
     * @return 是否合法合规
     */
    private boolean checkMessageModeration(String message) {
        // TODO see https://github.com/houbb/sensitive-word
        return !message.isEmpty();
    }

    // region 发言频率限制

    /**
     * 发言频率计时器
     */
    Int2ObjectMap<LongArrayFIFOQueue> speakingTimes = new Int2ObjectOpenHashMap<>();

    /**
     * 消息频率检查
     *
     * @param player 玩家对象
     * @return 是否在约定阈值内
     */
    private boolean checkMessageFre(Player player) {
        var list = speakingTimes.computeIfAbsent(player.getUid(), i -> new LongArrayFIFOQueue());
        var now = System.currentTimeMillis();
        list.enqueue(now);
        var t = now - 60_000;
        while (list.firstLong() < t)
            list.dequeueLong();
        return list.size() <= plugin.getConfig().messageFreLimitPerMinute;
    }

    // endregion
}
