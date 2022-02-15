package com.e404.clean.util

import java.util.logging.Level
import java.util.logging.Logger

object Log {
    private val log = Logger.getLogger("EClean")
    fun String.color() = this.replace("&", "§")
    fun info(s: String) = log.info(s.color())
    fun warn(s: String, throwable: Throwable? = null) {
        if (throwable != null) log.log(Level.WARNING, s.color(), throwable)
        else log.log(Level.WARNING, s.color())
        s.sendToOperatorWithPrefix()
    }
    private val debugLog = Logger.getLogger("ECleanDebug")
    fun debug(s: String) {
        if (config().getBoolean("debug")) debugLog.info("&b$s".color())
    }
}