package book

class LibraryInfo(
    val siteUrl: String,
    val process: (String) -> List<BookInfo>
)