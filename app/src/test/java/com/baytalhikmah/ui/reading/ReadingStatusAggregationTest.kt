package com.baytalhikmah.ui.reading

import com.baytalhikmah.data.local.ReadingSectionEntity
import com.baytalhikmah.data.local.dao.ReadingWorkSummaryRow
import com.baytalhikmah.data.model.ReadingStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class ReadingStatusAggregationTest {

    private fun section(id: String, book: Int = 1) = ReadingSectionEntity(
        id = id,
        workId = "work",
        bookNumber = book,
        bookLabel = "Book $book",
        sectionLabel = id,
        heading = "",
        originalText = "text",
        translationText = "translation",
        newWordIds = emptyList(),
        sortOrder = 0
    )

    @Test
    fun workAndBookFinishOnlyWhenEverySectionIsFinished() {
        val sections = listOf(section("1"), section("2"), section("3"))
        val state = ReadingUiState(
            workSummaries = mapOf(
                "work" to ReadingWorkSummaryRow(
                    workId = "work",
                    sectionCount = 3,
                    startedSections = 3,
                    finishedSections = 3
                )
            ),
            sections = sections,
            sectionStatus = mapOf(
                "1" to ReadingStatus.FINISHED,
                "2" to ReadingStatus.FINISHED,
                "3" to ReadingStatus.FINISHED
            )
        )
        assertEquals(ReadingStatus.FINISHED, state.statusForWork("work"))
        assertEquals(ReadingStatus.FINISHED, state.statusForBook(1))
    }

    @Test
    fun oneOpenSectionKeepsTheBookAndWorkReading() {
        val sections = listOf(section("1"), section("2"))
        val state = ReadingUiState(
            workSummaries = mapOf(
                "work" to ReadingWorkSummaryRow(
                    workId = "work",
                    sectionCount = 2,
                    startedSections = 1,
                    finishedSections = 0
                )
            ),
            sections = sections,
            sectionStatus = mapOf("1" to ReadingStatus.READING)
        )
        assertEquals(ReadingStatus.READING, state.statusForWork("work"))
        assertEquals(ReadingStatus.READING, state.statusForBook(1))
        assertEquals(ReadingStatus.UNREAD, state.statusForSection("2"))
    }
}
