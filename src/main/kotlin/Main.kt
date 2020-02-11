import java.io.File
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.*


const val SITE_URL = "http://www.catb.org/esr/writings/taoup/html/index.html"
const val MAX_DEPTH = 5

fun main(args: Array<String>) {
    crawl(SITE_URL, NextLinkFetcher())
}

fun crawl(url: String, fetcher: PageFetcher, depth: Int = MAX_DEPTH) {
    val data = fetchData(url)
    saveData(url, data)
    if (fetcher.hasNext(data) && depth > 0) {
        crawl(fetcher.getNextUrl(data), fetcher, depth - 1)
    }
}

private fun fetchData(url: String): String {
    Scanner(
        URL(url).openStream(),
        StandardCharsets.UTF_8.toString()
    ).use { scanner ->
        scanner.useDelimiter("\\A")
        return if (scanner.hasNext()) scanner.next() else ""
    }
}

private fun saveData(url: String, data: String) {
    val fileName = "./download/" + url.substring(url.lastIndexOf("/"))
    File(fileName).printWriter().use { out ->
        out.println(data)
    }
}

