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
- [ ] Chat Moderation
- [ ] Chat api _(~~OneBot api~~)_
- [ ] ...

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

`/serverchat` can be aliased as `/sc`

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
  msgTooFrequentFeedback: "服务器设置每分钟仅允许发言{limit}次"
}
```

