package com.baytalhikmah.domain

import com.baytalhikmah.data.model.ReadingQuestionDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class ReadingTestTest {
    private val bank = listOf(
        ReadingQuestionDto(id = "q1", prompt = "One?", options = listOf("A1", "B1", "C1", "D1"), correctIndex = 0),
        ReadingQuestionDto(id = "q2", prompt = "Two?", options = listOf("A2", "B2", "C2", "D2"), correctIndex = 1),
        ReadingQuestionDto(id = "q3", prompt = "Three?", options = listOf("A3", "B3", "C3", "D3"), correctIndex = 2),
        ReadingQuestionDto(id = "q4", prompt = "Four?", options = listOf("A4", "B4", "C4", "D4"), correctIndex = 3)
    )

    @Test
    fun drawReturnsTwoDistinctFourOptionQuestions() {
        repeat(40) { seed ->
            val drawn = ReadingTest.draw(bank, Random(seed))
            assertEquals(2, drawn.size)
            assertEquals(2, drawn.map { it.id }.toSet().size)
            drawn.forEach { question ->
                assertEquals(4, question.options.size)
                assertEquals(4, question.options.toSet().size)
                assertTrue(question.correctIndex in question.options.indices)
            }
        }
    }

    @Test
    fun drawShufflesAwayAuthoringOrderAndRemapsTheAnswer() {
        // With all authored answers at index 0, a shuffled draw must still know
        // which displayed option is correct.
        val single = listOf(
            ReadingQuestionDto(id = "q1", prompt = "One?", options = listOf("correct", "wrong-a", "wrong-b", "wrong-c"), correctIndex = 0),
            ReadingQuestionDto(id = "q2", prompt = "Two?", options = listOf("correct", "wrong-a", "wrong-b", "wrong-c"), correctIndex = 0)
        )
        var observedMoved = false
        repeat(40) { seed ->
            val drawn = ReadingTest.draw(single, Random(seed))
            drawn.forEach { question ->
                assertEquals("correct", question.options[question.correctIndex])
                if (question.correctIndex != 0) observedMoved = true
            }
        }
        assertTrue("option order never changed", observedMoved)
    }

    @Test
    fun bothCorrectIsRequiredToPass() {
        val drawn = ReadingTest.draw(bank, Random(7))
        val allRight = drawn.map { it.correctIndex }
        assertTrue(ReadingTest.grade(drawn, allRight).passed)

        val oneWrong = allRight.toMutableList()
        oneWrong[0] = (oneWrong[0] + 1) % 4
        val failed = ReadingTest.grade(drawn, oneWrong)
        assertEquals(1, failed.correct)
        assertFalse(failed.passed)
    }

    @Test
    fun aBankWithFewerThanTwoQuestionsCannotStartATest() {
        assertTrue(ReadingTest.draw(bank.take(1)).isEmpty())
        assertTrue(ReadingTest.draw(emptyList()).isEmpty())
    }
}
