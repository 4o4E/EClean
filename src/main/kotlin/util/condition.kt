package top.e404.eclean.util

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * 判断CommandSender是否是玩家
 *
 * @param sendNotice 非玩家时是否发送提示
 * @return 若是玩家则返回true
 */
fun CommandSender.isPlayer(sendNotice: Boolean = true): Boolean {
    if (this is Player) return true
    if (sendNotice) sendNotPlayer()
    return false
}

/**
 * 判断CommandSender是否是玩家
 *
 * @param sendNotice 非玩家时是否发送提示
 * @return 若是玩家则返回为玩家对象
 */
fun CommandSender.asPlayer(sendNotice: Boolean = true): Player? {
    if (this is Player) return this
    if (sendNotice) sendNotPlayer()
    return null
}

/**
 * 判断是否拥有权限
 *
 * @param perm 权限节点
 * @param sendNotice 无权限时是否发送提示
 * @return 若有权限则返回true
 */
fun CommandSender.hasPerm(perm: String, sendNotice: Boolean = true): Boolean {
    if (hasPermission(perm)) return true
    if (sendNotice) sendNoperm()
    return false
}