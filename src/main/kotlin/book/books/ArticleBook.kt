package book.books

import book.BookInfo
import java.io.File

val article = BookInfo(
    "Agentic Engineering Patterns",
    "https://simonwillison.net/guides/agentic-engineering-patterns/",
    ::getFileName,
    CssSelectorFetcher("#primary", File("in/links.txt").readLines()),
    coverImageName = "cover.jpg"
)

val articles = listOf(
    BookInfo(
        "Kotlin Native Guide",
        "https://superkotlin.com/kotlin-native/",
        ::getFileName,
        CssSelectorFetcher(".entry-content-wrapper"),
    )
)


private fun getFileName(depth: Int, url: String) = "$depth${url.substring(url.lastIndexOf("/") + 1)}"
