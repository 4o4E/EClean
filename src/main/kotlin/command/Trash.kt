package top.e404.eclean.command

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import top.e404.eclean.PL
import top.e404.eclean.clean.Trashcan
import top.e404.eclean.config.Config
import top.e404.eclean.config.Lang
import top.e404.eplugin.command.ECommand

object Trash : ECommand(
    PL,
    "trash",
    "(?i)t|trash",
    true,
    "eclean.trash"
) {
    override val usage get() = Lang["command.usage.trash"]

    override fun onCommand(sender: CommandSender, args: Array<out String>) {
        sender as Player
        if (!Config.config.trashcan.enable) {
            plugin.sendMsgWithPrefix(sender, Lang["command.trash_disable"])
            return
        }
        Trashcan.open(sender)
        plugin.sendMsgWithPrefix(sender, Lang["command.trash_open"])
    }
}
