package book.books

import book.PageFetcher

class PhilosophyOfUnixFetcher : PageFetcher {
    private val matchText = "<link rel=\"next\" href=\""

    override fun hasNext(pageData: String): Boolean {
        return pageData.contains(matchText)
    }

    override fun getNextUrl(pageData: String): String {
        val start = pageData.indexOf(matchText) + matchText.length
        val end = pageData.indexOf("\"", start)
        val extracted = pageData.substring(start, end)
        return "http://www.catb.org/esr/writings/taoup/html/$extracted"
    }
}