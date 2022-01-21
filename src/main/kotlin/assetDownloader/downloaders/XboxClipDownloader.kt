package assetDownloader.downloaders

import assetDownloader.AssetInfo
import assetDownloader.AssetPageFetcher

// https://xboxclips.co/iceburg33308#
class XboxClipDownloader(private val userName: String) : AssetPageFetcher {
    private val assetDownloadUrlLineStart = "<a href=\"https://xboxclips.co/$userName/"
    private val assetUrlStart = "data-url=\""
    private val assetUrlEnd = "\""

    override fun baseUrl(): String {
        return "https://xboxclips.co/$userName"
    }

    override fun hasNext(pageData: String): Boolean {
        return false
    }

    override fun getNextUrl(pageData: String): String {
        return ""
    }

    override fun getAssetInfos(url: String, pageData: String): List<AssetInfo> {
        return pageData
            .split("\n")
            .filter { it.contains(assetDownloadUrlLineStart) }
            .map { extractInfoFromLine(it) }
    }

    private fun extractInfoFromLine(line: String): AssetInfo {
        val start = line.indexOf(assetUrlStart) + assetUrlStart.length
        val end = line.indexOf(assetUrlEnd, start)
        val url = line.substring(start, end).replace("amp;", "")

        val uniqueName = url.substring(url.lastIndexOf("=") + 1)
        val fileName = "./download/$uniqueName.mp4"
        return AssetInfo(url, fileName)
    }
}