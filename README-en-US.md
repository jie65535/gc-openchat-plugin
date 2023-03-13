# gc-openchat-plugin

[![GitHub license](https://img.shields.io/github/license/jie65535/gc-openchat-plugin)](https://github.com/jie65535/gc-openchat-plugin/blob/main/LICENSE)
[![GitHub stars](https://img.shields.io/github/stars/jie65535/gc-openchat-plugin)](https://github.com/jie65535/gc-openchat-plugin/stargazers)
[![Github All Releases](https://img.shields.io/github/downloads/jie65535/gc-openchat-plugin/total.svg)](https://github.com/jie65535/gc-openchat-plugin/releases)
[![GitHub release](https://img.shields.io/github/v/release/jie65535/gc-openchat-plugin)](https://github.com/jie65535/gc-openchat-plugin/releases/latest)
[![Build](https://github.com/jie65535/gc-openchat-plugin/actions/workflows/build.yml/badge.svg)](https://github.com/jie65535/gc-openchat-plugin/actions/workflows/build.yml)

[简中](README.md) | [繁中](README-zh-TW.md) | EN | [RU](README-ru-RU.md)

Chat with players in the server

![chat example](doc/Chat.png)

Talking to the server account in the game is equivalent to sending to the world channel, and all players in the server can receive the message.

## TODO List
- [x] Chat between players
- [x] Chat management commands
- [x] Speaking Frequency Limit
- [x] Chat api (OneBot)
- [x] Player on/offline broadcast
- [x] Chat Moderation
- [x] op execution commands
- [ ] Chat api (Minimal)

## Install

1. Download the `jar` in [Release](https://github.com/jie65535/gc-openchat-plugin/releases).
2. Put it in the `plugins` folder.

## Commands
Player command:
- `/chat on` Accept chat messages (default)
- `/chat off` block chat messages

Server command (requires `server.chat.others` permissions) :
- `/serverchat on` Enable server chat (default)
- `/serverchat off` Disable server chat
- `/serverchat ban|mute @uid [time(Minutes)]` Mute the specified player for the specified time (minutes) (optional)
- `/serverchat unban|unmute @uid` Unmute a specified player
- `/serverchat limit <timesPerMinute>` Set a frequency limit for sending messages
- `/serverchat reload` reload config.json
- `/serverchat group <groupId>` Set the connected group id
- `/serverchat op|deop <userId(QQ)>` Set or remove op
  - The account set as op can directly execute commands with the admin prefix in the specified group
  - The command prefix can be set in the configuration file `adminPrefix`, the default is `/`, for example `/sc ban @10002`
  - At present, there is no reply when executing commands in the group, because the console execution process will only log to the console, which is not easy to capture

`/serverchat` can be aliased as `/sc`

## Bot Connect
![Bot Connect example](/doc/Chat-OneBot.png)

### Connect | Bot -> OpenChat
1. Create a WebSocket Client
2. Add header `Authorization: Bearer **token**` for authentication connection
3. Add header `X-Self-ID: 123456` to identify the connected bot account (optional)
4. Connect to `/openchat`, for example `ws://127.0.0.1:443/openchat`

### Message ([OneBot-v11](https://github.com/botuniverse/onebot-11))
Only the fields used by the plugin are listed below
#### Group message | Bot -> OpenChat
```json
{
  "post_type": "message",
  "message_type": "group",
  "sub_type": "normal",
  "group_id": 123456,
  "raw_message": "Plain Message",
  "sender": {
    "role": "member",
    "level": "71",
    "user_id": 123456789,
    "nickname": "NickName",
    "title": "UserTitle",
    "card": "UserCard"
  }
}
```

#### Group message | OpenChat -> Bot
```json
{
  "action": "send_group_msg",
  "params": {
    "group_id": 123456,
    "message": "Plain Message",
    "auto_escape": true
  }
}
```

#### Channel message | Bot -> OpenChat
```json
{
  "post_type": "message",
  "message_type": "group",
  "sub_type": "normal",
  "guildId": "123456",
  "channel_id": "1234",
  "user_id": "123456789",
  "message": "Plain Message",
  "sender": {
    "user_id": 123456789,
    "nickname": "NickName"
  }
}
```

#### Channel message | OpenChat -> Bot
```json
{
  "action": "send_guild_channel_msg",
  "params": {
    "guild_id": "123456",
    "channel_id": "1234",
    "message": "Plain Message",
    "auto_escape": true
  }
}
```

## Config
```json5
{
  serverChatEnabled: true,
  serverChatFormat: "<color=#99CC99>{nickName}({uid})</color>: {message}",
  
  // The limit on the number of speaking messages per minute
  messageFreLimitPerMinute: 20,

  // Whether to send a message when a player joins
  sendJoinMessage: true,

  // The content of the message sent when the player joins
  // Can be used to prompt the player how to switch the chat function
  joinMessage: "本服已启用聊天，/chat on 开启（默认），/chat off 屏蔽",

  // Banned Feedback Message
  bannedFeedback: "你已经被禁言！",

  // Message too frequent feedback message
  // {limit} messageFreLimitPerMinute
  msgTooFrequentFeedback: "服务器设置每分钟仅允许发言{limit}次",

  // Whether to log the chat
  "logChat": true,

  // WebSocket Access Token
  // security token, only authorized connections are allowed
  // If it is empty, a 32-bit random token will be automatically generated at startup and displayed on the console
  "wsToken": "",

  // WebSocket Path
  // The reverse WS path, that is, the bot connect to the WS interface path opened by this plug-in
  // If you don't want to open WS, leave it empty, the default is "/openchat"
  // OneBot setting example: ws://127.0.0.1:443/openchat
  "wsPath": "/openchat",

  // // WebSocket Address
  // // Forward WS address, that is, the WS interface address opened by this plug-in to actively connect to the bot
  // // Example: ws://127.0.0.1:8080
  // // Leave blank if not required
  // // TODO: Due to the need to introduce external dependencies, the forward WS method is not implemented yet
  //    public String wsAddress: "",

  // Group id
  // You can use the command `/sc group <groupId>` to set
  "groupId": 0,

  // Group to Game format
  // {id}       User id
  // {name}     Card or Nickname
  // {message}  message
  "groupToGameFormat": "<color=#6699CC>[QQ]</color><color=#99CC99>{name}</color>: {message}",

  // Game to Group format
  // {nickName}   Player Nick name
  // {uid}        Player Uid
  // {message}    message
  "gameToGroupFormat": "[GC]{nickName}({uid}): {message}",

  //    /**
  //     * Guild id
  //     */
  //    public String guildId: "",
  //    /**
  //     * Channel ids
  //     */
  //    public List<String> channelIds: new ArrayList<>(),

  // Enable forwarding in-game chat to group chat
  "isSendToBot": true,

  // Enable receiving group messages and send them to the game
  "isSendToGame": true,

  // Admin id
  "adminIds": [0],

  // Admin command prefix
  "adminPrefix": "/",

  // Is enable login message to bot
  "sendLoginMessageToBot": true,

  // Login format
  // {nickName}   Player Nick name
  // {uid}        Player Uid
  "loginMessageFormat": "{nickName}({uid}) 加入了服务器",

  // Is enable login message to game
  "sendLoginMessageToGame": true,

  // Player login message format in game
  // {nickName}   Player Nick name
  // {uid}        Player Uid
  "loginMessageFormatInGame": "<color=#99CC99>{nickName}({uid}) 加入了游戏</color>",

  // Is enable logout message to bot
  "sendLogoutMessageToBot": true,

  // Player logout message format
  // {nickName}   Player Nick name
  // {uid}        Player Uid
  "logoutMessageFormat": "{nickName}({uid}) 离开了服务器",

  // Is enable logout message to game
  "sendLogoutMessageToGame": true,

  // Logout message format in game
  // {nickName}   Player Nick name
  // {uid}        Player Uid
  "logoutMessageFormatInGame": "<color=#99CC99>{nickName}({uid}) 离开了游戏</color>",
}
```

## Sensitive word filtering system
At present, the most basic sensitive word filtering function has been implemented, and a streamlined sensitive word library is attached.
The thesaurus will be released to the plug-in data directory when it is first started.

The file name is `SensitiveWordList.txt`, and each line contains a sensitive word. You can maintain this file yourself, and you can use `/sc reload` to read it again after modification.

When sensitive words are detected in the in-game player chat, it will not be forwarded and will be printed in the console.

There is currently no penalty mechanism set up, it's just that it's sent out and others can't see it, and I don't know that it hasn't been sent out.

If you have better suggestions, welcome [submit issue](https://github.com/jie65535/gc-openchat-plugin/issues/new)