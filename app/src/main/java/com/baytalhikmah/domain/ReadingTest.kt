package com.baytalhikmah.domain

import com.baytalhikmah.data.model.ReadingQuestionDto
import kotlin.random.Random

data class DrawnReadingQuestion(
    val id: String,
    val prompt: String,
    val passage: String? = null,
    val options: List<String>,
    val correctIndex: Int
)

data class ReadingTestOutcome(
    val correct: Int,
    val total: Int
) {
    val passed: Boolean get() = total >= ReadingTest.QUESTIONS_PER_TEST && correct == total
}

/**
 * Draws distinct questions from the bank and shuffles each question's options.
 * A seeded Random makes the draw reproducible in tests.
 */
object ReadingTest {
    const val QUESTIONS_PER_TEST = 2

    fun draw(
        bank: List<ReadingQuestionDto>,
        random: Random = Random.Default
    ): List<DrawnReadingQuestion> {
        val valid = bank.filter { question ->
            question.options.size == 4 &&
                question.options.toSet().size == 4 &&
                question.options.all { it.isNotBlank() } &&
                question.correctIndex in question.options.indices
        }
        if (valid.size < QUESTIONS_PER_TEST) return emptyList()
        return valid
            .distinctBy { it.id }
            .shuffled(random)
            .take(QUESTIONS_PER_TEST)
            .map { question ->
                val order = question.options.indices.shuffled(random)
                DrawnReadingQuestion(
                    id = question.id,
                    prompt = question.prompt,
                    passage = question.passage,
                    options = order.map(question.options::get),
                    correctIndex = order.indexOf(question.correctIndex)
                )
            }
    }

    fun grade(
        questions: List<DrawnReadingQuestion>,
        selected: List<Int?>
    ): ReadingTestOutcome {
        require(questions.size == selected.size) { "One selection per question is required." }
        val correct = questions.indices.count { index ->
            selected[index] == questions[index].correctIndex
        }
        return ReadingTestOutcome(correct = correct, total = questions.size)
    }
}
