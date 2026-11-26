package com.baytalhikmah.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.baytalhikmah.data.local.TestQuestionEntity
import com.baytalhikmah.data.local.TestSessionEntity
import com.baytalhikmah.data.model.VocabBook
import kotlinx.coroutines.flow.Flow

@Dao
interface TestDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertSession(session: TestSessionEntity)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertQuestions(questions: List<TestQuestionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSessionsForRestore(sessions: List<TestSessionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestionsForRestore(questions: List<TestQuestionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSession(session: TestSessionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertQuestion(question: TestQuestionEntity)

    @Query("SELECT * FROM test_sessions WHERE id = :sessionId")
    suspend fun session(sessionId: String): TestSessionEntity?

    @Query("SELECT * FROM test_sessions WHERE id = :sessionId")
    fun observeSession(sessionId: String): Flow<TestSessionEntity?>

    @Query("SELECT * FROM test_questions WHERE id = :questionId")
    suspend fun question(questionId: String): TestQuestionEntity?

    @Query("SELECT * FROM test_questions WHERE sessionId = :sessionId ORDER BY groupIndex, orderInGroup, rowid")
    suspend fun questions(sessionId: String): List<TestQuestionEntity>

    @Query("SELECT * FROM test_questions WHERE sessionId = :sessionId ORDER BY groupIndex, orderInGroup, rowid")
    fun observeQuestions(sessionId: String): Flow<List<TestQuestionEntity>>

    @Query("SELECT * FROM test_questions WHERE sessionId = :sessionId AND groupIndex = :groupIndex ORDER BY orderInGroup, rowid")
    suspend fun questionsInGroup(sessionId: String, groupIndex: Int): List<TestQuestionEntity>

    @Query("SELECT * FROM test_questions WHERE sessionId = :sessionId AND isCorrect IS NULL ORDER BY groupIndex, orderInGroup, rowid LIMIT 1")
    suspend fun nextUnanswered(sessionId: String): TestQuestionEntity?

    @Query("SELECT COUNT(*) FROM test_questions WHERE sessionId = :sessionId AND isCorrect IS NULL")
    suspend fun unansweredCount(sessionId: String): Int

    @Query("SELECT COUNT(*) FROM test_questions WHERE sessionId = :sessionId")
    suspend fun questionCount(sessionId: String): Int

    @Query("SELECT COUNT(*) FROM test_questions WHERE sessionId = :sessionId AND isCorrect = 1")
    suspend fun correctCount(sessionId: String): Int

    @Query("SELECT COUNT(*) FROM test_questions WHERE sessionId = :sessionId AND isCorrect = 0")
    suspend fun incorrectCount(sessionId: String): Int

    @Query("SELECT MIN(groupIndex) FROM test_questions WHERE sessionId = :sessionId AND isCorrect IS NULL")
    suspend fun firstUnansweredGroup(sessionId: String): Int?

    @Query("SELECT MAX(groupIndex) FROM test_questions WHERE sessionId = :sessionId")
    suspend fun maxGroup(sessionId: String): Int?

    @Query("SELECT * FROM test_sessions WHERE languageCode = :language AND book = :book AND status = 'IN_PROGRESS' ORDER BY createdAt DESC")
    fun observeInProgress(language: String, book: VocabBook): Flow<List<TestSessionEntity>>

    @Query("SELECT * FROM test_sessions WHERE languageCode = :language AND book = :book AND status = 'IN_PROGRESS' ORDER BY createdAt DESC")
    suspend fun inProgress(language: String, book: VocabBook): List<TestSessionEntity>

    @Query("SELECT * FROM test_sessions WHERE sessionDate = :date AND status = 'IN_PROGRESS'")
    suspend fun inProgressForDate(date: String): List<TestSessionEntity>

    @Query(
        """
        SELECT * FROM test_sessions
        WHERE languageCode = :language AND book = :book AND testType != 'GROUP_QUIZ'
        ORDER BY createdAt DESC
        """
    )
    fun observeHistory(language: String, book: VocabBook): Flow<List<TestSessionEntity>>

    @Query("SELECT * FROM test_sessions WHERE languageCode = :language AND book = :book AND testType = :testType AND sessionDate = :date ORDER BY createdAt DESC LIMIT 1")
    suspend fun latestSession(language: String, book: VocabBook, testType: String, date: String): TestSessionEntity?

    @Query("SELECT * FROM test_sessions WHERE dailyTaskId = :dailyTaskId AND testType = :testType ORDER BY createdAt DESC LIMIT 1")
    suspend fun latestForTask(dailyTaskId: String, testType: String): TestSessionEntity?

    @Query("SELECT * FROM test_sessions")
    suspend fun allSessions(): List<TestSessionEntity>

    @Query("SELECT * FROM test_questions")
    suspend fun allQuestions(): List<TestQuestionEntity>

    @Query("DELETE FROM test_questions")
    suspend fun deleteAllQuestions()

    @Query("DELETE FROM test_sessions")
    suspend fun deleteAllSessions()

    @Query("DELETE FROM test_sessions WHERE id = :sessionId")
    suspend fun deleteSession(sessionId: String)

    @Query("DELETE FROM test_questions WHERE id = :questionId")
    suspend fun deleteQuestion(questionId: String)

    @Query("DELETE FROM test_questions WHERE sessionId = :sessionId")
    suspend fun deleteQuestionsForSession(sessionId: String)

    @Query(
        "DELETE FROM test_questions WHERE sessionId = :sessionId " +
            "AND vocabularyId = :vocabularyId AND isCorrect IS NULL"
    )
    suspend fun deleteUnansweredForVocabulary(sessionId: String, vocabularyId: String)

    @Query("DELETE FROM test_questions WHERE sessionId NOT IN (SELECT id FROM test_sessions)")
    suspend fun deleteOrphanQuestions()

    @Query("SELECT COUNT(*) FROM test_sessions WHERE status = 'COMPLETED' AND testType != 'GROUP_QUIZ'")
    fun observeCompletedTestCount(): Flow<Int>
}
