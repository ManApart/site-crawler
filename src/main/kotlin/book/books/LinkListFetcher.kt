package book.books

import book.PageFetcher
import java.io.File

class LinkListFetcher(val getContent: (String) -> String) : PageFetcher {
    private val links = File("in/links.txt").readLines()
    private var i = 0

    override fun hasNext(pageData: String): Boolean {
        return i < links.size
    }

    override fun getNextUrl(pageData: String): String {
        return links[i].also { i++ }
    }

    override fun contentFilter(url: String, pageData: String) = getContent(pageData)
}
