package com.github.jie65535.openchat;

import emu.grasscutter.GameConstants;
import emu.grasscutter.game.chat.ChatSystem;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.server.game.GameServer;

public class OpenChatSystem extends ChatSystem {
    private final OpenChatPlugin plugin;
    public OpenChatSystem(GameServer server, OpenChatPlugin plugin) {
        super(server);
        this.plugin = plugin;
    }

    @Override
    public void sendPrivateMessage(Player player, int targetUid, String message) {
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
        if (!plugin.getConfig().serverChatEnabled) {
            return;
        }
        // 刷新列表
        plugin.updateBanList();
        // 检测是否正在禁言中
        if (plugin.getData().banList.containsKey(player.getUid())) {
            return;
        }
        // 处理发言频率限制与发言内容审查
        if (!checkMessageFre(player) || !checkMessageModeration(message)) {
            // 可提示也可忽略，忽略可让玩家以为自己发送成功，其实别人看不到
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
            if (p != player && plugin.getData().offChatPlayers.contains(p.getUid())) {
                p.dropMessage(message);
            }
        }
    }

    /**
     * 消息内容审查
     * @param message 消息
     * @return 是否合法合规
     */
    private boolean checkMessageModeration(String message) {
        // TODO
        return true;
    }

    /**
     * 消息频率检查
     * @param player 玩家对象
     * @return 是否在约定阈值内
     */
    private boolean checkMessageFre(Player player) {
        // TODO
        return true;
    }
}
