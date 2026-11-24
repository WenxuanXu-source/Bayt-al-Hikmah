package com.baytalhikmah.data

import android.content.Context
import com.baytalhikmah.data.model.ReferencePack
import com.baytalhikmah.data.model.ReferenceTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class ReferenceRepository(private val context: Context) {
    private val json = Json { ignoreUnknownKeys = true }
    private val cache = mutableMapOf<String, List<ReferenceTable>>()

    suspend fun tables(languageCode: String): List<ReferenceTable> = withContext(Dispatchers.IO) {
        cache[languageCode]?.let { return@withContext it }
        val tables = runCatching {
            context.assets.open("reference/$languageCode.json").bufferedReader().use { it.readText() }
                .let { text -> json.decodeFromString(ReferencePack.serializer(), text).tables }
        }.getOrElse { emptyList() }
        cache[languageCode] = tables
        tables
    }
}
