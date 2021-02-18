package book

import java.io.File
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.*

val books: List<BookInfo> = listOf()

const val MAX_DEPTH = 100

const val HEADER = "<html>\n" +
        "<body>\n" +
        "  <h1>Table of Contents</h1>\n"

const val FOOTER = "</body>\n" +
        "\n" +
        "</html>"


fun main(args: Array<String>) {
    books.forEach { book ->
        crawl(book.siteUrl, book.pageFetcher, book.fileName, book.siteName)
    }
}

fun crawl(
    url: String,
    fetcher: PageFetcher,
    fileNameBuilder: (Int, String) -> String,
    siteName: String,
    depth: Int = 0,
    pageList: MutableList<String> = mutableListOf(),
    previousData: String = ""
) {
    val data = fetchData(url)
    val fileName = ensureEndsInHTML(fileNameBuilder(depth, url))
    val baseName = siteName.filter { it.isLetter() }
    val pathName = "./download/${baseName}/$fileName"
    saveData(pathName, data)
    pageList.add(fileName)
    if (fetcher.hasNext(data) && depth < MAX_DEPTH && data != previousData) {
        crawl(fetcher.getNextUrl(data), fetcher, fileNameBuilder, siteName, depth + 1, pageList, data)
    } else {
        createIndexPage(baseName, pageList)
    }
}

fun fetchData(url: String): String {
    Scanner(
        URL(url).openStream(),
        StandardCharsets.UTF_8.toString()
    ).use { scanner ->
        scanner.useDelimiter("\\A")
        return if (scanner.hasNext()) scanner.next() else ""
    }
}

private fun saveData(fileName: String, data: String) {
    File(fileName).also { it.parentFile.mkdirs() }.printWriter().use { out ->
        out.println(data)
    }
}

private fun createIndexPage(siteName: String, pageList: List<String>) {
    var indexData = HEADER
    pageList.forEach {
        indexData += "<a href=\"$it\">$it</a><br />\n"
    }
    indexData += FOOTER

    saveData("./download/${siteName}/${siteName}.html", indexData)
}

private fun ensureEndsInHTML(fileName: String): String {
    return if (fileName.endsWith(".html")) {
        fileName
    } else {
        "$fileName.html"
    }
}

