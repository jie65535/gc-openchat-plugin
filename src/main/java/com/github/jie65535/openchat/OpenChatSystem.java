package com.github.jie65535.openchat;

import emu.grasscutter.GameConstants;
import emu.grasscutter.game.chat.ChatSystem;
import emu.grasscutter.game.player.Player;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue;

import java.util.Date;

public class OpenChatSystem extends ChatSystem {
    private final OpenChatPlugin plugin;
    public OpenChatSystem(OpenChatPlugin plugin) {
        super(plugin.getServer());
        this.plugin = plugin;
        plugin.getLogger().debug("OpenChatSystem created.");
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
        plugin.getLogger().debug("handlePlayerMessage enter");
        if (!plugin.getConfig().serverChatEnabled) {
            return;
        }

        // 检测是否正在禁言中
        if (checkIsBanning(player)) {
            // 可提示也可忽略，忽略可让玩家以为自己发送成功，其实别人看不到
            plugin.getLogger().warn(String.format("Message blocked (banning): player=%s(%d): \"%s\"",
                    player.getNickname(), player.getUid(), message));
            return;
        }

        // 处理发言频率限制
        if (!checkMessageFre(player)) {
            // 可提示也可忽略，忽略可让玩家以为自己发送成功，其实别人看不到
            plugin.getLogger().warn(String.format("Message blocked (too often): player=%s(%d): \"%s\"",
                    player.getNickname(), player.getUid(), message));
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
            if (banList.get(player.getUid()).before(new Date())) {
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
