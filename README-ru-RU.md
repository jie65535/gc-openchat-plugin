# gc-openchat-plugin

[![GitHub license](https://img.shields.io/github/license/jie65535/gc-openchat-plugin)](https://github.com/jie65535/gc-openchat-plugin/blob/main/LICENSE)
[![GitHub stars](https://img.shields.io/github/stars/jie65535/gc-openchat-plugin)](https://github.com/jie65535/gc-openchat-plugin/stargazers)
[![Github All Releases](https://img.shields.io/github/downloads/jie65535/gc-openchat-plugin/total.svg)](https://github.com/jie65535/gc-openchat-plugin/releases)
[![GitHub release](https://img.shields.io/github/v/release/jie65535/gc-openchat-plugin)](https://github.com/jie65535/gc-openchat-plugin/releases/latest)
[![Build](https://github.com/jie65535/gc-openchat-plugin/actions/workflows/build.yml/badge.svg)](https://github.com/jie65535/gc-openchat-plugin/actions/workflows/build.yml)

[简中](README.md) | [繁中](README-zh-TW.md) | [EN](README-en-US.md) | RU

Разрешить игрокам общаться внутри сервера

![Пример чата](doc/Chat.png)

Разговор с учетной записью сервера в игре эквивалентен отправке на мировой канал, и все игроки на сервере могут получить сообщение.

## TODO List
- [x] Чат между игроками
- [x] Команды управления чатом
- [x] Ограничение скорости речи
- [ ] Модерация контента чата
- [ ] API чата _(~~OneBot api~~)_
- [ ] ...

## Установить

1. Загрузите `jar` из [Release](https://github.com/jie65535/gc-openchat-plugin/releases)
2. Поместите его в папку `plugins`

## Порядок
Игроки используют:
- `/chat on` принимать сообщения чата (по умолчанию)
- `/chat off` отключает сообщения чата

Для управления (требуется разрешение `server.chat.others`):
- `/serverchat on` включить серверный чат (по умолчанию)
- `/serverchat off` отключить серверный чат
- `/serverchat ban @uid [время (минуты)]` забанить определенных игроков
- `/serverchat unban @uid` Разблокировать указанного игрока
- `/serverchat limit <количество раз в минуту>` установить ограничение частоты отправки сообщений
- `/serverchat reload` перезагрузить файл конфигурации

`/serverchat` может иметь псевдоним `/sc`


## конфигурация
```json5
{
  // переключение чата на сервере
  serverChatEnabled: true,
  
  // формат сообщения серверного чата
  // {nickName}   никнейм игрока
  // {uid}        это UID игрока
  // {message}    это содержимое сообщения
  serverChatFormat: "<color=#99CC99>{nickName}({uid})</color>: {message}",
  
  // Ограничить количество говорящих сообщений в минуту
  messageFreLimitPerMinute: 20,
  
  // Отправлять ли сообщение, когда игрок присоединяется
  sendJoinMessage: true,
  
  // Отправляем сообщение, когда игрок присоединяется
  joinMessage: "本服已启用聊天，/chat on 开启（默认），/chat off 屏蔽",

  // Запрещенное сообщение обратной связи
  bannedFeedback: "你已经被禁言！",

  // Сообщение обратной связи слишком часто
  // {limit} Максимальное время, установленное сервером
  msgTooFrequentFeedback: "服务器设置每分钟仅允许发言{limit}次"
}
```

