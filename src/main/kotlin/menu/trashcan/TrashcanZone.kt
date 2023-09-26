package top.e404.eclean.menu.trashcan

import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import top.e404.eclean.PL
import top.e404.eclean.clean.Trashcan
import top.e404.eclean.clean.Trashcan.sign
import top.e404.eplugin.menu.zone.MenuButtonZone
import top.e404.eplugin.util.emptyItem
import top.e404.eplugin.util.splitByPage
import kotlin.math.max
import kotlin.math.min

class TrashcanZone(
    override val menu: TrashcanMenu,
    override val data: MutableList<TrashInfo>
) : MenuButtonZone<TrashInfo>(menu, 0, 0, 9, 5, data) {
    override val inv = menu.inv

    override fun update() {
        if (page != 0 && page * pageSize >= data.size) page--
        val byPage = data.splitByPage(pageSize, page)
        for (i in 0 until pageSize) {
            val displayable = byPage.getOrNull(i)
            // 不在列表中的设置为空
            if (displayable == null) {
                menu.inv.setItem(zone2menu(i)!!, emptyItem)
                continue
            }
            // 更新图标
            displayable.update()
            // 更新菜单物品
            menu.inv.setItem(zone2menu(i)!!, displayable.item)
        }
    }

    override fun onClick(menuIndex: Int, zoneIndex: Int, itemIndex: Int, event: InventoryClickEvent): Boolean {
        val player = event.whoClicked as Player
        val info = data.getOrNull(itemIndex) ?: return true
        // 计划拿取的物品数量
        val planTake = when (event.click) {
            // 左键 拿一个
            ClickType.LEFT, ClickType.DOUBLE_CLICK -> 1
            // shift + 左键 拿一组
            ClickType.SHIFT_LEFT -> info.item.maxStackSize
            // 右键 拿一半
            ClickType.RIGHT -> max(min(info.item.maxStackSize / 2, info.amount / 2), 1)
            // 其他点击方式 不拿
            else -> {
                player.playSound(player.location, Sound.ENTITY_BLAZE_DEATH, 1F, 1F)
                return true
            }
        }.let { min(it, info.amount) }

        // 要拿取的物品数量
        var waitForTake = planTake
        PL.debug { "玩家${player.name}计划从公共垃圾桶中拿取${info.origin.type}x${planTake}, 预计剩余${info.amount - planTake}" }
        val maxStackSize = info.origin.type.maxStackSize
        // 遍历背包
        for (i in (0 until 36)) {
            if (waitForTake == 0) break
            require(waitForTake > 0)

            val item = player.inventory.getItem(i)
            // 空槽位
            if (item == null || item.type == Material.AIR) {
                val count = min(waitForTake, maxStackSize)
                waitForTake -= count
                player.inventory.setItem(i, info.origin.clone().apply { amount = count })
                continue
            }
            // 类型不一致
            if (!item.isSimilar(info.origin)) continue
            // full stack
            if (item.amount >= maxStackSize) continue
            // 同类型合并
            val count = min(waitForTake, maxStackSize - item.amount)
            waitForTake -= count
            player.inventory.setItem(i, item.clone().apply { amount += count })
        }

        // 此时total的数量是info中剩余物品的数量

        // 所有拿取的数量
        val totalTake = planTake - waitForTake
        PL.debug { "玩家${player.name}实际从公共垃圾桶中拿取${info.origin.type}x${totalTake}, 实际剩余${info.amount - totalTake}" }

        // 从垃圾桶中移除拿取的部分
        info.amount -= totalTake
        require(info.amount >= 0)

        // 拿取了全部物品
        if (info.amount == 0) {
            Trashcan.trashData.remove(info.origin.sign())
            Trashcan.trashValues.removeAt(itemIndex)
        }

        // 更新垃圾桶
        Trashcan.update()
        return true
    }

    fun onClickSelfInv(event: InventoryClickEvent) {
        val player = event.whoClicked as Player
        event.isCancelled = true
        val clicked = event.currentItem
        if (clicked == null || clicked.type == Material.AIR) return
        // 放入的物品数量
        val count = when (event.click) {
            // 左键 放入一个
            ClickType.LEFT, ClickType.DOUBLE_CLICK -> 1
            // shift + 左键 放入全部
            ClickType.SHIFT_LEFT -> clicked.amount
            // 右键 放入一半
            ClickType.RIGHT -> max(clicked.amount / 2, 1)

            // 其他点击方式 不放
            else -> {
                player.playSound(player.location, Sound.ENTITY_BLAZE_DEATH, 1F, 1F)
                return
            }
        }
        PL.debug { "玩家${player.name}向公共垃圾桶中放入${clicked.type}x${count}, 剩余${clicked.amount - count}" }
        // 全部放入
        if (count == clicked.amount) {
            event.currentItem = emptyItem
            Trashcan.addItem(clicked)
            Trashcan.update()
            return
        }
        // 放入指定数量的
        clicked.amount -= count
        event.currentItem = clicked
        Trashcan.addItem(clicked.clone().apply { amount = count })
        Trashcan.update()
    }

    /**
     * shift将选择的ItemStack全部放入垃圾桶
     *
     * @param clicked 点击的物品
     * @return
     */
    fun onShiftPutin(clicked: ItemStack, event: InventoryClickEvent) {
        if (clicked.type == Material.AIR) return
        Trashcan.addItem(clicked)
        Trashcan.update()
        event.whoClicked.inventory.setItem(event.slot, emptyItem)
    }
}
