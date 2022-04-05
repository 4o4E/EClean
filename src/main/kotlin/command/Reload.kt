package top.e404.eclean.command

import org.bukkit.command.CommandSender
import top.e404.eclean.clean.Clean
import top.e404.eclean.config.Config
import top.e404.eclean.util.color
import top.e404.eclean.util.sendMsgWithPrefix
import top.e404.eclean.util.sendOrElse
import top.e404.eclean.util.warn

object Reload : AbstractCommand(
    "reload",
    false,
    "eclean.admin"
) {
    private val s1 = "&c配置文件`config.yml`格式错误".color()
    private val s2 = "&c计划清理任务时出现异常".color()
    override val help = "&a/ec reload &f重载插件".color()
    override fun onCommand(sender: CommandSender, args: Array<out String>) {
        try {
            Config.load(sender)
            Clean.update()
        } catch (t: Throwable) {
            sender.sendOrElse(s1) { warn(s1, t) }
            return
        }
        try {
            Clean.schedule()
        } catch (t: Throwable) {
            sender.sendOrElse(s2) { warn(s2, t) }
            return
        }
        sender.sendMsgWithPrefix("&a重载完成")
    }
}