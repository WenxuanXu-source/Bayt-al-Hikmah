package com.baytalhikmah.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
private data class ReadingWordIndex(
    val schemaVersion: Int = 1,
    val language: String = "",
    val sections: Map<String, List<String>> = emptyMap()
)

/** Basic/Intermediate vocabulary occurring in each reading section. */
class ReadingWordIndexRepository(private val context: Context) {
    private val json = Json { ignoreUnknownKeys = true }
    private var index: Map<String, List<String>>? = null

    suspend fun ids(sectionId: String): List<String> = withContext(Dispatchers.IO) {
        load()[sectionId].orEmpty()
    }

    private fun load(): Map<String, List<String>> {
        index?.let { return it }
        val text = context.assets.open(ASSET).bufferedReader().use { it.readText() }
        val payload = json.decodeFromString(ReadingWordIndex.serializer(), text)
        index = payload.sections
        return payload.sections
    }

    companion object {
        const val ASSET = "reading/section_words.json"
    }
}
