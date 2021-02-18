package book.books

import book.PageFetcher
import org.jsoup.Jsoup

class WorldInvisibilityFetcher(private val baseUrl: String) : PageFetcher {
    private val fetchedPages = mutableListOf<String>()

    override fun hasNext(pageData: String): Boolean {
        val urlPart = getNext(pageData)
        return urlPart != null
    }

    override fun getNextUrl(pageData: String): String {
        val urlPart = getNext(pageData)!!
        fetchedPages.add(urlPart)
        return baseUrl + urlPart
    }

    private fun getNext(data: String): String? {
        return Jsoup.parse(data).select("a").firstOrNull { link ->
            link.text().contains("chapter", true) && !fetchedPages.contains(link.attr("href"))
        }?.attr("href")
    }

}