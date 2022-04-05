package top.e404.eclean.config

import org.bukkit.configuration.ConfigurationSection

data class ChunkConfig(
    val enable: Boolean,
    val disableWorld: List<String>,
    val name: Boolean,
    val lead: Boolean,
    val mount: Boolean,
    val check: Map<String, Int>,
    val count: Int,
    val format: String?,
) {
    companion object {
        @JvmStatic
        fun ConfigurationSection.getChunkConfig(path: String) =
            getConfigurationSection(path)!!.let { config ->
                ChunkConfig(
                    config.getBoolean("enable"),
                    config.getStringList("disable_world"),
                    config.getBoolean("settings.name"),
                    config.getBoolean("settings.lead"),
                    config.getBoolean("settings.mount"),
                    config.getConfigurationSection("limit")!!.let { limit ->
                        limit.getKeys(false).associateWith { limit.getInt(it) }
                    },
                    config.getInt("count"),
                    getString("format")
                )
            }
    }
}