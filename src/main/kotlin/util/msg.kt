package top.e404.eclean.util

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import top.e404.eclean.config.Config

/**
 * 转换颜色代码
 *
 * @return 转换完成的颜色代码
 */
fun String.color() =
    replace("&", "§")

/**
 * 添加插件前缀并转换颜色代码
 *
 * @return 添加了插件前缀的字符串
 */
fun String.withPrefix() =
    "${Config.prefix} $this".color()

private const val noperm = "&c无权限"
private const val notPlayer = "&c仅玩家可用"
private const val unknown = "&c未知指令"
private const val invalidArgs = "&c无效参数"

/**
 * 处理颜色代码, 添加前缀, 发送给CommandSender
 *
 * @param s 内容
 */
fun CommandSender.sendMsgWithPrefix(s: String) =
    sendMessage(s.withPrefix())

/**
 * 发送无权限的消息
 */
fun CommandSender.sendNoperm() =
    sendMsgWithPrefix(noperm)

/**
 * 发送仅玩家可用的消息
 */
fun CommandSender.sendNotPlayer() =
    sendMsgWithPrefix(notPlayer)

/**
 * 发送未知指令的消息
 */
fun CommandSender.sendUnknown() =
    sendMsgWithPrefix(unknown)

/**
 * 发送无效参数的消息
 */
fun CommandSender.sendInvalidArgs() =
    sendMsgWithPrefix(invalidArgs)

// placeholder
/**
 * 字符串批量替换占位符
 *
 * @param placeholder 占位符 格式为 <"world", world> 将会替换字符串中的 {world} 为 world
 * @return 经过替换的字符串
 */
fun String.placeholder(placeholder: Map<String, Any>): String {
    var s = this
    for ((k, v) in placeholder.entries) s = s.replace("{$k}", v.toString())
    return s.color()
}

/**
 * 字符串批量替换占位符
 *
 * @param placeholder 占位符 格式为 <"world", world> 将会替换字符串中的 {world} 为 world
 * @return 经过替换的字符串
 */
fun String.placeholder(vararg placeholder: Pair<String, Any>): String {
    var s = this
    for ((k, v) in placeholder) s = s.replace("{$k}", v.toString())
    return s.color()
}

/**
 * 发送消息
 *
 * @param msg 消息内容
 * @param onElse sender不是Player的时候执行
 */
fun CommandSender?.sendOrElse(msg: String, onElse: () -> Unit) {
    if (this is Player) sendMsgWithPrefix(msg)
    else onElse()
}

/**
 * 发送消息并在控制台打印异常
 *
 * @param msg 消息内容
 * @param t 异常
 */
fun CommandSender?.sendAndWarn(msg: String, t: Throwable? = null) {
    if (this is Player) sendMsgWithPrefix(msg)
    if (t != null) warn(msg, t) else warn(msg)
}

/**
 * 向所有在线玩家发送消息(消息不处理)
 *
 * @param msg 消息
 */
fun sendAllMsg(msg: String) {
    for (player in Bukkit.getOnlinePlayers()) player.sendMessage(msg)
}

/**
 * 向所有在线玩家发送消息(添加前缀)
 *
 * @param msg 消息
 */
fun sendAllMsgWithPrefix(msg: String) {
    for (player in Bukkit.getOnlinePlayers()) player.sendMsgWithPrefix(msg)
}


/**
 * 向所有在线OP发送消息(消息不处理)
 *
 * @param msg 消息
 */
fun sendOpMsg(msg: String) {
    for (player in Bukkit.getOnlinePlayers()) if (player.isOp) player.sendMessage(msg)
}

/**
 * 向所有在线OP发送消息(添加前缀)
 *
 * @param msg 消息
 */
fun sendOpMsgWithPrefix(msg: String) {
    for (player in Bukkit.getOnlinePlayers()) if (player.isOp) player.sendMsgWithPrefix(msg)
}