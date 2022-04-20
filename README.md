<div align="center">

# [EClean](https://github.com/4o4E/EClean)

> 基于BukkitAPI的清理插件, 适用于Spigot和Paper等Bukkit的下游分支核心
>
> 同时支持插件和mod的混合核心(如`Arclight`/`Mohist`/`CatServer`等)不在支持范围内, 若需要使用可自行尝试, 我不提供相关支持

[![Release](https://img.shields.io/github/v/release/4o4E/EClean?label=Release)](https://github.com/4o4E/EClean/releases/latest)
[![Downloads](https://img.shields.io/github/downloads/4o4E/EClean/total?label=Download)](https://github.com/4o4E/EClean/releases)

</div>

## 指令

> 插件主命令为`/eclean`，包括缩写`/ecl`，如果`/ecl`与其他插件冲突，请使用`/eclean`

1. `/eclean reload` 重载插件, 重载后计划清理的任务将重新开始计时
2. `/eclean clean` 立刻执行一次清理(不显示清理前提示，在有玩家的服务器中慎用)
3. `/eclean clean entity` 立刻执行一次实体清理(不显示清理前提示)
4. `/eclean clean entity <世界名>` 立刻在指定世界执行一次实体清理(不显示清理前提示)
5. `/eclean clean drop` 立刻执行一次掉落物清理(不显示清理前提示)
6. `/eclean clean drop <世界名>` 立刻在指定世界执行一次掉落物清理(不显示清理前提示)
7. `/eclean clean chunk` 立刻执行一次密集实体清理(不显示清理前提示)
8. `/eclean clean chunk <世界名>` 立刻在指定世界执行一次密集实体清理(不显示清理前提示)
9. `/eclean entity <实体名>` 统计当前世界每个区块的指定实体
10. `/eclean entity <实体名> <世界名>` 统计指定世界每个区块的指定实体
11. `/eclean entity <实体名> <世界名> <纳入统计所需数量>` 统计指定世界每个区块的指定实体并隐藏不超过指定数量的内容
12. `/eclean stats` 统计当前所在世界的实体和区块统计
13. `/eclean stats <世界名>` 统计实体和区块统计
14. `/eclean trash` 打开垃圾桶

## 权限

- `eclean.admin` 使用插件指令
- `eclean.trash` 打开垃圾桶

## 配置

插件默认配置见[配置文件](src/main/resources/config.yml), 配置项均有注释描述用法和含义

## 下载

- [最新版](https://github.com/4o4E/EClean/releases/latest)

## Bstats

![bstats](https://bstats.org/signatures/bukkit/EClean.svg)
