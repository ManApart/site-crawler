package book.books

import book.PageFetcher

class BazaarFetcher : PageFetcher {
    private val matchText = "<a accesskey=\"n\" href=\""

    override fun hasNext(pageData: String): Boolean {
        return pageData.contains(matchText)
    }

    override fun getNextUrl(pageData: String): String {
        val start = pageData.indexOf(matchText) + matchText.length
        val end = pageData.indexOf("\"", start)
        val extracted = pageData.substring(start, end)
        return "http://www.catb.org/~esr/writings/cathedral-bazaar/cathedral-bazaar/$extracted"
    }
}