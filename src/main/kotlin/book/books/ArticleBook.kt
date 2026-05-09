package book.books

import book.BookInfo
import book.wrapHtml
import org.jsoup.Jsoup

val article = BookInfo(
    "Agentic Engineering Patterns",
    "https://simonwillison.net/guides/agentic-engineering-patterns/",
    { depth: Int, url: String -> "$depth${url.substring(url.lastIndexOf("/") + 1)}" },
    LinkListFetcher { data ->
        wrapHtml(Jsoup.parse(data).select("#primary").html())
    }
)
