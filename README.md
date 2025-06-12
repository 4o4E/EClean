# [EClean](https://github.com/4o4E/EClean)

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

详见[release](https://github.com/4o4E/EClean/releases)

