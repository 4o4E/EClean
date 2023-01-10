package top.e404.eclean.config

import kotlinx.serialization.Serializable

@Serializable
data class ConfigData(
    var debug: Boolean = false,
    var update: Boolean = true,
    val duration: Long,
    val message: Map<Long, String>,
    val living: LivingConfig,
    val drop: DropConfig,
    val chunk: ChunkConfig,
)