# gc-openchat-plugin

[![GitHub license](https://img.shields.io/github/license/jie65535/gc-openchat-plugin)](https://github.com/jie65535/gc-openchat-plugin/blob/main/LICENSE)
[![GitHub stars](https://img.shields.io/github/stars/jie65535/gc-openchat-plugin)](https://github.com/jie65535/gc-openchat-plugin/stargazers)
[![Github All Releases](https://img.shields.io/github/downloads/jie65535/gc-openchat-plugin/total.svg)](https://github.com/jie65535/gc-openchat-plugin/releases)
[![GitHub release](https://img.shields.io/github/v/release/jie65535/gc-openchat-plugin)](https://github.com/jie65535/gc-openchat-plugin/releases/latest)
[![Build](https://github.com/jie65535/gc-openchat-plugin/actions/workflows/build.yml/badge.svg)](https://github.com/jie65535/gc-openchat-plugin/actions/workflows/build.yml)

[简中](README.md) | 繁中 | [EN](README-en-US.md) | [RU](README-ru-RU.md)

讓玩家在服務器內聊天

![聊天示例](doc/Chat.png)

在遊戲內與服務器賬號對話，相當於發送到世界頻道，服務器內所有玩家均可收到消息。

## TODO List
- [x] 玩家間聊天
- [x] 聊天管理命令
- [x] 發言頻率限制
- [ ] 聊天內容審查
- [ ] 聊天api _(~~OneBot api~~)_
- [ ] ...

## 安裝

1. 在 [Release](https://github.com/jie65535/gc-openchat-plugin/releases) 下載`jar`
2. 放入 `plugins` 文件夾即可

## 命令
玩家用：
- `/chat on` 接受聊天消息（默認）
- `/chat off` 屏蔽聊天消息

管理用（需要 `server.chat.others` 權限）：
- `/serverchat on` 啟用服務器聊天（默認）
- `/serverchat off` 關閉服務器聊天
- `/serverchat ban @uid [時間（分鐘）]` 禁言指定玩家
- `/serverchat unban @uid` 解除指定玩家禁言
- `/serverchat limit <次每分钟>` 設置發消息頻率限制
- `/serverchat reload` 重載配置文件

`/serverchat` 可用别名 `/sc`


## 配置
```json5
{
  // 服務器聊天開關
  serverChatEnabled: true,
  
  // 服務器聊天消息格式
  // {nickName}   為玩家暱稱
  // {uid}        為玩家UID
  // {message}    為消息內容
  serverChatFormat: "<color=#99CC99>{nickName}({uid})</color>: {message}",
  
  // 每分鐘發言消息數限制
  messageFreLimitPerMinute: 20,
  
  // 是否在玩家加入時發送消息
  sendJoinMessage: true,
  
  // 玩家加入時發送消息
  joinMessage: "本服已启用聊天，/chat on 开启（默认），/chat off 屏蔽",

  // 被禁言反饋消息
  bannedFeedback: "你已经被禁言！",

  // 消息太頻繁反饋消息
  // {limit} 服務器設置的限制次數
  msgTooFrequentFeedback: "服务器设置每分钟仅允许发言{limit}次"
}
```

