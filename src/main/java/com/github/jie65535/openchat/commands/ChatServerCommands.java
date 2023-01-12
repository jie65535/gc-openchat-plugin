package com.github.jie65535.openchat.commands;

import com.github.jie65535.openchat.OpenChatPlugin;
import emu.grasscutter.command.Command;
import emu.grasscutter.command.CommandHandler;
import emu.grasscutter.game.player.Player;

import java.util.List;

@Command(label = "serverchat",
        aliases = { "sc" },
        usage = { "on/off", "unban|unmute @uid", "ban|mute @uid [time(Minutes)]", "limit <timesPerMinute>", "reload" },
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
        var subCommand = args.get(0).toLowerCase();
        switch (subCommand) {
            case "on" -> {
                plugin.getConfig().serverChatEnabled = true;
                plugin.saveConfig();
                CommandHandler.sendMessage(sender, "OK");
            }
            case "off" -> {
                plugin.getConfig().serverChatEnabled = false;
                plugin.saveConfig();
                CommandHandler.sendMessage(sender, "OK");
            }
            case "unban", "unmute" -> {
                plugin.getData().banList.remove(targetPlayer.getUid());
                plugin.saveData();
                CommandHandler.sendMessage(sender, "OK");
            }
            case "ban", "mute" -> {
                var time = 2051190000L;
                if (args.size() == 2) {
                    try {
                        time = System.currentTimeMillis() + Integer.parseInt(args.get(1)) * 60_000L;
                    } catch (NumberFormatException ignored) {
                        CommandHandler.sendTranslatedMessage(sender, "commands.ban.invalid_time");
                        return;
                    }
                }
                if (targetPlayer == null) {
                    sendUsageMessage(sender);
                } else {
                    plugin.getData().banList.put(targetPlayer.getUid(), time);
                    plugin.saveData();
                    CommandHandler.sendMessage(sender, "OK");
                }
            }
            case "limit" -> {
                var times = 20;
                if (args.size() == 2) {
                    try {
                        times = Integer.parseInt(args.get(1));
                    } catch (NumberFormatException ignored) {
                        sendUsageMessage(sender);
                        return;
                    }
                }
                plugin.getConfig().messageFreLimitPerMinute = times;
                plugin.saveConfig();
                CommandHandler.sendMessage(sender, "OK");
            }
            case "reload" -> {
                plugin.loadConfig();
                CommandHandler.sendMessage(sender, "OK");
            }
        }
    }
}
