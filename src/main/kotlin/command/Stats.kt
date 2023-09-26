package top.e404.eclean.command

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import top.e404.eclean.PL
import top.e404.eclean.config.Lang
import top.e404.eplugin.EPlugin.Companion.color
import top.e404.eplugin.command.ECommand

object Stats : ECommand(
    PL,
    "stats",
    "(?i)s|stats",
    false,
    "eclean.admin"
) {
    override val usage get() = Lang["command.usage.stats"].color()

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
                if (!PL.isPlayer(sender)) return
                sender.sendWorldStats((sender as Player).world.name)
            }

            2 -> sender.sendWorldStats(args[1])
            else -> sender.sendMessage(usage)
        }
    }
}
