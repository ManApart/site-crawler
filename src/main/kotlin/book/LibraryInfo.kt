package book

class LibraryInfo(
    val siteUrl: String,
    val matchText: String,
    val process: (String) -> List<BookInfo>
)