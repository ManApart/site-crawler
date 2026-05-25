package book.books

import book.PageFetcher
import book.wrapHtml
import org.jsoup.Jsoup

class CssSelectorFetcher(val cssSelector: String, val links: List<String> = listOf()) : PageFetcher {
    private var i = 0
    override fun hasNext(pageData: String) = i < links.size
    override fun getNextUrl(pageData: String) = links[i].also { i++ }
    override fun contentFilter(url: String, pageData: String): String {
        val data = Jsoup.parse(pageData)
        val el = data.select(cssSelector).first() ?: data.also { println("Could not find element for $cssSelector in $url") }
        return wrapHtml(el.html())
    }
}
