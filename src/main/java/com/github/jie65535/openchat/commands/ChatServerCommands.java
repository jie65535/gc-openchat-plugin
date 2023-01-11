package com.github.jie65535.openchat.commands;

import com.github.jie65535.openchat.OpenChatPlugin;
import emu.grasscutter.command.Command;
import emu.grasscutter.command.CommandHandler;
import emu.grasscutter.game.player.Player;

import java.sql.Date;
import java.util.List;

@Command(label = "serverchat",
        aliases = { "sc" },
        usage = { "on/off", "unban|unmute @uid", "ban|mute @uid [time(Minutes)]"},
        permission = "server.chat",
        permissionTargeted = "server.chat.others")
public class ChatServerCommands implements CommandHandler {
    @Override
    public void execute(Player sender, Player targetPlayer, List<String> args) {
        if (args.size() < 1) {
            sendUsageMessage(sender);
            return;
        }

        var plugin = OpenChatPlugin.getInstance();
        var subCommand = args.get(0);
        switch (subCommand) {
            case "on" -> {
                plugin.getConfig().serverChatEnabled = true;
                CommandHandler.sendMessage(sender, "OK");
            }
            case "off" -> {
                plugin.getConfig().serverChatEnabled = false;
                CommandHandler.sendMessage(sender, "OK");
            }
            case "unban", "unmute" -> {
                plugin.getData().banList.remove(targetPlayer.getUid());
                CommandHandler.sendMessage(sender, "OK");
            }
            case "ban", "mute" -> {
                var time = new Date(2051190000);
                if (args.size() == 2) {
                    try {
                        time = new Date(System.currentTimeMillis() / 1000 + Integer.parseInt(args.get(0)) * 60L);
                    } catch (NumberFormatException ignored) {
                        CommandHandler.sendTranslatedMessage(sender, "commands.ban.invalid_time");
                        return;
                    }
                }
                plugin.getData().banList.put(targetPlayer.getUid(), time);
                CommandHandler.sendMessage(sender, "OK");
            }
        }
    }
}
