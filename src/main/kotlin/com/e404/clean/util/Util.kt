package com.e404.clean.util

import com.e404.clean.EClean
import com.e404.clean.util.Log.color
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

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
 * 获取玩家主手的物品
 *
 * @return 物品, 如果是AIR返回null
 */
fun Player.getItemInMainHand(): ItemStack? {
    val item = inventory.itemInMainHand
    if (item.type == Material.AIR) return null
    return item
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
 * 检查sender是否是玩家, 若不是玩家则发送提醒消息
 *
 * @return 若sender是player返回true
 */
fun CommandSender.isPlayer(): Boolean {
    if (this is Player) return true
    sendNotPlayer()
    return false
}

/**
 * 未知指令
 */
fun CommandSender.sendUnknow() {
    sendMsgWithPrefix("&c未知指令, 使用&a/ew help&f查看帮助")
}

/**
 * 仅玩家可用
 */
fun CommandSender.sendNotPlayer() {
    sendMsgWithPrefix("&c仅玩家可用")
}

/**
 * 无权限
 */
fun CommandSender.sendNoperm() {
    sendMsgWithPrefix("&c无权限")
}

/**
 * 无效参数
 */
fun CommandSender.sendInvalidArgs() {
    sendMsgWithPrefix("&c无效参数, 使用&a/ew help&f查看帮助")
}

/**
 * 给sender发送消息并在sender不是控制台时在控制台发送info
 *
 * @param s 消息
 */
fun CommandSender?.sendAndInfo(s: String) {
    if (this !is ConsoleCommandSender) Log.info(s)
    this?.sendMsgWithPrefix(s)
}

/**
 * 给sender发送消息并在sender不是控制台时在控制台发送warn
 *
 * @param s 消息
 * @param e 异常
 */
fun CommandSender?.sendAndWarn(s: String, e: Exception) {
    if (this !is ConsoleCommandSender) Log.warn(s, e)
    this?.sendMsgWithPrefix("&c$s")
}

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
 * 执行指令
 *
 * @param sender 执行的对象
 */
fun String.exec(sender: CommandSender = Bukkit.getConsoleSender()) {
    Bukkit.dispatchCommand(sender, this)
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

fun doWarnable(description: String, listener: CommandSender?, task: () -> Unit) {
    try {
        task()
    } catch (e: Exception) {
        val s = "${description}时出现异常"
        listener.sendAndWarn(s, e)
    }
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
