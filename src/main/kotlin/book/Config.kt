package book

import kotlinx.serialization.Serializable

val jsonMapper = kotlinx.serialization.json.Json {
    ignoreUnknownKeys = true
    prettyPrint = true
}

@Serializable
data class Config(
    val title: String
)
