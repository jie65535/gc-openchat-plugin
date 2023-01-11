package com.github.jie65535.openchat;

import emu.grasscutter.command.Command;
import emu.grasscutter.command.CommandHandler;

@Command(label = "chat",
        aliases = { "openchat" },
        usage = { "on/off", "unban <uid>", "ban <uid> <time(Minutes)>"},
        permission = "player.chat",
        permissionTargeted = "server.chat")
public class ChatServerCommands implements CommandHandler {
}
