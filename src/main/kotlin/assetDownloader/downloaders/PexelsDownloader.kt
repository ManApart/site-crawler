package assetDownloader.downloaders

import assetDownloader.AssetInfo
import assetDownloader.AssetPageFetcher
import assetDownloader.mapper
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.module.kotlin.readValue
import org.jsoup.Jsoup


@JsonIgnoreProperties(ignoreUnknown = true)
private data class DownloadInfo(val data: List<PicDownload>)
@JsonIgnoreProperties(ignoreUnknown = true)
private data class PicDownload(val attributes: PicAttributes)
@JsonIgnoreProperties(ignoreUnknown = true)
private data class PicAttributes(val slug: String, val image: PicImage)
@JsonIgnoreProperties(ignoreUnknown = true)
private data class PicImage(val download_link: String)


//https://www.pexels.com/search/landscape/
//https://images.pexels.com/photos/2662116/pexels-photo-2662116.jpeg
//https://www.pexels.com/en-us/api/v3/search/photos?page=2&per_page=24&query=landscape&orientation=all&size=all&color=all
class PexelsDownloader(private val query: String = "landscape", private val start: Int = 1, val perPage: Int = 24, val max: Int = 200) : AssetPageFetcher {
    private val baseUrl = "https://www.pexels.com/en-us/api/v3/search/photos?page=$start&per_page=$perPage&query=$query&orientation=all&size=all&color=all"
    private var currentPage = start

    override fun baseUrl(): String {
        return baseUrl
    }

    override fun hasNext(pageData: String): Boolean {
        return currentPage* perPage < max
    }

    override fun getNextUrl(pageData: String): String {
        currentPage++
        return "https://www.pexels.com/en-us/api/v3/search/photos?page=$currentPage&per_page=$perPage&query=$query&orientation=all&size=all&color=all"
    }

    override fun getAssetInfos(url: String, pageData: String): List<AssetInfo> {
        return mapper.readValue<DownloadInfo>(pageData).data.map { image ->
            AssetInfo(image.attributes.image.download_link, "./download/${image.attributes.slug}.jpg")
        }
    }

}