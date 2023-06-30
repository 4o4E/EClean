package top.e404.eclean.menu

import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.scheduler.BukkitTask
import top.e404.eclean.PL
import top.e404.eplugin.menu.EMenuManager

object MenuManager : EMenuManager(PL) {
    val temps = mutableMapOf<Player, Temp>()

    @EventHandler
    fun PlayerQuitEvent.onEvent() {
        // 30s内退出游戏则传送回之前的位置
        temps.remove(player)?.run {
            task.cancel()
            player.teleport(location)
        }
    }
}


data class Temp(
    val player: Player,
    val location: Location,
    val task: BukkitTask
)
