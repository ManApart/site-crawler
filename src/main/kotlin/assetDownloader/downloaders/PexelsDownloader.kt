package assetDownloader.downloaders

import assetDownloader.AssetInfo
import assetDownloader.AssetPageFetcher
import jsonMapper


//https://www.pexels.com/search/landscape/
//https://images.pexels.com/photos/2662116/pexels-photo-2662116.jpeg
//https://www.pexels.com/en-us/api/v3/search/photos?page=2&per_page=24&query=landscape&orientation=all&size=all&color=all
class PexelsDownloader(private val query: String = "landscape", private val start: Int = 1, val perPage: Int = 24, val max: Int = 200, val apiKey: String) : AssetPageFetcher {
    private val baseUrl = "https://api.pexels.com/v1/search?query=$query&page=$start&per_page=$perPage&query=$query&orientation=all&size=all&color=all"
    private var currentPage = start

    override fun baseUrl(): String {
        return baseUrl
    }

    override fun hasNext(pageData: String): Boolean {
        return (currentPage - start) * perPage < max
    }

    override fun getNextUrl(pageData: String): String {
        currentPage++
        return "https://api.pexels.com/v1/search?query=$query&page=$currentPage&per_page=$perPage&query=$query&orientation=all&size=all&color=all"
    }

    override fun getAssetInfos(url: String, pageData: String): List<AssetInfo> {
        return jsonMapper.decodeFromString<PageResults>(pageData).photos.map { image ->
            AssetInfo(image.src.original, "./download/${image.id}.jpg")
        }
    }

    override fun getHeaders(): Map<String, String> {
        return mapOf("Authorization" to apiKey)
    }

}
