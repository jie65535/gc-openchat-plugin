package com.github.jie65535.openchat;

import emu.grasscutter.server.event.player.PlayerJoinEvent;

public final class EventListeners {
    private static final OpenChatConfig config = OpenChatPlugin.getInstance().getConfig();

    public static void onJoin(PlayerJoinEvent event) {
        if (!config.sendJoinMessage || config.joinMessage.isEmpty()) return;

        event.getPlayer().dropMessage(config.joinMessage);
    }
}
