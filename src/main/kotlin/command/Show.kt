package top.e404.eclean.command

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import top.e404.eclean.PL
import top.e404.eclean.config.Config
import top.e404.eclean.config.Lang
import top.e404.eclean.menu.MenuManager
import top.e404.eclean.menu.dense.DenseMenu
import top.e404.eclean.menu.dense.EntityInfo
import top.e404.eplugin.command.ECommand

object Show : ECommand(
    PL,
    "show",
    "(?i)s|show",
    true,
    "eclean.admin"
) {
    override val usage get() = Lang["command.usage.show"]

    override fun onCommand(sender: CommandSender, args: Array<out String>) {
        sender as Player
        plugin.runTaskAsync {
            val data = Bukkit.getServer().worlds.flatMap { world ->
                world.loadedChunks.toList()
            }.flatMap { chunk ->
                chunk.entities.groupBy(Entity::getType).filter { (_, list) ->
                    list.size > Config.config.chunk.count
                }.map { (type, list) ->
                    EntityInfo(type, list.size, chunk)
                }
            }.sortedByDescending { it.amount }.toMutableList()
            plugin.runTask {
                MenuManager.openMenu(DenseMenu(data), sender)
            }
        }
    }
}
