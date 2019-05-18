package com.github.kuro46.logmanager

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.function.BiPredicate
import java.util.regex.Pattern
import kotlin.streams.toList

object LogUtils {
    /**
     * group1: file name(without extension)
     * group2: year
     * group3: month
     * group4: day
     * group5: number
     * group6: extension
     */
    private val LOG_PATTERN = Pattern.compile("((\\d+)-(\\d+)-(\\d+)-(\\d+))(\\.log(?:\\.gz)?)")

    val LOG_DIRECTORY = Paths.get("./logs/")!!

    fun trimExtensionStr(filePath: Path): String {
        val matcher = LOG_PATTERN.matcher(filePath.fileName.toString())
        if (!matcher.find()) {
            throw IllegalArgumentException("'$filePath' isn't log file")
        }
        return matcher.group(1)
    }

    fun getLogDate(filePath: Path): LogDate {
        val matcher = LOG_PATTERN.matcher(filePath.fileName.toString())
        if (!matcher.find()) {
            throw IllegalArgumentException("'$filePath' isn't log file")
        }

        return LogDate(
            year = matcher.group(2).toInt(),
            month = matcher.group(3).toInt(),
            day = matcher.group(4).toInt(),
            number = matcher.group(5).toInt()
        )
    }

    fun getLogFiles(includeLatest: Boolean): List<Path> {
        return Files.find(LOG_DIRECTORY, 1, BiPredicate { path, _ ->
            return@BiPredicate isLogFile(path, includeLatest)
        }).toList()
    }

    fun isLogFile(filePath: Path, includeLatest: Boolean): Boolean {
        val fileName = filePath.fileName.toString()
        return if (fileName == "latest.log") {
            includeLatest
        } else {
            LOG_PATTERN.matcher(fileName).find()
        }
    }
}

data class LogDate(val year: Int, val month: Int, val day: Int, val number: Int)
