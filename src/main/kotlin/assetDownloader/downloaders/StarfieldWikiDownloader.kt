package assetDownloader.downloaders

import assetDownloader.AssetInfo
import assetDownloader.AssetPageFetcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.jsoup.Jsoup

//https://starfieldwiki.net/wiki/Category:Starfield-Skill_Images
class StarfieldWikiDownloader(private val base: String, private val limit: Int = 100000) : AssetPageFetcher {

    override fun baseUrl(): String {
        return base
    }

    override fun hasNext(pageData: String): Boolean {
        return false
    }

    override fun getNextUrl(pageData: String): String {
        return ""
    }

    override fun getAssetInfos(url: String, pageData: String): List<AssetInfo> {
        val urlMap = Jsoup.parse(pageData).select(".gallerytext").map { gallery ->
            val a = gallery.select("a").first()!!
            val title = a.attr("title").cleanTitle()
            val href = "https://starfieldwiki.net" + a.attr("href")
            title to href
        }.take(limit).associate { it.first to it.second }.toMutableMap()

        println("Finding downloads for ${urlMap.keys.size} assets")
        runBlocking {
            urlMap.keys.map { title ->
                async {
                    urlMap[title] = Jsoup.connect(urlMap[title]!!).get()
                        .select(".fullMedia").first()!!.select("a").first()!!
                        .attr("href").cleanImageUrl()
                }
            }.awaitAll()
        }
        return urlMap.entries.map { AssetInfo(it.value, "./download/${it.key}.jpg") }
    }

    private fun String.cleanTitle(): String {
        return replace("File:SF-skill-", "")
    }

    private fun String.cleanImageUrl(): String {
        return "https:$this"
    }

}