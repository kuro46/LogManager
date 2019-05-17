package com.github.kuro46.logmanager

import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.zip.GZIPInputStream

object Decompressor {
    fun decompressAllLogs() {
        LogUtils.getLogFiles(false)
            .filter { it.toString().endsWith(".log.gz") }
            .forEach {
                val withoutExtension = LogUtils.trimExtensionStr(it.fileName.toString()) + ".log"
                decompress(
                    it,
                    withoutExtension
                )
            }
    }

    private fun decompress(gzipPath: Path, decompressedFileName: String) {
        val decompressTo = Files.createTempFile(null, null)

        var gzipInputStream: GZIPInputStream? = null
        var outputStream: OutputStream? = null
        try {
            gzipInputStream = GZIPInputStream(Files.newInputStream(gzipPath).buffered())
            outputStream = Files.newOutputStream(decompressTo).buffered()

            val buffer = ByteArray(1024)
            while (true) {
                val len = gzipInputStream.read(buffer)
                if (len == -1) {
                    break
                }
                outputStream.write(buffer, 0, len)
            }
        } finally {
            try {
                gzipInputStream?.close()
            } finally {
                outputStream?.close()
            }
        }

        Files.move(
            decompressTo,
            gzipPath.parent.resolve(decompressedFileName),
            StandardCopyOption.REPLACE_EXISTING
        )
        Files.delete(gzipPath)
    }
}
