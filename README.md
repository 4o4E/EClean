<div align="center">

# [EClean](https://github.com/4o4E/EClean)
基于BukkitAPI的清理插件, 适用于Spigot或Paper以及其他绝大多数Bukkit的下游分支核心

[![Release](https://img.shields.io/github/v/release/4o4E/EClean?label=Release)](https://github.com/4o4E/EClean/releases/latest)
[![Downloads](https://img.shields.io/github/downloads/4o4E/EClean/total?label=Download)](https://github.com/4o4E/EClean/releases)
</div>

## 指令

> 插件主命令为`/eclean`，包括缩写`/ec`，如果`/ec`与其他插件冲突，请使用`/eclean`

1. `/eclean reload` 重载插件
2. `/eclean clean` 立刻执行一次清理(不会有清理前的提示，在有玩家的服务器中慎用)
3. `/eclean clean entity` 立刻执行一次实体清理(不会有清理前的提示，在有玩家的服务器中慎用)
4. `/eclean clean entity <世界名>` 立刻在指定世界执行一次实体清理(没有清理提示)
5. `/eclean clean drop` 立刻执行一次掉落物清理(不会有清理前的提示，在有玩家的服务器中慎用)
6. `/eclean clean drop <世界名>` 立刻在指定世界执行一次掉落物清理(没有清理提示)
7. `/eclean clean chunk` 立刻执行一次密集实体清理(不会有清理前的提示，在有玩家的服务器中慎用)
8. `/eclean clean chunk <世界名>` 立刻在指定世界执行一次密集实体清理(没有清理提示)
9. `/eclean entity <实体名>` 统计当前世界每个区块的指定实体
10. `/eclean entity <实体名> <世界名>` 统计指定世界个区块的指定实体
11. `/eclean entity <实体名> <世界名> <纳入统计所需数量>` 统计指定世界个区块的指定实体并隐藏数量不超过指定数量的内容
12. `/eclean trash` 打开垃圾桶
13. `/eclean stats` 统计当前所在世界的实体和区块统计
14. `/eclean stats <世界名>` 统计实体和区块统计
## 权限

* `eclean.trash` 打开垃圾桶
* `eclean.admin` 使用插件指令
## 配置

> 插件的绝大部分可选内容皆需要在配置文件中修改

> [配置文件](src/main/resources/config.yml) 中所有的配置项均有注释描述用法和含义
## 下载

* [下载最新版](https://github.com/4o4E/EClean/releases/latest)
## Bstats

![bstats](https://bstats.org/signatures/bukkit/EClean.svg)
