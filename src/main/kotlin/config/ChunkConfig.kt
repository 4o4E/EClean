package top.e404.eclean.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChunkConfig(
    val enable: Boolean,
    @SerialName("disable_world")
    val disableWorld: List<String>,
    val finish: String,
    val settings: Settings,
    val count: Int,
    val format: String?,
    val limit: Map<String, Int>,
)