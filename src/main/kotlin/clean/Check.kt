package top.e404.eclean.clean

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.EntityType
import top.e404.eclean.clean.Clean.info
import top.e404.eclean.util.color
import top.e404.eclean.util.sendMsgWithPrefix

/**
 * 检查世界实体
 */
object Check {
    fun CommandSender.sendWorldStats(worldName: String) {
        val world = Bukkit.getWorld(worldName)
        if (world == null) {
            sendMsgWithPrefix("&c不存在名为&e${world}&c的世界")
            return
        }
        val entity = world
            .entities
            .groupBy { it.type }
            .map { (k, v) -> k to v.size }
            .sortedByDescending { it.second }
            .joinToString("&7, ") { (k, v) -> "&f$k: ${v.withColor()}个" }
        sendMessage("""&f世界&a${worldName}&f共加载区块${world.loadedChunks.size}个
            |&b实体统计信息:
            |$entity""".trimMargin().color())
    }

    fun CommandSender.sendEntityStats(worldName: String, typeName: String, min: Int = 0) {
        val world = Bukkit.getWorld(worldName)
        if (world == null) {
            sendMsgWithPrefix("&c不存在名为&e${worldName}&c的世界")
            return
        }
        kotlin.runCatching {
            EntityType.valueOf(typeName.uppercase())
        }.onFailure {
            sendMsgWithPrefix("&e${typeName}&c不是有效的实体类型")
        }.onSuccess { type ->
            val entity = world
                .loadedChunks
                .map { it.info() to it.entities.count { e -> e.type == type } }
                .filter { it.second > min }
                .sortedByDescending { e -> e.second }
                .joinToString("\n") { (k, v) -> "&f$k: ${v.withColor()}个" }
                .color()
                .let { s -> if (s == "") "&c无结果" else s }
            sendMessage("&f实体&e${typeName}&f的统计信息\n$entity".color())
        }
    }

    private fun Int.withColor() = when {
        this > 60 -> "&c$this"
        this > 30 -> "&e$this"
        else -> "&a$this"
    }
}