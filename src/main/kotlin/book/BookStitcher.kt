package book

import java.io.File
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.*

const val SITE_NAME = "Philosophy of Unix"
const val SITE_URL = "http://www.catb.org/esr/writings/taoup/html/index.html"
const val MAX_DEPTH = 1000

const val HEADER = "<html>\n" +
        "<body>\n" +
        "  <h1>Table of Contents</h1>\n"

const val FOOTER = "</body>\n" +
        "\n" +
        "</html>"


fun main(args: Array<String>) {
    crawl(SITE_URL, NextLinkFetcher())
}

fun crawl(url: String, fetcher: PageFetcher, depth: Int = 0, pageList: MutableList<String> = mutableListOf(), previousData: String = "") {
    val data = fetchData(url)
    val fileName = "$depth${url.substring(url.lastIndexOf("/") + 1)}"
    val pathName = "./download/$fileName"
    saveData(pathName, data)
    pageList.add(fileName)
    if (fetcher.hasNext(data) && depth < MAX_DEPTH && data != previousData) {
        crawl(fetcher.getNextUrl(data), fetcher, depth + 1, pageList, data)
    } else {
        createIndexPage(pageList)
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

private fun saveData(fileName: String, data: String) {
    File(fileName).printWriter().use { out ->
        out.println(data)
    }
}

private fun createIndexPage(pageList: List<String>) {
    var indexData = HEADER
    pageList.forEach {
        indexData += "<a href=\"$it\">$it</a><br />\n"
    }
    indexData += FOOTER

    println(indexData)
    saveData("./download/$SITE_NAME.html", indexData)
}

