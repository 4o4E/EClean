package trash

import be.seeseemelk.mockbukkit.inventory.SimpleInventoryViewMock
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack
import org.junit.jupiter.api.*
import top.e404.eclean.clean.Trashcan
import top.e404.eclean.clean.cleanDrop
import top.e404.eclean.config.Config
import top.e404.eclean.menu.MenuManager
import top.e404.eclean.test.*
import top.e404.eplugin.menu.menu.InventoryMenu
import kotlin.test.assertNotNull

abstract class TrashcanTest {

    @BeforeEach
    fun setup() {
        resetConfig()
        Config.config.trashcan.enable = true
        Config.config.trashcan.duration = null

        Trashcan.trashData.clear()
        Trashcan.trashValues.clear()
    }

    @AfterEach
    fun cleanUp() {
        player.inventory.clear()
        MenuManager.closeMenu(player)
        Trashcan.trashData.clear()
        Trashcan.trashValues.clear()
    }

    @Nested
    @DisplayName("清理时收集掉落物到垃圾桶")
    inner class TestCollectItemWhenClean {
        @Test
        @DisplayName("启用")
        fun enable() {
            Config.config.drop.enable = true
            Config.config.drop.match = mutableListOf(Regex(".*"))
            Config.config.trashcan.collect = true

            val chunk = world.getChunkAt(0, 0)
            chunk.load()
            val location = Location(world, 8.0, 8.0, 8.0)
            val amount = 64
            world.dropItem(location, ItemStack(Material.STONE, amount))

            cleanDrop()

            assert(Trashcan.trashValues.size == 1) { "清理的掉落物未放入公共垃圾箱\n$consoleOut" }
            assert(Trashcan.trashValues[0].amount == amount) { "放入公共垃圾箱的物品数量不正确\n$consoleOut" }
        }

        @Test
        @DisplayName("禁用")
        fun disable() {
            Config.config.drop.enable = true
            Config.config.drop.match = mutableListOf(Regex(".*"))
            Config.config.trashcan.collect = false

            val chunk = world.getChunkAt(0, 0)
            chunk.load()
            val location = Location(world, 8.0, 8.0, 8.0)
            val amount = 64
            world.dropItem(location, ItemStack(Material.STONE, amount))

            cleanDrop()

            assert(Trashcan.trashValues.isEmpty()) {
                "清理的掉落物不应放入公共垃圾箱\n$consoleOut"
            }
        }
    }

    private fun leftClick(slot: Int): Pair<InventoryMenu, InventoryClickEvent> {
        Trashcan.open(player)
        val menu = MenuManager.menus[player]!!
        val inventoryClickEvent = InventoryClickEvent(
            SimpleInventoryViewMock(
                player,
                menu.inv,
                player.inventory,
                InventoryType.CHEST
            ),
            InventoryType.SlotType.CONTAINER,
            slot,
            ClickType.LEFT,
            InventoryAction.COLLECT_TO_CURSOR
        )
        Bukkit.getPluginManager().callEvent(inventoryClickEvent)
        return menu to inventoryClickEvent
    }

    private fun rightClick(slot: Int): Pair<InventoryMenu, InventoryClickEvent> {
        Trashcan.open(player)
        val menu = MenuManager.menus[player]!!
        val inventoryClickEvent = InventoryClickEvent(
            SimpleInventoryViewMock(
                player,
                menu.inv,
                player.inventory,
                InventoryType.CHEST
            ),
            InventoryType.SlotType.CONTAINER,
            slot,
            ClickType.RIGHT,
            InventoryAction.PICKUP_HALF
        )
        Bukkit.getPluginManager().callEvent(inventoryClickEvent)
        return menu to inventoryClickEvent
    }

    private fun shiftLeftClick(slot: Int): Pair<InventoryMenu, InventoryClickEvent> {
        Trashcan.open(player)
        val menu = MenuManager.menus[player]!!
        val inventoryClickEvent = InventoryClickEvent(
            SimpleInventoryViewMock(
                player,
                menu.inv,
                player.inventory,
                InventoryType.CHEST
            ),
            InventoryType.SlotType.CONTAINER,
            slot,
            ClickType.SHIFT_LEFT,
            InventoryAction.MOVE_TO_OTHER_INVENTORY
        )
        Bukkit.getPluginManager().callEvent(inventoryClickEvent)
        return menu to inventoryClickEvent
    }

    @Nested
    @DisplayName("从垃圾桶中拿取物品")
    inner class TestTakeItemFromTrashcan {
        @Test
        @DisplayName("拿取1个")
        fun takeOne() {
            Trashcan.addItem(ItemStack(Material.STONE, 2))

            val (menu, event) = leftClick(0)
            assert(event.isCancelled) { "玩家点击菜单时的操作应被取消\n$consoleOut" }
            val item = menu.inv.getItem(0)
            assertNotNull(item) { "拿取后菜单中应还有1个物品\n$consoleOut" }
            assert(item.type != Material.AIR) { "物品不应为空\n$consoleOut" }
            assert(Trashcan.trashValues.size == 1) { "垃圾桶中应有1种物品\n$consoleOut" }
            assert(Trashcan.trashValues.size == 1) { "垃圾桶中应有1种物品\n$consoleOut" }
            assert(Trashcan.trashValues[0].amount == 1) { "垃圾桶中应剩余1个物品\n$consoleOut" }

            val playerItem = player.inventory.getItem(0)
            assertNotNull(playerItem) { "拿取后玩家背包中应有该物品\n$consoleOut" }
            assert(playerItem.type == Material.STONE) { "该物品应类型相同\n$consoleOut" }
            assert(playerItem.amount == 1) { "该物品数量应为1\n$consoleOut" }
        }

        @Test
        @DisplayName("拿取一半")
        fun takeHalf() {
            Trashcan.addItem(ItemStack(Material.STONE, 64))

            val (menu, event) = rightClick(0)
            assert(event.isCancelled) { "玩家点击菜单时的操作应被取消\n$consoleOut" }
            val item = menu.inv.getItem(0)
            assertNotNull(item) { "菜单中物品应不为空\n$consoleOut" }
            assert(item.type == Material.STONE) { "菜单中物品应类型不变\n$consoleOut" }
            assert(Trashcan.trashValues.size == 1) { "垃圾桶中应剩余1种\n$consoleOut" }
            assert(Trashcan.trashValues[0].amount == 32) { "垃圾桶中应有32个\n$consoleOut" }
            val playerItem = player.inventory.getItem(0)
            assertNotNull(playerItem) { "背包中物品应不为空\n$consoleOut" }
            assert(playerItem.type == Material.STONE) { "背包中物品应类型相同\n$consoleOut" }
            assert(playerItem.amount == 32) { "背包中应有32个\n$consoleOut" }
        }

        @Test
        @DisplayName("拿取一组")
        fun takeFullStack() {
            Trashcan.addItems(listOf(ItemStack(Material.STONE, 64), ItemStack(Material.STONE, 64)))

            val (menu, event) = shiftLeftClick(0)
            assert(event.isCancelled) { "玩家点击菜单时的操作应被取消\n$consoleOut" }
            val item = menu.inv.getItem(0)
            assertNotNull(item) { "菜单中物品应不为空\n$consoleOut" }
            assert(item.type == Material.STONE) { "菜单中物品应类型不变\n${Trashcan.trashValues[0]}\\n$consoleOut" }
            assert(Trashcan.trashValues.size == 1) { "垃圾桶中应剩余1种\n${Trashcan.trashValues[0]}\\n$consoleOut" }
            assert(Trashcan.trashValues[0].amount == 64) { "垃圾桶中应有64个\n${Trashcan.trashValues[0]}\n$consoleOut" }
            val playerItem = player.inventory.getItem(0)
            assertNotNull(playerItem) { "背包中物品应不为空\n$consoleOut" }
            assert(playerItem.type == Material.STONE) { "背包中物品应类型相同\n${playerItem}\n$consoleOut" }
            assert(playerItem.amount == 64) { "背包中应有64个\n${playerItem}\n$consoleOut" }
        }

        @Test
        @DisplayName("拿取全部")
        fun takeAll() {
            Trashcan.addItems(listOf(ItemStack(Material.STONE, 64)))

            val (menu, event) = shiftLeftClick(0)
            assert(event.isCancelled) { "玩家点击菜单时的操作应被取消\n$consoleOut" }
            val item = menu.inv.getItem(0)
            assert(item == null || item.type == Material.AIR) { "垃圾桶菜单中应没有物品\n$consoleOut" }
            assert(Trashcan.trashValues.isEmpty()) { "垃圾桶中应没有物品\n$consoleOut" }
            val playerItem = player.inventory.getItem(0)
            assertNotNull(playerItem) { "背包中物品应不为空\n$consoleOut" }
            assert(playerItem.type == Material.STONE) { "背包中物品应类型相同\n$consoleOut" }
            assert(playerItem.amount == 64) { "背包中应有64个\n$consoleOut" }
        }
    }

    @Nested
    @DisplayName("向垃圾桶中放入物品")
    inner class TestPutItemToTrashcan {
        @Test
        @DisplayName("放入1个")
        fun putOne() {
            player.inventory.setItem(0, ItemStack(Material.STONE, 64))

            val (menu, event) = leftClick(81)
            assert(event.isCancelled) { "玩家点击菜单时的操作应被取消\n$consoleOut" }
            val item = menu.inv.getItem(0)
            assertNotNull(item) { "菜单中物品应不为空\n$consoleOut" }
            assert(item.type == Material.STONE) { "菜单中物品应类型不变\n${Trashcan.trashValues[0]}\\n$consoleOut" }
            assert(Trashcan.trashValues.size == 1) { "垃圾桶中应剩余1种\n${Trashcan.trashValues[0]}\\n$consoleOut" }
            assert(Trashcan.trashValues[0].amount == 1) { "垃圾桶中应有1个\n${Trashcan.trashValues[0]}\n$consoleOut" }
            val playerItem = player.inventory.getItem(0)
            assertNotNull(playerItem) { "背包中物品应不为空\n$consoleOut" }
            assert(playerItem.type == Material.STONE) { "背包中物品应类型相同\n${playerItem}\n$consoleOut" }
            assert(playerItem.amount == 63) { "背包中应有63个\n${playerItem}\n$consoleOut" }
        }

        @Test
        @DisplayName("放入一半")
        fun putHalf() {
            player.inventory.setItem(0, ItemStack(Material.STONE, 64))

            val (menu, event) = rightClick(81)
            assert(event.isCancelled) { "玩家点击菜单时的操作应被取消\n$consoleOut" }
            val item = menu.inv.getItem(0)
            assertNotNull(item) { "菜单中物品应不为空\n$consoleOut" }
            assert(item.type == Material.STONE) { "菜单中物品应类型不变\n${Trashcan.trashValues[0]}\\n$consoleOut" }
            assert(Trashcan.trashValues.size == 1) { "垃圾桶中应剩余1种\n${Trashcan.trashValues[0]}\\n$consoleOut" }
            assert(Trashcan.trashValues[0].amount == 32) { "垃圾桶中应有32个\n${Trashcan.trashValues[0]}\n$consoleOut" }
            val playerItem = player.inventory.getItem(0)
            assertNotNull(playerItem) { "背包中物品应不为空\n$consoleOut" }
            assert(playerItem.type == Material.STONE) { "背包中物品应类型相同\n${playerItem}\n$consoleOut" }
            assert(playerItem.amount == 32) { "背包中应有32个\n${playerItem}\n$consoleOut" }
        }

        @Test
        @DisplayName("放入一组")
        fun put() {
            player.inventory.setItem(0, ItemStack(Material.STONE, 64))
            player.inventory.setItem(1, ItemStack(Material.STONE, 64))

            val (menu, event) = shiftLeftClick(81)
            assert(event.isCancelled) { "玩家点击菜单时的操作应被取消\n$consoleOut" }
            val item = menu.inv.getItem(0)
            assertNotNull(item) { "菜单中物品应不为空\n$consoleOut" }
            assert(item.type == Material.STONE) { "菜单中物品应类型不变\n${Trashcan.trashValues[0]}\\n$consoleOut" }
            assert(Trashcan.trashValues.size == 1) { "垃圾桶中应剩余1种\n${Trashcan.trashValues[0]}\\n$consoleOut" }
            assert(Trashcan.trashValues[0].amount == 64) { "垃圾桶中应有64个\n${Trashcan.trashValues[0]}\n$consoleOut" }
            val item1 = player.inventory.getItem(0)
            assert(item1 == null || item1.type == Material.AIR) {
                "放入一组之后第1个应为空的\n$consoleOut"
            }
            val item2 = player.inventory.getItem(1)
            assert(item2 != null && item2.amount == 64) {
                "放入一组之后第二个应为64个\n$consoleOut"
            }
        }
    }
}