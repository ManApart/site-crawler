package book

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.Entities
import org.jsoup.parser.Parser
import java.nio.charset.StandardCharsets

fun htmlToXhtml(rawHtml: String, chapterTitle: String): String {
    val doc = Jsoup.parse(rawHtml, "", Parser.htmlParser())
    doc.apply {
        head().empty()
        head().appendElement("title").text(chapterTitle)
        head().appendElement("meta")
            .attr("http-equiv", "Content-Type")
            .attr("content", "text/html; charset=UTF-8")
        val html = doc.selectFirst("html") ?: doc.prependElement("html")
        html.attr("xmlns", "http://www.w3.org/1999/xhtml")
        html.attr("xml:lang", "en")
        html.attr("lang", "en")
        select("script, noscript, iframe, form").remove()
        select(".edit-page-link, [data-admin-url]").remove()
        select("center").forEach { center ->
            val replacement = Element("div")
            replacement.attr("style", "text-align: center;")
            replacement.html(center.html())
            center.replaceWith(replacement)
        }
        select("a[href]").forEach { link ->
            val href = link.attr("href").trim()
            when {
                href.startsWith("#") -> Unit
                href.startsWith("http://") || href.startsWith("https://") -> Unit
                href.startsWith("/") -> link.unwrap()
                else -> Unit
            }
        }
        select("img[src]").forEach { img ->
            val src = img.attr("src").trim()
            if (src.startsWith("/") || src.startsWith("http://") || src.startsWith("https://")) {
                img.remove()
            }
        }
        outputSettings(
            Document.OutputSettings()
                .syntax(Document.OutputSettings.Syntax.xml)
                .escapeMode(Entities.EscapeMode.xhtml)
                .charset(StandardCharsets.UTF_8)
                .prettyPrint(false)
        )
    }
    return """<?xml version="1.0" encoding="UTF-8"?>""" + "\n" + doc.outerHtml()
}
