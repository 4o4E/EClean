package top.e404.eclean.hook

import top.e404.eclean.PL
import top.e404.eplugin.hook.EHookManager
import top.e404.eplugin.hook.placeholderapi.PlaceholderAPIHook

object HookManager : EHookManager(PL, PapiHook)

object PapiHook : PlaceholderAPIHook(PL)