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
        return Jsoup.parse(pageData)
            .select("a.title")
            .map {
                //TODO - parse contents page
                //TODO - pass additional infos for chapter sections
                //  - filename group by book
                val title = it.text().cleanTitle()
                AssetInfo(it.attr("href"), "./download/${title}.html") { data ->
                    Jsoup.parse(data)
                        .select("h5:contains(Table)").first()?.siblingElements()?.select("ul")
                        ?.select("a")?.map { chapter ->
                            val chTitle = chapter.text().cleanTitle()
                            AssetInfo(chapter.attr("href"), "./download/${title}-$chTitle.html")
                        } ?: emptyList()
                }
            }
    }

    private fun String.cleanTitle(): String {
        return replace(" ", "_")
    }

}
