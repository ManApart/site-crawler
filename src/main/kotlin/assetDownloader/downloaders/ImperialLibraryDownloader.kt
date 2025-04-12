package assetDownloader.downloaders

import assetDownloader.AssetInfo
import assetDownloader.AssetPageFetcher
import org.jsoup.Jsoup

//https://www.imperial-library.info/content/pocket-guide-empire-and-its-environs-first-edition#4cd0e52a-1a9a-4308-87e9-ae9d8af022be-link
class ImperialLibraryDownloader() : AssetPageFetcher {

    override fun baseUrl() = "https://www.imperial-library.info/game-books/all-elder-scrolls-books"

    override fun hasNext(pageData: String): Boolean {
        return false
    }

    override fun getNextUrl(pageData: String): String {
        return ""
    }

    override fun getAssetInfos(url: String, pageData: String): List<AssetInfo> {
        val parsed = Jsoup.parse(pageData)
        return (parsed.select("#menu-item-236").select("ul.sub-menu").select("a") +
                parsed.select("a.title"))
            .map {
                val title = it.text().cleanTitle()
                AssetInfo(it.attr("href"), "./download/${title}.html") { data ->
                    Jsoup.parse(data)
                        .select("h5:contains(Table)").first()?.siblingElements()?.select("ul")
                        ?.select("a")?.mapIndexed { i, chapter ->
                            val chUrl = chapter.attr("href").let { u -> if (u.startsWith("/")) "https://www.imperial-library.info$u" else u }
                            AssetInfo(chUrl, "./download/${title}-$i.html")
                        } ?: emptyList()
                }
            }
    }

    private fun String.cleanTitle(): String {
        return replace(" ", "_")
            .replace("/", "_")
            .replace(",", "")
    }

}
