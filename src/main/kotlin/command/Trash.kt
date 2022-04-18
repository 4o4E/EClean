package top.e404.eclean.command

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import top.e404.eclean.util.color
import top.e404.eclean.util.sendMsgWithPrefix

object Trash : AbstractCommand(
    "trash",
    true,
    "eclean.trash"
) {
    override val help = "&a/eclean trash &f打开垃圾桶".color()
    override fun onCommand(sender: CommandSender, args: Array<out String>) {
        val inv = Bukkit.createInventory(null, 54, "&6垃圾桶, &4关闭后垃圾桶内物品无法找回".color())
        (sender as Player).openInventory(inv)
        sender.sendMsgWithPrefix("&a已打开垃圾桶&4(关闭后垃圾桶内物品无法找回)")
    }
}