package top.e404.eclean.command

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import top.e404.eclean.PL
import top.e404.eclean.config.Lang
import top.e404.eplugin.EPlugin.Companion.color
import top.e404.eplugin.command.ECommand

object Players : ECommand(
    PL,
    "players",
    "(?i)p|players?",
    false,
    "eclean.admin"
) {
    override val usage get() = Lang["command.usage.players"]

    override fun onCommand(
        sender: CommandSender,
        args: Array<out String>,
    ) {
        Bukkit.getOnlinePlayers().groupBy { it.world }.forEach { (world, list) ->
            val s = list.joinToString { "\n  &b${it.name}&f: ${it.location.run { "$blockX $blockY $blockZ" }}" }
            sender.sendMessage("&6${world.name}:$s".color())
        }
    }
}
