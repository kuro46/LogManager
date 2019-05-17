package com.github.kuro46.logmanager

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.function.BiPredicate
import kotlin.streams.toList

object LogUtils {
    val LOG_DIRECTORY = Paths.get("./logs/")!!

    fun trimExtensionStr(fileName: String): String {
        fun trimExtension(extension: String): String {
            val lastIndex = fileName.lastIndex
            val lastIncludeIndex = lastIndex - extension.length

            return fileName.substring(0, lastIncludeIndex + 1)
        }

        if (fileName.endsWith(".log.gz")) {
            return trimExtension(".log.gz")
        } else if (fileName.endsWith(".log")) {
            return trimExtension(".log")
        }

        throw IllegalArgumentException("'$fileName' isn't log file")
    }

    fun getLogDate(fileName: String): LogDate {
        val buffer = StringBuilder()
        val dataList = ArrayList<String>()

        fun flushBufIfNeeded() {
            if (buffer.isNotEmpty()) {
                dataList.add(buffer.toString())
                buffer.clear()
            }
        }

        for (char in fileName.toCharArray()) {
            if (char == '-') {
                flushBufIfNeeded()
                continue
            }

            buffer.append(char)
        }

        flushBufIfNeeded()

        return LogDate(
            year = dataList[0].toInt(),
            month = dataList[1].toInt(),
            day = dataList[2].toInt(),
            number = dataList[3].toInt()
        )
    }

    fun getLogFiles(includeLatest: Boolean): List<Path> {
        return Files.find(LOG_DIRECTORY, 1, BiPredicate { path, _ ->
            val fileName = path.fileName.toString()

            return@BiPredicate isLogFile(fileName, includeLatest)
        }).toList()
    }

    fun isLogFile(fileName: String, includeLatest: Boolean): Boolean {
        return if (isLatestLog(fileName)) {
            includeLatest
        } else {
            isLogExtension(fileName)
        }
    }

    private fun isLatestLog(fileName: String): Boolean {
        return fileName == "latest.log"
    }

    private fun isLogExtension(fileName: String): Boolean {
        return fileName.endsWith(".log") || fileName.endsWith(".log.gz")
    }
}

data class LogDate(val year: Int, val month: Int, val day: Int, val number: Int)
