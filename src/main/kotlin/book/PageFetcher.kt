package book

interface PageFetcher {
    fun hasNext(pageData: String) : Boolean
    fun getNextUrl(pageData: String) : String
    fun contentFilter(url: String, pageData: String): String = pageData
}
