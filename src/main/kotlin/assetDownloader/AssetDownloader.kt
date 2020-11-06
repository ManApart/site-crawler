package assetDownloader

import java.io.File
import java.net.URL
import java.net.URLConnection
import java.nio.charset.StandardCharsets
import java.nio.file.Files.copy
import java.nio.file.StandardCopyOption
import java.util.*
import kotlinx.coroutines.*

const val MAX_DEPTH = 1000
const val CHUNK_SIZE = 3

fun main() {
//    val fetcher = XboxScreenShotDownloader("iceburg-33308")
//    val fetcher = XboxClipDownloader("iceburg%2033308")
    val fetcher = MarioSpriteDownloader()
    val assetInfos = crawl(fetcher, fetcher.baseUrl())
    println("Found ${assetInfos.size} assets.")

//    download(assetInfos.first())

    val totalChunks = assetInfos.size/ CHUNK_SIZE
    assetInfos.chunked(CHUNK_SIZE).withIndex().forEach {
        downloadChunk(it.value, it.index, totalChunks)
    }
}

fun crawl(fetcher: AssetPageFetcher, url: String, depth: Int = 0): List<AssetInfo> {
    println("Finding assets at $url")
    val data = fetchData(url)

    val infos = fetcher.getAssetInfos(url, data)

    if (fetcher.hasNext(data) && depth < MAX_DEPTH) {
        val nextUrl = fetcher.getNextUrl(data)
        if (url != nextUrl) {
            return infos + crawl(fetcher, nextUrl, depth + 1)
        }
    }
    return infos
}

fun fetchData(url: String): String {
    val connection: URLConnection = URL(url).openConnection()
    //fake we're a browser for https
    connection.setRequestProperty(
        "User-Agent",
        "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11"
    )
    connection.connect()

    Scanner(
        connection.getInputStream(),
        StandardCharsets.UTF_8.toString()
    ).use { scanner ->
        scanner.useDelimiter("\\A")
        return if (scanner.hasNext()) scanner.next() else ""
    }

}

fun downloadChunk(infos: List<AssetInfo>, i: Int, totalChunks: Int) {
    println("Downloading chunk $i/$totalChunks")
    runBlocking {
        infos.forEach {
            async {
                download(it)
            }
        }
    }
}

private fun download(info: AssetInfo) {
    val connection: URLConnection = URL(info.url).openConnection()
    //fake we're a browser for https
    connection.setRequestProperty(
        "User-Agent",
        "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11"
    )
    connection.connect()

    val file = File(info.fileName)
    copy(connection.getInputStream(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);

}


fun String.makePipeSafe() : String {
    return this.replace("|", "[")
}