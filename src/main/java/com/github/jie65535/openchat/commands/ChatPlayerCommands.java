package com.github.jie65535.openchat;

import emu.grasscutter.command.Command;
import emu.grasscutter.command.CommandHandler;
import emu.grasscutter.game.player.Player;

import java.util.List;

@Command(label = "chat", usage = { "<on|off>" })
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
            CommandHandler.sendMessage(sender);
        } else if (subCommand.equals("off")) {
            set.add(targetPlayer.getUid());
        }
    }
}
