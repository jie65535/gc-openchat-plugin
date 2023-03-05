# gc-openchat-plugin

[![GitHub license](https://img.shields.io/github/license/jie65535/gc-openchat-plugin)](https://github.com/jie65535/gc-openchat-plugin/blob/main/LICENSE)
[![GitHub stars](https://img.shields.io/github/stars/jie65535/gc-openchat-plugin)](https://github.com/jie65535/gc-openchat-plugin/stargazers)
[![Github All Releases](https://img.shields.io/github/downloads/jie65535/gc-openchat-plugin/total.svg)](https://github.com/jie65535/gc-openchat-plugin/releases)
[![GitHub release](https://img.shields.io/github/v/release/jie65535/gc-openchat-plugin)](https://github.com/jie65535/gc-openchat-plugin/releases/latest)
[![Build](https://github.com/jie65535/gc-openchat-plugin/actions/workflows/build.yml/badge.svg)](https://github.com/jie65535/gc-openchat-plugin/actions/workflows/build.yml)

简中 | [繁中](README-zh-TW.md) | [EN](README-en-US.md) | [RU](README-ru-RU.md)

让玩家在服务器内聊天

![聊天示例](doc/Chat.png)

在游戏内与服务器账号对话，相当于发送到世界频道，服务器内所有玩家均可收到消息。

## TODO List
- [x] 玩家间聊天
- [x] 聊天管理命令
- [x] 发言频率限制
- [x] 聊天api (OneBot)
- [x] 玩家上下线提醒
- [ ] 指定管理账号在群内执行控制台命令
- [ ] 聊天内容审查

## 安装

1. 在 [Release](https://github.com/jie65535/gc-openchat-plugin/releases) 下载`jar`
2. 放入 `plugins` 文件夹即可

## 命令
玩家用：
- `/chat on` 接受聊天消息（默认）
- `/chat off` 屏蔽聊天消息

管理用（需要 `server.chat.others` 权限）：
- `/serverchat on` 启用服务器聊天（默认）
- `/serverchat off` 关闭服务器聊天
- `/serverchat ban @uid [时间（分钟）]` 禁言指定玩家
- `/serverchat unban @uid` 解除指定玩家禁言
- `/serverchat limit <次每分钟>` 设置发消息频率限制
- `/serverchat reload` 重载配置文件
- `/serverchat group <groupId>` 设置互联群号
- `/serverchat op|deop <userId(QQ)>` 设置或解除管理员
  - 被设置为管理的账号可以在指定群内直接用管理前缀执行命令
  - 命令前缀可在配置文件中设置 `adminPrefix` ，默认为 `/`，例 `/sc ban @10002`
  - 目前在群内执行命令暂时没有回复，因为控制台执行过程只会log到控制台，不好捕获

`/serverchat` 可用别名 `/sc`，例如 `/sc ban @xxx`

## 群服互联

推荐使用 [go-cqhttp](https://github.com/Mrs4s/go-cqhttp) ，[快速开始](https://docs.go-cqhttp.org/guide/quick_start.htm)

![群服互联聊天示例](/doc/Chat-OneBot.png)

除了登录设置外，需[配置](https://docs.go-cqhttp.org/guide/config.html) `config.yml` 中以下内容
- `access-token: ''` 为插件配置中的token
- `ws-reverse:`
  - `universal: ws://your_websocket_universal.server` 为OpenChat地址，例如 `ws://127.0.0.1:443/openchat`

建议使用 `Android Watch` 协议登录（在 `device.json` 中 `"protocol": 5` 修改为 `"protocol": 2` ）

### 群服互联参考流程
1. 装好插件启动后，记录下首次生成的 `Token`，或者自己填写一个 `Token`
2. 下载 [go-cqhttp](https://github.com/Mrs4s/go-cqhttp) 并初始化配置
3. 在 `access-token: ''` 填写前面所述的 `Token` 内容
4. 在 `ws-reverse` 选项下的 `universal` 填写GC的服务器地址加路径，例如 `ws://127.0.0.1:443/openchat`
5. 配置你的Bot账号和登录协议，建议使用 `Android Watch` 登录。具体参考文档 [配置](https://docs.go-cqhttp.org/guide/config.html)。

---

## 插件配置
```json5
{
  // 服务器聊天开关
  "serverChatEnabled": true,
  
  // 服务器聊天消息格式
  // {nickName}   为玩家昵称
  // {uid}        为玩家UID
  // {message}    为消息内容
  "serverChatFormat": "<color=#99CC99>{nickName}({uid})</color>: {message}",
  
  // 每分钟发言消息数限制
  "messageFreLimitPerMinute": 20,
  
  // 是否在玩家加入时发送消息
  "sendJoinMessage": true,
  
  // 玩家加入时发送消息
  "joinMessage": "本服已启用聊天，/chat on 开启（默认），/chat off 屏蔽",

  // 被禁言反馈消息
  "bannedFeedback": "你已经被禁言！",

  // 消息太频繁反馈消息
  // {limit} 服务器设置的限制次数
  "msgTooFrequentFeedback": "服务器设置每分钟仅允许发言{limit}次",
  
  // 是否将聊天log
  "logChat": true,

  // WebSocket Access Token
  // 安全令牌，仅允许授权的连接
  // 如果为空将会在启动时自动生成一个32位随机令牌并显示在控制台
  "wsToken": "",

  // WebSocket Path
  // 反向WS的路径，即机器人连接到本插件开放的WS接口路径
  // 若不想开放WS，则留空，默认为 /openchat
  // OneBot设置示例：ws://127.0.0.1:443/openchat
  "wsPath": "/openchat",

  //   // WebSocket Address
  //   // 正向WS的地址，即本插件主动连接机器人开放的WS接口地址
  //   // 示例：ws://127.0.0.1:8080
  //   // 若不需要，则留空
  //   // TODO：由于需要引入外部依赖，正向WS方式暂不实现
  //    public String wsAddress: "",

  // 群ID
  // 可以使用指令 `/sc group <groupId>` 设定 
  "groupId": 0,

  // 群消息格式化
  // {id}       为QQ号
  // {name}     为群名片，如果为空则显示昵称
  // {message}  为消息
  "groupToGameFormat": "<color=#6699CC>[QQ]</color><color=#99CC99>{name}</color>: {message}",

  // 服务器聊天消息格式
  // {nickName}   为玩家昵称
  // {uid}        为玩家UID
  // {message}    为消息内容
  "gameToGroupFormat": "[GC]{nickName}({uid}): {message}",

  //    /**
  //     * 频道ID
  //     */
  //    public String guildId: "",
  //    /**
  //     * 子频道ID集
  //     */
  //    public List<String> channelIds: new ArrayList<>(),

  // 是否将游戏里的聊天转发到群聊
  "isSendToBot": true,

  // 是否接收群消息并发送到游戏里
  "isSendToGame": true,

  // 管理员账号
  "adminIds": [0],
  
  // 管理员执行命令前缀
  "adminPrefix": "/",

  // 是否启用登录消息
  // 当玩家登录服务器时，发送消息通知到群里
  "sendLoginMessageToBot": true,

  // 玩家登录服务器消息格式
  // {nickName}   为玩家昵称
  // {uid}        为玩家UID
  "loginMessageFormat": "{nickName}({uid}) 加入了服务器",

  // 是否启用登录消息
  // 当玩家登录服务器时，发送消息通知到群里
  "sendLoginMessageToGame": true,

  // 玩家登录服务器消息格式
  // {nickName}   为玩家昵称
  // {uid}        为玩家UID
  "loginMessageFormatInGame": "<color=#99CC99>{nickName}({uid}) 加入了游戏</color>",

  // 是否启用登出消息
  // 当玩家离开服务器时，发送消息通知到群里
  "sendLogoutMessageToBot": true,

  // 玩家登出服务器消息格式
  // {nickName}   为玩家昵称
  // {uid}        为玩家UID
  "logoutMessageFormat": "{nickName}({uid}) 离开了服务器",

  // 是否启用登出消息
  // 当玩家离开服务器时，发送消息通知到群里
  "sendLogoutMessageToGame": true,

  // 玩家登出服务器消息格式
  // {nickName}   为玩家昵称
  // {uid}        为玩家UID
  "logoutMessageFormatInGame": "<color=#99CC99>{nickName}({uid}) 离开了游戏</color>",
}
```

