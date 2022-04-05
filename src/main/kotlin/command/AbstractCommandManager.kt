package top.e404.eclean.command

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import top.e404.eclean.util.color
import top.e404.eclean.util.isPlayer
import top.e404.eclean.util.sendNoperm
import top.e404.eclean.util.sendUnknown

/**
 * 代表一个抽象的指令管理器
 *
 * @property instance 插件实例
 * @property commands 指令列表
 */
@Suppress("UNUSED")
abstract class AbstractCommandManager(
    val instance: JavaPlugin,
    val commands: List<AbstractCommand>,
) : TabExecutor {
    fun register(name: String) = instance.getCommand(name)?.also {
        it.setExecutor(this)
        it.tabCompleter = this
    }

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>,
    ): Boolean {
        if (args.isEmpty()) {
            sender.sendHelp()
            return true
        }
        val head = args[0].lowercase()
        if (head == "help") {
            sender.sendHelp()
            return true
        }
        for (c in commands) {
            // 匹配指令头
            if (c.name.equals(head, true)) {
                // 无权限
                if (!c.hasPerm(sender)) {
                    sender.sendNoperm()
                    return true
                }
                // 此指令只能由玩家执行 && 执行者不是玩家
                if (c.mustByPlayer && !sender.isPlayer()) return true
                c.onCommand(sender, args)
                return true
            }
        }
        sender.sendUnknown()
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>,
    ): MutableList<String> {
        val list = ArrayList<String>()
        val size = args.size // 最小只会为1
        val canUse = commands.filter { it.hasPerm(sender) }
        // 接管长度为1的指令的tab补全
        if (size == 1) return canUse.map { it.name }.toMutableList()
        // 其他长度
        val head = args[0]
        // 匹配指令头
        for (c in canUse) if (c.matchHead(head)) {
            // 此指令只能由玩家执行 && 执行者不是玩家
            if (c.mustByPlayer && sender !is Player) continue
            // 传递给指令
            c.onTabComplete(sender, args, list)
        }
        return list
    }

    private fun CommandSender.sendHelp() {
        val help = commands
            .filter { it.hasPerm(this) && (!it.mustByPlayer || this is Player) }
            .joinToString("\n") { it.help }
        instance.run {
            description.run {
                sendMessage("&7-=[ &6${name} V$version&b by $authors &7]=-\n${help}".color())
            }
        }
    }
}