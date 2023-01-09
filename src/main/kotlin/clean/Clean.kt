package top.e404.eclean.clean

import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.entity.Item
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask
import top.e404.eclean.PL
import top.e404.eclean.config.*
import top.e404.eplugin.EPlugin.Companion.color
import top.e404.eplugin.EPlugin.Companion.placeholder

object Clean {
    private var tasks = ArrayList<BukkitTask>()
    private fun String.isMatch(list: List<Regex>) = list.any { it matches this }

    private val chunkCfg: ChunkConfig
        get() = Config.config.chunk
    private val dropCfg: DropConfig
        get() = Config.config.drop
    private val livingCfg: LivingConfig
        get() = Config.config.living
    private val duration: Long
        get() = Config.config.duration

    fun schedule() {
        tasks.apply {
            forEach(BukkitTask::cancel)
            clear()
        }
        // 清理任务
        PL.info("&f设置清理任务, 间隔${duration}秒")
        tasks.add(PL.runTaskTimer(duration * 20, duration * 20, Clean::clean))
        // 提醒消息
        Config.config.message.filter { (stamp, message) ->
            (stamp < duration).also {
                if (!it) PL.warn(Lang["warn.out_of_range", "message" to message, "duration" to duration])
            }
        }.mapNotNull { (delay, msg) ->
            // 提醒
            msg.let {
                PL.info("&f设置清理前${delay}秒提醒: ${it.color()}")
                PL.runTaskTimer((duration - delay) * 20, duration * 20) { PL.broadcastMsg(it) }
            }
        }.forEach(tasks::add)
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
        val worlds = Bukkit.getWorlds().filterNot {
            dropCfg.disableWorld.contains(it.name)
        }
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
        val clean = result.sumOf { it.first }
        val all = result.sumOf { it.second }
        val finish = dropCfg.finish
        if (!finish.isNullOrBlank())
            PL.broadcastMsg(finish.placeholder("clean" to clean, "all" to all))
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
        val clean = items.groupBy {
            it.itemStack.type.name
        }.filter { (type, list) ->
            type.isMatch(match).let {
                if (black) it else !it
            }.also { bool ->
                PL.debug { Lang["debug.clean", "bool" to Lang["debug.bool.$bool"], "type" to type, "count" to list.size] }
            }
        }.flatMap { it.value }
        clean.forEach(Item::remove)
        return Pair(clean.size, items.size)
    }

    /**
     * 清理全服生物
     */
    fun cleanLiving() {
        if (!livingCfg.enable) return
        val worlds = Bukkit.getWorlds().filterNot { livingCfg.disableWorld.contains(it.name) }
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
        val clean = result.sumOf { it.first }
        val all = result.sumOf { it.second }
        val finish = livingCfg.finish
        if (finish.isNotBlank()) PL.broadcastMsg(finish.placeholder(mapOf("clean" to clean, "all" to all)))
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
        }.let { list ->
            // 不清理被命名的生物
            if (!livingCfg.settings.name) list.filter { it.customName == null }
            // 清理远离玩家不会消失的生物
            else list
        }.let { list ->
            // 不清理拴绳拴住的生物
            if (!livingCfg.settings.lead) list.filter { !it.isLeashed }
            // 清理拴绳拴住的生物
            else list
        }.let { list ->
            // 不清理乘骑中的生物
            if (!livingCfg.settings.mount) list.filter { !it.isInsideVehicle }
            // 清理乘骑中的生物
            else list
        }
        clean.forEach(Entity::remove)
        return Pair(clean.size, all.size)
    }

    /**
     * 清理全服区块中的密集实体
     */
    fun cleanChunk() {
        if (!chunkCfg.enable) return
        val worlds = Bukkit.getWorlds().filterNot { chunkCfg.disableWorld.contains(it.name) }
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
        Config.config.chunk.finish.also { finish ->
            if (finish.isNotBlank()) PL.broadcastMsg(finish.placeholder("clean" to result.sum()))
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
                            PL.broadcastMsg(
                                format.placeholder(
                                    mapOf(
                                        "chunk" to chunk.info(),
                                        "entity" to type,
                                        "count" to list.size
                                    )
                                )
                            )
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

    fun Chunk.info() = Lang["chunk_info", "chunkX" to x, "chunkZ" to z, "x" to x * 16 + 8, "z" to z * 16 + 8]
}