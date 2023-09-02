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
package com.github.jie65535.openchat;

import emu.grasscutter.command.CommandMap;
import emu.grasscutter.server.event.game.ReceiveCommandFeedbackEvent;
import emu.grasscutter.server.event.player.PlayerJoinEvent;

public final class EventListeners {
    public static void onJoin(PlayerJoinEvent event) {
        var cs = OpenChatPlugin.getInstance().getServer().getChatSystem();
        if (cs instanceof OpenChatSystem) {
            ((OpenChatSystem) cs).onPlayerJoin(event);
        }
    }


    private static final StringBuilder consoleMessageHandler = new StringBuilder();
    private static StringBuilder commandResponseHandler;
    public static String runConsoleCommand(String rawCommand) {
        synchronized (consoleMessageHandler) {
            commandResponseHandler = consoleMessageHandler;
            consoleMessageHandler.setLength(0);
            // 尝试执行管理员命令
            CommandMap.getInstance().invoke(null, null, rawCommand);
            commandResponseHandler = null;
            return consoleMessageHandler.toString();
        }
    }

    /**
     * 命令执行反馈事件处理
     */
    public static void onCommandResponse(ReceiveCommandFeedbackEvent event) {
        if (commandResponseHandler == null || event.getPlayer() != null) return;

        if (!consoleMessageHandler.isEmpty()) {
            // New line
            consoleMessageHandler.append(System.lineSeparator());
        }
        consoleMessageHandler.append(event.getMessage());
    }
}
