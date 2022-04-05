package top.e404.eclean.update

import com.google.gson.JsonParser
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import top.e404.eclean.EClean
import top.e404.eclean.listener.EListener
import top.e404.eclean.util.color
import top.e404.eclean.util.info
import top.e404.eclean.util.sendMsgWithPrefix
import top.e404.eclean.util.warn
import java.net.URL

object Update : EListener {
    private const val url = "https://api.github.com/repos/4o4E/EClean/releases"
    private const val mcbbs = "https://www.mcbbs.net/thread-1305548-1-1.html"
    private const val github = "https://github.com/4o4E/EClean"
    private val jp = JsonParser()
    private var latest: String? = null
    private lateinit var instance: EClean
    private lateinit var nowVer: String

    // 返回最新的版本
    private fun getLatest() = jp.parse(URL(url).readText())
        .asJsonArray[0]
        .asJsonObject["tag_name"]
        .asString!!

    private fun String.asVersion() = replace(".", "").toInt()

    fun init() {
        instance = EClean.instance
        nowVer = instance.description.version
        register()
        Bukkit.getScheduler().runTaskTimerAsynchronously(instance, Runnable {
            runCatching {
                val v = getLatest()
                val now = instance.description.version
                if (v.asVersion() > now.asVersion()) {
                    latest = v
                    info("""有新版本, 当前版本: &c$nowVer&f, 最新版本: &a$latest
                        |&f更新发布于:&b $mcbbs
                        |&f开源于:&b $github""".trimMargin().color())
                    return@runCatching
                }
            }.onFailure {
                warn("检查版本更新时出现异常, 若需要手动更新请前往&b $mcbbs")
            }
        }, 0, 20 * 60 * 60 * 6)
    }

    @EventHandler
    fun PlayerJoinEvent.onOpJoinGame() = run {
        if (!player.isOp || latest == null) return@run
        player.sendMsgWithPrefix("""&f插件有更新哦, 当前版本: &c$nowVer&f, 最新版本: &a$latest
            |&f更新发布于:&b $mcbbs
            |&f开源于:&b $github""".trimMargin().color())
    }
}