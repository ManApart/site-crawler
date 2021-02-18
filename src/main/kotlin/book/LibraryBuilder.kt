package book

import book.books.worldInvisible
import java.io.File

val library = worldInvisible

fun main() {
    val data = fetchData(library.siteUrl)
    val books = library.process(data)
//    listOf(books[6]).forEach { book ->
    books.forEach { book ->
        if (!isDownloaded(book)) {
            println("Downloading ${book.siteName}")
            try {
                crawl(book.siteUrl, book.pageFetcher, book.fileName, book.siteName)
            } catch (exception: Exception) {
                println("${book.siteName} failed to download")
            }
        }
    }
}

fun isDownloaded(book: BookInfo): Boolean {
    val baseName = book.siteName.filter { it.isLetter() }
    val pathName = "./download/${baseName}/"
    return File(pathName).exists()
}
