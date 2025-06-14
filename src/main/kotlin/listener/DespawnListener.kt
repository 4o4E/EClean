package top.e404.eclean.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.ItemDespawnEvent
import top.e404.eclean.PL
import top.e404.eclean.clean.Trashcan
import top.e404.eclean.config.Config
import top.e404.eplugin.config.matches
import top.e404.eplugin.listener.EListener

object DespawnListener : EListener(PL) {
    @EventHandler(priority = EventPriority.HIGHEST)
    fun ItemDespawnEvent.onEvent() {
        val item = entity.itemStack
        PL.debug { "物品到时间后销毁: ${item.type.name}, 世界: ${entity.world.name}" }
        Config.config.trashcan.run {
            if (!enable
                || !despawn.enable
                || despawn.disableWorlds.matches(entity.world.name)
                || !despawn.match.matches(item.type.name)
            ) return
        }
        PL.debug { "回收匹配物品到垃圾桶: ${item.type.name}, 世界: ${entity.world.name}" }
        Trashcan.addItem(item.clone())
        // 确保物品被移除
        entity.remove()
    }
}