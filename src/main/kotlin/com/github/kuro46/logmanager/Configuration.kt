package com.github.kuro46.logmanager

import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import java.nio.file.Files
import java.nio.file.Path

/**
 * @author shirokuro
 */
class Configuration(
    val decompressAllLogs: Boolean,
    val logProcessing: LogProcessing
) {
    companion object {
        fun load(configFile: Path): Configuration {
            val configuration =
                Files.newBufferedReader(configFile).use { YamlConfiguration.loadConfiguration(it) }
            val decompressAllLogs = configuration.getBoolean("decompress-all-logs")
            val logProcessing = loadLogProcessing(configuration)

            return Configuration(decompressAllLogs, logProcessing)
        }

        private fun loadLogProcessing(configuration: YamlConfiguration): LogProcessing {
            val section = configuration.getConfigurationSection("log-processing")
            val enabled = section.getBoolean("enabled")
            val processDaysBefore = section.getInt("process-days-before")
            val processType = ProcessType.valueOf(section.getString("process-type"))
            val processingOption = loadLogProcessingOption(processType, section)

            return LogProcessing(enabled, processDaysBefore, processType, processingOption)
        }

        private fun loadLogProcessingOption(
            processType: ProcessType,
            configuration: ConfigurationSection
        ): ProcessingOption {
            if (configuration.currentPath != "log-processing") {
                throw IllegalArgumentException()
            }

            val optionSection =
                configuration.getConfigurationSection("options.${processType.name.toLowerCase()}")

            return if (processType == ProcessType.COMPRESS) {
                ProcessingOption.Compress(optionSection.getString("fileName"))
            } else {
                ProcessingOption.None
            }
        }
    }
}

data class LogProcessing(
    val enabled: Boolean,
    val processDaysBefore: Int,
    val processType: ProcessType,
    val optionOfType: ProcessingOption
)

sealed class ProcessingOption {
    object None : ProcessingOption()
    data class Compress(val fileName: String) : ProcessingOption()
}
