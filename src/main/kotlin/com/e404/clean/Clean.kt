package com.e404.clean

import com.e404.clean.util.*
import com.e404.clean.util.Log.color
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.entity.Entity
import org.bukkit.entity.Item
import org.bukkit.entity.LivingEntity
import org.bukkit.scheduler.BukkitTask

object Clean {
    private val scheduler = Bukkit.getScheduler()
    private var tasks = ArrayList<BukkitTask>()
    private val regex = Regex("\\d+")

    fun schedule() {
        val c = config()
        tasks.apply {
            forEach(BukkitTask::cancel)
            clear()
        }
        val duration = c.getLong("duration")
        // 清理任务
        Log.info("&f设置清理任务, 间隔${duration}秒")
        tasks.add(scheduler.runTaskTimer(instance(), ::clean, duration * 20, duration * 20))
        // 提醒消息
        val message = c.getConfigurationSection("message") ?: return
        message.getKeys(false).asSequence().filter {
            // 检查数字
            regex.matches(it).also { b ->
                if (!b) Log.warn("清理消息的时长只能小于清理间隔的数字")
            }
        }.associate {
            it.toLong() to message.getString(it)
        }.filter {
            (it.key < duration).also { b ->
                if (!b) Log.warn("清理前的消息`${it.value}`设置的时长超过清理间隔`$duration`, 请在设置中修改(此消息将不会被发送)")
            }
        }.mapNotNull { (delay, msg) ->
            // 提醒
            msg?.let {
                Log.info("&f设置清理前${delay}秒提醒: ${it.color()}")
                scheduler.runTaskTimer(instance(), Runnable {
                    sendAllMsg(it.color())
                }, (duration - delay) * 20, duration * 20)
            }
        }.forEach(tasks::add)
    }

    fun clean() {
        cleanDrop()
        cleanAlive()
        cleanChunk()
    }

    // 清理掉落物
    private fun cleanDrop() {
        val c = config()
        if (!c.getBoolean("drop.enable")) return
        val disable = c.getStringList("drop.disable_world")
        val worlds = Bukkit.getWorlds().filterNot { disable.contains(it.name) }
        val match = c.getStringList("drop.match")
        val black = c.getBoolean("drop.is_black")
        Log.debug("""开始清理掉落物
            |启用掉落物清理的世界: [${worlds.joinToString(",") { it.name }}]
            |${if (black) "" else "不"}匹配[${match.joinToString(",")}]的掉落物将会被清理"""
            .trimMargin())
        val all: Int
        // 清理
        val drop = worlds.flatMap { w ->
            w.entities.filterIsInstance<Item>()
        }.apply {
            all = size
        }.groupBy {
            it.itemStack.type.name
        }.filter { (t, list) ->
            t.isMatch(match).let {
                if (black) it else !it
            }.also {
                Log.debug("${if (it) "" else "不"}清理${t}(数量${list.size})")
            }
        }.flatMap {
            it.value
        }
        // 清理
        drop.forEach(Entity::remove)
        c.getString("drop.finish")?.apply {
            val s = placeholder(mapOf(
                "clean" to drop.size,
                "all" to all
            ))
            Log.info(s)
            sendAllMsg(s)
        }
    }

    // 清理生物
    private fun cleanAlive() {
        val c = config()
        if (!c.getBoolean("living.enable")) return
        val disable = c.getStringList("living.disable_world")
        val worlds = Bukkit.getWorlds().filterNot { disable.contains(it.name) }
        val name = c.getBoolean("living.settings.name")
        val lead = c.getBoolean("living.settings.lead")
        val mount = c.getBoolean("living.settings.mount")
        val black = c.getBoolean("living.is_black")
        val match = c.getStringList("living.match")
        Log.debug("""开始清理生物
            |启用生物清理的世界: [${worlds.joinToString(",") { it.name }}]
            |${if (name) "" else "不"}清理被命名的生物
            |${if (lead) "" else "不"}清理拴绳拴住的生物
            |${if (lead) "" else "不"}清理乘骑中的生物
            |${if (black) "" else "不"}匹配[${match.joinToString(",")}]的生物将会被清理"""
            .trimMargin())
        // 清理
        val all: Int
        val entities = worlds.flatMap {
            it.livingEntities
        }.apply {
            all = size
        }.groupBy {
            it.type
        }.filter { (t, list) ->
            t.name.isMatch(match).let {
                if (black) it else !it
            }.also {
                Log.debug("${if (it) "" else "不"}清理${t}(数量${list.size})")
            }
        }.flatMap {
            it.value
        }.let {
            if (!name) { // 不清理被命名的生物
                it.filter { entity ->
                    entity.customName == null
                }
            } else it // 清理远离玩家不会消失的生物
        }.let {
            if (!lead) { // 不清理拴绳拴住的生物
                it.filter { entity ->
                    !entity.isLeashed
                }
            } else it // 清理拴绳拴住的生物
        }.let {
            if (!mount) { // 不清理乘骑中的生物
                it.filter { entity ->
                    !entity.isInsideVehicle
                }
            } else it // 清理乘骑中的生物
        }
        // 清理
        entities.forEach(Entity::remove)
        c.getString("living.finish")?.apply {
            val s = placeholder(mapOf(
                "clean" to entities.size,
                "all" to all
            ))
            Log.info(s)
            sendAllMsg(s)
        }
    }

    // 清理区块
    private fun cleanChunk() {
        val c = config()
        if (!c.getBoolean("chunk.enable")) return
        val disable = c.getStringList("chunk.disable_world")
        val worlds = Bukkit.getWorlds().filterNot { disable.contains(it.name) }
        val name = c.getBoolean("chunk.settings.name")
        val lead = c.getBoolean("chunk.settings.lead")
        val mount = c.getBoolean("chunk.settings.mount")
        val limit = c.getConfigurationSection("chunk.limit") ?: return
        val check = limit.getKeys(false).associateWith {
            limit.getInt(it)
        }
        Log.debug("""开始检查密集实体
            |启用检查的世界: [${worlds.joinToString(",") { it.name }}]
            |${if (name) "" else "不"}清理被命名的生物
            |${if (lead) "" else "不"}清理拴绳拴住的生物
            |${if (lead) "" else "不"}清理乘骑中的生物
            |限制实体包含: [${check.entries.joinToString(",") { "${it.key}: ${it.value}" }}]"""
            .trimMargin())
        val count = c.getInt("chunk.count")
        val format = c.getString("chunk.format")
        val clean = worlds.flatMap { w ->
            w.loadedChunks.toList()
        }.flatMap { chunk ->
            chunk.entities.groupBy {
                it.type.name
            }.flatMap a@{ (type, list) ->
                val i = check[type] // 上限
                if (i == null) {
                    // op通知
                    if (list.size > count) format?.apply {
                        scheduler.runTask(instance(), Runnable {
                            val s = placeholder(mapOf(
                                "chunk" to chunk.info(),
                                "entity" to type,
                                "count" to list.size
                            ))
                            Log.info(s)
                            sendOpMsg(s)
                        })
                    }
                    return@a emptyList()
                }
                // 数量超出限制部分
                val num = list.size - i
                // 检查settings
                if (num > 0) list.let {
                    if (!name) { // 不清理被命名的生物
                        it.filter { entity ->
                            entity.customName == null
                        }
                    } else it // 清理远离玩家不会消失的生物
                }.let {
                    if (!lead) { // 不清理拴绳拴住的生物
                        it.filter { entity ->
                            // 非生物 || 未被拴绳拴住
                            entity !is LivingEntity || !entity.isLeashed
                        }
                    } else it // 清理拴绳拴住的生物
                }.let {
                    if (!mount) { // 不清理乘骑中的生物
                        it.filter { entity ->
                            !entity.isInsideVehicle
                        }
                    } else it // 清理乘骑中的生物
                }.take(num).also {
                    Log.debug("清理密集生物${type}(数量${list.size})位于${chunk.info()}(设置的上限为${i})")
                }
                else emptyList()
            }
        }
        // 清理
        clean.forEach(Entity::remove)
        c.getString("chunk.finish")?.apply {
            val s = placeholder(mapOf(
                "clean" to clean.size
            ))
            Log.info(s)
            sendAllMsg(s)
        }
    }

    private fun Chunk.info() = """区块x: $x(${x * 16} - ${(x + 1) * 16}), z: $z(${z * 16} - ${(z + 1) * 16})"""
}