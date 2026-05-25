package book.books

import book.BookInfo
import java.io.File

val article = BookInfo(
    "Agentic Engineering Patterns",
    "https://simonwillison.net/guides/agentic-engineering-patterns/",
    ::getArticleFileName,
    CssSelectorFetcher("#primary", File("in/links.txt").readLines()),
    coverImageName = "cover.jpg"
)

fun getArticleFileName(depth: Int, url: String) = "$depth"
