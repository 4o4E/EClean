package top.e404.eclean.command

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import top.e404.eclean.PL
import top.e404.eclean.config.Lang
import top.e404.eplugin.command.ECommand

object Trash : ECommand(
    PL,
    "trash",
    "(?i)t|trash",
    true,
    "eclean.trash"
) {
    override val usage: String
        get() = Lang["command.usage.trash"]

    override fun onCommand(sender: CommandSender, args: Array<out String>) {
        val inv = Bukkit.createInventory(null, 54, Lang["trash.title"])
        sender as Player
        sender.closeInventory()
        sender.openInventory(inv)
        plugin.sendMsgWithPrefix(sender, Lang["command.trash_open"])
    }
}