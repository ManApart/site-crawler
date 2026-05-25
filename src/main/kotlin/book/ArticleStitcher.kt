package book

import ArticleConfig
import book.books.CssSelectorFetcher
import book.books.getArticleFileName
import jsonMapper
import readConfig


fun main() {
    val articles = readConfig().articles
    if (articles.isEmpty()) println("Add books to config: \"articles\": [" + jsonMapper.encodeToString(ArticleConfig()) + "]")
    else articles.map { it.toBook() }.process()
}


private fun ArticleConfig.toBook() = BookInfo(siteName, links.first(), ::getArticleFileName, CssSelectorFetcher(cssFilter, links.drop(1)), author, cover)
