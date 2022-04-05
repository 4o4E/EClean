package top.e404.eclean.command

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import top.e404.eclean.clean.Clean
import top.e404.eclean.clean.Clean.cleanChunk
import top.e404.eclean.clean.Clean.cleanDrop
import top.e404.eclean.clean.Clean.cleanLiving
import top.e404.eclean.util.color
import top.e404.eclean.util.sendMsgWithPrefix

object Clean : AbstractCommand(
    "clean",
    false,
    "eclean.admin"
) {
    override val help = """&a/ec clean &f立刻执行一次清理(执行清理通知, 按照配置文件中的规则)
        |&a/ec clean entity &f立刻执行一次实体清理(执行清理通知, 按照配置文件中的规则)
        |&a/ec clean entity <世界名> &f立刻在指定世界执行一次实体清理(&c不&f执行清理通知, 按照配置文件中的规则)
        |&a/ec clean drop &f立刻执行一次掉落物清理(执行清理通知, 按照配置文件中的规则)
        |&a/ec clean drop <世界名> &f立刻在指定世界执行一次掉落物清理(&c不&f执行清理通知, 按照配置文件中的规则)
        |&a/ec clean chunk &f立刻执行一次密集实体清理(执行清理通知, 按照配置文件中的规则)
        |&a/ec clean chunk <世界名> &f立刻在指定世界执行一次密集实体清理(&c不&f执行清理通知, 按照配置文件中的规则)
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
                "entity" -> cleanLiving()
                "drop" -> cleanDrop()
                "chunk" -> cleanChunk()
                else -> sender.sendMessage(help)
            }
            3 -> {
                val world = Bukkit.getWorld(args[2])
                if (world == null) {
                    sender.sendMsgWithPrefix("&c不存在名为`${args[2]}`的世界")
                    return
                }
                val i = when (args[1].lowercase()) {
                    "entity" -> world.cleanLiving().run { "($first/$second)" }
                    "drop" -> world.cleanDrop().run { "($first/$second)" }
                    "chunk" -> world.cleanChunk()
                    else -> {
                        sender.sendMessage(help)
                        return
                    }
                }
                sender.sendMsgWithPrefix("&a共清理&6${i}&a个实体")
            }
            else -> sender.sendMessage(help)
        }
    }
}