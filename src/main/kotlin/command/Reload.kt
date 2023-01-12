package top.e404.eclean.command

import org.bukkit.command.CommandSender
import top.e404.eclean.PL
import top.e404.eclean.clean.Clean
import top.e404.eclean.config.Config
import top.e404.eclean.config.Lang
import top.e404.eplugin.EPlugin.Companion.color
import top.e404.eplugin.command.ECommand

object Reload : ECommand(
    PL,
    "reload",
    "(?i)r|reload",
    false,
    "eclean.admin"
) {
    private val s1 = "&c配置文件`config.yml`格式错误".color()
    private val s2 = "&c计划清理任务时出现异常".color()
    override val usage: String
        get() = Lang["command.usage.reload"]

    override fun onCommand(sender: CommandSender, args: Array<out String>) {
        plugin.runTaskAsync {
            try {
                Config.load(sender)
            } catch (e: Exception) {
                plugin.sendAndWarn(
                    sender,
                    Lang[
                        "message.invalid_config",
                        "file" to "config.yml"
                    ],
                    e
                )
                return@runTaskAsync
            }
            try {
                Clean.schedule()
            } catch (e: Exception) {
                plugin.sendAndWarn(
                    sender,
                    Lang[
                        "message.invalid_config",
                        "file" to "config.yml"
                    ],
                    e
                )
                return@runTaskAsync
            }
            plugin.sendMsgWithPrefix(sender, Lang["command.reload_done"])
        }
    }
}