package top.e404.eclean.command

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import top.e404.eclean.util.sendMsgWithPrefix

/**
 * 代表一个指令
 *
 * @property name 指令的名字
 * @property permission 指令所需权限
 */
@Suppress("UNUSED")
abstract class AbstractCommand(
    val name: String,
    val mustByPlayer: Boolean,
    private vararg val permission: String,
) {
    companion object {
        fun MutableList<String>.addOnlinePlayers() = Bukkit.getOnlinePlayers().forEach { add(it.name) }
        fun MutableList<String>.addOnlineOps() = Bukkit.getOnlinePlayers().forEach { if (it.isOp) add(it.name) }
    }

    /**
     * 指令处理器
     *
     * @param sender 发送者
     * @param args 参数
     * @since 1.0.0
     */
    abstract fun onCommand(sender: CommandSender, args: Array<out String>)

    /**
     * tab补全处理器
     *
     * @param sender 发送者
     * @param args 参数
     * @return 补全结果
     * @since 1.0.0
     */
    open fun onTabComplete(sender: CommandSender, args: Array<out String>, complete: MutableList<String>) {}

    /**
     * 帮助内容
     *
     * @return 帮助内容, 开头和结尾不要带\n
     * @since 1.0.0
     */
    abstract val help: String

    /**
     * 检查sender权限
     *
     * @param sender 对象
     * @return 若sender有所有需要的权限则返回true
     * @since 1.0.0
     */
    fun hasPerm(sender: CommandSender): Boolean {
        return permission.all { sender.hasPermission(it) }
    }

    /**
     * 检测第一个参数是否匹配指令名字
     *
     * @param head 第一个参数
     * @return 若匹配则返回true
     * @since 1.0.0
     */
    fun matchHead(head: String): Boolean {
        return head.equals(name, true)
    }

    /**
     * 检查参数长度是否符合要求, 若不符合则发送帮助并且返回false
     *
     * @param args 参数
     * @param length 需要的长度
     * @param sender sender
     * @return 若符合要求则返回ture
     * @since 1.0.0
     */
    fun errorArgsLength(args: Array<out String>, length: Int, sender: CommandSender): Boolean {
        if (args.size != length) {
            sender.sendMsgWithPrefix(help)
            return true
        }
        return false
    }

    /**
     * 检查参数长度是否符合要求, 若不符合则发送帮助并且返回false
     *
     * @param args 参数
     * @param range 需要的长度
     * @param sender sender
     * @return 若符合要求则返回ture
     * @since 1.0.0
     */
    fun errorArgsLength(args: Array<out String>, range: IntRange, sender: CommandSender): Boolean {
        if (args.size !in range) {
            sender.sendMsgWithPrefix(help)
            return true
        }
        return false
    }
}