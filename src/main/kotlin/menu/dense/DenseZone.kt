package top.e404.eclean.menu.dense

import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import top.e404.eclean.PL
import top.e404.eclean.clean.info
import top.e404.eclean.config.Lang
import top.e404.eclean.menu.MenuManager
import top.e404.eclean.menu.Temp
import top.e404.eplugin.menu.zone.MenuButtonZone

class DenseZone(
    override val menu: DenseMenu,
    override val data: MutableList<EntityInfo>
) : MenuButtonZone<EntityInfo>(menu, 0, 0, 9, 5, data) {
    override val inv = menu.inv
    override fun onClick(menuIndex: Int, zoneIndex: Int, itemIndex: Int, event: InventoryClickEvent): Boolean {
        val info = data.getOrNull(itemIndex) ?: return true
        val player = event.whoClicked as Player
        // 右键点击清理区块实体
        if (event.isRightClick) {
            val entities = info.chunk.entities.filter { it.type == info.type }
            PL.sendMsgWithPrefix(
                player,
                Lang[
                    "menu.dense.clean",
                    "chunk" to info.chunk.info(),
                    "type" to info.type.name,
                    "count" to entities.size
                ]
            )
            entities.forEach(Entity::remove)
            data.removeAt(itemIndex)
            menu.updateIcon()
            return true
        }
        // 左键点击传送到区块
        val x = info.chunk.x * 16 + 8
        val z = info.chunk.z * 16 + 8
        val y = info.chunk.world.getHighestBlockYAt(x, z)
        val oldLocation = player.location
        player.teleport(Location(info.chunk.world, x + 0.5, y + 1.0, z + 0.5))
        if (!menu.temp) {
            PL.sendMsgWithPrefix(player, Lang["command.teleport.done"])
            return true
        }
        val exists = MenuManager.temps.remove(player)
        if (exists != null) {
            MenuManager.temps[player] = Temp(
                exists.player,
                exists.location,
                PL.runTaskLater(600) {
                    MenuManager.temps.remove(player)
                    player.teleport(exists.location)
                    PL.sendMsgWithPrefix(player, Lang["command.teleport.cover"])
                }
            )
            return true
        }
        PL.sendMsgWithPrefix(player, Lang["command.teleport.temp"])
        MenuManager.temps[player] = Temp(
            player,
            oldLocation,
            PL.runTaskLater(600) {
                MenuManager.temps.remove(player)
                player.teleport(oldLocation)
                PL.sendMsgWithPrefix(player, Lang["command.teleport.back"])
            }
        )
        return true
    }
}
