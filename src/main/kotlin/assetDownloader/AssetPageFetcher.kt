package assetDownloader

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

val mapper = jacksonObjectMapper()

interface AssetPageFetcher {
    fun baseUrl() : String
    fun hasNext(pageData: String) : Boolean
    fun getNextUrl(pageData: String) : String
    fun getAssetInfos(url: String, pageData: String) : List<AssetInfo>
    fun getHeaders(): Map<String, String> = mapOf()
}