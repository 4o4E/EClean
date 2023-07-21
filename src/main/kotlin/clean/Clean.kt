package top.e404.eclean.clean

import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.entity.Item
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.BookMeta
import org.bukkit.scheduler.BukkitTask
import top.e404.eclean.PL
import top.e404.eclean.config.*
import top.e404.eplugin.EPlugin.Companion.color
import top.e404.eplugin.EPlugin.Companion.placeholder
import top.e404.eplugin.util.asMutableList
import top.e404.eplugin.util.mapMutable

object Clean {
    private var task: BukkitTask? = null
    private fun String.isMatch(list: List<Regex>) = list.any { it matches this }

    private val chunkCfg: ChunkConfig
        get() = Config.config.chunk
    private val dropCfg: DropConfig
        get() = Config.config.drop
    private val livingCfg: LivingConfig
        get() = Config.config.living
    private val duration: Long
        get() = Config.config.duration

    /**
     * 计数, 每20tick++
     */
    var count = 0L
        private set

    var lastLiving = 0
        private set
    var lastDrop = 0
        private set
    var lastChunk = 0
        private set

    fun schedule() {
        count = 0
        task?.cancel()
        // 清理任务
        PL.info("&f设置清理任务, 间隔${duration}秒")// 提醒消息
        Config.config.message.forEach { (delay, message) ->
            if (delay > duration) PL.warn(Lang["warn.out_of_range", "message" to message, "duration" to duration])
            else PL.info("&f设置清理前${delay}秒提醒: ${message.color()}")
        }
        task = PL.runTaskTimer(20, 20) {
            count++
            Config.config.message[duration - count]?.let { PL.broadcastMsg(it) }
            if (count >= duration) {
                count = 0
                clean()
            }
        }
    }

    fun clean() {
        cleanDrop()
        cleanLiving()
        cleanChunk()
    }

    /**
     * 清理全服掉落物
     */
    fun cleanDrop() {
        if (!dropCfg.enable) return
        val worlds = Bukkit.getWorlds().asSequence().filterNot { chunkCfg.disableWorld.contains(it.name) }
        val match = dropCfg.match
        val black = dropCfg.black
        PL.debug {
            Lang[
                "debug.start.clean",
                "world" to worlds.joinToString(Lang["debug.spacing"]) { it.name },
                "black" to Lang["debug.bool.$black"],
                "match" to match.joinToString(Lang["debug.spacing"])
            ]
        }
        val result = worlds.map { it.cleanDrop() }
        lastDrop = result.sumOf { it.first }
        val all = result.sumOf { it.second }
        val finish = dropCfg.finish
        if (!finish.isNullOrBlank()) PL.broadcastMsg(finish.placeholder("clean" to lastDrop, "all" to all))
    }

    /**
     * 清理指定世界的掉落物
     *
     * @return Pair(clean, all)
     */
    fun World.cleanDrop(): Pair<Int, Int> {
        PL.debug { Lang["debug.start.drop", "name" to name] }
        val match = dropCfg.match
        val black = dropCfg.black
        val items = entities.filterIsInstance<Item>()
        val entries = items.groupBy {
            it.itemStack.type.name
        }.entries.mapMutable { (k, v) -> k to v.asMutableList() }
        // 附魔物品
        if (Config.config.drop.enchant) entries.forEach { (_, v) ->
            v.removeIf { it.itemStack.itemMeta?.hasEnchants() == true }
        }
        // 有内容的书 成书应从黑白名单处添加
        if (Config.config.drop.writtenBook) entries.forEach { (_, v) ->
            v.removeIf {
                val itemStack = it.itemStack
                if (itemStack.type != Material.WRITABLE_BOOK) return@removeIf false
                val meta = itemStack.itemMeta as? BookMeta ?: return@removeIf false
                meta.hasPages()
            }
        }
        // 黑白名单
        entries.removeIf { (type, list) ->
            type.isMatch(match).also { bool ->
                PL.debug { Lang["debug.clean", "bool" to Lang["debug.bool.$bool"], "type" to type, "count" to list.size] }
            }.let {
                if (black) !it else it
            }
        }
        val clean = entries.flatMap { it.second }
        clean.forEach(Item::remove)
        return clean.size to items.size
    }

    /**
     * 清理全服生物
     */
    fun cleanLiving() {
        if (!livingCfg.enable) return
        val worlds = Bukkit.getWorlds().asSequence().filterNot { chunkCfg.disableWorld.contains(it.name) }
        PL.debug {
            Lang[
                "debug.start.living",
                "world" to worlds.joinToString(Lang["debug.spacing"]) { it.name },
                "name" to Lang["debug.bool.${livingCfg.settings.name}"],
                "lead" to Lang["debug.bool.${livingCfg.settings.lead}"],
                "mount" to Lang["debug.bool.${livingCfg.settings.mount}"],
                "black" to Lang["debug.bool.${livingCfg.black}"],
                "match" to livingCfg.match.joinToString(Lang["debug.spacing"]) { it.pattern },
            ]
        }
        val result = worlds.map { it.cleanLiving() }
        lastLiving = result.sumOf { it.first }
        val all = result.sumOf { it.second }
        val finish = livingCfg.finish
        if (finish.isNotBlank()) PL.broadcastMsg(finish.placeholder(mapOf("clean" to lastLiving, "all" to all)))
    }

    /**
     * 清理指定世界的生物
     *
     * @return Pair(clean, all)
     */
    fun World.cleanLiving(): Pair<Int, Int> {
        val all = livingEntities.filterNot { it is Player }
        val clean = all.groupBy {
            it.type
        }.filter { (type, list) ->
            type.name.isMatch(livingCfg.match).let {
                if (livingCfg.black) it else !it
            }.also { bool ->
                PL.debug { Lang["debug.clean", "bool" to Lang["debug.bool.$bool"], "type" to type, "count" to list.size] }
            }
        }.flatMap {
            it.value
        }.asMutableList()

        // 命名的生物
        if (!livingCfg.settings.name) clean.removeIf { it.customName != null }
        // 拴绳拴住的生物
        if (!livingCfg.settings.lead) clean.removeIf { it.isLeashed }
        // 乘骑中的生物
        if (!livingCfg.settings.mount) clean.removeIf { it.isInsideVehicle }
        clean.forEach(Entity::remove)
        return Pair(clean.size, all.size)
    }

    /**
     * 清理全服区块中的密集实体
     */
    fun cleanChunk() {
        if (!chunkCfg.enable) return
        val worlds = Bukkit.getWorlds().asSequence().filterNot { chunkCfg.disableWorld.contains(it.name) }
        PL.debug {
            Lang[
                "debug.start.chunk",
                "world" to worlds.joinToString(Lang["debug.spacing"]) { it.name },
                "name" to Lang["debug.bool.${chunkCfg.settings.name}"],
                "lead" to Lang["debug.bool.${chunkCfg.settings.lead}"],
                "mount" to Lang["debug.bool.${chunkCfg.settings.mount}"],
                "limit" to chunkCfg.limit.entries.joinToString(Lang["debug.spacing"]) { (type, limit) ->
                    Lang["debug.chunk_entity_data", "type" to type, "limit" to limit]
                },
            ]
        }
        val result = worlds.map { it.cleanChunk() }
        lastChunk = result.sum()
        Config.config.chunk.finish.also { finish ->
            if (finish.isNotBlank()) PL.broadcastMsg(finish.placeholder("clean" to lastChunk))
        }
    }

    /**
     * 清理指定世界的密集实体
     *
     * @return 清理数量
     */
    fun World.cleanChunk(): Int {
        val clean = loadedChunks.flatMap { chunk ->
            chunk.entities.groupBy {
                it.type.name
            }.flatMap a@{ (type, list) ->
                val limit = chunkCfg.limit[type] // 上限
                if (limit == null) {
                    // op通知
                    if (list.size > chunkCfg.count) chunkCfg.format?.also { format ->
                        PL.runTask {
                            PL.sendOpMsg(format.placeholder("chunk" to chunk.info(), "entity" to type, "count" to list.size))
                        }
                    }
                    return@a emptyList()
                }
                // 数量超出限制部分
                val num = list.size - limit
                // 检查settings
                if (num > 0) list.let { l ->
                    // 不清理被命名的生物
                    if (!chunkCfg.settings.name) l.filter { it.customName == null }
                    // 清理远离玩家不会消失的生物
                    else l
                }.let { l ->
                    // 不清理拴绳拴住的生物
                    if (!chunkCfg.settings.lead) l.filter { it !is LivingEntity || !it.isLeashed }
                    // 清理拴绳拴住的生物
                    else l
                }.let { l ->
                    // 不清理乘骑中的生物
                    if (!chunkCfg.settings.mount) l.filter { !it.isInsideVehicle }
                    // 清理乘骑中的生物
                    else l
                }.take(num).also {
                    PL.debug {
                        Lang[
                            "debug.chunk_clean",
                            "type" to type,
                            "count" to list.size,
                            "chunk" to chunk.info(),
                            "limit" to limit,
                        ]
                    }
                }
                else emptyList()
            }
        }
        clean.forEach(Entity::remove)
        return clean.size
    }

    fun Chunk.info() =
        Lang["chunk_info", "xFrom" to x * 16, "xTo" to x * 16 + 15, "zFrom" to z * 16, "zTo" to z * 16 + 15]
}
