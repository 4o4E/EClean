package top.e404.eclean.command

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import top.e404.eclean.PL
import top.e404.eclean.config.Config
import top.e404.eclean.config.Lang
import top.e404.eplugin.EPlugin.Companion.color
import top.e404.eplugin.command.AbstractDebugCommand

/**
 * debug指令
 */
object Debug : AbstractDebugCommand(
    PL,
    "eclean.admin"
) {
    override val usage: String
        get() = Lang["command.usage.debug"].color()

    override fun onCommand(
        sender: CommandSender,
        args: Array<out String>,
    ) {
        if (sender !is Player) {
            if (Config.config.debug) {
                Config.config.debug = false
                plugin.sendMsgWithPrefix(sender, Lang["debug.console_disable"])
            } else {
                Config.config.debug = true
                plugin.sendMsgWithPrefix(sender, Lang["debug.console_enable"])
            }
            return
        }
        val senderName = sender.name
        if (senderName in plugin.debuggers) {
            plugin.debuggers.remove(senderName)
            plugin.sendMsgWithPrefix(sender, Lang["debug.player_disable"])
        } else {
            plugin.debuggers.add(senderName)
            plugin.sendMsgWithPrefix(sender, Lang["debug.player_enable"])
        }
    }
}