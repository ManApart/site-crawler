package assetDownloader.downloaders

import kotlinx.serialization.Serializable

@Serializable
data class PageResults(val page: Int, val photos: List<Photo>)

@Serializable
data class Photo(val id: Int, val src: PhotoSource)

@Serializable
data class PhotoSource(val original: String)
