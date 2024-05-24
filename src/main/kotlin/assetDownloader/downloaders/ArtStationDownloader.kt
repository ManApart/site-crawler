package assetDownloader.downloaders

import assetDownloader.AssetInfo
import assetDownloader.AssetPageFetcher
import org.jsoup.Jsoup

//If 403, crawl local
//https://magazine.artstation.com/2022/01/343-industries-halo-infinite-art-blast/
//https://magazine.artstation.com/wp-content/uploads/2022/01/David_Heidhoff_1012744-1536x932.jpg
class ArtStationDownloader(private val base: String, private val uploadPrefixes: List<String>) : AssetPageFetcher {

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
        return Jsoup.parse(pageData)
            .select("img")
            .map { it.attr("src") }
            .filter { img -> uploadPrefixes.any { img.startsWith(it) } }
            .map {
                AssetInfo(it.replace("smaller_square", "large"), "./download/${it.cleanTitle()}")
            }
    }

    private fun String.cleanTitle(): String {
        val end = if (indexOf("?") != -1) indexOf("?") else length
        return substring(lastIndexOf("/") + 1, end)
    }

}