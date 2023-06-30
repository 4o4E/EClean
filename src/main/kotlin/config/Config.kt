package top.e404.eclean.config

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import top.e404.eclean.PL
import top.e404.eplugin.config.JarConfigDefault
import top.e404.eplugin.config.KtxConfig

object Config : KtxConfig<ConfigData>(
    plugin = PL,
    path = "config.yml",
    default = JarConfigDefault(PL, "config.yml"),
    serializer = ConfigData.serializer(),
    format = Yaml(configuration = YamlConfiguration(strictMode = false))
)
