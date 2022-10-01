package book.books

import assetDownloader.fetchData
import book.PageFetcher
import org.jsoup.Jsoup

class WorldInvisibilityFetcher(private val baseUrl: String, private val initialUrl: String) : PageFetcher {
    private val urls by lazy { fetchInitialUrls() }
    private var i = -1

    private fun fetchInitialUrls(): List<String> {
        val links = Jsoup.parse(fetchData(initialUrl, mapOf())).select("a")

        return links.map { it.attr("href") }
            .filter {
                it.isNotBlank()
                        && !it.contains("www.")
                        && !it.contains("worldinvisible.com")
                        && !it.contains("../")
                        && !it.contains("#")
            }.map { baseUrl + it }
    }

    override fun hasNext(pageData: String): Boolean {
        return i < urls.size - 1
    }

    override fun getNextUrl(pageData: String): String {
        i++
        return urls[i]
    }

}