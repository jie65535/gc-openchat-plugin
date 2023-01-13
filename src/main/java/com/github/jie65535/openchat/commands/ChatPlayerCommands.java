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

@Command(label = "chat", usage = { "<on|off>" }, permissionTargeted = "player.chat.others")
public class ChatPlayerCommands implements CommandHandler {
    @Override
    public void execute(Player sender, Player targetPlayer, List<String> args) {
        if (args.size() < 1) {
            sendUsageMessage(sender);
            return;
        }

        String subCommand = args.get(0).toLowerCase();
        var set = OpenChatPlugin.getInstance().getData().offChatPlayers;
        if (subCommand.equals("on")) {
            set.remove(targetPlayer.getUid());
            OpenChatPlugin.getInstance().saveData();
            CommandHandler.sendMessage(sender, "OK");
        } else if (subCommand.equals("off")) {
            set.add(targetPlayer.getUid());
            OpenChatPlugin.getInstance().saveData();
            CommandHandler.sendMessage(sender, "OK");
        } else {
            sendUsageMessage(sender);
        }
    }
}
