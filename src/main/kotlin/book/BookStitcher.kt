package book

import java.io.File
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.*

val books: List<BookInfo> = listOf(
)

const val MAX_DEPTH = 100

const val HEADER = "<html>\n" +
        "<body>\n" +
        "  <h1>Table of Contents</h1>\n"

const val FOOTER = "</body>\n" +
        "\n" +
        "</html>"

fun main() {
    books.process()
}

fun List<BookInfo>.process() = forEach { book ->
    crawl(book.siteUrl, book.pageFetcher, book.fileName, book.siteName)
    val baseName = book.siteName.filter { it.isLetter() }.trim()
    val (numberedFiles, namedFiles) = File("./download/${baseName}").listFiles()!!.toList()
        .partition { it.nameWithoutExtension.toIntOrNull() != null }
    val chapters = namedFiles.sortedBy { it.nameWithoutExtension } + numberedFiles.sortedBy { it.nameWithoutExtension.toIntOrNull() }
    createEpub(book.siteName.trim(), book.author ?: "NA", chapters, book.coverImageName)
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
    val fileName = ensureEndsInHTML(fileNameBuilder(depth, url))
    val baseName = siteName.filter { it.isLetter() }.trim()
    val pathName = "./download/${baseName}/$fileName"
    val downloaded = File(pathName).also { it.parentFile.mkdirs() }
    val data = fetchData(url, downloaded).let { fetcher.contentFilter(url, it) }.let { htmlToXhtml(it, fileName.replace(".html", "")) }
    saveData(pathName, data)
    pageList.add(fileName)
    if (fetcher.hasNext(data) && depth < MAX_DEPTH && data != previousData) {
        crawl(fetcher.getNextUrl(data), fetcher, fileNameBuilder, siteName, depth + 1, pageList, data)
    } else {
        createIndexPage(baseName, pageList)
    }
}

fun fetchData(url: String, downloaded: File? = null): String {
    if (downloaded?.exists() == true) return downloaded.readText()
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
    if (pageList.size == 1) return

    var indexData = HEADER
    pageList.forEach {
        indexData += "<a href=\"$it\">$it</a><br />\n"
    }
    indexData += FOOTER

    saveData("./download/${siteName}/${siteName}.html", htmlToXhtml(indexData, "Contents"))
}

private fun ensureEndsInHTML(fileName: String): String {
    return if (fileName.endsWith(".html")) {
        fileName
    } else {
        "$fileName.html"
    }
}

fun wrapHtml(data: String): String {
    return "<!DOCTYPE html><html lang=en-US><body>$data</body></html>"
}
