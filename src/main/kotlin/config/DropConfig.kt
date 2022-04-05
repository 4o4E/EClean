package top.e404.eclean.config

import org.bukkit.configuration.ConfigurationSection

data class DropConfig(
    val enable: Boolean,
    val disableWorld: List<String>,
    val match: List<String>,
    val black: Boolean,
    val finish: String?,
) {
    companion object {
        @JvmStatic
        fun ConfigurationSection.getDropConfig(path: String) =
            getConfigurationSection(path)!!.let { config ->
                DropConfig(
                    config.getBoolean("enable"),
                    config.getStringList("disable_world"),
                    config.getStringList("match"),
                    config.getBoolean("is_black"),
                    config.getString("finish")
                )
            }
    }
}