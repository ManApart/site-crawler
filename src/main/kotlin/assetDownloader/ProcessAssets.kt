package assetDownloader

import assetDownloader.processors.StarfieldInaraProcessor
import java.io.File

val processor = StarfieldInaraProcessor()

fun main(){
    File(processor.downloadPath()).listFiles()!!.forEach { processor.process(it) }
    processor.allFilesProcessed()
}
