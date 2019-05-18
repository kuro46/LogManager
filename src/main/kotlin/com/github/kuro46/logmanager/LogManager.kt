package com.github.kuro46.logmanager

/**
 * @author shirokuro
 */
class LogManager(plugin: LogManagerPlugin) {

    init {
        plugin.saveDefaultConfig()

        val configuration =
            Configuration.load(plugin.dataFolder.resolve("config.yml").toPath())

        if (configuration.logProcessing.enabled) {
            LogProcessor(configuration)
        }
        if (configuration.decompressAllLogs) {
            Decompressor.decompressAllLogs()
        }
    }

    companion object {
        private var instance: LogManager? = null

        fun init(plugin: LogManagerPlugin) {
            if (instance != null) {
                throw IllegalStateException("Already initialized")
            }

            instance = LogManager(plugin)
        }

        fun reset() {
            if (instance == null) {
                throw IllegalStateException("Not initialized yet")
            }

            instance = null
        }
    }
}
