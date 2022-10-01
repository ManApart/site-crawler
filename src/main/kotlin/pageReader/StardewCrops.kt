package pageReader

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.io.File


fun main() {
    val source = File("./download/crops.html").readText()
    val parsed = Jsoup.parse(source)
    val crops = parsed.getElementsByClass("wikitable").toList()
        .filter { it.child(0).child(0).child(0).text() == "Seeds" }
        .map { parsecrop(it.child(0)) }

    println(HEADER)
    println(crops.joinToString("\n") { it.toRow() })
}

fun parsecrop(table: Element): CropInfo {
    return CropInfo(
        parseName(table),
        parseSeedCost(table),
        parseInitialGrowth(table),
        parseRegrowth(table),
        parseSellPrice(table)
    )
}

private fun parseName(table: Element): String {
    return table.child(1).child(0).text().split(" ").first()
}

private fun parseSeedCost(table: Element): Int {
    return table.child(1).child(0).text().split(" ").firstOrNull { it.isPrice() }?.toPrice() ?: 0
}

private fun parseInitialGrowth(table: Element): Int {
    return table.getElementsContainingText("Total:").last()!!.text().getNumberInMiddle("Total:", "day")
}

private fun parseRegrowth(table: Element): Int {
    val matches = table.getElementsContainingText("Regrowth:")
    return if (matches.isEmpty()) 0 else {
        matches.last()!!.text().getNumberInMiddle("Regrowth:", "day")
    }
}

private fun parseSellPrice(table: Element): Int {
    return textByHeader(table, "Sells For").split(" ").first { it.isPrice() }.toPrice()
}


private fun textByHeader(table: Element, headerText: String): String {
    val header = table.child(0)
    val col = header.children().first { it.text().contains(headerText) }.let { header.children().indexOf(it) }
    val adjusted = header.children().subList(0, col).map { it.attr("colspan").toIntOrNull() ?: 1 }.sum()
    return table.child(1).child(adjusted).text()
}

private fun String.isPrice(): Boolean {
    return endsWith("g") && get(length - 2).isDigit()
}

private fun String.toPrice(): Int {
    return split("-").last().replace("g", "").replace(",", "").trim().toInt()
}

private fun String.getNumberInMiddle(prefix: String, suffix: String): Int {
    val start = indexOf(prefix) + prefix.length
    val end = indexOf(suffix)
    return substring(start, end).trim().toInt()
}