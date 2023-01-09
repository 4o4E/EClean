package top.e404.eclean.update

import top.e404.eclean.PL
import top.e404.eclean.config.Config
import top.e404.eplugin.update.EUpdater

object Update : EUpdater(
    plugin = PL,
    url = "https://api.github.com/repos/4o4E/EClean/releases",
    mcbbs = "https://www.mcbbs.net/thread-1305548-1-1.html",
    github = "https://github.com/4o4E/EClean"
) {
    override fun enableUpdate() = Config.config.update
}