package top.e404.eclean.command

import org.bukkit.command.CommandSender
import top.e404.eclean.PL
import top.e404.eclean.clean.Clean
import top.e404.eclean.config.Config
import top.e404.eclean.config.Lang
import top.e404.eplugin.command.ECommand

object Reload : ECommand(
    PL,
    "reload",
    "(?i)r|reload",
    false,
    "eclean.admin"
) {
    override val usage get() = Lang["command.usage.reload"]

    override fun onCommand(sender: CommandSender, args: Array<out String>) {
        plugin.runTaskAsync {
            Lang.load(sender)
            Config.load(sender)
            plugin.runTask {
                Clean.schedule()
                plugin.sendMsgWithPrefix(sender, Lang["command.reload_done"])
            }
        }
    }
}
