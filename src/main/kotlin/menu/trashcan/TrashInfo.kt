package top.e404.eclean.menu.trashcan

import org.bukkit.inventory.ItemStack
import top.e404.eclean.config.Lang
import top.e404.eplugin.menu.Displayable
import top.e404.eplugin.util.editItemMeta

data class TrashInfo(
    val origin: ItemStack,
    var amount: Int,
) : Displayable {
    private val placeholders = arrayOf<Pair<String, *>>("amount" to amount)

    override fun update() {
        placeholders[0] = "amount" to amount
        item = generateItem(placeholders)
    }

    override var needUpdate = false
    override var item = generateItem(placeholders)

    private fun generateItem(placeholders: Array<Pair<String, *>>) = origin.clone().editItemMeta {
        lore = (lore ?: mutableListOf()).apply {
            addAll(Lang.get("menu.trashcan.item.lore", *placeholders).removeSuffix("\n").lines())
        }
    }.apply { amount = 1 }
}