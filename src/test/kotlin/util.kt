package top.e404.eclean.test

import be.seeseemelk.mockbukkit.ServerMock
import be.seeseemelk.mockbukkit.WorldMock
import be.seeseemelk.mockbukkit.entity.PlayerMock
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.inventory.ItemStack
import top.e404.eclean.EClean
import top.e404.eclean.config.*

lateinit var server: ServerMock
lateinit var plugin: EClean
lateinit var world: WorldMock
lateinit var player: PlayerMock

fun WorldMock.spawnEntities(
    location: Location,
    type: EntityType,
    count: Int,
    edit: (index: Int, spawned: Entity) -> Unit = { _, _ -> }
) = (0 until count).map { index ->
    val spawned = spawnEntity(location, type)
    edit(index, spawned)
    spawned
}

fun WorldMock.dropItems(
    location: Location,
    count: Int,
    generator: (index: Int) -> ItemStack
) = (0 until count).map { index ->
    val spawned = dropItem(location, generator(index))
    spawned
}

private val serializer = LegacyComponentSerializer.builder().build()
private val colorRegex = Regex("§[\\da-fk-or]")
val consoleOut
    get() = buildString {
        while (true) {
            val component = server.consoleSender.nextComponentMessage() ?: break
            appendLine(
                serializer.serialize(component)
                    .replace(colorRegex, "")
                    .replace("[ECleanDebug]", "[DEBUG]")
                    .replace("[EClean]", "[INFO ]")
            )
        }
    }

val enableDebug = System.getProperty("eclean.debug") != null
fun resetConfig() {
    world.entities.forEach(Entity::remove)
    Config.config = ConfigData(
        debug = enableDebug,
        update = false,
        duration = Long.MAX_VALUE,
        message = mutableMapOf(),
        living = LivingConfig(enable = false),
        drop = DropConfig(enable = false),
        chunk = ChunkConfig(enable = false),
        trashcan = TrashcanConfig(),
        noOnline = NoOnlineConfig()
    )
    // 清空控制台输出
    consoleOut
}