package assetDownloader.downloaders

import assetDownloader.AssetInfo
import assetDownloader.AssetPageFetcher
import org.jsoup.Jsoup

//https://en.uesp.net/wiki/General:Wallpapers
//https://en.uesp.net/wiki/File:25_years_of_Elder_Scrolls-2560x2560.jpg
//https://images.uesp.net/e/ed/25_years_of_Elder_Scrolls-2560x2560.jpg
class ESOWallpaperDownloader : AssetPageFetcher {

    override fun baseUrl(): String {
        return "https://en.uesp.net/wiki/General:Wallpapers"
    }

    override fun hasNext(pageData: String): Boolean {
        return false
    }

    override fun getNextUrl(pageData: String): String {
        return ""
    }

    override fun getAssetInfos(url: String, pageData: String): List<AssetInfo> {
        val urlMap = Jsoup.parse(pageData).select("ul.gallery").map { gallery ->
            gallery.select(".gallerytext").map { image ->
                val link = image.select("a").last()
                val title = cleanTitle(link.attr("title"))
                val href = "https://en.uesp.net" + link.attr("href")
                title to href
            }
        }.flatten().associate { it.first to it.second }.toMutableMap()

        urlMap.keys.forEach { title ->
            urlMap[title] = "http:" + Jsoup.connect(urlMap[title]).get().select(".fullMedia a").first().attr("href")
        }
        return urlMap.entries.map { AssetInfo(it.value, "./download/" + it.key) }
    }

    private fun cleanTitle(title: String): String {
        return title
            .replace("File:", "")
            .replace("-wallpaper-", "")
            .replace("OB", "")
            .replace("SI", "")
            .replace("MW", "")
            .replace("SR", "")
            .replace("SK", "")
            .replace("TR", "")
            .replace("ON", "")
    }

}