package top.e404.eclean.command

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import top.e404.eclean.clean.Check.sendEntityStats
import top.e404.eclean.util.color
import top.e404.eclean.util.isPlayer
import top.e404.eclean.util.sendMsgWithPrefix

object EntityStats : AbstractCommand(
    "entity",
    false,
    "eclean.admin"
) {
    override val help = """&a/eclean entity <实体名> &f- 统计当前世界每个区块的指定实体
        |&a/eclean entity <实体名> <世界名> &f- 统计指定世界个区块的指定实体
        |&a/eclean entity <实体名> <世界名> <纳入统计所需数量> &f- 统计指定世界个区块的指定实体并隐藏数量不超过指定数量的内容
    """.trimMargin().color()

    override fun onTabComplete(
        sender: CommandSender,
        args: Array<out String>,
        complete: MutableList<String>,
    ) {
        when (args.size) {
            2 -> EntityType.values().forEach { complete.add(it.name) }
            3 -> Bukkit.getWorlds().forEach { complete.add(it.name) }
        }
    }

    override fun onCommand(
        sender: CommandSender,
        args: Array<out String>,
    ) {
        when (args.size) {
            2 -> {
                if (!sender.isPlayer()) return
                sender.sendEntityStats((sender as Player).world.name, args[1])
            }
            3 -> sender.sendEntityStats(args[2], args[1])
            4 -> {
                val min = args[3].toIntOrNull()
                if (min == null) {
                    sender.sendMsgWithPrefix("${args[3]}不是有效数字")
                    return
                }
                sender.sendEntityStats(args[2], args[1], min)
            }
            else -> sender.sendMessage(help)
        }
    }
}