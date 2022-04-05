package top.e404.eclean.config

import org.bukkit.configuration.ConfigurationSection

data class LivingConfig(
    val enable: Boolean,
    val disableWorld: List<String>,
    val name: Boolean,
    val lead: Boolean,
    val mount: Boolean,
    val black: Boolean,
    val match: List<String>,
    val finish: String?,
) {
    companion object {
        @JvmStatic
        fun ConfigurationSection.getLivingConfig(path: String) =
            getConfigurationSection(path)!!.let { config ->
                LivingConfig(
                    config.getBoolean("enable"),
                    config.getStringList("disable_world"),
                    config.getBoolean("settings.name"),
                    config.getBoolean("settings.lead"),
                    config.getBoolean("settings.mount"),
                    config.getBoolean("is_black"),
                    config.getStringList("match"),
                    config.getString("finish"),
                )
            }
    }
}