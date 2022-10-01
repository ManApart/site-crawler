package book.books

import book.BookInfo
import book.LibraryInfo
import org.jsoup.Jsoup

val worldInvisible = LibraryInfo(
    "https://www.worldinvisible.com/library/bookcat.htm",
    ::process
)

private fun process(data: String): List<BookInfo> {
    return Jsoup.parse(data).select("a").toList()
        .filter {
            val url = it.attr("href")
            url.isNotBlank()
                    && !url.endsWith(".pdf")
                    && !url.contains("www.")
                    && !url.contains("worldinvisible.com")
        }
        .map { link ->
            val url = "https://www.worldinvisible.com/library/" + link.attr("href")
            var title = link.select("b").text()
            val baseUrl = url.substring(0, url.lastIndexOf("/") + 1)

            if (title.isNullOrBlank()) {
                val withoutLastSlash = baseUrl.substring(0, url.lastIndexOf("/"))
                title = withoutLastSlash.substring(withoutLastSlash.lastIndexOf("/") + 1)
                println("No Title found. Using $title")
            }

            BookInfo(title, url, { page, _ -> "$page" }, WorldInvisibilityFetcher(baseUrl, url))
        }
}