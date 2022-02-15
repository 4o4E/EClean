package com.e404.clean.util

import com.e404.clean.util.Log.color
import org.bukkit.Bukkit

/**
 * 将消息发送给所有在线玩家
 */
fun String.sendToAllWithPrefix() {
    for (player in Bukkit.getOnlinePlayers()) player.sendMsgWithPrefix(this.color())
}

/**
 * 将消息发送给所有在线op
 */
fun String.sendToOperatorWithPrefix() {
    for (player in Bukkit.getOnlinePlayers()) if (player.isOp) player.sendMsgWithPrefix(this.color())
}