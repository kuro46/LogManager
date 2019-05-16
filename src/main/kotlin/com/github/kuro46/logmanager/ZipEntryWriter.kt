package com.github.kuro46.logmanager

// https://gist.github.com/kuro46/840b770d51bc6e7f367f17cd4c425122

import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

/**
 * @author shirokuro
 */
class ZipEntryWriter(val path: Path) : AutoCloseable {
    private val tempFilePath: Path = Files.createTempFile(null, null)
    private val writer = ZipOutputStream(Files.newOutputStream(tempFilePath).buffered())

    init {
        moveEntriesIfNeeded()
    }

    private fun moveEntriesIfNeeded() {
        if (Files.notExists(path)) {
            return
        }

        ZipInputStream(Files.newInputStream(path).buffered()).use { reader ->
            val buffer = ByteArray(1024)
            while (true) {
                val entry = reader.nextEntry ?: break
                writer.putNextEntry(ZipEntry(entry.name))

                while (true) {
                    val len = reader.read(buffer)
                    if (len == -1) {
                        break
                    }

                    writer.write(buffer, 0, len)
                }
            }
        }
    }

    fun writeEntry(entryData: ZipEntryData) {
        writer.putNextEntry(ZipEntry(entryData.name))
        val buffer = ByteArray(1024)
        while (true) {
            val len = entryData.input.read(buffer)
            if (len == -1) {
                break
            }
            writer.write(buffer, 0, len)
        }
    }

    override fun close() {
        writer.close()
        Files.move(tempFilePath, path, StandardCopyOption.REPLACE_EXISTING)
    }
}

/**
 * @author shirokuro
 */
data class ZipEntryData(val name: String, val input: InputStream)
