package assetDownloader

//https://xboxclips.co/iceburg-33308/screenshots
//https://xboxclips.co/iceburg-33308/screenshots/54e96eb8-7b9d-401b-9cb0-df74fcda36e4
//https://screenshotscontent-t3002.xboxlive.com/xuid-2533274911938188-public/54e96eb8-7b9d-401b-9cb0-df74fcda36e4_Thumbnail.PNG
//https://screenshotscontent-t2001.xboxlive.com/xuid-2533274911938188-public/727d6571-f8dc-4c2b-9199-f7d74b6924ea_Thumbnail.PNG

class XboxScreenShotDownloader(private val userName: String) : AssetPageFetcher {
    private val downloadUrlLineStart = "<a href=\"/$userName/screenshots/"
    private val assetUrlStart = "data-bg=\"url("
    private val assetUrlEnd = ")"

    override fun baseUrl(): String {
        return "https://xboxclips.co/$userName/screenshots"
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
            .filter { it.startsWith(downloadUrlLineStart) }
            .map { extractUrlFromLine(it) }
    }

    private fun extractUrlFromLine(line: String): AssetInfo {
        val start = line.indexOf(assetUrlStart) + assetUrlStart.length
        val end = line.indexOf(assetUrlEnd, start)
        val url = line.substring(start, end)
        val fileName = "./download/abc.png"
        return AssetInfo(url, fileName)
    }
}