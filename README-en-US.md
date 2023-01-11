# gc-openchat-plugin

[中文](README.md) | English

Chat with players in the server

Talking to the server account in the game is equivalent to sending to the world channel, and all players in the server can receive the message.

## TODO List
- [x] Chat between players
- [x] Chat management commands
- [ ] Chat speech limit
- [ ] Chat Moderation
- [ ] Console talk
- [ ] Chat api _(~~OneBot api~~)_
- [ ] ...

## Install

1. Download the `jar` in Release.
2. Put it in the `plugins` folder.

## Commands
Player command:
- `/chat on` Accept chat messages (default)
- `/chat off` block chat messages

Server command (requires `server.chat.others` permissions) :
- `/serverchat on` Enable server chat (default) (do not save)
- `/serverchat off` Disable server chat (without saving)
- `/serverchat ban|mute @uid [time(Minutes)]` Mute the specified player for the specified time (minutes) (optional)
- `/serverchat unban|unmute @uid` Unmute a specified player

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
  joinMessage: "本服已启用聊天，/chat on 开启（默认），/chat off 屏蔽"
}
```

