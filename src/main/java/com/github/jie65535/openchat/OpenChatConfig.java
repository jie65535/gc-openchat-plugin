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

import java.util.ArrayList;
import java.util.List;

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

    /**
     * 是否发送玩家加入消息
     */
    public boolean sendJoinMessage = true;

    /**
     * 玩家加入消息
     */
    public String joinMessage = "本服已启用聊天，/chat on 开启（默认），/chat off 屏蔽";

    /**
     * 被禁言反馈消息
     */
    public String bannedFeedback = "你已经被禁言！";

    /**
     * 消息太频繁反馈消息
     * {limit} 服务器设置的限制次数
     */
    public String msgTooFrequentFeedback = "服务器设置每分钟仅允许发言{limit}次";

    /**
     * 将聊天log
     */
    public boolean logChat = true;

    /**
     * WebSocket Access Token
     * 安全令牌，仅允许授权的连接
     * 如果为空将会在启动时自动生成一个32位随机令牌并显示在控制台
     */
    public String wsToken = "";

    /**
     * WebSocket Path
     * 反向WS的路径，即机器人连接到本插件开放的WS接口路径
     * 若不想开放WS，则留空，默认为 /openchat
     * OneBot设置示例：ws://127.0.0.1:443/openchat
     */
    public String wsPath = "/openchat";

//    /**
//     * WebSocket Address
//     * 正向WS的地址，即本插件主动连接机器人开放的WS接口地址
//     * 示例：ws://127.0.0.1:8080
//     * 若不需要，则留空
//     * TODO：由于需要引入外部依赖，正向WS方式暂不实现
//     */
//    public String wsAddress = "";

    /**
     * 群ID
     */
    public Long groupId = 0L;

    /**
     * 群消息格式化
     * {id}       为QQ号
     * {name}     为群名片，如果为空则显示昵称
     * {message}  为消息
     */
    public String groupToGameFormat = "<color=#6699CC>[QQ]</color><color=#99CC99>{name}</color>: {message}";

    /**
     * 服务器聊天消息格式
     * {nickName}   为玩家昵称
     * {uid}        为玩家UID
     * {message}    为消息内容
     */
    public String gameToGroupFormat = "[GC]{nickName}({uid}): {message}";

//    /**
//     * 频道ID
//     */
//    public String guildId = "";
//    /**
//     * 子频道ID集
//     */
//    public List<String> channelIds = new ArrayList<>();

    /**
     * 是否将游戏里的聊天转发到群聊
     */
    public boolean isSendToBot = true;

    /**
     * 是否接收群消息并发送到游戏里
     */
    public boolean isSendToGame = true;

    /**
     * 管理员账号列表
     * 所有来自管理员的消息，如果和命令前缀匹配，将作为控制台命令执行
     */
    public ArrayList<Long> adminIds = new ArrayList<>(List.of(0L));

    /**
     * 管理员执行命令前缀
     */
    public String adminPrefix = "/";

    /**
     * 是否启用登录消息
     * 当玩家登录服务器时，发送消息通知到群里
     */
    public boolean sendLoginMessageToBot = true;

    /**
     * 玩家登录服务器消息格式
     * {nickName}   为玩家昵称
     * {uid}        为玩家UID
     */
    public String loginMessageFormat = "{nickName}({uid}) 加入了服务器";

    /**
     * 是否启用登录消息
     * 当玩家登录服务器时，发送消息通知到游戏里
     */
    public boolean sendLoginMessageToGame = true;

    /**
     * 玩家登录服务器消息格式（游戏内）
     * {nickName}   为玩家昵称
     * {uid}        为玩家UID
     */
    public String loginMessageFormatInGame = "<color=#99CC99>{nickName}({uid}) 加入了游戏</color>";

    /**
     * 是否启用登出消息
     * 当玩家离开服务器时，发送消息通知到群里
     */
    public boolean sendLogoutMessageToBot = true;

    /**
     * 玩家登出服务器消息格式
     * {nickName}   为玩家昵称
     * {uid}        为玩家UID
     */
    public String logoutMessageFormat = "{nickName}({uid}) 离开了服务器";

    /**
     * 是否启用登出消息
     * 当玩家登录服务器时，发送消息通知到游戏里
     */
    public boolean sendLogoutMessageToGame = true;

    /**
     * 玩家登出服务器消息格式（游戏内）
     * {nickName}   为玩家昵称
     * {uid}        为玩家UID
     */
    public String logoutMessageFormatInGame = "<color=#99CC99>{nickName}({uid}) 离开了游戏</color>";
}
