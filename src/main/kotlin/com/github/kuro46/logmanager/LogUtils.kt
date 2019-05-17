package com.github.kuro46.logmanager

import java.nio.file.Paths

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
        for (char in fileName.toCharArray()) {
            if (char == '-' && buffer.isNotEmpty()) {
                dataList.add(buffer.toString())
                buffer.clear()
            }

            buffer.append(char)
        }

        return LogDate(
            year = dataList[0].toInt(),
            month = dataList[1].toInt(),
            day = dataList[2].toInt(),
            number = dataList[3].toInt()
        )
    }

    fun isLogFile(fileName: String, includeLatest: Boolean): Boolean {
        return (includeLatest && isLatestLog(fileName)) || isLogExtension(fileName)
    }

    private fun isLatestLog(fileName: String): Boolean {
        return fileName == "latest.log"
    }

    private fun isLogExtension(fileName: String): Boolean {
        return fileName.endsWith(".log") || fileName.endsWith(".log.gz")
    }
}

data class LogDate(val year: Int, val month: Int, val day: Int, val number: Int)
