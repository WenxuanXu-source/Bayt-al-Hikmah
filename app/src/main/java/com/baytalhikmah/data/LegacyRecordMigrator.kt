package com.baytalhikmah.data

import android.util.Log
import androidx.room.withTransaction
import com.baytalhikmah.data.local.AppDatabase
import com.baytalhikmah.data.local.LearningRecordEntity
import com.baytalhikmah.data.local.LearningSessionEntity
import com.baytalhikmah.data.local.SavedWordEntity
import com.baytalhikmah.data.local.TestSessionEntity
import com.baytalhikmah.data.local.VocabularyEntity
import com.baytalhikmah.data.model.LearningPhase
import com.baytalhikmah.data.model.LearningStatus
import com.baytalhikmah.data.model.NewLearningStatus
import com.baytalhikmah.data.model.OverallStatus
import com.baytalhikmah.data.model.PostTestStatus
import com.baytalhikmah.data.model.PreliminaryStatus
import com.baytalhikmah.data.model.TestStatus
import com.baytalhikmah.data.model.TestType
import com.baytalhikmah.data.model.VocabBook
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LegacyRecordMigrator(private val database: AppDatabase) {
    data class LegacyKey(
        val oldId: String,
        val languageCode: String,
        val normalizedHeadword: String,
        val pos: String
    ) {
        val matchKey = "$languageCode|$normalizedHeadword|$pos"
    }

    private val vocabularyDao = database.vocabularyDao()
    private val learningDao = database.learningDao()
    private val testDao = database.testDao()

    suspend fun readKeys(): List<LegacyKey> = withContext(Dispatchers.IO) {
        val db = database.openHelper.writableDatabase
        val present = db.query(
            "SELECT 1 FROM sqlite_master WHERE type='table' AND name='legacy_word_keys'"
        ).use { it.moveToFirst() }
        if (!present) return@withContext emptyList()
        db.query(
            "SELECT vocabularyId, languageCode, normalizedHeadword, pos FROM legacy_word_keys"
        ).use { cursor ->
            buildList {
                while (cursor.moveToNext()) {
                    add(
                        LegacyKey(
                            cursor.getString(0),
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getString(3)
                        )
                    )
                }
            }
        }
    }

    suspend fun reattach(keys: List<LegacyKey>, installed: List<VocabularyEntity>): Int =
        withContext(Dispatchers.IO) {
            database.withTransaction { reattachWithinTransaction(keys, installed) }
        }

    internal suspend fun reattachWithinTransaction(
        keys: List<LegacyKey>,
        installed: List<VocabularyEntity>
    ): Int {
        if (keys.isEmpty() || installed.isEmpty()) return 0
        val language = installed.first().languageCode
        val installedIds = installed.mapTo(HashSet()) { it.id }
        val byContent = installed.groupBy {
            "${it.languageCode}|${it.normalizedHeadword}|${it.pos.name}"
        }.mapValues { (_, candidates) ->
            candidates.minWith(
                compareBy<VocabularyEntity> { it.book.ordinal }
                    .thenBy { it.frequencyRank ?: Int.MAX_VALUE }
                    .thenBy { it.id }
            )
        }
        val target = keys.associate { key ->
            key.oldId to if (key.oldId in installedIds) key.oldId else byContent[key.matchKey]?.id
        }
        fun resolve(id: String): String? = target[id] ?: id.takeIf { it in installedIds }

        var moved = 0
        val records = learningDao.allRecords().filter { it.languageCode == language }
        records.forEach { record ->
            val next = resolve(record.vocabularyId)
            if (next != null && next != record.vocabularyId) moved++
        }
        val mergedRecords = records.mapNotNull { record ->
            resolve(record.vocabularyId)?.let { it to record }
        }.groupBy({ it.first }, { it.second }).map { (targetId, records) ->
            mergeRecords(targetId, records)
        }
        learningDao.upsertRecords(mergedRecords)
        records.filter { it.vocabularyId !in installedIds }.forEach {
            learningDao.deleteRecord(it.vocabularyId)
        }

        val saved = learningDao.allSaved().filter { it.languageCode == language }
        val mergedSaved = saved.mapNotNull { word ->
            resolve(word.vocabularyId)?.let { it to word }
        }.groupBy({ it.first }, { it.second }).map { (targetId, words) ->
            SavedWordEntity(
                vocabularyId = targetId,
                languageCode = words.first().languageCode,
                savedOn = words.minOf { it.savedOn }
            )
        }
        learningDao.saveWords(mergedSaved)
        saved.filter { it.vocabularyId !in installedIds }.forEach {
            learningDao.removeSavedWord(it.vocabularyId)
        }

        val testSessions = testDao.allSessions().filter { it.languageCode == language }
        val testSessionIds = testSessions.mapTo(HashSet()) { it.id }
        val questionsBySession = testDao.allQuestions()
            .filter { it.sessionId in testSessionIds }
            .groupBy { it.sessionId }
        val survivingTests = mutableMapOf<String, TestSessionEntity>()
        testSessions.forEach { session ->
            val questions = questionsBySession[session.id].orEmpty().mapNotNull { question ->
                val next = resolve(question.vocabularyId)
                if (next == null) {
                    testDao.deleteQuestion(question.id)
                    null
                } else {
                    question.copy(vocabularyId = next).also { testDao.upsertQuestion(it) }
                }
            }
            if (questions.isEmpty()) {
                testDao.deleteQuestionsForSession(session.id)
                testDao.deleteSession(session.id)
                return@forEach
            }
            val ids = (session.vocabularyIds.mapNotNull(::resolve) + questions.map { it.vocabularyId })
                .distinct()
            val firstAttemptTotal = questions.count { it.retryCount == 0 }
            val firstAttemptCorrect = questions.count { it.retryCount == 0 && it.isCorrect == true }
            val score = if (firstAttemptTotal > 0) {
                firstAttemptCorrect * 100.0 / firstAttemptTotal
            } else {
                session.scorePercent
            }
            val completed = session.status != TestStatus.IN_PROGRESS
            val passed = if (completed && session.testType == TestType.PRELIMINARY &&
                session.passingThreshold != null && firstAttemptTotal > 0 && score != null
            ) {
                score >= session.passingThreshold
            } else {
                session.passed
            }
            val nextGroup = questions.filter { it.isCorrect == null }.minOfOrNull { it.groupIndex }
                ?: session.currentGroup.coerceAtMost(questions.maxOf { it.groupIndex })
            val updated = session.copy(
                totalVocabularyItems = ids.size,
                currentGroup = nextGroup,
                currentQuestion = questions.count {
                    it.groupIndex == nextGroup && it.selectedIndex != null
                },
                vocabularyIds = ids,
                firstAttemptCorrect = firstAttemptCorrect,
                firstAttemptTotal = firstAttemptTotal,
                scorePercent = if (completed) score else null,
                passed = if (completed) passed else null,
                status = if (completed && session.testType == TestType.PRELIMINARY && passed == false) {
                    TestStatus.FAILED
                } else {
                    session.status
                },
                completedAt = session.completedAt.takeIf { completed }
            )
            testDao.upsertSession(updated)
            survivingTests[updated.id] = updated
        }

        val learningSessions = learningDao.allLearningSessions().filter { it.languageCode == language }
        val survivingLearningSessions = mutableMapOf<String, LearningSessionEntity>()
        learningSessions.forEach { session ->
            val ids = session.vocabularyIds.mapNotNull(::resolve).distinct()
            if (ids.isEmpty()) {
                learningDao.deleteLearningSession(session.id)
                return@forEach
            }
            val passed = session.passedIds.mapNotNull(::resolve).filter { it in ids }.distinct()
            val pending = session.pendingQuizIds.mapNotNull(::resolve)
                .filter { it in ids && it !in passed }
                .distinct()
            val active = session.activeQuizSessionId?.takeIf { id ->
                survivingTests[id]?.let {
                    it.status == TestStatus.IN_PROGRESS && it.learningSessionId == session.id
                } == true
            }
            val complete = session.phase == LearningPhase.COMPLETE && ids.all { it in passed }
            val phase = when {
                complete -> LearningPhase.COMPLETE
                active != null -> LearningPhase.QUIZ
                else -> LearningPhase.CARDS
            }
            val updated = session.copy(
                vocabularyIds = ids,
                targetCount = ids.size,
                cardCursor = if (session.phase == LearningPhase.QUIZ && phase == LearningPhase.CARDS) {
                    ids.lastIndex
                } else {
                    session.cardCursor.coerceIn(0, ids.lastIndex)
                },
                passedIds = passed,
                pendingQuizIds = if (complete) emptyList() else pending,
                phase = phase,
                activeQuizSessionId = active,
                completedOn = session.completedOn.takeIf { complete }
            )
            learningDao.upsertLearningSession(updated)
            survivingLearningSessions[updated.id] = updated
        }
        survivingTests.values.filter { it.learningSessionId !in survivingLearningSessions }.forEach {
            testDao.upsertSession(it.copy(learningSessionId = null))
        }
        testDao.deleteOrphanQuestions()

        val sessionsByTask = survivingLearningSessions.values.associateBy {
            "${it.languageCode}|${it.book.name}|${it.learningDate}"
        }
        learningDao.allDailyTasks().filter { it.languageCode == language }.forEach { task ->
            val session = sessionsByTask["${task.languageCode}|${task.book.name}|${task.learningDate}"]
            val tests = survivingTests.values.filter { it.dailyTaskId == task.id }
            if (session == null) {
                if (task.newLearningStatus == NewLearningStatus.IN_PROGRESS) {
                    learningDao.upsertDailyTask(
                        task.copy(
                            completedNewWords = 0,
                            newLearningStatus = NewLearningStatus.NOT_STARTED,
                            postTestStatus = PostTestStatus.NOT_READY,
                            overallStatus = OverallStatus.READY
                        )
                    )
                }
                return@forEach
            }
            val preliminary = tests.filter { it.testType == TestType.PRELIMINARY }.maxByOrNull { it.createdAt }
            val postTest = tests.filter { it.testType == TestType.POST_LEARNING }.maxByOrNull { it.createdAt }
            val newStatus = if (session.phase == LearningPhase.COMPLETE) {
                NewLearningStatus.COMPLETED
            } else {
                NewLearningStatus.IN_PROGRESS
            }
            val postStatus = when {
                newStatus != NewLearningStatus.COMPLETED -> PostTestStatus.NOT_READY
                postTest?.status == TestStatus.COMPLETED -> PostTestStatus.COMPLETED
                else -> PostTestStatus.PENDING
            }
            val preliminaryStatus = when {
                preliminary?.passed == true -> PreliminaryStatus.PASSED
                preliminary?.passed == false -> PreliminaryStatus.FAILED
                preliminary != null -> PreliminaryStatus.PENDING
                else -> task.preliminaryStatus
            }
            val overall = when {
                preliminaryStatus == PreliminaryStatus.FAILED -> OverallStatus.PRELIMINARY_REQUIRED
                newStatus == NewLearningStatus.COMPLETED && postStatus == PostTestStatus.COMPLETED ->
                    OverallStatus.COMPLETED
                newStatus == NewLearningStatus.COMPLETED -> OverallStatus.NEW_WORDS_DONE
                else -> OverallStatus.LEARNING
            }
            learningDao.upsertDailyTask(
                task.copy(
                    plannedNewWords = session.targetCount,
                    completedNewWords = session.passedIds.size,
                    preliminaryStatus = preliminaryStatus,
                    newLearningStatus = newStatus,
                    postTestStatus = postStatus,
                    overallStatus = overall
                )
            )
        }
        Log.i(TAG, "Re-attached $moved learning records for $language")
        return moved
    }

    suspend fun clearKeys(languages: Set<String>) = withContext(Dispatchers.IO) {
        if (languages.isEmpty()) return@withContext
        database.withTransaction {
            val db = database.openHelper.writableDatabase
            val present = db.query(
                "SELECT 1 FROM sqlite_master WHERE type='table' AND name='legacy_word_keys'"
            ).use { it.moveToFirst() }
            if (!present) return@withTransaction
            languages.chunked(CHUNK).forEach { chunk ->
                val placeholders = chunk.joinToString(",") { "?" }
                db.execSQL(
                    "DELETE FROM legacy_word_keys WHERE languageCode IN ($placeholders)",
                    chunk.toTypedArray()
                )
            }
        }
    }

    private fun mergeRecords(targetId: String, records: List<LearningRecordEntity>): LearningRecordEntity {
        val first = records.first()
        return first.copy(
            vocabularyId = targetId,
            status = records.maxOf { it.status },
            introducedOn = records.mapNotNull { it.introducedOn }.minOrNull(),
            firstLearnedOn = records.mapNotNull { it.firstLearnedOn }.minOrNull(),
            lastReviewedOn = records.mapNotNull { it.lastReviewedOn }.maxOrNull(),
            totalAttempts = records.sumOf { it.totalAttempts },
            correctAttempts = records.sumOf { it.correctAttempts },
            incorrectAttempts = records.sumOf { it.incorrectAttempts },
            difficult = records.any { it.difficult }
        )
    }

    private companion object {
        const val TAG = "LegacyRecordMigrator"
        const val CHUNK = 400
    }
}
