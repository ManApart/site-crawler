import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

val jsonMapper = Json {
    ignoreUnknownKeys = true
    prettyPrint = true
    encodeDefaults = true
}


@Serializable
data class MainConfig(
    val book: BookConfig = BookConfig()
)

@Serializable
data class BookConfig(
    val title: String = "",
    val author: String = "",
)

fun readConfig(): MainConfig {
    val configFile = File("config.json")
    return if (configFile.exists()) {
        configFile.readText().let { jsonMapper.decodeFromString(it) }
    } else {
        configFile.writeText(jsonMapper.encodeToString(MainConfig()))
        throw IllegalStateException("Fill out your config file!")
    }
}
