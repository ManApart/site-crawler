package book.books

import book.BookInfo
import book.LibraryInfo
import org.jsoup.Jsoup

val worldInvisible = LibraryInfo(
    "https://www.worldinvisible.com/library/bookcat.htm",
    "<a href=",
    ::process
)

private fun process(data: String): List<BookInfo> {
    return Jsoup.parse(data).select("a").map { link ->
        val url = "https://www.worldinvisible.com/library/" + link.attr("href")
        val title = link.select("b").text()
        val pageBase = title.replace(" ", "_")
        val baseUrl = url.substring(0, url.lastIndexOf("/") + 1)

        BookInfo(title, url, { page, _ -> "$pageBase$page" }, WorldInvisibilityFetcher(baseUrl))
    }
}