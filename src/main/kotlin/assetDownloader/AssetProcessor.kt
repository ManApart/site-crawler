package assetDownloader

import java.io.File

interface AssetProcessor {
    fun downloadPath(): String
    fun process(file: File)
    fun allFilesProcessed()
}
