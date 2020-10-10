package book

class PhilosophyOfUnix : BookInfo {
    override val siteName = "Philosophy of Unix"
    override val siteUrl = "http://www.catb.org/esr/writings/taoup/html/index.html"
    override val fileName  = { depth: Int, url: String -> "$depth${url.substring(url.lastIndexOf("/") + 1)}"}
    override val pageFetcher = PhilosophyOfUnixFetcher()

}