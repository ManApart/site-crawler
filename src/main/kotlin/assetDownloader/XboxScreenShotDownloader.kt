package assetDownloader

//https://gamerdvr.com/gamer/iceburg-33308/screenshots?page=1
//https://gamerdvr.com/gamer/iceburg-33308/screenshot/18012598
//https://gamerdvr.com/gamer/iceburg-33308/screenshot/60b40326-c9e1-4e7b-ad18-a7963dbddd8b/f8c8b756-05eb-4109-9a83-a44339684bd50
//https://gamerdvr.com/xbox/load/screenshot/https%3A%2F%2Fscreenshotscontent-d3002.xboxlive.com%2Fxuid-2533274911938188-private%2F54e96eb8-7b9d-401b-9cb0-df74fcda36e4.PNG%3Fsv%3D2015-12-11%26sr%3Db%26si%3DDefaultAccess%26sig%3DxmmjGcW56sViLY%252FE1%252B21Gx5V1gDUUVJoGTE6Tm92b7Q%253D.png
class XboxScreenShotDownloader(private val userName: String) : AssetPageFetcher {
    private val nextUrlMatch = "<li class=\"next\"><a href=\"/gamer/$userName/screenshots?page="
    private val nextPageNumberStart = "page="
    private val nextPageNumberEnd = "\""

    private val innerPageUrlLineStart = "<a target=\"\" href=\"/gamer/$userName/screenshot/"
    private val innerPageUrlStart = "href=\""
    private val innerPageUrlEnd = "\""

    private val assetDownloadUrlLineStart = "<a onClick=\"ga('send', 'event', 'Screenshot', 'action', 'Download');"
    private val assetUrlStart = "href=\""
    private val assetUrlEnd = "\""

    override fun baseUrl(): String {
        return "https://gamerdvr.com/gamer/$userName/screenshots?page=1"
    }

    override fun hasNext(pageData: String): Boolean {
        return pageData.contains(nextUrlMatch)
    }

    override fun getNextUrl(pageData: String): String {
        val line = pageData
            .split("\n")
            .first { it.startsWith(nextUrlMatch) }

        val start = line.indexOf(nextPageNumberStart) + nextPageNumberStart.length
        val end = line.indexOf(nextPageNumberEnd, start)
        val pageNumber = line.substring(start, end)
        return "https://gamerdvr.com/gamer/$userName/screenshots?page=$pageNumber"
    }

    override fun getAssetInfos(url: String, pageData: String): List<AssetInfo> {
        return pageData
            .split("\n")
            .filter { it.startsWith(innerPageUrlLineStart) }
            .map { extractInfoFromLine(it) }
    }

    private fun extractInfoFromLine(line: String): AssetInfo {
        val start = line.indexOf(innerPageUrlStart) + innerPageUrlStart.length
        val end = line.indexOf(innerPageUrlEnd, start)
        val urlToInnerPage = "https://gamerdvr.com" + line.substring(start, end)

        val url = getRealUrl(urlToInnerPage)

        val uniqueName = urlToInnerPage.substring(urlToInnerPage.lastIndexOf("/") + 1)
        val fileName = "./download/$uniqueName.png"
        return AssetInfo(url, fileName)
    }

    private fun getRealUrl(urlToInnerPage: String): String {
        val pageData = fetchData(urlToInnerPage)

        val line = pageData
            .split("\n")
            .first { it.startsWith(assetDownloadUrlLineStart) }

        val start = line.indexOf(assetUrlStart) + assetUrlStart.length
        val end = line.indexOf(assetUrlEnd, start)
        return line.substring(start, end)
    }
}