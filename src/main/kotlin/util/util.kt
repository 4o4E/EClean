package top.e404.eclean.util

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

/**
 * 文本匹配正则列表
 *
 * @param list 正则列表
 * @return 若列表中存在匹配的正则则返回true
 */
fun String.isMatch(list: List<String>): Boolean {
    for (regex in list) try {
        if (matches(Regex(regex))) return true
    } catch (t: Throwable) {
        throw RuntimeException("错误的正则: $regex", t)
    }
    return false
}

/**
 * 执行指令
 *
 * @param sender 发送指令的对象
 */
fun String.dispatch(sender: CommandSender = Bukkit.getConsoleSender()) {
    Bukkit.dispatchCommand(sender, this)
}

/**
 * 获取插件实例
 *
 * @param name 插件名字
 * @return 插件实例, 若未加载则为null
 */
fun getPlugin(name: String) = Bukkit.getPluginManager().getPlugin(name)