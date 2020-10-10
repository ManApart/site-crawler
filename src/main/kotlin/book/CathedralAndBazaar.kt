package book

class CathedralAndBazaar : BookInfo {
    override val siteName = "Cathedral and the Bazaar"
    override val siteUrl = "http://www.catb.org/~esr/writings/cathedral-bazaar/cathedral-bazaar/"
    override val fileName  = { depth: Int, url: String -> "$depth${url.substring(url.lastIndexOf("/") + 1)}"}
    override val pageFetcher = BazaarFetcher()

}