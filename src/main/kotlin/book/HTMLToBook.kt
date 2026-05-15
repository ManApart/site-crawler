package book

import nl.siegmann.epublib.domain.Author
import nl.siegmann.epublib.domain.Book
import nl.siegmann.epublib.domain.Resource
import nl.siegmann.epublib.epub.EpubWriter
import readConfig
import java.io.File
import java.io.FileOutputStream

//Post creation, import into calibre and reconvert to epub for reader to work
fun main() {
    val config = readConfig().book
    val input = File("in/local.html")
    input.writeText(htmlToXhtml(input.readText(), config.title))

    createEpub(config.title, config.author, listOf(input))
}


fun createEpub(title: String, author: String, chapters: List<File>, coverImageName: String? = null) {
    val book = Book().apply {
        metadata.addTitle(title)
        metadata.addAuthor(Author(author))
        if (coverImageName != null) {
            coverImage = Resource(File("in/pictures/$coverImageName").inputStream(), coverImageName)
        }
        chapters.forEach { ch ->
            addSection(ch.nameWithoutExtension.replace("_", " "), Resource(ch.inputStream(), ch.name))
        }

    }
    EpubWriter().write(book, FileOutputStream("out/$title.epub"))
}
