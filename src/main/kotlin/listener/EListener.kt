package top.e404.eclean.listener

import org.bukkit.Bukkit
import org.bukkit.event.Listener
import top.e404.eclean.EClean

interface EListener : Listener {
    fun register() {
        Bukkit.getPluginManager().registerEvents(this, EClean.instance)
    }
}