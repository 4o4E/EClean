package top.e404.eclean.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import top.e404.eplugin.config.serialization.RegexSerialization

@Serializable
data class LivingConfig(
    val enable: Boolean,
    @SerialName("disable_world")
    val disableWorld: List<String>,
    val finish: String,
    val settings: Settings,
    @SerialName("is_black")
    val black: Boolean,
    val match: List<@Serializable(RegexSerialization::class) Regex>,
)