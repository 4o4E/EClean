package top.e404.eclean.config

import org.bukkit.configuration.file.YamlConfiguration

object Config : AbstractConfig("config.yml") {
    var prefix = "&7[&2清理&7]"
    var debug = false
    var duration = 600L
    var update = false
    override fun YamlConfiguration.onLoad() {
        getString("prefix")?.also { prefix = it }
        duration = getLong("duration")
        debug = getBoolean("debug")
        update = getBoolean("update")
    }
}