package book

import book.books.worldInvisible

val library = worldInvisible

fun main() {
    val data = fetchData(library.siteUrl)
    val books = library.process(data)
    books.subList(0,1).forEach { book ->
//    books.forEach { book ->
        crawl(book.siteUrl, book.pageFetcher, book.fileName, book.siteName)
    }
}