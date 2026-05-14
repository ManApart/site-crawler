package assetDownloader.processors

import assetDownloader.AssetProcessor
import org.jsoup.Jsoup
import java.io.File

private data class SFResource(val name: String, val type: String, val description: String)

class StarfieldInaraProcessor : AssetProcessor {
    private val resources = mutableListOf<SFResource>()

    override fun downloadPath() = "download/inara"

    override fun process(file: File) {
        if (file.extension == "md") return
        val block = Jsoup.parse(file).select(".mainBlock").first()
        if (block == null){
            println("Could not find info for ${file.nameWithoutExtension}")
            return
        }
        val title = block.select(".itemname").first()?.text() ?: ""
        val type = block.select(".itemtype").first()?.text() ?: ""
        val description = block.select(".griditem").first()?.select("div")?.get(2)?.text() ?: ""

        resources.add(SFResource(title, type, description))
    }

    override fun allFilesProcessed() {
        val content = resources.joinToString("\n\n") { """
            ## ${it.name}
            ${it.type}
            
            ${it.description}
        """.trimIndent() }
        File("download/inara/resources.md").writeText(content)
    }
}
