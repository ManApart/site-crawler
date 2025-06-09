package assetDownloader.downloaders

import assetDownloader.AssetInfo
import assetDownloader.AssetPageFetcher
import org.jsoup.Jsoup
import java.io.File

class ImperialLibraryPictureDownloader : AssetPageFetcher {

    override fun baseUrl() = "https://www.imperial-library.info/game-books/all-elder-scrolls-books"
    override fun hasNext(pageData: String) = false
    override fun getNextUrl(pageData: String) = ""

    override fun getAssetInfos(url: String, pageData: String): List<AssetInfo> {
        //Parse all the files locally
        return File("download/es-books").listFiles()!!
//            .take(50)
//            .filter { it.nameWithoutExtension == "The_Improved_Emperor’s_Guide_to_Tamriel" }
            .flatMap { file ->
                val folder = "./download/${file.nameWithoutExtension}/"
                val data = Jsoup.parse(file.readText())
                val contentAssets = data.select("meta").map { it.attr("content") }
                    .filter { it.endsWith(".png") || it.endsWith(".jpg") || it.endsWith(".jpeg") }.map { meta ->
                        val title = meta.cleanTitle()
                        AssetInfo(meta, "$folder${title}")
                    }
                val imgAssets = data.select("img").map { img ->
                    val link = img.attr("src").takeIf { it.isNotBlank() && !it.startsWith("data:") } ?: img.attr("data-src").takeIf { it.isNotBlank() && !it.startsWith("data:") }
                    if (link == null) null else {
                        val href = link.cleanImageUrl()
                        val title = href.cleanTitle()
                        AssetInfo(href, "$folder${title}")
                    }
                }
                (contentAssets + imgAssets).toSet().filterNotNull()
            }.groupBy { it.url }.entries.map { it.value.first() }
    }

    private fun String.fileFormat(): String {
        return when {
            this.contains(".jpeg") -> ".jpeg"
            this.contains(".jpg") -> ".jpg"
            this.contains(".png") -> ".png"
            this.contains(".webp") -> ".webp"
            this.contains(".gif") -> ".gif"
            else -> ".jpg".also { println("Unknown format: $this") }
        }
    }

    private fun String.cleanImageUrl(): String {
        return when {
            this.startsWith("/wp") -> "https://www.imperial-library.info$this"
            else -> this
        }.replace("http://45.55.65.46/", "https://imperial-library.info/")
    }

    private fun String.cleanTitle(): String {
        return substring(lastIndexOf("/") + 1)
    }

}
