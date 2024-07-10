package assetDownloader.downloaders

import assetDownloader.AssetInfo
import assetDownloader.AssetPageFetcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.jsoup.Jsoup

//https://59parks.net/collections/all/posters
//https://59parks.net/collections/all/posters?page=2
//https://59parks.net/collections/all/products/the-big-sur-coast-poster
//https://59parks.net/cdn/shop/products/fifty-nine-parks-print-series-the-big-sur-coast-by-shepard-fairey-open-edition-poster_1200x.jpg
class FiftyNineParksDownloader : AssetPageFetcher {
    private val base = "https://59parks.net"
    override fun baseUrl() = base
    private var pageCount = 0


    override fun hasNext(pageData: String): Boolean {
        return pageCount < 5
    }

    override fun getNextUrl(pageData: String): String {
        pageCount++
        return "$base/collections/all/posters?page=$pageCount"
    }

    override fun getAssetInfos(url: String, pageData: String): List<AssetInfo> {
        return runBlocking {
            Jsoup.parse(pageData)
                .select(".grid-product__image-link")
                .map { it.attr("href") }
                .map { async { getInnerImage("$base$it") } }
                .awaitAll().filterNotNull()
        }
    }

    private fun getInnerImage(url: String): AssetInfo? {
        return try {
            val urlRef = Jsoup.connect(url).get()
                .select("#ProductPhotoImg")
                .attr("src")

            AssetInfo("https:$urlRef", urlRef.cleanTitle())
        } catch (e: Exception) {
            println("Could not fetch info for $url")
            println(e)
            null
        }
    }

    private fun String.cleanTitle(): String {
        val end = if (indexOf("?") != -1) indexOf("?") else length
        return "./download/" + substring(lastIndexOf("/") + 1, end)
            .replace("fifty-nine-parks-print-series-great-smoky-national-", "")
            .replace("_1200x", "")
    }

}
