package assetDownloader

//http://www.mariouniverse.com/sprites-snes-yi/
//http://www.mariouniverse.com/wp-content/img/sprites/snes/yi/amazee-dayzee.png
class MarioSpriteDownloader() : AssetPageFetcher {
    private val assetUrlStart = "<a href=\""
    private val assetUrlEnd = "\""

    override fun baseUrl(): String {
        return "http://www.mariouniverse.com/maps-snes-yi/"
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
            .filter { isAssetLine(it) }
            .map { extractInfoFromLine(it) }
    }

    private fun isAssetLine(line: String) : Boolean {
        return line.startsWith(assetUrlStart) && line.contains(".png")
    }

    private fun extractInfoFromLine(line: String): AssetInfo {
        val start = line.indexOf(assetUrlStart) + assetUrlStart.length
        val end = line.indexOf(assetUrlEnd, start)
        val url = line.substring(start, end)

        val uniqueName = url.substring(url.lastIndexOf("/") + 1)
        val fileName = "./download/$uniqueName"
        return AssetInfo(url, fileName)
    }

}