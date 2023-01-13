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

import emu.grasscutter.GameConstants;
import emu.grasscutter.game.chat.ChatSystem;
import emu.grasscutter.game.player.Player;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue;

public class OpenChatSystem extends ChatSystem {
    private final OpenChatPlugin plugin;
    public OpenChatSystem(OpenChatPlugin plugin) {
        super(plugin.getServer());
        this.plugin = plugin;
        plugin.getLogger().debug("OpenChatSystem created.");
    }

    IntSet hasHistory = new IntOpenHashSet();

    @Override
    public void clearHistoryOnLogout(Player player) {
        super.clearHistoryOnLogout(player);
        hasHistory.remove(player.getUid());
    }

    @Override
    public void handlePullRecentChatReq(Player player) {
        super.handlePullRecentChatReq(player);
        if (!hasHistory.contains(player.getUid())) {
            hasHistory.add(player.getUid());
            if (plugin.getConfig().sendJoinMessage && !plugin.getConfig().joinMessage.isEmpty()) {
                plugin.getLogger().debug(String.format("send join message to %s(%d)",
                        player.getNickname(), player.getUid()));
                player.dropMessage(plugin.getConfig().joinMessage);
            }
        }
    }

    @Override
    public void sendPrivateMessage(Player player, int targetUid, String message) {
        plugin.getLogger().debug(String.format("onSendPrivateMessage: player=%s(%d) targetUid=%d message=%s",
                player.getNickname(), player.getUid(), targetUid, message));
        // Sanity checks.
        if (message == null || message.length() == 0) {
            return;
        }
        super.sendPrivateMessage(player, targetUid, message);

        if (targetUid != GameConstants.SERVER_CONSOLE_UID || message.charAt(0) == '/' || message.charAt(0) == '!') {
            return;
        }

        handlePlayerMessage(player, message);
    }

    /**
     * 处理玩家消息
     * @param player 玩家对象
     * @param message 消息内容
     */
    private void handlePlayerMessage(Player player, String message) {
        // 检查服务器是否启用聊天，或者玩家是否禁用聊天
        if (!plugin.getConfig().serverChatEnabled || plugin.getData().offChatPlayers.contains(player.getUid())) {
            return;
        }

        // 检测是否正在禁言中
        if (checkIsBanning(player)) {
            plugin.getLogger().warn(String.format("Message blocked (banning): player=%s(%d): \"%s\"",
                    player.getNickname(), player.getUid(), message));
            if (!plugin.getConfig().bannedFeedback.isEmpty()) {
                player.dropMessage(plugin.getConfig().bannedFeedback);
            }
            return;
        }

        // 处理发言频率限制
        if (!checkMessageFre(player)) {
            // 可提示也可忽略，忽略可让玩家以为自己发送成功，其实别人看不到
            plugin.getLogger().warn(String.format("Message blocked (too often): player=%s(%d): \"%s\"",
                    player.getNickname(), player.getUid(), message));
            if (!plugin.getConfig().msgTooFrequentFeedback.isEmpty()) {
                player.dropMessage(
                        plugin.getConfig().msgTooFrequentFeedback
                                .replace("{limit}", String.valueOf(plugin.getConfig().messageFreLimitPerMinute)));
            }
            return;
        }

        // 处理发言内容审查
        if (!checkMessageModeration(message)) {
            plugin.getLogger().warn(String.format("Message blocked (moderation): player=%s(%d): \"%s\"",
                    player.getNickname(), player.getUid(), message));
            return;
        }

        // 格式化消息
        message = OpenChatPlugin.getInstance().getConfig().serverChatFormat
                .replace("{nickName}", player.getNickname())
                .replace("{uid}", String.valueOf(player.getUid()))
                .replace("{message}", message);
        // 转发给其它玩家
        for (Player p : getServer().getPlayers().values()) {
            // 将消息发送给除了自己以外所有未关闭聊天的玩家
            if (p != player && !plugin.getData().offChatPlayers.contains(p.getUid())) {
                p.dropMessage(message);
            }
        }
    }

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
     * @param message 消息
     * @return 是否合法合规
     */
    private boolean checkMessageModeration(String message) {
        // TODO see https://github.com/houbb/sensitive-word
        return !message.isEmpty();
    }

    Int2ObjectMap<LongArrayFIFOQueue> speakingTimes = new Int2ObjectOpenHashMap<>();

    /**
     * 消息频率检查
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
}
