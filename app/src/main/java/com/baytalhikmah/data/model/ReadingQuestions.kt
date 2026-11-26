package com.baytalhikmah.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ReadingQuestionBank(
    val schemaVersion: Int = 1,
    val sections: List<ReadingSectionQuestions> = emptyList()
)

@Serializable
data class ReadingSectionQuestions(
    val sectionId: String = "",
    val questions: List<ReadingQuestionDto> = emptyList()
)

/**
 * A content-comprehension question about one reading section. Options are
 * ordered as authored; the runtime draw shuffles them and remaps the answer.
 */
@Serializable
data class ReadingQuestionDto(
    val id: String,
    val prompt: String,
    val passage: String? = null,
    val options: List<String> = emptyList(),
    val correctIndex: Int = -1
)
