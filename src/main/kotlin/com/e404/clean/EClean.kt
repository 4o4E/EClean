package com.e404.clean

import com.e404.clean.util.Log
import com.e404.clean.util.Log.color
import com.e404.clean.util.checkPerm
import com.e404.clean.util.logo
import com.e404.clean.util.sendMsgWithPrefix
import org.bstats.bukkit.Metrics
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
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

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>,
    ): Boolean {
        if (!sender.checkPerm("eclean.admin")) return true
        if (args.isEmpty()) {
            sender.sendMessage("""&bEClean 作者404E
                |&a/eclean reload &f- 重载插件
                |&a/eclean clean &f- 马上触发一次清理"""
                .trimMargin()
                .color())
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
        if (args.size == 1) return arrayListOf("reload", "clean")
        return null
    }
}