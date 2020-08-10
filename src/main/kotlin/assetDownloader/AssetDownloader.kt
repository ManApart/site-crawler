package assetDownloader

import java.io.File
import java.net.URL
import java.net.URLConnection
import java.nio.charset.StandardCharsets
import java.nio.file.Files.copy
import java.nio.file.StandardCopyOption
import java.util.*


const val MAX_DEPTH = 1000

fun main() {
    val fetcher = XboxScreenShotDownloader("iceburg-33308")
    crawl(fetcher, fetcher.baseUrl())
}

fun crawl(fetcher: AssetPageFetcher, url: String, depth: Int = 0) {
    val data = fetchData(url)

    //download single asset
    val info = fetcher.getAssetInfos(url, data).first()
    download(info)

//    fetcher.getAssetInfos(url, data).forEach {
//        download(it)
//    }

    if (fetcher.hasNext(data) && depth < MAX_DEPTH) {
        val nextUrl = fetcher.getNextUrl(data)
        if (url != nextUrl) {
            crawl(fetcher, nextUrl, depth + 1)
        }
    }
}

private fun fetchData(url: String): String {
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

    println("Downloaded ${info.fileName}")
}
