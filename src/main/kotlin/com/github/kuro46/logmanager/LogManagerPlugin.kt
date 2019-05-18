package com.github.kuro46.logmanager

import org.bukkit.plugin.java.JavaPlugin

/**
 * @author shirokuro
 */
class LogManagerPlugin : JavaPlugin() {
    override fun onEnable() {
        LogManager.init(this)
    }

    override fun onDisable() {
        LogManager.reset()
    }
}
