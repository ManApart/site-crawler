package assetDownloader.downloaders

import assetDownloader.AssetInfo
import assetDownloader.AssetPageFetcher
import org.jsoup.Jsoup

//https://inara.cz/starfield/resources/
class StarfieldInaraDownloader : AssetPageFetcher {

    override fun baseUrl(): String {
        return "https://inara.cz/starfield/resources/"
    }

    override fun hasNext(pageData: String): Boolean {
        return false
    }

    override fun getNextUrl(pageData: String): String {
        return ""
    }

    override fun getHeaders(): Map<String, String> {
        return mapOf(
            "accept" to "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8",
            "accept-language" to "en-US,en;q=0.7",
        )
    }

    override fun getAssetInfos(url: String, pageData: String): List<AssetInfo> {
        val urlMap = Jsoup.parse(pageData).select("th").mapNotNull { row ->
            val a = row.select("a").firstOrNull() ?: return@mapNotNull null
            val title = a.text()
            val href = "https://inara.cz" + a.attr("href")
            title to href
        }.associate { it.first to it.second }
        return urlMap.entries.map { AssetInfo(it.value, "./download/inara/${it.key}.html") }
    }

}
