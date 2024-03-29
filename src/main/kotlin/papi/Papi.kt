package top.e404.eclean.papi

import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import top.e404.eclean.PL
import top.e404.eclean.clean.*
import top.e404.eclean.config.Config
import top.e404.eplugin.hook.placeholderapi.PapiExpansion
import top.e404.eplugin.util.parseSecondAsDuration

/**
 * Papi扩展
 *
 * - `%eclean_before_next%` - 距离下一次清理的时间, 单位秒
 * - `%eclean_before_next_formatted%` - 距离下一次清理的时间, 格式化的时间
 * - `%eclean_last_drop%` - 上次清理的掉落物数量
 * - `%eclean_last_living%` - 上次清理的生物数量
 * - `%eclean_last_chunk%` - 上次清理的密集实体数量
 * - `%eclean_trashcan_countdown%` - 垃圾桶清理倒计时, 单位秒
 * - `%eclean_trashcan_countdown_formatted%` - 垃圾桶清理倒计时, 格式化的时间
 */
object Papi : PapiExpansion(PL, "eclean") {
    override fun onPlaceholderRequest(player: Player?, params: String) = onRequest(player, params)

    override fun onRequest(player: OfflinePlayer?, params: String): String? {
        return when (params.lowercase()) {
            "before_next" -> (Config.config.duration - Clean.count).toString()
            "before_next_formatted" -> (Config.config.duration - Clean.count).parseSecondAsDuration()
            "last_drop" -> lastDrop.toString()
            "last_living" -> lastLiving.toString()
            "last_chunk" -> lastChunk.toString()
            "trashcan_countdown" -> Trashcan.countdown.toString()
            "trashcan_countdown_formatted" -> Trashcan.countdown.parseSecondAsDuration()
            else -> null
        }
    }

    private val placeholders = mutableListOf(
        "%eclean_before_next%",
        "%eclean_before_next_formatted%",
        "%eclean_last_drop%",
        "%eclean_last_living%",
        "%eclean_last_chunk%",
        "%eclean_trashcan_countdown%",
        "%eclean_trashcan_countdown_formatted%",
    )

    override fun getPlaceholders() = placeholders
}
