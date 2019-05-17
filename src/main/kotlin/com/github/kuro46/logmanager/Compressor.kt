package com.github.kuro46.logmanager

import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDate
import java.util.function.BiPredicate
import java.util.zip.GZIPInputStream
import kotlin.streams.toList

/**
 * @author shirokuro
 */
class Compressor(private val compressNDaysAgo: Int = 2) {
    init {
        execute()
    }

    private fun execute() {
        val compressableFiles = getCompressableFiles()

        if (compressableFiles.isEmpty()) {
            return
        }

        val archiveFile = LogUtils.LOG_DIRECTORY.resolve("logs.zip")
        if (Files.notExists(archiveFile)) {
            Files.createFile(archiveFile)
        }

        ZipEntryWriter(archiveFile).use { zipOutput ->
            for (compressableFile in compressableFiles) {
                val fileName = compressableFile.fileName.toString()

                val sourceStream = Files.newInputStream(compressableFile).buffered()
                val stream = if (!fileName.endsWith(".log.gz")) {
                    sourceStream
                } else {
                    GZIPInputStream(sourceStream)
                }

                stream.use {
                    val withoutExtension = LogUtils.trimExtensionStr(fileName)

                    val data = ZipEntryData("$withoutExtension.log", stream)
                    zipOutput.writeEntry(data)
                }

                Files.delete(compressableFile)
            }
        }
    }

    private fun getCompressableFiles(): List<Path> {
        return Files.find(LogUtils.LOG_DIRECTORY, 1, BiPredicate { path, _ ->
            val fileName = path.fileName.toString()

            if (!LogUtils.isLogFile(fileName, false)) {
                return@BiPredicate false
            }

            val logDate = LogUtils.getLogDate(LogUtils.trimExtensionStr(fileName))
            val fileLocalDate = LocalDate.of(logDate.year, logDate.month, logDate.day)
            val twoDaysAgo = LocalDate.now().minusDays(compressNDaysAgo.toLong())

            return@BiPredicate fileLocalDate.toEpochDay() <= twoDaysAgo.toEpochDay()
        }).toList()
    }
}
