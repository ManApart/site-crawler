package assetDownloader.downloaders

import assetDownloader.AssetInfo
import assetDownloader.AssetPageFetcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.jsoup.Jsoup
import java.io.File
import java.util.Base64

//https://runescape.wiki/images/Spade.png?77106
class RunescapeWikiDownloader(private val start: Int) : AssetPageFetcher {
    private val baseUrl = "https://runescape.wiki/images/"

    override fun baseUrl(): String {
        return baseUrl
    }

    override fun hasNext(pageData: String): Boolean {
        return false
    }

    override fun getNextUrl(pageData: String): String {
        return baseUrl
    }

    override fun getAssetInfos(url: String, pageData: String): List<AssetInfo> {
        return File("./src/in/item-ids.csv")
            .readLines()
            .filter { it.contains(",") && !it.contains("null") }
            .drop(start)
            .map { it.split(",").last().replace(" ", "_") }
            .map { AssetInfo("$baseUrl$it.png", "./download/$it.png") }

    }

}