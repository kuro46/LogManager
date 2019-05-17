package com.github.kuro46.logmanager

/**
 * @author shirokuro
 */
class LogManager {

    init {
        Compressor()
        Decompressor.decompressAllLogs()
    }

    companion object {
        private var instance: LogManager? = null

        fun init() {
            if (instance != null) {
                throw IllegalStateException("Already initialized")
            }

            instance = LogManager()
        }

        fun reset() {
            if (instance == null) {
                throw IllegalStateException("Not initialized yet")
            }

            instance = null
        }
    }
}
