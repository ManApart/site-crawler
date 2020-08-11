package assetDownloader

//https://xbox-clips.com/iceburg%2033308/

//https://gameclipscontent-d3021.xboxlive.com/xuid-2533274911938188-private/b7f6585c-4d61-414b-903f-66e306a811f3.MP4?sv=2015-12-11&sr=b&si=DefaultAccess&sig=t6EAWASHnPpikH25ZqiFrSTbsI1Cvd95mxfBhjiSd54%3D&__gda__=1597107235_c299992ac79f507b8e8ac083606b135d
class XboxClipDownloader(private val userName: String) : AssetPageFetcher {
    private val assetDownloadUrlLineStart = "<a href=\"https://gameclipscontent"
    private val assetUrlStart = "href=\""
    private val assetUrlEnd = "\""

    override fun baseUrl(): String {
        return "https://xbox-clips.com/$userName/"
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