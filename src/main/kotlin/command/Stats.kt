package top.e404.eclean.command

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import top.e404.eclean.clean.Check.sendWorldStats
import top.e404.eclean.util.color
import top.e404.eclean.util.isPlayer

object Stats : AbstractCommand(
    "stats",
    false,
    "eclean.admin"
) {
    override val help = """&a/eclean stats &f- 统计当前所在世界的实体和区块统计
        |&a/eclean stats <世界名> &f- 统计实体和区块统计
    """.trimMargin().color()

    override fun onTabComplete(
        sender: CommandSender,
        args: Array<out String>,
        complete: MutableList<String>,
    ) {
        if (args.size == 2) Bukkit.getWorlds().forEach { complete.add(it.name) }
    }

    override fun onCommand(
        sender: CommandSender,
        args: Array<out String>,
    ) {
        when (args.size) {
            1 -> {
                if (!sender.isPlayer()) return
                sender.sendWorldStats((sender as Player).world.name)
            }
            2 -> sender.sendWorldStats(args[1])
            else -> sender.sendMessage(help)
        }
    }
}