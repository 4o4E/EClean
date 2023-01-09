package top.e404.eclean

import org.bukkit.Bukkit
import top.e404.eclean.clean.Clean
import top.e404.eclean.command.CommandManager
import top.e404.eclean.config.Config
import top.e404.eclean.config.Lang
import top.e404.eclean.update.Update
import top.e404.eplugin.EPlugin

class EClean : EPlugin() {
    companion object {
        val logo = listOf(
            """&6 ______     ______     __         ______     ______     __   __   """.color(),
            """&6/\  ___\   /\  ___\   /\ \       /\  ___\   /\  __ \   /\ "-.\ \  """.color(),
            """&6\ \  __\   \ \ \____  \ \ \____  \ \  __\   \ \  __ \  \ \ \-.  \ """.color(),
            """&6 \ \_____\  \ \_____\  \ \_____\  \ \_____\  \ \_\ \_\  \ \_\\"\_\""".color(),
            """&6  \/_____/   \/_____/   \/_____/   \/_____/   \/_/\/_/   \/_/ \/_/""".color()
        )
    }

    override val debugPrefix: String
        get() = langManager.getOrElse("debug_prefix") { "&7[&aEClean&7]" }
    override val prefix: String
        get() = langManager.getOrElse("prefix") { "&7[&6ECleanDebug&7]" }

    override val bstatsId = 14312
    override var debug: Boolean
        get() = Config.config.debug
        set(value) {
            Config.config.debug = value
        }
    override val langManager by lazy { Lang }

    override fun onEnable() {
        PL = this
        bstats()
        Lang.load(null)
        Config.load(null)
        CommandManager.register()
        Update.register()
        Clean.schedule()
        for (line in logo) info(line)
        info("&a加载完成, 作者404E, 感谢使用".color())
    }

    override fun onDisable() {
        Bukkit.getScheduler().cancelTasks(this)
        info("&a已卸载, 作者404E, 感谢使用".color())
    }
}

lateinit var PL: EPlugin
    private set