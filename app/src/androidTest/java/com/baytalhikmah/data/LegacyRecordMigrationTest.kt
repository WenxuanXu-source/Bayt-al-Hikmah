package com.baytalhikmah.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.baytalhikmah.data.local.AppDatabase
import com.baytalhikmah.data.local.ReadingProgressEntity
import com.baytalhikmah.data.local.VocabularyEntity
import com.baytalhikmah.data.model.Gender
import com.baytalhikmah.data.model.LearningPhase
import com.baytalhikmah.data.model.LearningStatus
import com.baytalhikmah.data.model.NewLearningStatus
import com.baytalhikmah.data.model.OverallStatus
import com.baytalhikmah.data.model.Pos
import com.baytalhikmah.data.model.PostTestStatus
import com.baytalhikmah.data.model.PreliminaryStatus
import com.baytalhikmah.data.model.ReadingStatus
import com.baytalhikmah.data.model.TestStatus
import com.baytalhikmah.data.model.TestType
import com.baytalhikmah.data.model.VocabBook
import com.baytalhikmah.domain.SearchNormalizer
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LegacyRecordMigrationTest {
    private lateinit var context: Context
    private val databaseName = "legacy-record-migration.db"

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        context.deleteDatabase(databaseName)
    }

    @After
    fun tearDown() {
        context.deleteDatabase(databaseName)
    }

    @Test
    fun migrationReattachesLearningStateAndRecomputesDailyTasks() = runBlocking {
        seedVersionOne()
        migrateToCurrentVersion()
        val database = Room.databaseBuilder(context, AppDatabase::class.java, databaseName)
            .addMigrations(
                AppDatabase.MIGRATION_1_2,
                AppDatabase.MIGRATION_2_3,
                AppDatabase.MIGRATION_3_4,
                AppDatabase.MIGRATION_4_5
            )
            .build()
        try {
            val migrator = LegacyRecordMigrator(database)
            val keys = migrator.readKeys()
            assertEquals(setOf("grc-001", "grc-002", "grc-003"), keys.map { it.oldId }.toSet())

            val words = listOf(
                vocabulary("grc-100", "λόγος", Pos.NOUN, VocabBook.BASIC, 10),
                vocabulary("grc-200", "λόγος", Pos.NOUN, VocabBook.ADVANCED, 20)
            )
            database.vocabularyDao().upsertAll(words)
            assertEquals(2, migrator.reattach(keys, words))

            val record = database.learningDao().record("grc-100")
            assertTrue(record != null)
            assertEquals(LearningStatus.LEARNED, record!!.status)
            assertEquals("2023-12-01", record.introducedOn)
            assertEquals("2024-03-01", record.lastReviewedOn)
            assertEquals(7, record.totalAttempts)
            assertEquals(5, record.correctAttempts)
            assertEquals(2, record.incorrectAttempts)
            assertTrue(record.difficult)
            assertNull(database.learningDao().record("grc-001"))
            assertNull(database.learningDao().record("grc-002"))
            assertNull(database.learningDao().record("grc-003"))
            assertEquals(1, database.learningDao().allRecords().count { it.languageCode == "grc" })

            val saved = database.learningDao().savedWords("grc").single()
            assertEquals("grc-100", saved.vocabularyId)
            assertEquals("2024-01-01", saved.savedOn)

            val test = database.testDao().session("test-old")
            assertTrue(test != null)
            assertEquals(TestStatus.COMPLETED, test!!.status)
            assertEquals(listOf("grc-100"), test.vocabularyIds)
            assertEquals(1, test.totalVocabularyItems)
            assertEquals(1, test.firstAttemptTotal)
            assertEquals(1, test.firstAttemptCorrect)
            assertEquals(100.0, test.scorePercent!!, 0.0001)
            assertEquals(true, test.passed)
            assertEquals(0, test.currentGroup)
            assertEquals(1, test.currentQuestion)
            assertEquals(1, database.testDao().questions("test-old").size)
            assertNull(database.testDao().session("test-empty"))
            assertEquals(0, database.testDao().allQuestions().count { it.sessionId == "test-empty" })

            val session = database.learningDao().learningSession("learn-old")
            assertTrue(session != null)
            assertEquals(listOf("grc-100"), session!!.vocabularyIds)
            assertEquals(1, session.targetCount)
            assertEquals(listOf("grc-100"), session.passedIds)
            assertEquals(LearningPhase.COMPLETE, session.phase)

            val task = database.learningDao().dailyTaskById("task-old")
            assertTrue(task != null)
            assertEquals(1, task!!.plannedNewWords)
            assertEquals(1, task.completedNewWords)
            assertEquals(NewLearningStatus.COMPLETED, task.newLearningStatus)
            assertEquals(PostTestStatus.PENDING, task.postTestStatus)
            assertEquals(PreliminaryStatus.PASSED, task.preliminaryStatus)
            assertEquals(OverallStatus.NEW_WORDS_DONE, task.overallStatus)

            migrator.clearKeys(setOf("grc"))
            assertTrue(migrator.readKeys().isEmpty())
        } finally {
            database.close()
        }
    }

    @Test
    fun readingProgressStartsReadingAndOnlyFinishesOnAPerfectAttempt() = runBlocking {
        val database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        try {
            val repository = DictionaryRepository(database)
            assertNull(database.readingDao().progress("herodotus"))

            repository.startReading("herodotus", 1_000L)
            var progress = database.readingDao().progress("herodotus")!!
            assertEquals(ReadingStatus.READING, progress.status)
            assertEquals(1_000L, progress.startedAt)
            assertNull(progress.finishedAt)
            assertEquals(0, progress.attempts)

            repository.recordReadingTest("herodotus", correct = 1, total = 2, now = 2_000L)
            progress = database.readingDao().progress("herodotus")!!
            assertEquals(ReadingStatus.READING, progress.status)
            assertEquals(1, progress.attempts)
            assertEquals(1, progress.lastCorrect)
            assertEquals(2, progress.lastTotal)
            assertEquals(1, progress.bestCorrect)
            assertNull(progress.finishedAt)

            repository.recordReadingTest("herodotus", correct = 2, total = 2, now = 3_000L)
            progress = database.readingDao().progress("herodotus")!!
            assertEquals(ReadingStatus.FINISHED, progress.status)
            assertEquals(2, progress.attempts)
            assertEquals(2, progress.bestCorrect)
            assertEquals(3_000L, progress.finishedAt)

            repository.recordReadingTest("herodotus", correct = 0, total = 2, now = 4_000L)
            progress = database.readingDao().progress("herodotus")!!
            assertEquals(ReadingStatus.FINISHED, progress.status)
            assertEquals(3, progress.attempts)
            assertEquals(3_000L, progress.finishedAt)
            assertEquals(2, progress.bestCorrect)
        } finally {
            database.close()
        }
    }

    @Test
    fun readingProgressMigratesFromVersionThree() = runBlocking {
        val helper = migrationHelper()
        helper.createDatabase(databaseName, 3).close()
        helper.runMigrationsAndValidate(
            databaseName,
            5,
            true,
            AppDatabase.MIGRATION_3_4,
            AppDatabase.MIGRATION_4_5
        ).close()
        val database = Room.databaseBuilder(context, AppDatabase::class.java, databaseName)
            .addMigrations(AppDatabase.MIGRATION_3_4, AppDatabase.MIGRATION_4_5)
            .build()
        try {
            database.readingDao().upsertProgress(
                ReadingProgressEntity(
                    workId = "john",
                    status = ReadingStatus.READING,
                    startedAt = 10L,
                    finishedAt = null,
                    lastTestedAt = null,
                    attempts = 0,
                    lastCorrect = null,
                    lastTotal = null,
                    bestCorrect = 0
                )
            )
            assertEquals(ReadingStatus.READING, database.readingDao().progress("john")!!.status)
        } finally {
            database.close()
        }
    }

    private fun seedVersionOne() {
        val helper = migrationHelper()
        val database = helper.createDatabase(databaseName, 1)
        try {
            listOf("grc-001", "grc-002").forEach {
                database.insertVocabulary(it, "λόγος", Pos.NOUN)
            }
            database.insertVocabulary("grc-003", "ἀρχαῖος", Pos.ADJECTIVE)

            database.insertRecord(
                id = "grc-001",
                status = LearningStatus.IN_PROGRESS,
                introducedOn = "2024-01-05",
                firstLearnedOn = null,
                lastReviewedOn = "2024-02-01",
                totalAttempts = 2,
                correctAttempts = 1,
                incorrectAttempts = 1,
                difficult = true
            )
            database.insertRecord(
                id = "grc-002",
                status = LearningStatus.LEARNED,
                introducedOn = "2023-12-01",
                firstLearnedOn = "2023-12-02",
                lastReviewedOn = "2024-03-01",
                totalAttempts = 5,
                correctAttempts = 4,
                incorrectAttempts = 1,
                difficult = false
            )
            database.insertRecord(
                id = "grc-003",
                status = LearningStatus.LEARNED,
                introducedOn = "2024-01-01",
                firstLearnedOn = "2024-01-01",
                lastReviewedOn = "2024-01-01",
                totalAttempts = 1,
                correctAttempts = 1,
                incorrectAttempts = 0,
                difficult = false
            )

            database.insertValues(
                "saved_words",
                "vocabularyId" to "grc-001",
                "languageCode" to "grc",
                "savedOn" to "2024-02-15"
            )
            database.insertValues(
                "saved_words",
                "vocabularyId" to "grc-002",
                "languageCode" to "grc",
                "savedOn" to "2024-01-01"
            )
            database.insertValues(
                "saved_words",
                "vocabularyId" to "grc-003",
                "languageCode" to "grc",
                "savedOn" to "2024-01-10"
            )

            database.insertLearningSession()
            database.insertDailyTask()
            database.insertTestSession(
                id = "test-old",
                testType = TestType.PRELIMINARY,
                status = TestStatus.COMPLETED,
                vocabularyIds = """["grc-001","grc-003"]""",
                totalItems = 2,
                currentGroup = 1,
                firstCorrect = 1,
                firstTotal = 2,
                score = 50.0,
                passed = 1
            )
            database.insertQuestion(
                id = "q1",
                sessionId = "test-old",
                vocabularyId = "grc-001",
                selectedIndex = 0,
                isCorrect = 1,
                groupIndex = 0
            )
            database.insertQuestion(
                id = "q2",
                sessionId = "test-old",
                vocabularyId = "grc-003",
                selectedIndex = null,
                isCorrect = null,
                groupIndex = 1
            )
            database.insertTestSession(
                id = "test-empty",
                testType = TestType.SAVED_WORDS,
                status = TestStatus.IN_PROGRESS,
                vocabularyIds = """["grc-003"]""",
                totalItems = 1,
                currentGroup = 0,
                firstCorrect = 0,
                firstTotal = 0,
                score = null,
                passed = null
            )
            database.insertQuestion(
                id = "q3",
                sessionId = "test-empty",
                vocabularyId = "grc-003",
                selectedIndex = null,
                isCorrect = null,
                groupIndex = 0
            )
        } finally {
            database.close()
        }
    }

    private fun migrateToCurrentVersion() {
        migrationHelper()
            .runMigrationsAndValidate(
                databaseName,
                5,
                true,
                AppDatabase.MIGRATION_1_2,
                AppDatabase.MIGRATION_2_3,
                AppDatabase.MIGRATION_3_4,
                AppDatabase.MIGRATION_4_5
            )
            .close()
    }

    private fun migrationHelper() = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java
    )

    private fun SupportSQLiteDatabase.insertVocabulary(id: String, headword: String, pos: Pos) {
        val values = ContentValues().apply {
            put("id", id)
            put("languageCode", "grc")
            put("headword", headword)
            put("normalizedHeadword", SearchNormalizer.normalize(headword))
            put("pos", pos.name)
            putNull("gender")
            put("definitions", """["a definition"]""")
            put("grammaticalClass", "")
            put("inflectionType", "")
            put("principalParts", "[]")
            put("irregularForms", "[]")
            putNull("etymology")
            putNull("etymologyNote")
            putNull("sourceReference")
            putNull("frequencyRank")
            put("datasetVersion", 8)
        }
        insert("vocabulary", SQLiteDatabase.CONFLICT_ABORT, values)
    }

    private fun SupportSQLiteDatabase.insertRecord(
        id: String,
        status: LearningStatus,
        introducedOn: String?,
        firstLearnedOn: String?,
        lastReviewedOn: String?,
        totalAttempts: Int,
        correctAttempts: Int,
        incorrectAttempts: Int,
        difficult: Boolean
    ) {
        insertValues(
            "learning_records",
            "vocabularyId" to id,
            "languageCode" to "grc",
            "status" to status.name,
            "introducedOn" to introducedOn,
            "firstLearnedOn" to firstLearnedOn,
            "lastReviewedOn" to lastReviewedOn,
            "totalAttempts" to totalAttempts,
            "correctAttempts" to correctAttempts,
            "incorrectAttempts" to incorrectAttempts,
            "difficult" to if (difficult) 1 else 0
        )
    }

    private fun SupportSQLiteDatabase.insertLearningSession() {
        insertValues(
            "learning_sessions",
            "id" to "learn-old",
            "languageCode" to "grc",
            "learningDate" to "2026-01-01",
            "vocabularyIds" to """["grc-001","grc-003"]""",
            "targetCount" to 2,
            "cardCursor" to 1,
            "quizRound" to 1,
            "passedIds" to """["grc-001"]""",
            "pendingQuizIds" to "[]",
            "phase" to LearningPhase.COMPLETE.name,
            "activeQuizSessionId" to null,
            "completedOn" to "2026-01-01",
            "createdAt" to 1_000L,
            "updatedAt" to 2_000L
        )
    }

    private fun SupportSQLiteDatabase.insertDailyTask() {
        insertValues(
            "daily_tasks",
            "id" to "task-old",
            "languageCode" to "grc",
            "learningDate" to "2026-01-01",
            "plannedNewWords" to 2,
            "completedNewWords" to 1,
            "preliminaryStatus" to PreliminaryStatus.PENDING.name,
            "newLearningStatus" to NewLearningStatus.IN_PROGRESS.name,
            "postTestStatus" to PostTestStatus.NOT_READY.name,
            "overallStatus" to OverallStatus.LEARNING.name,
            "createdAt" to 1_000L,
            "updatedAt" to 2_000L
        )
    }

    private fun SupportSQLiteDatabase.insertTestSession(
        id: String,
        testType: TestType,
        status: TestStatus,
        vocabularyIds: String,
        totalItems: Int,
        currentGroup: Int,
        firstCorrect: Int,
        firstTotal: Int,
        score: Double?,
        passed: Int?
    ) {
        insertValues(
            "test_sessions",
            "id" to id,
            "languageCode" to "grc",
            "testType" to testType.name,
            "sessionDate" to "2026-01-01",
            "createdAt" to 1_500L,
            "completedAt" to if (status == TestStatus.COMPLETED) 2_500L else null,
            "totalVocabularyItems" to totalItems,
            "currentGroup" to currentGroup,
            "currentQuestion" to 0,
            "status" to status.name,
            "scorePercent" to score,
            "passed" to passed,
            "dailyTaskId" to if (id == "test-old") "task-old" else null,
            "learningSessionId" to null,
            "vocabularyIds" to vocabularyIds,
            "firstAttemptCorrect" to firstCorrect,
            "firstAttemptTotal" to firstTotal,
            "passingThreshold" to if (testType == TestType.PRELIMINARY) 50 else null
        )
    }

    private fun SupportSQLiteDatabase.insertQuestion(
        id: String,
        sessionId: String,
        vocabularyId: String,
        selectedIndex: Int?,
        isCorrect: Int?,
        groupIndex: Int
    ) {
        insertValues(
            "test_questions",
            "id" to id,
            "sessionId" to sessionId,
            "vocabularyId" to vocabularyId,
            "questionType" to "SEMANTIC",
            "prompt" to "Choose the meaning",
            "contextTranslation" to null,
            "highlightText" to null,
            "options" to """["A","B","C","D"]""",
            "correctIndex" to 0,
            "selectedIndex" to selectedIndex,
            "isCorrect" to isCorrect,
            "retryCount" to 0,
            "isReviewCopy" to 0,
            "groupIndex" to groupIndex,
            "orderInGroup" to 0
        )
    }

    private fun SupportSQLiteDatabase.insertValues(
        table: String,
        vararg values: Pair<String, Any?>
    ) {
        val content = ContentValues()
        values.forEach { (key, value) ->
            when (value) {
                null -> content.putNull(key)
                is Int -> content.put(key, value)
                is Long -> content.put(key, value)
                is Double -> content.put(key, value)
                is String -> content.put(key, value)
                is Boolean -> content.put(key, if (value) 1 else 0)
                else -> error("Unsupported test value for $key")
            }
        }
        insert(table, SQLiteDatabase.CONFLICT_ABORT, content)
    }

    private fun vocabulary(
        id: String,
        headword: String,
        pos: Pos,
        book: VocabBook,
        rank: Int
    ) = VocabularyEntity(
        id = id,
        languageCode = "grc",
        headword = headword,
        normalizedHeadword = SearchNormalizer.normalize(headword),
        pos = pos,
        gender = Gender.MASCULINE,
        book = book,
        definitions = listOf("a definition"),
        grammaticalClass = "",
        inflectionType = "",
        principalParts = emptyList(),
        irregularForms = emptyList(),
        etymology = null,
        etymologyNote = null,
        sourceReference = "test",
        frequencyRank = rank,
        datasetVersion = 9
    )
}
