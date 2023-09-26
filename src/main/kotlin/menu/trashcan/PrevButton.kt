package top.e404.eclean.menu.trashcan

import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import top.e404.eclean.config.Lang
import top.e404.eplugin.menu.slot.MenuButton
import top.e404.eplugin.util.buildItemStack
import top.e404.eplugin.util.emptyItem
import kotlin.math.max

class PrevButton(viewMenu: TrashcanMenu) : MenuButton(viewMenu) {
    val zone = viewMenu.zone
    private val btn =
        buildItemStack(Material.ARROW, 1, Lang["menu.trashcan.prev.name"], Lang["menu.trashcan.prev.lore"].lines())

    override var item = if (zone.hasPrev) btn else emptyItem
    override fun onClick(
        slot: Int,
        event: InventoryClickEvent,
    ): Boolean {
        if (zone.hasPrev) {
            val player = event.whoClicked as Player
            player.playSound(player.location, Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1F, 1F)
            zone.prevPage()
            menu.updateIcon()
        }
        return true
    }

    override fun updateItem() =
        if (!zone.hasPrev) item = emptyItem
        else item = btn.also { it.amount = max(1, zone.page) }
}
