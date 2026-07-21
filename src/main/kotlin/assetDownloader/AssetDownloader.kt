package assetDownloader

import assetDownloader.downloaders.PexelsDownloader
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import readConfig
import java.io.File
import java.net.URI
import java.nio.charset.StandardCharsets
import java.nio.file.Files.copy
import java.nio.file.StandardCopyOption
import java.util.*
import java.util.Collections.emptyList

const val MAX_DEPTH = 10
const val CHUNK_SIZE = 10
const val DOWNLOAD_FIRST_ONLY = false
const val CRAWL_LOCAL = false
const val REDOWNLOAD_EXISTING = false
const val DOWNLOAD_AS_CURL = false
const val VERBOSE = false

val config = readConfig().assetDownloader

//    val fetcher = XboxScreenShotDownloader("iceburg-33308")
//    val fetcher = XboxClipDownloader("iceburg33308")
//    val fetcher = ESOWallpaperDownloader()
//    val fetcher = WikiArtDownloader("https://www.wikiart.org/en/norman-rockwell/all-works/text-list")
//val fetcher = ImperialLibraryPictureDownloader()
//val fetcher = StarfieldInaraDownloader()
//val fetcher = UESPPictureDownloader("https://en.uesp.net/wiki/Shivering:Concept_Art")
//val fetcher = SteamAchievementIconDownloader("https://steamcommunity.com/stats/2623190/achievements")
//val fetcher = LonerganDownloader()
//val fetcher = ImageByCssClassDownloader("https://steamcommunity.com/stats/1716740/achievements", ".achieveImgHolder")

//    val fetcher = FiftyNineParksDownloader()
//    val fetcher = ArtStationDownloader("https://www.artstation.com/nicatorshields", listOf("https://cdna.artstation.com/p/assets/images/", "https://cdnb.artstation.com/p/assets/images/"))
val fetcher = PexelsDownloader("ireland", 1, 20, 200, config.pexelsApiKey)

//    val fetcher = PrintableBrickDownloader(60, 40)
//    val fetcher = StarfieldWikiDownloader("https://starfieldwiki.net/wiki/Category:Starfield-Skill_Images")
fun main() {
    crawlAndDownload(fetcher, fetcher.baseUrl(), fetcher.getHeaders())
}

private fun crawlAndDownload(fetcher: AssetPageFetcher, url: String, headers: Map<String, String>, depth: Int = 0) {
    val (data, assetInfos) = if (CRAWL_LOCAL) crawlLocal(fetcher) else crawl(fetcher, url, headers)
    if (VERBOSE) println("Crawling $url")
    println("Found ${assetInfos.size} assets.")

    if (DOWNLOAD_FIRST_ONLY) {
        download(assetInfos.first())
    } else {
        val totalChunks = assetInfos.size / CHUNK_SIZE
        assetInfos.chunked(CHUNK_SIZE).withIndex().forEach {
            downloadChunk(it.value, it.index, totalChunks)
        }

        if (fetcher.hasNext(data) && depth < MAX_DEPTH) {
            val nextUrl = fetcher.getNextUrl(data)
            if (url != nextUrl) {
                crawlAndDownload(fetcher, nextUrl, headers, depth + 1)
            }
        }
    }
}

private fun crawl(fetcher: AssetPageFetcher, url: String, headers: Map<String, String>, depth: Int = 0): Pair<String, List<AssetInfo>> {
    println("Finding assets at $url")
    val data = fetchData(url, headers)

    val infos = try {
        fetcher.getAssetInfos(url, data)
    } catch (e: Exception) {
        println("Failed to get asset infos: $e")
        emptyList()
    }

    return Pair(data, infos)
}

private fun crawlLocal(fetcher: AssetPageFetcher, useHtml: Boolean = true): Pair<String, List<AssetInfo>> {
    println("Finding assets for local")
    val file = if (useHtml) File("./in/local.html") else File("./in/local.json")
    val data = file.readText()

    return Pair(data, fetcher.getAssetInfos("local", data))
}

fun fetchData(url: String, headers: Map<String, String>): String {
    with(URI.create(url).toURL().openConnection()) {
        setRequestProperty(
            "User-Agent", "curl/8.5.0"
        )
        headers.entries.forEach { (key, value) ->
            setRequestProperty(key, value)
        }
        connect()
        Scanner(
            getInputStream(),
            StandardCharsets.UTF_8.toString()
        ).use { scanner ->
            scanner.useDelimiter("\\A")
            return if (scanner.hasNext()) scanner.next() else ""
        }
    }

}

fun downloadChunk(infos: List<AssetInfo>, i: Int, totalChunks: Int) {
    println("Downloading chunk $i/$totalChunks")
    with(File("./download")) {
        if (!exists()) mkdirs()
    }
    runBlocking {
        infos.map {
            async {
                download(it)
            }
        }.awaitAll()
    }
}

private fun download(info: AssetInfo) {
    val file = File(info.fileName).also { it.parentFile.mkdirs() }
    val download = !file.exists() || REDOWNLOAD_EXISTING
    if (VERBOSE) {
        if (download) println("Downloading ${info.url}") else println("Skipping ${info.url}")
    }
    if (info.additionalInfos != null) {
        val data = if (download) fetchData(info.url, fetcher.getHeaders()) else file.readText()
        if (!file.exists()) File(info.fileName).writeText(data)
        info.additionalInfos.invoke(data).also { if (it.isNotEmpty()) println("Getting ${it.size} additional assets") }.forEach { download(it) }
    } else if (download) downloadToFile(info, fetcher.getHeaders())
}

private fun downloadToFile(info: AssetInfo, headers: Map<String, String>) {
    if (DOWNLOAD_AS_CURL) downloadToFileCurl(info, headers) else downloadToFileKotlin(info, headers)
}


private fun downloadToFileKotlin(info: AssetInfo, headers: Map<String, String>) {
    with(URI.create(info.url).toURL().openConnection()) {
        try {
            setRequestProperty(
                "User-Agent", "curl/8.5.0"
            )
            headers.entries.forEach { (key, value) ->
                setRequestProperty(key, value)
            }
            connect()
        } catch (e: Exception) {
            println("Failed to connect to ${info.url}")
        }

        try {
            val file = File(info.fileName)
            copy(getInputStream(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (ex: Exception) {
            println("Failed to download ${info.fileName} from ${info.url}")
            println(ex)
        }
    }
}

private fun downloadToFileCurl(info: AssetInfo, headers: Map<String, String>) {
//    val cmd = """curl --fail "${info.url}" -o "${info.fileName}" """ + headers.entries.joinToString(" ") { "-H '${it.key}: ${it.value}'" }
    val cmd = listOf("curl", "--fail", info.url, "-o", info.fileName) + headers.entries.flatMap { listOf("-H", "${it.key}: ${it.value}") }
    File(".").runCommand(cmd)
}
