package com.e404.clean.util

import org.bukkit.Bukkit
import java.util.logging.Level
import java.util.logging.Logger

object Log {
    private val log = Logger.getLogger("EClean")
    fun String.color() = this.replace("&", "§")
    fun info(s: String) = log.info(s.color())
    fun warn(s: String, throwable: Throwable? = null) {
        val msg = s.color()
        if (throwable != null) log.log(Level.WARNING, msg, throwable)
        else log.log(Level.WARNING, msg)
        for (player in Bukkit.getOnlinePlayers()) if (player.isOp) player.sendMsgWithPrefix(msg)
    }

    private val debugLog = Logger.getLogger("ECleanDebug")
    fun debug(s: String) {
        if (config().getBoolean("debug")) debugLog.info("&b$s".color())
    }
}