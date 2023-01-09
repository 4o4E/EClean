package top.e404.eclean.command

import top.e404.eclean.PL
import top.e404.eplugin.command.ECommandManager

object CommandManager : ECommandManager(
    PL,
    "eclean",
    Reload,
    Clean,
    Stats,
    EntityStats,
    Trash,
    Players,
    Debug
)