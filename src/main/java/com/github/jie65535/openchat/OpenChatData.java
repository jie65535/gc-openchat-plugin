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

public class OpenChatConfig {

    /**
     * 服务器聊天开关
     */
    public boolean serverChatEnabled = true;

    /**
     * 服务器聊天消息格式
     * {nickName}   为玩家昵称
     * {uid}        为玩家UID
     * {message}    为消息内容
     */
    public String serverChatFormat = "<color=#99CC99>{nickName}({uid})</color>: {message}";

    /**
     * 每分钟发言消息数限制
     */
    public int messageFreLimitPerMinute = 20;
}
