package top.e404.eclean.command

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import top.e404.eclean.PL
import top.e404.eclean.clean.Clean
import top.e404.eclean.clean.Clean.cleanChunk
import top.e404.eclean.clean.Clean.cleanDrop
import top.e404.eclean.clean.Clean.cleanLiving
import top.e404.eclean.config.Lang
import top.e404.eplugin.EPlugin.Companion.color
import top.e404.eplugin.command.ECommand

object Clean : ECommand(
    PL,
    "clean",
    "(?i)clean",
    false,
    "eclean.admin"
) {
    override val usage = """&a/eclean clean &f立刻执行一次清理(执行清理通知, 按照配置文件中的规则)
        |&a/eclean clean entity &f立刻执行一次实体清理(执行清理通知, 按照配置文件中的规则)
        |&a/eclean clean entity <世界名> &f立刻在指定世界执行一次实体清理(&c不&f执行清理通知, 按照配置文件中的规则)
        |&a/eclean clean drop &f立刻执行一次掉落物清理(执行清理通知, 按照配置文件中的规则)
        |&a/eclean clean drop <世界名> &f立刻在指定世界执行一次掉落物清理(&c不&f执行清理通知, 按照配置文件中的规则)
        |&a/eclean clean chunk &f立刻执行一次密集实体清理(执行清理通知, 按照配置文件中的规则)
        |&a/eclean clean chunk <世界名> &f立刻在指定世界执行一次密集实体清理(&c不&f执行清理通知, 按照配置文件中的规则)
    """.trimMargin().color()

    private val arg = listOf("entity", "drop", "chunk")
    override fun onTabComplete(
        sender: CommandSender,
        args: Array<out String>,
        complete: MutableList<String>,
    ) {
        when (args.size) {
            2 -> complete.addAll(arg)
            3 -> Bukkit.getWorlds().forEach { complete.add(it.name) }
        }
    }

    override fun onCommand(
        sender: CommandSender,
        args: Array<out String>,
    ) {
        when (args.size) {
            1 -> Clean.clean()
            2 -> when (args[1].lowercase()) {
                "e", "entity" -> cleanLiving()
                "d", "drop" -> cleanDrop()
                "c", "chunk" -> cleanChunk()
                else -> sender.sendMessage(usage)
            }

            3 -> {
                val world = Bukkit.getWorld(args[2])
                if (world == null) {
                    PL.sendMsgWithPrefix(sender, Lang["message.invalid_world", "world" to args[2]])
                    return
                }
                val count = when (args[1].lowercase()) {
                    "e", "entity" -> world.cleanLiving().run { "($first/$second)" }
                    "d", "drop" -> world.cleanDrop().run { "($first/$second)" }
                    "c", "chunk" -> world.cleanChunk()
                    else -> {
                        sender.sendMessage(usage)
                        return
                    }
                }
                PL.sendMsgWithPrefix(sender, Lang["command.clean_done", "count" to count])
            }

            else -> sender.sendMessage(usage)
        }
    }
}