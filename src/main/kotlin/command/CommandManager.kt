package top.e404.eclean.command

import top.e404.eclean.EClean

object CommandManager : AbstractCommandManager(
    EClean.instance,
    listOf(
        Reload,
        Clean,
        Stats,
        EntityStats,
        Trash
    )
)