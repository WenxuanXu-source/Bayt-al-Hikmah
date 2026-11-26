package com.baytalhikmah.data

import android.content.Context
import com.baytalhikmah.data.model.ReadingQuestionBank
import com.baytalhikmah.data.model.ReadingQuestionDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

/**
 * Loads the per-section comprehension-question bank once per process. Each
 * section carries at least two content questions; the runtime draws two.
 */
class ReadingQuestionRepository(private val context: Context) {
    private val json = Json { ignoreUnknownKeys = true }
    private var bank: Map<String, List<ReadingQuestionDto>>? = null

    suspend fun questions(sectionId: String): List<ReadingQuestionDto> = withContext(Dispatchers.IO) {
        load()[sectionId].orEmpty()
    }

    suspend fun all(): Map<String, List<ReadingQuestionDto>> = withContext(Dispatchers.IO) {
        load()
    }

    private fun load(): Map<String, List<ReadingQuestionDto>> {
        bank?.let { return it }
        val text = context.assets.open(ASSET).bufferedReader().use { it.readText() }
        val payload = json.decodeFromString(ReadingQuestionBank.serializer(), text)
        val loaded = payload.sections.associate { section ->
            section.sectionId to section.questions.filter(::isValid)
        }
        bank = loaded
        return loaded
    }

    private fun isValid(question: ReadingQuestionDto): Boolean =
        question.id.isNotBlank() &&
            question.prompt.isNotBlank() &&
            question.options.size == OPTION_COUNT &&
            question.options.toSet().size == OPTION_COUNT &&
            question.options.all { it.isNotBlank() } &&
            question.correctIndex in question.options.indices

    companion object {
        const val ASSET = "reading/section_questions.json"
        const val OPTION_COUNT = 4
    }
}
