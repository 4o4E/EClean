package com.e404.clean.util

import com.e404.clean.EClean
import com.e404.clean.util.Log.color
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * 发送带前缀并且替换&的消息
 *
 * @param s 消息
 */
fun CommandSender.sendMsgWithPrefix(s: String) {
    sendMessage("${prefix()} $s".color())
}

/**
 * 给在线op发送带前缀并且替换&的消息
 *
 * @param s 消息
 */
fun sendOpMsg(s: String) {
    Bukkit.getOperators()
        .filter { it.isOnline }
        .forEach { it.player!!.sendMsgWithPrefix(s) }
}

/**
 * 给所有在线玩家发送消息
 *
 * @param s 消息
 */
fun sendAllMsg(s: String) {
    for (p in Bukkit.getOnlinePlayers()) p.sendMsgWithPrefix(s)
}

/**
 * 检查权限节点, 若没有权限则发送提醒消息
 *
 * @param perm 权限节点名字(省略utools.)
 * @return 若sender有此权限节点返回true
 */
fun CommandSender.checkPerm(perm: String): Boolean {
    if (hasPermission(perm)) return true
    sendNoperm()
    return false
}

/**
 * 检查sender是否是玩家, 若不是则发送提醒消息
 *
 * @return 若sender是player则返回true
 */
fun CommandSender.isPlayer(): Boolean {
    if (this is Player) return true
    sendNonPlayer()
    return false
}

/**
 * 无权限
 */
fun CommandSender.sendNoperm() =
    sendMsgWithPrefix("&c无权限")

/**
 * 非玩家
 */
fun CommandSender.sendNonPlayer() =
    sendMsgWithPrefix("&c此指令仅玩家可用")

/**
 * 未知指令
 */
fun CommandSender.sendUnknow() =
    sendMsgWithPrefix("&c未知指令")

/**
 * 文本匹配正则列表
 *
 * @param list 正则列表
 * @return 若列表中存在匹配的正则则返回true
 */
fun String.isMatch(list: List<String>): Boolean {
    for (regex in list) if (matches(Regex(regex))) return true
    return false
}

/**
 * 字符串批量替换占位符
 *
 * @param placeholder 占位符 格式为 <"world", world> 将会替换字符串中的 {world} 为 world
 * @return 经过替换的字符串
 */
fun String.placeholder(placeholder: Map<String, Any>): String {
    var s = this
    for ((k, v) in placeholder.entries) s = s.replace("{$k}", v.toString())
    return s
}

fun instance() = EClean.instance
fun config() = EClean.instance.config
fun prefix() = config().getString("prefix") ?: "&7[&2清理&7]"
fun logo() = """|
    |&6 ______     ______     __         ______     ______     __   __    
    |&6/\  ___\   /\  ___\   /\ \       /\  ___\   /\  __ \   /\ "-.\ \   
    |&6\ \  __\   \ \ \____  \ \ \____  \ \  __\   \ \  __ \  \ \ \-.  \  
    |&6 \ \_____\  \ \_____\  \ \_____\  \ \_____\  \ \_\ \_\  \ \_\\"\_\ 
    |&6  \/_____/   \/_____/   \/_____/   \/_____/   \/_/\/_/   \/_/ \/_/ """
    .trimMargin()
    .color()
