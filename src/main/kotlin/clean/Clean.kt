package top.e404.eclean.clean

import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.entity.Item
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask
import top.e404.eclean.EClean
import top.e404.eclean.config.ChunkConfig.Companion.getChunkConfig
import top.e404.eclean.config.Config
import top.e404.eclean.config.DropConfig.Companion.getDropConfig
import top.e404.eclean.config.LivingConfig.Companion.getLivingConfig
import top.e404.eclean.util.*

object Clean {
    private val scheduler = Bukkit.getScheduler()
    private val instance = EClean.instance
    private var tasks = ArrayList<BukkitTask>()
    private val regex = Regex("\\d+")

    private var chunkCfg = Config.config.getChunkConfig("chunk")
    private var dropCfg = Config.config.getDropConfig("drop")
    private var livingCfg = Config.config.getLivingConfig("living")

    fun update() {
        chunkCfg = Config.config.getChunkConfig("chunk")
        dropCfg = Config.config.getDropConfig("drop")
        livingCfg = Config.config.getLivingConfig("living")
    }

    fun schedule() {
        tasks.apply {
            forEach(BukkitTask::cancel)
            clear()
        }
        val d = Config.duration
        // 清理任务
        info("&f设置清理任务, 间隔${d}秒")
        tasks.add(scheduler.runTaskTimer(instance, Clean::clean, d * 20, d * 20))
        // 提醒消息
        val message = Config.config.getConfigurationSection("message") ?: return
        message.getKeys(false).asSequence().filter {
            // 检查数字
            regex.matches(it).also { b ->
                if (!b) warn("清理消息的时长只能小于清理间隔的数字")
            }
        }.associate {
            it.toLong() to message.getString(it)
        }.filter { (k, v) ->
            (k < d).also {
                if (!it) warn("清理前的消息`${v}`&e设置的时长超过清理间隔`$d`&e, 请在设置中修改(此消息将不会被发送)")
            }
        }.mapNotNull { (delay, msg) ->
            // 提醒
            msg?.let {
                info("&f设置清理前${delay}秒提醒: ${it.color()}")
                scheduler.runTaskTimer(instance, Runnable {
                    sendAllMsgWithPrefix(it)
                }, (d - delay) * 20, d * 20)
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
        debug("""开始清理掉落物
            |启用掉落物清理的世界: [${worlds.joinToString(",") { it.name }}]
            |${if (black) "" else "不"}匹配[${match.joinToString(",")}]的掉落物将会被清理
        """.trimMargin())
        val result = worlds.map { it.cleanDrop() }
        val clean = result.sumOf { it.first }
        val all = result.sumOf { it.second }
        dropCfg.finish?.apply {
            if (trim() == "") return
            val s = placeholder(mapOf("clean" to clean, "all" to all))
            info(s)
            sendAllMsgWithPrefix(s)
        }
    }

    /**
     * 清理指定世界的掉落物
     *
     * @return Pair(clean, all)
     */
    fun World.cleanDrop(): Pair<Int, Int> {
        debug("开始清理${name}的掉落物")
        val match = dropCfg.match
        val black = dropCfg.black
        val items = entities.filterIsInstance<Item>()
        val clean = items.groupBy {
            it.itemStack.type.name
        }.filter { (t, list) ->
            t.isMatch(match).let {
                if (black) it else !it
            }.also {
                debug("${if (it) "" else "不"}清理${t}(数量${list.size})")
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
        debug("""开始清理生物
            |启用生物清理的世界: [${worlds.joinToString(",") { it.name }}]
            |${if (livingCfg.name) "" else "不"}清理被命名的生物
            |${if (livingCfg.lead) "" else "不"}清理拴绳拴住的生物
            |${if (livingCfg.mount) "" else "不"}清理乘骑中的生物
            |${if (livingCfg.black) "" else "不"}匹配[${
            livingCfg.match.joinToString(",")
        }]的生物将会被清理
        """.trimMargin())
        val result = worlds.map { it.cleanLiving() }
        val clean = result.sumOf { it.first }
        val all = result.sumOf { it.second }
        livingCfg.finish?.apply {
            if (trim() == "") return
            val s = placeholder(mapOf("clean" to clean, "all" to all))
            info(s)
            sendAllMsgWithPrefix(s)
        }
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
        }.filter { (t, list) ->
            t.name.isMatch(livingCfg.match).let {
                if (livingCfg.black) it else !it
            }.also {
                debug("${if (it) "" else "不"}清理${t}(数量${list.size})")
            }
        }.flatMap {
            it.value
        }.let { list ->
            // 不清理被命名的生物
            if (!livingCfg.name) list.filter { it.customName == null }
            // 清理远离玩家不会消失的生物
            else list
        }.let { list ->
            // 不清理拴绳拴住的生物
            if (!livingCfg.lead) list.filter { !it.isLeashed }
            // 清理拴绳拴住的生物
            else list
        }.let { list ->
            // 不清理乘骑中的生物
            if (!livingCfg.mount) list.filter { !it.isInsideVehicle }
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
        debug("""开始检查密集实体
            |启用检查的世界: [${worlds.joinToString(",") { it.name }}]
            |${if (chunkCfg.name) "" else "不"}清理被命名的生物
            |${if (chunkCfg.lead) "" else "不"}清理拴绳拴住的生物
            |${if (chunkCfg.mount) "" else "不"}清理乘骑中的生物
            |限制实体包含: [${chunkCfg.check.entries.joinToString(",") { "${it.key}: ${it.value}" }}]"""
            .trimMargin())
        val result = worlds.map { it.cleanChunk() }
        Config.config.getString("chunk.finish")?.apply {
            if (trim() == "") return
            val s = placeholder(mapOf(
                "clean" to result.sum()
            ))
            info(s)
            sendAllMsgWithPrefix(s)
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
                val i = chunkCfg.check[type] // 上限
                if (i == null) {
                    // op通知
                    if (list.size > chunkCfg.count) chunkCfg.format?.apply {
                        scheduler.runTask(instance, Runnable {
                            val s = placeholder(mapOf("chunk" to chunk.info(), "entity" to type, "count" to list.size))
                            info(s)
                            sendOpMsg(s)
                        })
                    }
                    return@a emptyList()
                }
                // 数量超出限制部分
                val num = list.size - i
                // 检查settings
                if (num > 0) list.let { l ->
                    // 不清理被命名的生物
                    if (!chunkCfg.name) l.filter { it.customName == null }
                    // 清理远离玩家不会消失的生物
                    else l
                }.let { l ->
                    // 不清理拴绳拴住的生物
                    if (!chunkCfg.lead) l.filter { it !is LivingEntity || !it.isLeashed }
                    // 清理拴绳拴住的生物
                    else l
                }.let { l ->
                    // 不清理乘骑中的生物
                    if (!chunkCfg.mount) l.filter { !it.isInsideVehicle }
                    // 清理乘骑中的生物
                    else l
                }.take(num).also {
                    debug("清理密集生物${type}(数量${list.size})位于${chunk.info()}(设置的上限为${i})")
                }
                else emptyList()
            }
        }
        clean.forEach(Entity::remove)
        return clean.size
    }

    fun Chunk.info() = "区块: ($x, $z) 坐标: (${x * 16 + 8}, ${z * 16 + 8})"
}