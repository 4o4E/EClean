package top.e404.eclean.menu.trashcan

import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import top.e404.eclean.PL
import top.e404.eclean.clean.Trashcan
import top.e404.eclean.config.Lang
import top.e404.eplugin.menu.menu.ChestMenu

class TrashcanMenu : ChestMenu(PL, 6, Lang["menu.trashcan.title"], true) {
    val zone = TrashcanZone(this, Trashcan.trashValues)
    private val prev = PrevButton(this)
    private val next = NextButton(this)

    init {
        initSlots(
            listOf(
                "         ",
                "         ",
                "         ",
                "         ",
                "         ",
                "  p   n  ",
            )
        ) { _, char ->
            when (char) {
                'p' -> prev
                'n' -> next
                else -> null
            }
        }
        zones.add(zone)
    }

    override fun onClickSelfInv(event: InventoryClickEvent) {
        super.onClickSelfInv(event)
        zone.onClickSelfInv(event)
    }

    override fun onShiftPutin(clicked: ItemStack, event: InventoryClickEvent): Boolean {
        super.onShiftPutin(clicked, event)
        zone.onShiftPutin(clicked, event)
        return true
    }
}
