package top.e404.eclean.clean

import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import top.e404.eclean.PL
import top.e404.eclean.config.Config
import top.e404.eclean.util.info
import top.e404.eplugin.EPlugin.Companion.placeholder

private inline val chunkCfg get() = Config.config.chunk

/**
 * 最近一次清理区块密集实体的数量
 */
var lastChunk = 0
    private set

/**
 * 清理全服区块中的密集实体
 */
fun cleanDenseEntities() {
    if (!chunkCfg.enable) {
        PL.debug { "密集实体清理已禁用" }
        return
    }
    val worlds = Bukkit.getWorlds().filterNot { chunkCfg.disableWorld.contains(it.name) }
    PL.debug { "开始进行密集实体检查" }
    PL.debug {
        buildString {
            append("启用密集实体检查的世界: [")
            worlds.joinTo(this, ", ", transform = World::getName)
            append("]")
        }
    }
    PL.debug { if (chunkCfg.settings.name) "清理被命名的生物" else "不清理被命名的生物" }
    PL.debug { if (chunkCfg.settings.lead) "清理拴绳拴住的生物" else "不清理拴绳拴住的生物" }
    PL.debug { if (chunkCfg.settings.mount) "清理乘骑中的生物" else "不清理乘骑中的生物" }

    var time = System.currentTimeMillis()
    lastChunk = worlds.sumOf { it.cleanChunkDenseEntities() }
    time = System.currentTimeMillis() - time

    PL.debug { "密集实体清理共${lastChunk}个, 耗时${time}ms" }

    val finish = Config.config.chunk.finish
    if (finish.isNotBlank()) PL.broadcastMsg(finish.placeholder("clean" to lastChunk))
}

/**
 * 清理指定世界的密集实体
 *
 * @return 清理的实体数量
 */
fun World.cleanChunkDenseEntities() = loadedChunks.sumOf { it.cleanDenseEntities() }

private fun Chunk.cleanDenseEntities(): Int {
    // 最终要移除的实体
    val willBeRemoved = entities.toMutableList()
    if (willBeRemoved.isEmpty()) return 0
    PL.debug { "" }
    val chunkInfo = info()
    PL.debug { "开始检测区块${chunkInfo}中的密集实体" }
    PL.buildDebug {
        append("所有实体共").append(willBeRemoved.size).append("个: [")
        willBeRemoved.info().entries.joinTo(this, ", ") { (k, v) -> "$k: $v" }
        append("]")
    }
    val settings = chunkCfg.settings
    // chunkCfg.settings.name == true 时清理被命名的生物, false -> 从列表中移除(不清理)
    if (!settings.name) willBeRemoved.removeIf { it.customName != null }
    // chunkCfg.settings.lead == true 时清理拴绳拴住的生物, false -> 从列表中移除(不清理)
    if (!settings.lead) willBeRemoved.removeIf { it is LivingEntity && it.isLeashed }
    // chunkCfg.settings.mount == true 时清理乘骑中的生物, false 从列表中移除(不清理)
    if (!settings.mount) willBeRemoved.removeIf { it.isInsideVehicle || it.passengers.isNotEmpty() }

    var count = 0

    // 规则匹配
    chunkCfg.limit.entries.mapNotNull { (regex, limit) ->
        val matches = willBeRemoved.filter { it.type.name.matches(regex) }.toMutableList()
        val execute = matches.size > limit
        PL.debug { "检查区块($chunkInfo)的密集实体, 规则${regex}" }
        PL.buildDebug {
            append("匹配实体").append(matches.size).append("个(")
            if (execute) append("&c清理其中&a").append(matches.size - limit).append("个&b")
            else append("不清理")
            append("): ")
            matches.info().entries.joinTo(this, ", ", "[", "]") { (k, v) -> "$k: $v" }
        }

        // 不清理匹配数量未达阈值的
        if (!execute) return@mapNotNull null

        // 截取超出阈值的实体
        matches.subList(limit, matches.size).also {
            count += it.size
            // 从待清理中移除已匹配的实体
            willBeRemoved.removeAll(it)
        }
    }.forEach {
        it.forEach(Entity::remove)
    }
    if (!chunkCfg.format.isNullOrBlank()) {
        val recv = Bukkit.getOnlinePlayers().filter { it.hasPermission("eclean.admin") }
        entities.asList().info().filter { it.value > chunkCfg.count }.forEach { (entity, count) ->
            val message = chunkCfg.format!!.placeholder(
                "chunk" to chunkInfo,
                "entity" to entity,
                "count" to count,
            )
            recv.forEach { PL.sendMsgWithPrefix(it, message) }
        }
    }
    return count
}

fun Chunk.info() = "x: ${x * 16}..${x * 16 + 15}, z: ${z * 16}..${z * 16 + 15}"