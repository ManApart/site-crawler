package book

class BookInfo(
    val siteName: String,
    val siteUrl: String,
    val fileName: (Int, String) -> String,
    val pageFetcher: PageFetcher,
    val author: String? = null,
    val coverImageName: String? = null,
)
