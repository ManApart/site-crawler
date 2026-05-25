package book.books

import book.PageFetcher
import book.wrapHtml
import org.jsoup.Jsoup

class CssSelectorFetcher(val cssSelector: String, val links: List<String> = listOf()) : PageFetcher {
    private var i = 0
    override fun hasNext(pageData: String) = i < links.size
    override fun getNextUrl(pageData: String) = links[i].also { i++ }
    override fun contentFilter(url: String, pageData: String) : String {
        return wrapHtml(Jsoup.parse(pageData).select(cssSelector).first()!!.html())
    }
}
