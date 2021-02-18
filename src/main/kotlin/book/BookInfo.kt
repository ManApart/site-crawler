package book

class BookInfo(
    val siteName: String,
    val siteUrl: String,
    val fileName: (Int, String) -> String,
    val pageFetcher: PageFetcher
)