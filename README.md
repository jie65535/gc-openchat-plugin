# gc-openchat-plugin

[English](README.md) | 中文

让玩家在服务器内聊天

在游戏内与服务器账号对话，相当于发送到世界频道，服务器内所有玩家均可收到消息。

## TODO List
- [ ] 玩家间聊天
- [ ] 发言间隔限制
- [ ] 聊天内容审查
- [ ] 控制台发言（运维用）
- [ ] 聊天管理命令（禁言、解除禁言 `/chat ban|unban`）
- [ ] 聊天api _(~~OneBot api~~)_
- [ ] ...

## Install

1. 在 [Release](Release) 下载`jar`
2. 放入 `plugins` 文件夹即可

## Config
```json5
{
  // 服务器聊天开关
  serverChatEnabled: true,
  
  // 服务器聊天消息格式
  // {nickName}   为玩家昵称
  // {uid}        为玩家UID
  // {message}    为消息内容
  serverChatFormat: "<color=#99CC99>{nickName}({uid})</color>: {message}",
  
  // 每分钟发言消息数限制
  messageFreLimitPerMinute: 20
}
```

