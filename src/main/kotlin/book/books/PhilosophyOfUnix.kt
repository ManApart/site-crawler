package book.books

import book.BookInfo

val philosophyOfUnix = BookInfo(
    "Philosophy of Unix",
    "http://www.catb.org/esr/writings/taoup/html/index.html",
    { depth: Int, url: String -> "$depth${url.substring(url.lastIndexOf("/") + 1)}" },
    PhilosophyOfUnixFetcher()
)