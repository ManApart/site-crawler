package assetDownloader

data class AssetInfo(val url: String, val fileName: String, val additionalInfos: ((String) -> List<AssetInfo>)? = null)
