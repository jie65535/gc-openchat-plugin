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
package com.github.jie65535.openchat.commands;

import com.github.jie65535.openchat.OpenChatPlugin;
import emu.grasscutter.command.Command;
import emu.grasscutter.command.CommandHandler;
import emu.grasscutter.game.player.Player;

import java.util.List;

@Command(label = "serverchat",
        aliases = { "sc" },
        usage = {
                "on/off",
                "unban|unmute @<UID>",
                "ban|mute @<UID> [time(Minutes)]",
                "limit <timesPerMinute>",
                "reload",
                "group <groupId>",
                "op|deop <userId(QQ)>",
        },
        permission = "server.chat",
        permissionTargeted = "server.chat.others",
        targetRequirement = Command.TargetRequirement.NONE)
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
                if (targetPlayer == null) {
                    sendUsageMessage(sender);
                    CommandHandler.sendTranslatedMessage(sender, "commands.execution.need_target");
                    return;
                }
                plugin.getData().banList.remove(targetPlayer.getUid());
                plugin.saveData();
                CommandHandler.sendMessage(sender, "OK");
            }
            case "ban", "mute" -> {
                if (targetPlayer == null) {
                    sendUsageMessage(sender);
                    CommandHandler.sendTranslatedMessage(sender, "commands.execution.need_target");
                    return;
                }
                var timeMs = 0L;
                if (args.size() == 2) {
                    try {
                        timeMs = System.currentTimeMillis() + Integer.parseInt(args.get(1)) * 60_000L;
                    } catch (NumberFormatException ignored) {
                        CommandHandler.sendTranslatedMessage(sender, "commands.ban.invalid_time");
                        return;
                    }
                } else {
                    // default ban 1 year
                    timeMs = System.currentTimeMillis() + 1000L * 60L * 60L * 24L * 365L;
                }
                plugin.getData().banList.put(targetPlayer.getUid(), timeMs);
                plugin.saveData();
                CommandHandler.sendMessage(sender, "OK");
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
            case "group" -> {
                var groupId = 0L;
                try {
                    groupId = Long.parseLong(args.get(1));
                } catch (Exception ignored) {
                    sendUsageMessage(sender);
                    return;
                }
                plugin.getConfig().groupId = groupId;
                plugin.saveConfig();
                CommandHandler.sendMessage(sender, "OK");
            }
            case "op", "deop" -> {
                var adminId = 0L;
                try {
                    adminId = Long.parseLong(args.get(1));
                } catch (Exception ignored) {
                    sendUsageMessage(sender);
                    return;
                }
                if (subCommand.equals("op")) {
                    plugin.getConfig().adminIds.add(adminId);
                } else {
                    plugin.getConfig().adminIds.remove(adminId);
                }
                plugin.saveConfig();
                CommandHandler.sendMessage(sender, "OK");
            }
            default -> sendUsageMessage(sender);
        }
    }
}
