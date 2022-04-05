package top.e404.eclean

import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import top.e404.eclean.clean.Clean
import top.e404.eclean.command.CommandManager
import top.e404.eclean.config.Config
import top.e404.eclean.update.Update
import top.e404.eclean.util.color
import top.e404.eclean.util.info

class EClean : JavaPlugin() {
    companion object {
        val logo = listOf(
            """&6 ______     ______     __         ______     ______     __   __   """.color(),
            """&6/\  ___\   /\  ___\   /\ \       /\  ___\   /\  __ \   /\ "-.\ \  """.color(),
            """&6\ \  __\   \ \ \____  \ \ \____  \ \  __\   \ \  __ \  \ \ \-.  \ """.color(),
            """&6 \ \_____\  \ \_____\  \ \_____\  \ \_____\  \ \_\ \_\  \ \_\\"\_\""".color(),
            """&6  \/_____/   \/_____/   \/_____/   \/_____/   \/_/\/_/   \/_/ \/_/""".color())
        lateinit var instance: EClean
    }

    override fun onEnable() {
        instance = this
        Metrics(this, 14312)
        Config.load(null)
        CommandManager.register("eclean")
        Update.init()
        Clean.schedule()
        for (line in logo) info(line)
        info("&a加载完成, 作者404E".color())
    }

    override fun onDisable() {
        Bukkit.getScheduler().cancelTasks(this)
        info("&a已卸载 感谢使用".color())
    }
}