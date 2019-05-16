package com.github.kuro46.logmanager

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDate
import java.util.function.BiPredicate
import java.util.regex.Pattern
import java.util.zip.GZIPInputStream
import kotlin.streams.toList

/**
 * @author shirokuro
 */
class LogCompressor(private val compressNDaysAgo: Int = 2) {
    private val logDirectory = Paths.get("./logs/")

    init {
        execute()
    }

    private fun execute() {
        val compressableFiles = getCompressableFiles()

        if (compressableFiles.isEmpty()) {
            return
        }

        val archiveFile = logDirectory.resolve("logs.zip")
        if (Files.notExists(archiveFile)) {
            Files.createFile(archiveFile)
        }

        ZipEntryWriter(archiveFile).use { zipOutput ->
            for (compressableFile in compressableFiles) {
                val fileName = compressableFile.fileName.toString()

                val stream = if (fileName.endsWith(".log.gz")) { // this is gzip file
                    GZIPInputStream(Files.newInputStream(compressableFile).buffered())
                } else { // this is plain text
                    Files.newInputStream(compressableFile).buffered()
                }

                stream.use {
                    val withoutExtensionMatcher = WITHOUT_EXTENSION_PATTERN.matcher(fileName)
                    withoutExtensionMatcher.find()
                    val withoutExtension = withoutExtensionMatcher.group(1)

                    val data = ZipEntryData("$withoutExtension.log", stream)
                    zipOutput.writeEntry(data)
                }

                Files.delete(compressableFile)
            }
        }
    }

    private fun getCompressableFiles(): List<Path> {
        return Files.find(logDirectory, 1, BiPredicate { path, _ ->
            fun isLatestLog(fileName: String): Boolean {
                return fileName == "latest.log"
            }

            fun isLogExtension(fileName: String): Boolean {
                return fileName.endsWith(".log") || fileName.endsWith(".log.gz")
            }

            val fileName = path.fileName.toString()

            if (isLatestLog(fileName) || !isLogExtension(fileName)) {
                return@BiPredicate false
            }

            val withoutExtensionMatcher = WITHOUT_EXTENSION_PATTERN.matcher(fileName)
            withoutExtensionMatcher.find()
            val withoutExtension = withoutExtensionMatcher.group(1)

            val datePatternMatcher = DATE_PATTERN.matcher(withoutExtension)
            datePatternMatcher.find()
            val year = datePatternMatcher.group(1).toInt()
            val month = datePatternMatcher.group(2).toInt()
            val day = datePatternMatcher.group(3).toInt()

            val fileLocalDate = LocalDate.of(year, month, day)
            val twoDaysAgo = LocalDate.now().minusDays(compressNDaysAgo.toLong())

            return@BiPredicate fileLocalDate.toEpochDay() <= twoDaysAgo.toEpochDay()
        }).toList()
    }

    companion object {
        private val WITHOUT_EXTENSION_PATTERN = Pattern.compile("([^.]+)\\..+")
        private val DATE_PATTERN = Pattern.compile("([^-]+)-([^-]+)-([^-]+).*")
    }
}
