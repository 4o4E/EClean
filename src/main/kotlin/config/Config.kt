package top.e404.eclean.config

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import top.e404.eclean.PL
import top.e404.eplugin.config.JarConfigDefault
import top.e404.eplugin.config.KtxConfig
import top.e404.eplugin.config.serialization.RegexSerialization

object Config : KtxConfig<ConfigData>(
    plugin = PL,
    path = "config.yml",
    default = JarConfigDefault(PL, "config.yml"),
    serializer = ConfigData.serializer(),
    format = Yaml(configuration = YamlConfiguration(strictMode = false))
)

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

@Serializable
data class DropConfig(
    val enable: Boolean,
    @SerialName("disable_world")
    val disableWorld: List<String>,
    val finish: String?,
    @SerialName("is_black")
    val black: Boolean,
    val enchant: Boolean = false,
    @SerialName("written_book")
    val writtenBook: Boolean = false,
    val match: List<@Serializable(RegexSerialization::class) Regex>,
)

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

@Serializable
data class Settings(
    val name: Boolean,
    val lead: Boolean,
    val mount: Boolean,
)

@Serializable
data class ChunkConfig(
    val enable: Boolean,
    @SerialName("disable_world")
    val disableWorld: List<String>,
    val finish: String,
    val settings: Settings,
    val count: Int,
    val format: String?,
    val limit: Map<@Serializable(RegexSerialization::class) Regex, Int>,
)
