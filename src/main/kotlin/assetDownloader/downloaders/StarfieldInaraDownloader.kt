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
//            "accept" to "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8",
//            "accept-language" to "en-US,en;q=0.7",
            "accept" to "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8",
            "accept-language" to "en-US,en;q=0.7",
            "cache-control" to "no-cache",
            "Cookie" to "inarasite=3; __Host-InaraSID=0vqu5tpcde2dh20suq8j64iule",
            "dnt" to "1",
            "pragma" to "no-cache",
            "priority" to "u=0, i",
            "sec-ch-ua" to "\"Brave\";v=\"147\", \"Not.A/Brand\";v=\"8\", \"Chromium\";v=\"147\"",
            "sec-ch-ua-mobile" to "?0",
            "sec-ch-ua-platform" to "\"Linux\"",
            "sec-fetch-dest" to "document",
            "sec-fetch-mode" to "navigate",
            "sec-fetch-site" to "none",
            "sec-fetch-user" to "?1",
            "sec-gpc" to "1",
            "upgrade-insecure-requests" to "1",
            "user-agent" to "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36",
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
