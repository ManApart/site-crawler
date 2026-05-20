package assetDownloader.downloaders

import assetDownloader.AssetInfo
import assetDownloader.AssetPageFetcher
import org.jsoup.Jsoup
import org.jsoup.nodes.Entities

class ImageByCssClassDownloader(private val url: String, private val selector: String) : AssetPageFetcher {

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
            .select(selector)
            .select("img")
            .map { it.attr("src") }
            .toSet()
            .map {
                AssetInfo(it, "./download/${it.cleanTitle()}")
            }
    }

    private fun String.cleanTitle(): String {
        val end = if (indexOf("?") != -1) indexOf("?") else length
        return substring(lastIndexOf("/") + 1, end)
    }

}
