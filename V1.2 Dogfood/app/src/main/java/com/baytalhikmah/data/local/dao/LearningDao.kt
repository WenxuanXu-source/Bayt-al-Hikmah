package com.baytalhikmah.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.baytalhikmah.data.local.DailyTaskEntity
import com.baytalhikmah.data.local.LearningRecordEntity
import com.baytalhikmah.data.local.LearningSessionEntity
import com.baytalhikmah.data.local.SavedWordEntity
import com.baytalhikmah.data.local.VocabularyEntity
import com.baytalhikmah.data.model.Gender
import com.baytalhikmah.data.model.LearningStatus
import com.baytalhikmah.data.model.Pos
import kotlinx.coroutines.flow.Flow

data class SavedVocabularyRow(
    val id: String,
    val headword: String,
    val pos: Pos,
    val gender: Gender?,
    val definitions: List<String>,
    val savedOn: String
)

@Dao
interface LearningDao {
    @Query("SELECT * FROM learning_records WHERE vocabularyId = :vocabularyId")
    suspend fun record(vocabularyId: String): LearningRecordEntity?

    @Query("SELECT * FROM learning_records WHERE vocabularyId = :vocabularyId")
    fun observeRecord(vocabularyId: String): Flow<LearningRecordEntity?>

    @Query("SELECT * FROM learning_records WHERE languageCode = :language")
    fun observeRecords(language: String): Flow<List<LearningRecordEntity>>

    @Query("SELECT * FROM learning_records WHERE languageCode = :language")
    suspend fun records(language: String): List<LearningRecordEntity>

    @Query("SELECT COUNT(*) FROM learning_records WHERE languageCode = :language AND status = 'LEARNED'")
    fun observeLearnedCount(language: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM learning_records WHERE languageCode = :language AND status = 'LEARNED'")
    suspend fun learnedCount(language: String): Int

    @Query("SELECT * FROM learning_records WHERE languageCode = :language AND status = 'LEARNED'")
    suspend fun learnedRecords(language: String): List<LearningRecordEntity>

    @Query("SELECT * FROM learning_records WHERE languageCode = :language AND status = 'IN_PROGRESS'")
    suspend fun inProgressRecords(language: String): List<LearningRecordEntity>

    @Query("SELECT vocabularyId FROM learning_records WHERE languageCode = :language AND status IN ('IN_PROGRESS', 'LEARNED')")
    suspend fun introducedIds(language: String): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRecord(record: LearningRecordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRecords(records: List<LearningRecordEntity>)

    @Query("UPDATE learning_records SET status = :status, firstLearnedOn = COALESCE(firstLearnedOn, :date) WHERE vocabularyId IN (:ids)")
    suspend fun markLearned(ids: List<String>, status: LearningStatus, date: String)

    @Query(
        """
        UPDATE learning_records
        SET totalAttempts = totalAttempts + 1,
            correctAttempts = correctAttempts + :correct,
            incorrectAttempts = incorrectAttempts + :incorrect,
            lastReviewedOn = :date
        WHERE vocabularyId = :vocabularyId
        """
    )
    suspend fun recordAttempt(vocabularyId: String, correct: Int, incorrect: Int, date: String)

    @Query("UPDATE learning_records SET difficult = :difficult WHERE vocabularyId = :vocabularyId")
    suspend fun setDifficult(vocabularyId: String, difficult: Boolean)

    @Query("SELECT * FROM learning_records")
    suspend fun allRecords(): List<LearningRecordEntity>

    @Query("DELETE FROM learning_records")
    suspend fun deleteAllRecords()

    @Query("SELECT * FROM daily_tasks WHERE languageCode = :language AND learningDate = :date")
    suspend fun dailyTask(language: String, date: String): DailyTaskEntity?

    @Query("SELECT * FROM daily_tasks WHERE id = :id")
    suspend fun dailyTaskById(id: String): DailyTaskEntity?

    @Query("SELECT * FROM daily_tasks WHERE languageCode = :language AND learningDate = :date")
    fun observeDailyTask(language: String, date: String): Flow<DailyTaskEntity?>

    @Query("SELECT * FROM daily_tasks WHERE languageCode = :language ORDER BY learningDate DESC")
    fun observeDailyTasks(language: String): Flow<List<DailyTaskEntity>>

    @Query("SELECT * FROM daily_tasks WHERE languageCode = :language ORDER BY learningDate DESC LIMIT :limit")
    suspend fun recentDailyTasks(language: String, limit: Int): List<DailyTaskEntity>

    @Query("SELECT * FROM daily_tasks WHERE languageCode = :language AND overallStatus = 'COMPLETED'")
    suspend fun completedDailyTasks(language: String): List<DailyTaskEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDailyTask(task: DailyTaskEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDailyTasks(tasks: List<DailyTaskEntity>)

    @Query("SELECT * FROM daily_tasks")
    suspend fun allDailyTasks(): List<DailyTaskEntity>

    @Query("DELETE FROM daily_tasks")
    suspend fun deleteAllDailyTasks()

    @Query("SELECT * FROM learning_sessions WHERE id = :id")
    suspend fun learningSession(id: String): LearningSessionEntity?

    @Query("SELECT * FROM learning_sessions WHERE id = :id")
    fun observeLearningSession(id: String): Flow<LearningSessionEntity?>

    @Query("SELECT * FROM learning_sessions WHERE languageCode = :language AND learningDate = :date")
    suspend fun learningSessionForDate(language: String, date: String): LearningSessionEntity?

    @Query("SELECT * FROM learning_sessions WHERE languageCode = :language AND phase != 'COMPLETE' ORDER BY learningDate DESC")
    fun observeUnfinishedSessions(language: String): Flow<List<LearningSessionEntity>>

    @Query("SELECT * FROM learning_sessions WHERE phase != 'COMPLETE'")
    suspend fun allUnfinishedSessions(): List<LearningSessionEntity>

    @Query("SELECT * FROM learning_sessions WHERE languageCode = :language AND phase != 'COMPLETE' AND learningDate != :date ORDER BY learningDate DESC")
    suspend fun unfinishedSessionsBefore(language: String, date: String): List<LearningSessionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertLearningSession(session: LearningSessionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertLearningSessions(sessions: List<LearningSessionEntity>)

    @Query("SELECT * FROM learning_sessions")
    suspend fun allLearningSessions(): List<LearningSessionEntity>

    @Query("DELETE FROM learning_sessions")
    suspend fun deleteAllLearningSessions()

    @Query("SELECT * FROM saved_words WHERE languageCode = :language ORDER BY savedOn DESC")
    fun observeSaved(language: String): Flow<List<SavedWordEntity>>

    @Query(
        """
        SELECT v.id AS id, v.headword AS headword, v.pos AS pos, v.gender AS gender,
               v.definitions AS definitions, s.savedOn AS savedOn
        FROM saved_words s
        JOIN vocabulary v ON v.id = s.vocabularyId
        WHERE s.languageCode = :language
        ORDER BY v.normalizedHeadword
        """
    )
    fun observeSavedVocabulary(language: String): Flow<List<SavedVocabularyRow>>

    @Query("SELECT * FROM saved_words")
    suspend fun allSaved(): List<SavedWordEntity>

    @Query("SELECT * FROM saved_words WHERE languageCode = :language ORDER BY savedOn DESC")
    suspend fun savedWords(language: String): List<SavedWordEntity>

    @Query("SELECT COUNT(*) FROM saved_words WHERE languageCode = :language")
    suspend fun savedCount(language: String): Int

    @Query("SELECT * FROM saved_words WHERE vocabularyId = :vocabularyId")
    suspend fun savedEntry(vocabularyId: String): SavedWordEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM saved_words WHERE vocabularyId = :vocabularyId)")
    fun observeIsSaved(vocabularyId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveWord(word: SavedWordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveWords(words: List<SavedWordEntity>)

    @Query("DELETE FROM saved_words WHERE vocabularyId = :vocabularyId")
    suspend fun removeSavedWord(vocabularyId: String)

    @Query("SELECT COUNT(*) FROM saved_words WHERE languageCode = :language")
    fun observeSavedCount(language: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM saved_words")
    suspend fun savedCountAll(): Int

    @Query("DELETE FROM saved_words")
    suspend fun deleteAllSaved()
}
