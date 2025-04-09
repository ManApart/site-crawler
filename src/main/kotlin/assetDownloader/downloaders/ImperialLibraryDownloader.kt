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
            .map { it.attr("src") }
            .map {
                //TODO - parse contents page
                //TODO - pass additional infos for chapter sections
                //  - filename group by book
                AssetInfo(it.replace("smaller_square", "large"), "./download/${it.cleanTitle()}")
            }
    }

    private fun String.cleanTitle(): String {
        val end = if (indexOf("?") != -1) indexOf("?") else length
        return substring(lastIndexOf("/") + 1, end)
    }

}
