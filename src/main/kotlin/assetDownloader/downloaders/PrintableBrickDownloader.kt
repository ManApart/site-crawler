package assetDownloader.downloaders

import assetDownloader.AssetInfo
import assetDownloader.AssetPageFetcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.jsoup.Jsoup
import java.util.Base64

//https://printablebricks.com/bricks?page=245
class PrintableBrickDownloader(private val start: Int = 1, private val pagesToDownload: Int = 1) : AssetPageFetcher {
    private val baseUrl = "https://printablebricks.com/bricks?page=$start"
    private var currentPage = start
    private val base64 = Base64.getDecoder()

    override fun baseUrl(): String {
        return baseUrl
    }

    override fun hasNext(pageData: String): Boolean {
        return currentPage <= start + pagesToDownload
    }

    override fun getNextUrl(pageData: String): String {
        currentPage++
        return "https://printablebricks.com/bricks?page=$currentPage"
    }

    override fun getAssetInfos(url: String, pageData: String): List<AssetInfo> {
        val assetUrls = Jsoup.parse(pageData).select("a")
            .map { it.attr("href") }
            .filter { it.startsWith("/bricks/") }
            .map { "https://printablebricks.com$it" }

        return runBlocking {
            assetUrls.map { async { getAssetFromPage(it) } }.awaitAll().filterNotNull()
        }
    }

    private fun getAssetFromPage(url: String): AssetInfo? {
        return try {
            val decoded = Jsoup.connect(url).get()
                .select("canvas").first()!!
                .attr("sourceFiles")
                .let { String(base64.decode(String(base64.decode(it)))) }

            AssetInfo("https://printablebricks.com$decoded", cleanBrickName(decoded))
        } catch (e: Exception) {
            println("Could not fetch info for $url")
            println(e)
            null
        }
    }

    private fun cleanBrickName(decoded: String): String {
        val name = decoded
            .replace("/storage/stlfiles/files/", "")
            .replace(Regex("^[0-9]+_"), "")
            .replace(Regex("^[0-9]+\\w_"), "")
            .replace(Regex("/[0-9]+.stl"), "")
            .replace(Regex("/[0-9]+\\w.stl"), "")
            .replace("/", "")

        return "./download/$name.stl"
    }

}