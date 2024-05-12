package top.e404.eclean.util

import org.bukkit.Bukkit
import top.e404.eclean.config.Config

val noOnline get() = Bukkit.getOnlinePlayers().isEmpty()
val noOnlineClean get() = Config.config.noOnline.clean
val noOnlineMessage get() = Config.config.noOnline.message