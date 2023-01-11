package com.github.jie65535.openchat;

import emu.grasscutter.server.event.player.PlayerJoinEvent;

public final class EventListeners {
    private static final OpenChatPlugin plugin = OpenChatPlugin.getInstance();
    private static final OpenChatConfig config = OpenChatPlugin.getInstance().getConfig();

    public static void onJoin(PlayerJoinEvent event) {
        // 检查聊天系统是否被其它插件替换
        if (!(plugin.getServer().getChatSystem() instanceof OpenChatSystem)) {
            plugin.getLogger().warn("聊天系统已被其它插件更改，现已重置为 OpenChat !");
            plugin.getServer().setChatSystem(new OpenChatSystem(plugin));
        }

        if (!config.sendJoinMessage || config.joinMessage.isEmpty())
            return;
        plugin.getLogger().debug(String.format("玩家 %s(%d) 加入游戏，发送加入消息",
                event.getPlayer().getNickname(), event.getPlayer().getUid()));
        event.getPlayer().dropMessage(config.joinMessage);
    }
}
