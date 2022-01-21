package assetDownloader.downloaders

import assetDownloader.AssetInfo
import assetDownloader.AssetPageFetcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jsoup.Jsoup

//https://www.wikiart.org/en/norman-rockwell/all-works/text-list
class WikiArtDownloader(private val base: String, private val limit: Int = 100000) : AssetPageFetcher {

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
        val urlMap = Jsoup.parse(pageData).select("ul.painting-list-text").map { gallery ->
            gallery.select(".painting-list-text-row").map { listItem ->
                val link = listItem.select("a").last()
                val href = "https://www.wikiart.org" + link.attr("href")
                val title = href.cleanTitle()
                title to href
            }
        }.flatten().take(limit).associate { it.first to it.second }.toMutableMap()

        println("Finding downloads for ${urlMap.keys.size} assets")
        runBlocking {
            urlMap.keys.forEach { title ->
                launch {
                    urlMap[title] = Jsoup.connect(urlMap[title]).get()
                        .getElementsByAttributeValue("itemprop", "image").first()
                        .attr("src").cleanImageUrl()
                }
            }
        }
        return urlMap.entries.map { AssetInfo(it.value, "./download/${it.key}.jpg") }
    }

    private fun String.cleanTitle(): String {
        return substring(lastIndexOf("/") + 1)
    }

    private fun String.cleanImageUrl(): String {
        return replace("!Large.jpg", "")
    }

}