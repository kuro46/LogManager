package com.github.kuro46.logmanager

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDate
import java.util.function.BiPredicate
import java.util.zip.GZIPInputStream
import kotlin.streams.toList

/**
 * @author shirokuro
 */
class LogProcessor(
    configuration: Configuration
) {
    private val logProcessing = configuration.logProcessing

    init {
        execute()
    }

    private fun execute() {
        val filesToProcess = getFilesToProcess()

        if (filesToProcess.isEmpty()) {
            return
        }

        when (logProcessing.processType) {
            ProcessType.COMPRESS -> compress(filesToProcess)
            ProcessType.MOVE -> move(filesToProcess)
        }
    }

    private fun compress(files: List<Path>) {
        val archiveFile =
            Paths.get((logProcessing.optionOfType as ProcessingOption.Compress).fileName)
        if (Files.notExists(archiveFile)) {
            Files.createFile(archiveFile)
        }

        ZipEntryWriter(archiveFile).use { zipOutput ->
            for (filePath in files) {
                val fileName = filePath.fileName.toString()

                val sourceStream = Files.newInputStream(filePath).buffered()
                val stream = if (!fileName.endsWith(".log.gz")) {
                    sourceStream
                } else {
                    GZIPInputStream(sourceStream)
                }

                stream.use {
                    val withoutExtension = LogUtils.trimExtensionStr(filePath)

                    val data = ZipEntryData("$withoutExtension.log", stream)
                    zipOutput.writeEntry(data)
                }

                Files.delete(filePath)
            }
        }
    }

    private fun move(files: List<Path>) {
        val directoryString = (logProcessing.optionOfType as ProcessingOption.Move).directory

        fun buildDirectoryPath(logDate: LogDate): Path {
            val modifiedDirectory = directoryString
                .replace(":year", logDate.year.toString())
                .replace(":month", logDate.month.toString())
                .replace(":day", logDate.day.toString())

            return Paths.get(modifiedDirectory)
        }

        for (file in files) {
            val directory = buildDirectoryPath(
                LogUtils.getLogDate(file.fileName)
            )

            if (Files.notExists(directory)) {
                Files.createDirectories(directory)
            }
            Files.move(file, directory.resolve(file.fileName))
        }
    }

    private fun getFilesToProcess(): List<Path> {
        return Files.find(LogUtils.LOG_DIRECTORY, 1, BiPredicate { path, _ ->
            val fileName = path.fileName

            if (!LogUtils.isLogFile(fileName, false)) {
                return@BiPredicate false
            }

            val logDate = LogUtils.getLogDate(fileName)
            val fileLocalDate = LocalDate.of(logDate.year, logDate.month, logDate.day)
            val twoDaysAgo = LocalDate.now()
                .minusDays(logProcessing.processDaysBefore.toLong())

            return@BiPredicate fileLocalDate.toEpochDay() <= twoDaysAgo.toEpochDay()
        }).toList()
    }
}

enum class ProcessType {
    COMPRESS,
    MOVE
}
