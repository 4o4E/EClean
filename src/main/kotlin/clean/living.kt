package top.e404.eclean.clean

import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import top.e404.eclean.PL
import top.e404.eclean.config.Config
import top.e404.eclean.util.info
import top.e404.eclean.util.isMatch
import top.e404.eclean.util.noOnline
import top.e404.eclean.util.noOnlineMessage
import top.e404.eplugin.EPlugin.Companion.placeholder

private inline val livingCfg get() = Config.config.living

/**
 * 最近一次清理生物实体的数量
 */
var lastLiving = 0
    private set

/**
 * 清理全服生物
 */
fun cleanLiving() {
    if (!livingCfg.enable) {
        PL.debug { "生物清理已禁用" }
        return
    }

    val worlds: List<World>
    if (livingCfg.worldBlack) {
        PL.debug { "清理世界已开启黑名单" }
        worlds = Bukkit.getWorlds().filter { livingCfg.disableWorld.contains(it.name) }
    } else {
        PL.debug { "清理世界已开启白名单" }
        worlds = Bukkit.getWorlds().filterNot { livingCfg.disableWorld.contains(it.name) }
    }

    PL.buildDebug {
        append("开始清理生物, 启用生物清理的世界: [")
        worlds.joinTo(this, ", ", transform = World::getName)
        append("]")
    }
    PL.debug { if (livingCfg.settings.name) "清理被命名的生物" else "不清理被命名的生物" }
    PL.debug { if (livingCfg.settings.lead) "清理拴绳拴住的生物" else "不清理拴绳拴住的生物" }
    PL.debug { if (livingCfg.settings.mount) "清理乘骑中的生物" else "不清理乘骑中的生物" }

    var time = System.currentTimeMillis()
    val result = worlds.map { it.cleanLiving() }
    worlds.map { it.cleanlivingWhitekey() }
    time = System.currentTimeMillis() - time

    lastLiving = result.sumOf { it.first }
    PL.debug { "生物清理共${lastLiving}个, 耗时${time}ms" }

    if (noOnline) {
        if (noOnlineMessage) {
            val all = result.sumOf { it.second }
            val finish = livingCfg.finish
            if (finish.isNotBlank()) PL.broadcastMsg(finish.placeholder(mapOf("clean" to lastLiving, "all" to all)))
        }
    } else {
        val all = result.sumOf { it.second }
        val finish = livingCfg.finish
        if (finish.isNotBlank()) PL.broadcastMsg(finish.placeholder(mapOf("clean" to lastLiving, "all" to all)))
    }
}

/**
 * 清理指定世界的生物
 *
 * @return Pair(clean, all)
 */
fun World.cleanLiving(): Pair<Int, Int> {
    val all = livingEntities.filterNot { it is Player }.toMutableList()
    val total = all.size

    PL.debug { "" }
    PL.debug { "开始清理世界${name}的生物" }
    PL.buildDebug {
        append("所有实体共").append(total).append("个: [")
        all.info().entries.joinTo(this, ", ") { (k, v) -> "$k: $v" }
        append("]")
    }

    // livingCfg.settings.name == false 时不清理命名的生物(从列表中移除)
    if (!livingCfg.settings.name) all.removeIf { it.customName != null }
    // livingCfg.settings.lead == false 时不清理拴绳拴住的生物(从列表中移除)
    if (!livingCfg.settings.lead) all.removeIf { it.isLeashed }
    // livingCfg.settings.mount == false 时不清理乘骑中的生物(从列表中移除)
    if (!livingCfg.settings.mount) all.removeIf { it.isInsideVehicle || it.passengers.isNotEmpty() }

    val groupBy = mutableMapOf<String, MutableList<LivingEntity>>()
    for (entity in all) groupBy.getOrPut(entity.type.name) { mutableListOf() }.add(entity)

    // 黑名单 名字匹配的清理 名字不匹配的从列表中移除(不清理)
    if (livingCfg.black) groupBy.entries.removeIf { (type, list) ->
        // 首个匹配的正则
        val matchesRegex = type.isMatch(livingCfg.match)
        // 没有匹配的 -> noMatch = true -> remove -> 从列表中移除 -> 不清理
        val noMatch = matchesRegex == null
        PL.buildDebug {
            if (noMatch) append("不")
            append("清理").append(type).append("x").append(list.size)
            if (matchesRegex != null) append(", 命中规则: ").append(matchesRegex.pattern)
        }
        noMatch
    }
    // 白名单 名字匹配的从列表中移除(不清理) 名字不匹配的清理
    else groupBy.entries.removeIf { (type, list) ->
        val matchesRegex = type.isMatch(livingCfg.match)
        // 有匹配的 -> matches = true -> remove -> 从列表中移除 -> 不清理
        val matches = matchesRegex != null
        PL.buildDebug {
            if (matches) append("不")
            append("清理").append(type).append("x").append(list.size)
            if (matchesRegex != null) append(", 命中规则: ").append(matchesRegex.pattern)
        }
        matches
    }

    var count = 0
    groupBy.values.forEach {
        count += it.size
        it.forEach(LivingEntity::remove)
    }
    PL.debug { "世界${name}生物清理完成($count/${total})" }
    return count to total
}
/**
 * 清理指定世界的生物
 *
 * @return Pair(clean, all)
 */
fun World.cleanlivingWhitekey(): Pair<Int, Int> {
    val all = livingEntities.filterNot { it is Player }.toMutableList()
    val total = all.size

    PL.debug { "" }
    PL.debug { "Whitekey: 开始清理世界${name}的生物" }
    PL.buildDebug {
        append("Whitekey: 所有实体共").append(total).append("个: [")
        all.info().entries.joinTo(this, ", ") { (k, v) -> "$k: $v" }
        append("]")
    }

    // 删除名称为空的生物(不清理)
    if (livingCfg.whiteKey.isNotEmpty()) {
        all.removeIf {
            it.customName == null}
        }
    // 删除名称不为空并且不名称包含关键词生物(不清理)
    if (livingCfg.whiteKey.isNotEmpty()) {
        all.removeIf {
            it.customName != null && livingCfg.whiteKey.none { key -> it.customName!!.contains(key) }
        }
    }

    val groupBy = mutableMapOf<String, MutableList<LivingEntity>>()
    for (entity in all) groupBy.getOrPut(entity.type.name) { mutableListOf() }.add(entity)

    var count = 0
    groupBy.values.forEach {
        count += it.size
        it.forEach(LivingEntity::remove)
    }
    PL.debug { "Whitekey: 世界${name}生物清理完成($count/${total})" }
    return count to total
}