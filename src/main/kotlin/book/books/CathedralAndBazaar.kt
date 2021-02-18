package book.books

import book.BookInfo

val cathedralAndBazaar = BookInfo(
    "Cathedral and the Bazaar",
    "http://www.catb.org/~esr/writings/cathedral-bazaar/cathedral-bazaar/",
    { depth: Int, url: String -> "$depth${url.substring(url.lastIndexOf("/") + 1)}" },
    BazaarFetcher()
)