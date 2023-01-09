package top.e404.eclean.config

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.Serializable
import top.e404.eclean.PL
import top.e404.eplugin.config.ESerializationConfig
import top.e404.eplugin.config.JarConfig

object Config : ESerializationConfig<ConfigData>(
    plugin = PL,
    path = "config.yml",
    default = JarConfig(PL, "config.yml"),
    serializer = ConfigData.serializer(),
    format = Yaml.default
)

@Serializable
data class ConfigData(
    var debug: Boolean,
    var update: Boolean,
    val duration: Long,
    val message: Map<Long, String>,
    val living: LivingConfig,
    val drop: DropConfig,
    val chunk: ChunkConfig,
)