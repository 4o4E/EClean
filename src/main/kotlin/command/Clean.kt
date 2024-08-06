package top.e404.eclean.command

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import top.e404.eclean.PL
import top.e404.eclean.clean.*
import top.e404.eclean.clean.Clean
import top.e404.eclean.clean.Trashcan.cleanTrash
import top.e404.eclean.config.Lang
import top.e404.eplugin.command.ECommand

object Clean : ECommand(
    PL,
    "clean",
    "(?i)clean",
    false,
    "eclean.admin"
) {
    override val usage get() = Lang["command.usage.clean"]

    private val arg = listOf("entity", "drop", "chunk", "trash")
    override fun onTabComplete(
        sender: CommandSender,
        args: Array<out String>,
        complete: MutableList<String>,
    ) {
        when (args.size) {
            2 -> complete.addAll(arg)
            3 -> Bukkit.getWorlds().forEach { complete.add(it.name) }
        }
    }

    override fun onCommand(
        sender: CommandSender,
        args: Array<out String>,
    ) {
        when (args.size) {
            1 -> Clean.clean()
            2 -> when (args[1].lowercase()) {
                "e", "entity" -> cleanLiving()
                "d", "drop" -> cleanDrop()
                "c", "chunk" -> cleanDenseEntities()
                "t", "trash" -> cleanTrash()
                else -> sender.sendMessage(usage)
            }

            3 -> {
                val world = Bukkit.getWorld(args[2])
                if (world == null) {
                    PL.sendMsgWithPrefix(sender, Lang["message.invalid_world", "world" to args[2]])
                    return
                }
                val count = when (args[1].lowercase()) {
                    "e", "entity" -> world.cleanLiving().run { "($first/$second)" }
                    "d", "drop" -> world.cleanDrop().run { "($first/$second)" }
                    "c", "chunk" -> world.cleanChunkDenseEntities()
                    else -> {
                        sender.sendMessage(usage)
                        return
                    }
                }
                PL.sendMsgWithPrefix(sender, Lang["command.clean_done", "count" to count])
            }

            else -> sender.sendMessage(usage)
        }
    }
}
