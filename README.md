# [EClean](https://github.com/4o4E/EClean)

> [!IMPORTANT]
> 目前最新版本不支持java8, 请使用以下版本
> https://github.com/4o4E/EClean/releases/tag/1.16.1

> 基于BukkitAPI的清理插件, 适用于Spigot和Paper等Bukkit的下游分支核心, 支持`1.8.x`
> 及以上版本, `1.8和1.18.x`, `1.19.x`, `1.20.x`经过测试
>
> mod核心(`mohist`/`arclight`)不在支持范围内, 若一定要使用请不要在此反馈问题

[![Release](https://img.shields.io/github/v/release/4o4E/EClean?label=Release)](https://github.com/4o4E/EClean/releases/latest)
[![Downloads](https://img.shields.io/github/downloads/4o4E/EClean/total?label=Download)](https://github.com/4o4E/EClean/releases)

[![bstats](https://bstats.org/signatures/bukkit/EClean.svg)](https://bstats.org/plugin/bukkit/EClean)

## 支持设置

- 清理间隔
- 清理前通知
- 忽略的世界
- 生物/实体/掉落物类型匹配(支持正则)
- 设置拴绳拴住/乘骑中/捡起物品的实体是否清理
- 密集实体检测

## 指令

> 插件主命令为`/eclean`，包括缩写`/ecl`，如果`/ecl`与其他插件冲突，请使用`/eclean`

- `/eclean reload` 重载插件, 重载后计划清理的任务将重新开始计时
- `/eclean clean` 立刻执行一次清理(不显示清理前提示，在有玩家的服务器中慎用)
- `/eclean clean entity` 立刻执行一次实体清理(不显示清理前提示)
- `/eclean clean entity <世界名>` 立刻在指定世界执行一次实体清理(不显示清理前提示)
- `/eclean clean drop` 立刻执行一次掉落物清理(不显示清理前提示)
- `/eclean clean drop <世界名>` 立刻在指定世界执行一次掉落物清理(不显示清理前提示)
- `/eclean clean chunk` 立刻执行一次密集实体清理(不显示清理前提示)
- `/eclean clean chunk <世界名>` 立刻在指定世界执行一次密集实体清理(不显示清理前提示)
- `/eclean entity <实体名>` 统计当前世界每个区块的指定实体
- `/eclean entity <实体名> <世界名>` 统计指定世界每个区块的指定实体
- `/eclean entity <实体名> <世界名> <纳入统计所需数量>` 统计指定世界每个区块的指定实体并隐藏不超过指定数量的内容
- `/eclean stats` 统计当前所在世界的实体和区块统计
- `/eclean stats <世界名>` 统计实体和区块统计
- `/eclean trash` 打开垃圾桶
- `/eclean show` 打开密集实体统计信息菜单

## 权限

- `eclean.admin` 使用插件指令
- `eclean.trash` 打开垃圾桶

## PlaceholderAPI

- `%eclean_before_next%` - `距离下一次清理的时间, 单位秒`
- `%eclean_before_next_formatted%` - `距离下一次清理的时间, 格式化的时间`
- `%eclean_last_drop%` - `上次清理的掉落物数量`
- `%eclean_last_living%` - `上次清理的生物数量`
- `%eclean_last_chunk%` - `上次清理的密集实体数量`
- `%eclean_trashcan_countdown%` - `垃圾桶清理倒计时, 单位秒`
- `%eclean_trashcan_countdown_formatted%` - `垃圾桶清理倒计时, 格式化的时间`

## 配置

插件默认配置见[配置文件](src/main/resources/config.yml), 配置项均有注释描述用法和含义

## 下载

- [最新版](https://github.com/4o4E/EClean/releases/latest)

## 计划添加

- [x] ~~不清理附魔物品，以及书写过的书~~ 2023.01.11添加
- [ ] 清理规则按世界单独配置(判断优先级: 实体规则 -> 世界规则 -> 默认规则)
- [x] ~~公共垃圾桶(支持翻页, 物品过期时间)~~ ~~如果做会单独做一个插件~~ 写了轻量版的不在关服后持久化垃圾桶物品数据的实现
- [ ] 红石统计及高频清理
- [ ] 区块卸载
- [x] ~~区块上限实现多种实体共用一个上限~~ 2023.07.29添加

~~咕咕咕~~

## 更新记录

```
2022.02.15 - 1.0.1 发布插件
2022.02.15 - 1.0.2 添加更新检查；当设置中的finish字段设置为 "" 时将不会发送清理完成的消息，若希望清理结束只发送一次消息，可以只设置一个为发送消息，其余设置为 ""
2022.02.16 - 1.0.3 添加低版本支持（1.8.x - 1.18.x）
2022.03.14 - 1.0.4 添加区块实体统计和世界实体统计
2022.04.05 - 1.0.5 添加手动执行清理的指令
2022.04.17 - 1.0.6 修改指令格式, 更换指令别名`ec`至`ecl`以避免与其他指令冲突导致的无法补全
2022.04.19 - 1.0.7 添加垃圾桶功能 `eclean trash`
2022.04.20 - 1.0.8 优化插件, 添加更新检查开关
2023.01.09 - 1.0.9 优化插件, 修复kotlin依赖版本冲突导致的插件无法加载, 添加语言文件
2023.01.10 - 1.0.10 修复插件加载低版本配置文件时报错的问题
2023.01.11 - 1.0.11 修复玩家打开背包时无法打开垃圾桶的问题, 添加物品清理时关于附魔物品和写过的书的相关设置
2023.01.12 - 1.0.12 添加papi支持
2023.01.13 - 1.0.13 支持显示已有papi, 修复清理消息不正常发送的问题
2023.01.17 - 1.0.14 添加缺失的i18n, 修复错误的广播消息
2023.06.30 - 1.15.0 更改版本号方式, 更新密集实体统计信息菜单
2023.07.21 - 1.16.0 在1.8也使用utf8作为配置文件编码, 避免转码; 修复1.8中无法正常使用的bug; 添加菜单的右键移除实体功能; 支持密集实体清理中多种实体共用一个上限; 修复1.8中不兼容的音效的问题
2023.08.31 - 1.16.1 优化代码, 修复配置文件中区块清理配置和注释相反的问题
2023.09.27 - 1.17.0 补全单元测试, 优化处理逻辑和debug信息, 现在debug信息会完整输出搜索的过程细节, 添加公共垃圾箱功能
2023.09.27 - 1.17.1 补全垃圾桶清理的papi, 优化代码逻辑
2023.10.11 - 1.17.2 修复关闭垃圾桶功能时无法正确清理掉落物的bug
2024.05.12 - 1.18.0 添加无玩家在线时的配置
2024.06.22 - 1.18.1 修复不清理的bug
2025.01.21 - 1.19.0 修复上游依赖
```

