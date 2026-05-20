package assetDownloader.downloaders

import assetDownloader.AssetInfo
import assetDownloader.AssetPageFetcher
import org.jsoup.Jsoup
import org.jsoup.nodes.Entities

class SteamAchievementIconDownloader(private val url: String) : AssetPageFetcher {

    override fun baseUrl(): String {
        return url
    }

    override fun hasNext(pageData: String): Boolean {
        return false
    }

    override fun getNextUrl(pageData: String): String {
        return ""
    }

    override fun getAssetInfos(url: String, pageData: String): List<AssetInfo> {
        return Jsoup.parse(pageData)
            .select(".achieveRow").map { row ->
                val image = row.select(".achieveImgHolder").select("img").first()!!.attr("src")
                val desc = row.select(".achieveTxtHolder").select("h3").first()!!.text()
                image to desc
            }
            .groupBy { it.first }
            .entries.map { (url, entries) -> url to entries.first().second }
            .map {
                AssetInfo(it.first, "./download/${it.second.cleanTitle()}.jpg")
            }
    }

    private fun String.cleanTitle(): String {
        return replace(" ", "")
    }

}
