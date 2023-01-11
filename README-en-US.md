# gc-openchat-plugin

English | [中文](README_zh-CN.md)

Chat with players in the server

Talking to the server account in the game is equivalent to sending to the world channel, and all players in the server can receive the message.

## TODO List
- [ ] Chat between players
- [ ] Chat speech limit
- [ ] Chat Moderation
- [ ] Console talk
- [ ] Chat management commands (`/chat ban|unban`)
- [ ] Chat api _(~~OneBot api~~)_
- [ ] ...

## Install

1. Download the `jar` in [Release](Release).
2. Put it in the `plugins` folder.

## Config
```json5
{
  serverChatEnabled: true,
  serverChatFormat: "<color=#99CC99>{nickName}({uid})</color>: {message}",
  messageFreLimitPerMinute: 20
}
```

