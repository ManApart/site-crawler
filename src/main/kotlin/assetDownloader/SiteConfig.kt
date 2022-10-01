package assetDownloader

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

val mapper = jacksonObjectMapper()

data class SiteConfig(val url: String, val pageFetcher: AssetPageFetcher)