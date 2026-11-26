package com.baytalhikmah.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.baytalhikmah.data.local.ReadingLexemeEntity
import com.baytalhikmah.data.local.ReadingProgressEntity
import com.baytalhikmah.data.local.ReadingSectionEntity
import com.baytalhikmah.data.local.ReadingSectionProgressEntity
import com.baytalhikmah.data.local.ReadingWorkEntity
import kotlinx.coroutines.flow.Flow

data class ReadingBookRow(
    val bookNumber: Int,
    val bookLabel: String,
    val sectionCount: Int
)

data class ReadingWorkSummaryRow(
    val workId: String,
    val sectionCount: Int,
    val startedSections: Int,
    val finishedSections: Int
)

@Dao
interface ReadingDao {
    @Query("SELECT * FROM reading_works WHERE languageCode = :language ORDER BY sortOrder")
    fun observeWorks(language: String): Flow<List<ReadingWorkEntity>>

    @Query("SELECT * FROM reading_works ORDER BY languageCode, sortOrder")
    suspend fun getAllWorks(): List<ReadingWorkEntity>

    @Query("SELECT * FROM reading_works WHERE id = :id")
    suspend fun getWork(id: String): ReadingWorkEntity?

    @Query(
        """
        SELECT bookNumber AS bookNumber, bookLabel AS bookLabel, COUNT(*) AS sectionCount
        FROM reading_sections WHERE workId = :workId
        GROUP BY bookNumber ORDER BY bookNumber
        """
    )
    suspend fun books(workId: String): List<ReadingBookRow>

    @Query("SELECT * FROM reading_sections WHERE workId = :workId ORDER BY sortOrder")
    suspend fun sections(workId: String): List<ReadingSectionEntity>

    @Query("SELECT * FROM reading_sections WHERE workId = :workId AND id = :sectionId")
    suspend fun sectionInWork(workId: String, sectionId: String): ReadingSectionEntity?

    @Query("SELECT * FROM reading_sections WHERE id = :id")
    suspend fun getSection(id: String): ReadingSectionEntity?

    @Query("SELECT * FROM reading_sections WHERE workId = :workId AND bookNumber = :bookNumber ORDER BY sortOrder")
    suspend fun sectionsInBook(workId: String, bookNumber: Int): List<ReadingSectionEntity>

    @Query("SELECT * FROM reading_lexemes WHERE id IN (:ids)")
    suspend fun lexemesByIds(ids: List<String>): List<ReadingLexemeEntity>

    @Query("SELECT * FROM reading_lexemes WHERE workId = :workId ORDER BY surface")
    suspend fun lexemesForWork(workId: String): List<ReadingLexemeEntity>

    @Query("SELECT * FROM reading_progress")
    fun observeProgress(): Flow<List<ReadingProgressEntity>>

    @Query("SELECT * FROM reading_progress WHERE workId = :workId")
    suspend fun progress(workId: String): ReadingProgressEntity?

    @Query("SELECT * FROM reading_section_progress")
    fun observeSectionProgress(): Flow<List<ReadingSectionProgressEntity>>

    @Query("SELECT * FROM reading_section_progress WHERE sectionId = :sectionId")
    suspend fun sectionProgress(sectionId: String): ReadingSectionProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSectionProgress(item: ReadingSectionProgressEntity)

    @Query(
        """
        SELECT s.workId AS workId, COUNT(*) AS sectionCount,
               SUM(CASE WHEN p.status IS NOT NULL THEN 1 ELSE 0 END) AS startedSections,
               SUM(CASE WHEN p.status = 'FINISHED' THEN 1 ELSE 0 END) AS finishedSections
        FROM reading_sections s
        LEFT JOIN reading_section_progress p ON p.sectionId = s.id
        GROUP BY s.workId
        """
    )
    fun observeWorkSectionSummary(): Flow<List<ReadingWorkSummaryRow>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProgress(item: ReadingProgressEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertWorks(items: List<ReadingWorkEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertLexemes(items: List<ReadingLexemeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSections(items: List<ReadingSectionEntity>)

    @Query("DELETE FROM reading_sections WHERE workId = :workId")
    suspend fun deleteSections(workId: String)

    @Query("DELETE FROM reading_lexemes WHERE workId = :workId")
    suspend fun deleteLexemes(workId: String)
}
