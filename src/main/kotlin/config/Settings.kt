package top.e404.eclean.config

import kotlinx.serialization.Serializable

@Serializable
data class Settings(
    val name: Boolean,
    val lead: Boolean,
    val mount: Boolean,
)