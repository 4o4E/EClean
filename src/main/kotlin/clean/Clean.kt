package top.e404.eclean.clean

import org.bukkit.scheduler.BukkitTask
import top.e404.eclean.PL
import top.e404.eclean.config.Config
import top.e404.eclean.config.Lang
import top.e404.eclean.util.noOnline
import top.e404.eclean.util.noOnlineClean
import top.e404.eclean.util.noOnlineMessage
import top.e404.eplugin.EPlugin.Companion.color

object Clean {
    private var task: BukkitTask? = null
    private val duration get() = Config.config.duration

    /**
     * 计数, 每20tick++
     */
    var count = 0L
        private set

    fun schedule() {
        count = 0
        task?.cancel()
        // 清理任务
        PL.info("&f设置清理任务, 间隔${duration}秒")
        Config.config.message.forEach { (delay, message) ->
            if (delay > duration) PL.warn(Lang["warn.out_of_range", "message" to message, "duration" to duration])
            else PL.info("&f设置清理前${delay}秒提醒: ${message.color}")
        }
        task = PL.runTaskTimer(20, 20) {
            count++
            if (noOnline && noOnlineMessage) {
                Config.config.message[duration - count]?.let { PL.broadcastMsg(it) }
            }
            if (count >= duration) {
                count = 0
                if (noOnline && noOnlineClean) {
                    clean()
                }
            }
        }
    }

    fun clean() {
        cleanDrop()
        cleanLiving()
        cleanDenseEntities()
    }
}
