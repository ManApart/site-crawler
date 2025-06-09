package assetDownloader.downloaders

import assetDownloader.AssetInfo
import assetDownloader.AssetPageFetcher
import org.jsoup.Jsoup

//https://en.uesp.net/wiki/Oblivion:Concept_Art
class UESPPictureDownloader(private val base: String) : AssetPageFetcher {

    override fun baseUrl() = base
    override fun hasNext(pageData: String) = false
    override fun getNextUrl(pageData: String) = ""

    override fun getAssetInfos(url: String, pageData: String): List<AssetInfo> {
        return Jsoup.parse(pageData).select("#bodyContent").select("img").map { img ->
            val link = img.attr("src")
            val fileFormat = link.fileFormat()
            val href = "https:$link".cleanImageUrl(fileFormat)
            val title = href.cleanTitle()
            AssetInfo(href, "./download/${title}$fileFormat")
        }
    }

    private fun String.fileFormat(): String {
        return when {
            this.contains(".jpg") -> ".jpg"
            this.contains(".png") -> ".png"
            else -> ".jpg".also { println("Unknown format: $this") }
        }
    }

    private fun String.cleanImageUrl(fileFormat: String): String {
        val i = indexOf(fileFormat)
        return substring(0, i + fileFormat.length)
    }

    private fun String.cleanTitle(): String {
        return substring(lastIndexOf("/") + 1)
    }
}
