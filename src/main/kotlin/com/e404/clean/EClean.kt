package com.e404.clean

import com.e404.clean.Check.sendEntityStats
import com.e404.clean.Check.sendWorldStats
import com.e404.clean.util.*
import com.e404.clean.util.Log.color
import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class EClean : JavaPlugin() {
    companion object {
        lateinit var instance: EClean
        private fun load(sender: CommandSender?) {
            runCatching {
                instance.dataFolder.mkdirs()
                instance.saveDefaultConfig()
            }.onFailure {
                val s = "保存默认配置文件时出现异常"
                sender?.sendMsgWithPrefix("&c$s")
                Log.warn(s, it)
            }
            runCatching {
                instance.reloadConfig()
            }.onFailure {
                val s = "加载配置文件时出现异常"
                sender?.sendMsgWithPrefix("&c$s")
                Log.warn(s, it)
            }
            runCatching {
                Clean.schedule()
            }.onFailure {
                val s = "计划清理任务时出现异常"
                sender?.sendMsgWithPrefix("&c$s")
                Log.warn(s, it)
            }
        }
    }

    override fun onEnable() {
        instance = this
        Metrics(instance, 14312)
        Log.info(logo())
        load(null)
        Update.init()
        Log.info("&aEClean已启用, 作者404E".color())
    }

    private val help = """
        &bEClean 作者404E
        &a/eclean reload &f- 重载插件
        &a/eclean clean &f- 马上触发一次清理
        &a/eclean stats &f- 统计当前所在世界的实体和区块
        &a/eclean stats <世界名> &f- 统计实体和区块
        &a/eclean entity <实体名> &f- 统计当前世界的实体
        &a/eclean entity <实体名> <世界名> &f- 统计实体
        &a/eclean entity <实体名> <世界名> <纳入统计所需数量> &f- 统计实体
    """.trimIndent().color()

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>,
    ): Boolean {
        if (!sender.checkPerm("eclean.admin")) return true
        if (args.isEmpty()) {
            sender.sendMessage(help)
            return true
        }
        when (args[0].lowercase()) {
            "clean" -> {
                Clean.clean()
                sender.sendMsgWithPrefix("&a清理完成")
            }
            "reload" -> {
                load(sender)
                sender.sendMsgWithPrefix("&a重载结束")
            }
            "stats" -> {
                when (args.size) {
                    1 -> {
                        if (!sender.isPlayer()) return true
                        sender.sendWorldStats((sender as Player).world.name)
                    }
                    2 -> sender.sendWorldStats(args[1])
                    else -> sender.sendMessage("""
                        &a/eclean stats &f- 统计当前所在世界的实体和区块统计
                        &a/eclean stats <世界名> &f- 统计实体和区块统计
                    """.trimIndent().color())
                }
            }
            "entity" -> {
                when (args.size) {
                    2 -> {
                        if (!sender.isPlayer()) return true
                        sender.sendEntityStats((sender as Player).world.name, args[1])
                    }
                    3 -> sender.sendEntityStats(args[2], args[1])
                    4 -> {
                        val min = args[3].toIntOrNull()
                        if (min == null) {
                            sender.sendMsgWithPrefix("${args[3]}不是有效数字")
                            return true
                        }
                        sender.sendEntityStats(args[2], args[1], min)
                    }
                    else -> sender.sendMessage("""
                        &a/eclean entity <实体名> &f- 统计当前世界的实体统计
                        &a/eclean entity <实体名> <世界名> &f- 统计实体统计
                        &a/eclean entity <实体名> <世界名> <纳入统计所需数量> &f- 统计实体统计
                    """.trimIndent().color())
                }
            }
            else -> sender.sendUnknow()
        }
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>,
    ): MutableList<String>? {
        if (!sender.hasPermission("eclean.admin")) return null
        when (args.size) {
            1 -> return arrayListOf("reload", "clean", "stats", "entity")
            2 -> when (args[0].lowercase()) {
                "stats" -> return Bukkit.getWorlds().map { it.name }.toMutableList()
                "entity" -> return EntityType.values().map { it.name }.toMutableList()
            }
            3 -> if (args[0].equals("entity", true)) return Bukkit.getWorlds().map { it.name }.toMutableList()
        }
        return null
    }
}