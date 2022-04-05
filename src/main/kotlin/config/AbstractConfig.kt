package top.e404.eclean.config

import org.bukkit.command.CommandSender
import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.configuration.file.YamlConfiguration
import top.e404.eclean.EClean
import top.e404.eclean.util.sendOrElse
import top.e404.eclean.util.warn

/**
 * 代表一个配置文件
 *
 * @property plugin 属于的插件
 * @property jarPath 文件在jar中的路径
 * @property path 文件路径
 */
@Suppress("UNUSED")
abstract class AbstractConfig(
    val jarPath: String,
    val path: String = jarPath,
    val clearBeforeSave: Boolean = false,
) {
    val plugin = EClean.instance
    val default = plugin.getResource(jarPath)!!.readBytes().decodeToString()
    val file = plugin.dataFolder.resolve(jarPath)
    lateinit var config: YamlConfiguration

    /**
     * 保存默认的配置文件
     *
     * @param sender 出现异常时的接收者
     * @since 1.0.0
     */
    fun saveDefault(sender: CommandSender?) {
        if (!file.exists()) file.runCatching {
            if (!parentFile.exists()) parentFile.mkdirs()
            if (isDirectory) {
                val s = "`${path}`是目录, 请手动删除或重命名"
                sender.sendOrElse(s) { warn(s) }
                return
            }
            writeText(default)
        }.onFailure {
            val s = "保存默认配置文件`${path}`时出现异常"
            sender.sendOrElse(s) { warn(s, it) }
        }
    }

    open fun YamlConfiguration.onLoad() {}

    /**
     * 从文件加载配置文件
     *
     * @param sender 出现异常时的通知接收者
     * @since 1.0.0
     */
    fun load(sender: CommandSender?) {
        saveDefault(sender)
        val nc = YamlConfiguration()
        try {
            nc.load(file)
        } catch (e: InvalidConfigurationException) {
            val s = "配置文件`${path}`格式错误, 请检查配置文件, 此文件内容将不会重载"
            sender.sendOrElse(s) { warn(s, e) }
            return
        } catch (t: Throwable) {
            val s = "加载配置文件`${path}`时出现异常, 此文件内容将不会重载"
            sender.sendOrElse(s) { warn(s, t) }
            return
        }
        nc.onLoad()
        config = nc
    }

    open fun YamlConfiguration.beforeSave() {}

    open fun YamlConfiguration.afterSave() {}

    /**
     * 保存配置到文件
     *
     * @param sender 出现异常时的通知接收者
     * @since 1.0.0
     */
    fun save(sender: CommandSender?) {
        if (clearBeforeSave) config = YamlConfiguration()
        try {
            config.apply {
                beforeSave()
                save(file)
                afterSave()
            }
        } catch (t: Throwable) {
            val s = "保存配置文件`${path}`时出现异常"
            sender.sendOrElse(s) { warn(s, t) }
        }
    }
}