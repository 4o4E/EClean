package top.e404.eclean.command

import top.e404.eclean.PL
import top.e404.eplugin.command.ECommandManager

object Commands : ECommandManager(
    PL,
    "eclean",
    Debug,
    Reload,
    Clean,
    Stats,
    EntityStats,
    Trash,
    Players,
    Show
)
