package top.e404.eclean.command

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import top.e404.eclean.PL
import top.e404.eclean.config.Lang
import top.e404.eplugin.command.ECommand

object EntityStats : ECommand(
    PL,
    "entity",
    "(?i)e|entity",
    false,
    "eclean.admin"
) {
    override val usage get() = Lang["command.usage.entity"]

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
                if (!PL.isPlayer(sender)) return
                sender.sendEntityStats((sender as Player).world.name, args[1])
            }

            3 -> sender.sendEntityStats(args[2], args[1])
            4 -> {
                val min = args[3].toIntOrNull()
                if (min == null) {
                    PL.sendMsgWithPrefix(sender, Lang["message.invalid_number", "number" to args[3]])
                    return
                }
                sender.sendEntityStats(args[2], args[1], min)
            }

            else -> sender.sendMessage(usage)
        }
    }
}
