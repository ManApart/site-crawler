package assetDownloader.downloaders

import assetDownloader.AssetInfo
import assetDownloader.AssetPageFetcher
import jdk.internal.org.commonmark.internal.Bracket.image
import jdk.javadoc.internal.doclets.formats.html.markup.HtmlStyles
import org.jsoup.Jsoup

//https://www.jesselonergan.com/gallery
class LonerganDownloader : AssetPageFetcher {

    override fun baseUrl(): String {
        return "https://www.jesselonergan.com/gallery"
    }

    override fun hasNext(pageData: String): Boolean {
        return false
    }

    override fun getNextUrl(pageData: String): String {
        return ""
    }

    override fun getAssetInfos(url: String, pageData: String): List<AssetInfo> {
        return Jsoup.parse(pageData).select("img").map { image ->
            val link = image.attr("src")
            val title = image.attr("alt")
            AssetInfo(link, "./download/lonergan/$title")
        }

    }

}
