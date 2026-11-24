package com.baytalhikmah.data

import com.baytalhikmah.data.local.DailyTaskEntity
import com.baytalhikmah.data.local.LanguageEntity
import com.baytalhikmah.data.local.LearningRecordEntity
import com.baytalhikmah.data.local.LearningSessionEntity
import com.baytalhikmah.data.local.TestQuestionEntity
import com.baytalhikmah.data.local.TestSessionEntity
import com.baytalhikmah.data.model.LearningPhase
import com.baytalhikmah.data.model.LearningStatus
import com.baytalhikmah.data.model.NewLearningStatus
import com.baytalhikmah.data.model.OverallStatus
import com.baytalhikmah.data.model.PostTestStatus
import com.baytalhikmah.data.model.PreliminaryStatus
import com.baytalhikmah.data.model.QuestionType
import com.baytalhikmah.data.model.TestStatus
import com.baytalhikmah.data.model.TestType
import com.baytalhikmah.domain.QuestionFactory
import com.baytalhikmah.domain.QuestionDraft
import com.baytalhikmah.domain.VocabBundle
import androidx.room.withTransaction
import kotlinx.coroutines.flow.first
import java.util.UUID

data class DailyState(
    val language: LanguageEntity,
    val task: DailyTaskEntity,
    val learnedCount: Int,
    val totalCount: Int,
    val savedCount: Int,
    val session: LearningSessionEntity?,
    val activeQuiz: TestSessionEntity?,
    val preliminarySession: TestSessionEntity?,
    val postTestSession: TestSessionEntity?,
    val unfinishedPast: List<LearningSessionEntity>
) {
    val unlearnedCount: Int get() = (totalCount - learnedCount).coerceAtLeast(0)
}

data class TestProgress(
    val groupIndex: Int,
    val groupNumber: Int,
    val positionInGroup: Int,
    val questionsInGroup: Int,
    val totalQuestions: Int,
    val answered: Int,
    val correct: Int,
    val incorrect: Int,
    val remaining: Int
)

data class SessionSnapshot(
    val session: TestSessionEntity,
    val language: LanguageEntity,
    val question: TestQuestionEntity?,
    val secondaryQuestion: TestQuestionEntity?,
    val bundle: VocabBundle?,
    val progress: TestProgress
) {
    val finished: Boolean get() = session.status != TestStatus.IN_PROGRESS
}

data class AnswerOutcome(
    val answered: TestQuestionEntity,
    val correct: Boolean,
    val session: TestSessionEntity,
    val bundle: VocabBundle?,
    val result: SessionResult?
)

data class SessionResult(
    val session: TestSessionEntity,
    val language: LanguageEntity,
    val missedWords: List<VocabBundle>,
    val correct: Int,
    val incorrect: Int,
    val totalQuestions: Int,
    val firstAttemptScore: Double
)

sealed class StartOutcome {
    data class Started(val sessionId: String) : StartOutcome()
    data object NotRequired : StartOutcome()
    data class Failed(val message: String) : StartOutcome()
}

class StudyRepository(
    private val database: com.baytalhikmah.data.local.AppDatabase,
    private val dictionary: DictionaryRepository,
    private val settingsRepository: SettingsRepository,
    private val dateProvider: com.baytalhikmah.domain.DateProvider,
    private val random: kotlin.random.Random = kotlin.random.Random.Default
) {
    private val languageDao = database.languageDao()
    private val learningDao = database.learningDao()
    private val vocabularyDao = database.vocabularyDao()
    private val testDao = database.testDao()

    private fun today(): String = dateProvider.today().toString()

    // ---------------------------------------------------------------- daily state

    suspend fun ensureDailyTask(language: String, date: String = today()): DailyTaskEntity {
        val learned = learningDao.learnedCount(language)
        val existing = learningDao.dailyTask(language, date)
        if (existing == null) {
            val requiresReview = learned >= PRELIMINARY_MIN_WORDS
            val task = DailyTaskEntity(
                id = taskId(language, date),
                languageCode = language,
                learningDate = date,
                plannedNewWords = 0,
                completedNewWords = 0,
                preliminaryStatus = if (requiresReview) PreliminaryStatus.PENDING else PreliminaryStatus.NOT_REQUIRED,
                newLearningStatus = NewLearningStatus.NOT_STARTED,
                postTestStatus = PostTestStatus.NOT_READY,
                overallStatus = if (requiresReview) OverallStatus.PRELIMINARY_REQUIRED else OverallStatus.READY,
                createdAt = dateProvider.nowMillis(),
                updatedAt = dateProvider.nowMillis()
            )
            learningDao.upsertDailyTask(task)
            return task
        }
        var task = existing
        if (learned >= PRELIMINARY_MIN_WORDS &&
            task.preliminaryStatus == PreliminaryStatus.NOT_REQUIRED &&
            task.newLearningStatus == NewLearningStatus.NOT_STARTED
        ) {
            task = task.copy(preliminaryStatus = PreliminaryStatus.PENDING)
        }
        val derived = deriveOverallStatus(task, learned)
        if (task.overallStatus != derived) {
            task = task.copy(overallStatus = derived, updatedAt = dateProvider.nowMillis())
        }
        if (task != existing) learningDao.upsertDailyTask(task)
        return task
    }

    private fun deriveOverallStatus(task: DailyTaskEntity, learned: Int): OverallStatus = when {
        task.postTestStatus == PostTestStatus.COMPLETED -> OverallStatus.COMPLETED
        task.newLearningStatus == NewLearningStatus.COMPLETED -> OverallStatus.NEW_WORDS_DONE
        task.newLearningStatus == NewLearningStatus.IN_PROGRESS -> OverallStatus.LEARNING
        learned >= PRELIMINARY_MIN_WORDS && task.preliminaryStatus != PreliminaryStatus.PASSED ->
            OverallStatus.PRELIMINARY_REQUIRED
        else -> OverallStatus.READY
    }

    suspend fun dailyState(languageCode: String, date: String = today()): DailyState? {
        val language = languageDao.get(languageCode) ?: return null
        val task = ensureDailyTask(languageCode, date)
        val session = learningDao.learningSessionForDate(languageCode, date)
        val reconciled = session?.let { reconcileLearningSession(it) }
        return DailyState(
            language = language,
            task = learningDao.dailyTask(languageCode, date) ?: task,
            learnedCount = learningDao.learnedCount(languageCode),
            totalCount = vocabularyDao.count(languageCode),
            savedCount = learningDao.savedCount(languageCode),
            session = reconciled,
            activeQuiz = reconciled?.activeQuizSessionId?.let { testDao.session(it) },
            preliminarySession = testDao.latestSession(languageCode, TestType.PRELIMINARY.name, date),
            postTestSession = testDao.latestForTask(task.id, TestType.POST_LEARNING.name)
                ?: testDao.latestSession(languageCode, TestType.POST_LEARNING.name, date),
            unfinishedPast = learningDao.unfinishedSessionsBefore(languageCode, date)
        )
    }

    suspend fun learningState(languageCode: String, date: String = today()): LearningState? {
        val language = languageDao.get(languageCode) ?: return null
        val state = dailyState(languageCode, date) ?: return null
        val session = state.session
        val bundle = if (session != null && session.phase == LearningPhase.CARDS && session.vocabularyIds.isNotEmpty()) {
            val index = session.cardCursor.coerceIn(0, session.vocabularyIds.lastIndex)
            dictionary.bundle(session.vocabularyIds[index])
        } else null
        val groupStart = ((session?.cardCursor ?: 0) / 10) * 10
        val groupEnd = minOf(groupStart + 10, session?.vocabularyIds?.size ?: 0)
        val groupNumber = groupStart / 10 + 1
        val groupPosition = (session?.cardCursor ?: 0) - groupStart + 1
        val groupSize = (groupEnd - groupStart).coerceAtLeast(0)
        val available = (state.totalCount - (state.learnedCount + learningDao.inProgressRecords(languageCode).size))
            .coerceAtLeast(0)
        return LearningState(
            daily = state,
            bundle = bundle,
            groupNumber = groupNumber,
            groupPosition = groupPosition,
            groupSize = groupSize,
            isLastCardOfGroup = session != null && session.phase == LearningPhase.CARDS && groupSize > 0 &&
                session.cardCursor == groupEnd - 1,
            availableNewWords = available
        )
    }

    suspend fun savedCountFor(language: String): Int = learningDao.savedCount(language)

    suspend fun statistics(language: String): LanguageStatistics {
        val records = learningDao.records(language)
        val learned = records.count { it.status == LearningStatus.LEARNED }
        val inProgress = records.count { it.status == LearningStatus.IN_PROGRESS }
        val difficult = records.count { it.difficult }
        val attempts = records.sumOf { it.totalAttempts }
        val correct = records.sumOf { it.correctAttempts }
        val completedDays = learningDao.completedDailyTasks(language).size
        return LanguageStatistics(
            languageCode = language,
            totalWords = vocabularyDao.count(language),
            learned = learned,
            inProgress = inProgress,
            difficult = difficult,
            saved = learningDao.savedCount(language),
            attempts = attempts,
            correctAttempts = correct,
            completedDays = completedDays
        )
    }

    suspend fun recentDailyTasks(language: String, limit: Int = 14): List<DailyTaskEntity> =
        learningDao.recentDailyTasks(language, limit)

    suspend fun completedTestCount(): Int = testDao.observeCompletedTestCount().first()

    // ------------------------------------------------------------- preliminary test

    suspend fun startPreliminaryTest(languageCode: String, date: String = today()): StartOutcome {
        val task = ensureDailyTask(languageCode, date)
        val learned = learningDao.learnedRecords(languageCode)
        if (learned.size < PRELIMINARY_MIN_WORDS) {
            if (task.preliminaryStatus != PreliminaryStatus.NOT_REQUIRED) {
                updateTask(task.id) { it.copy(preliminaryStatus = PreliminaryStatus.NOT_REQUIRED) }
            }
            return StartOutcome.NotRequired
        }
        if (task.preliminaryStatus == PreliminaryStatus.PASSED) return StartOutcome.NotRequired
        val existing = testDao.inProgressForDate(date).firstOrNull {
            it.languageCode == languageCode &&
                it.testType == TestType.PRELIMINARY &&
                it.dailyTaskId == task.id
        }
        if (existing != null) return StartOutcome.Started(existing.id)
        val threshold = settingsRepository.current().passingThreshold
        val words = learned.shuffled(random).take(PRELIMINARY_MIN_WORDS).map { it.vocabularyId }
        return try {
            val session = createTestSession(
                languageCode = languageCode,
                type = TestType.PRELIMINARY,
                vocabularyIds = words,
                date = date,
                dailyTaskId = task.id,
                learningSessionId = null,
                threshold = threshold
            )
            updateTask(task.id) {
                it.copy(
                    preliminaryStatus = PreliminaryStatus.PENDING,
                    overallStatus = OverallStatus.PRELIMINARY_REQUIRED,
                    updatedAt = dateProvider.nowMillis()
                )
            }
            StartOutcome.Started(session.id)
        } catch (error: IllegalStateException) {
            StartOutcome.Failed(error.message ?: "Unable to build the test.")
        }
    }

    // ---------------------------------------------------------------- new learning

    suspend fun startLearning(languageCode: String, target: Int, date: String = today()): Result<String> {
        val task = ensureDailyTask(languageCode, date)
        learningDao.learningSessionForDate(languageCode, date)?.let { return Result.success(it.id) }
        val learned = learningDao.learnedCount(languageCode)
        if (learned >= PRELIMINARY_MIN_WORDS && task.preliminaryStatus != PreliminaryStatus.PASSED) {
            return Result.failure(IllegalStateException("Pass the previous vocabulary test before learning new words."))
        }
        if (task.newLearningStatus == NewLearningStatus.COMPLETED) {
            return Result.failure(IllegalStateException("Today's new vocabulary is already complete."))
        }
        val selected = dictionary.nextUnintroduced(languageCode, target)
        if (selected.isEmpty()) {
            return Result.failure(IllegalStateException("No unlearned vocabulary remains in this dictionary."))
        }
        val language = languageDao.get(languageCode)
            ?: return Result.failure(IllegalStateException("Unknown language."))
        database.withTransaction {
            selected.forEach { word ->
                val current = learningDao.record(word.id)
                val record = if (current == null) {
                    LearningRecordEntity(
                        vocabularyId = word.id,
                        languageCode = language.code,
                        status = LearningStatus.IN_PROGRESS,
                        introducedOn = date,
                        firstLearnedOn = null,
                        lastReviewedOn = null,
                        totalAttempts = 0,
                        correctAttempts = 0,
                        incorrectAttempts = 0,
                        difficult = false
                    )
                } else {
                    current.copy(status = LearningStatus.IN_PROGRESS, introducedOn = current.introducedOn ?: date)
                }
                learningDao.upsertRecord(record)
            }
            val session = LearningSessionEntity(
                id = learningSessionId(languageCode, date),
                languageCode = languageCode,
                learningDate = date,
                vocabularyIds = selected.map { it.id },
                targetCount = selected.size,
                cardCursor = 0,
                quizRound = 0,
                passedIds = emptyList(),
                pendingQuizIds = emptyList(),
                phase = LearningPhase.CARDS,
                activeQuizSessionId = null,
                completedOn = null,
                createdAt = dateProvider.nowMillis(),
                updatedAt = dateProvider.nowMillis()
            )
            learningDao.upsertLearningSession(session)
            updateTask(task.id) {
                it.copy(
                    plannedNewWords = selected.size,
                    newLearningStatus = NewLearningStatus.IN_PROGRESS,
                    overallStatus = OverallStatus.LEARNING,
                    updatedAt = dateProvider.nowMillis()
                )
            }
        }
        return Result.success(learningSessionId(languageCode, date))
    }

    suspend fun updateCardCursor(learningSessionId: String, index: Int) {
        val session = learningDao.learningSession(learningSessionId) ?: return
        if (session.phase != LearningPhase.CARDS || session.vocabularyIds.isEmpty()) return
        val groupStart = (session.cardCursor / 10) * 10
        val groupEnd = minOf(groupStart + 10, session.vocabularyIds.size)
        val clamped = index.coerceIn(groupStart, groupEnd - 1)
        if (clamped != session.cardCursor) {
            learningDao.upsertLearningSession(
                session.copy(cardCursor = clamped, updatedAt = dateProvider.nowMillis())
            )
        }
    }

    suspend fun beginGroupQuiz(learningSessionId: String): Result<String> {
        val session = learningDao.learningSession(learningSessionId)
            ?: return Result.failure(IllegalStateException("Learning session not found."))
        session.activeQuizSessionId?.let { active ->
            testDao.session(active)?.takeIf { it.status == TestStatus.IN_PROGRESS }?.let {
                return Result.success(active)
            }
        }
        if (session.phase != LearningPhase.CARDS) {
            return Result.failure(IllegalStateException("The group quiz cannot start in the current phase."))
        }
        val groupStart = (session.cardCursor / 10) * 10
        val groupEnd = minOf(groupStart + 10, session.vocabularyIds.size)
        if (session.cardCursor != groupEnd - 1) {
            return Result.failure(IllegalStateException("Finish the group's cards before starting the quiz."))
        }
        val groupWords = session.vocabularyIds.subList(groupStart, groupEnd)
        val quizWords = (groupWords + session.pendingQuizIds).distinct()
        return try {
            val taskId = taskId(session.languageCode, session.learningDate)
            val quiz = createTestSession(
                languageCode = session.languageCode,
                type = TestType.GROUP_QUIZ,
                vocabularyIds = quizWords,
                date = session.learningDate,
                dailyTaskId = taskId,
                learningSessionId = session.id,
                threshold = null
            )
            learningDao.upsertLearningSession(
                session.copy(
                    phase = LearningPhase.QUIZ,
                    activeQuizSessionId = quiz.id,
                    updatedAt = dateProvider.nowMillis()
                )
            )
            Result.success(quiz.id)
        } catch (error: IllegalStateException) {
            Result.failure(IllegalStateException(error.message ?: "Unable to build the group quiz."))
        }
    }

    suspend fun ensurePostLearningTest(languageCode: String, date: String = today()): String? {
        val session = learningDao.learningSessionForDate(languageCode, date) ?: return null
        if (session.phase != LearningPhase.COMPLETE) return null
        val task = learningDao.dailyTask(languageCode, date) ?: return null
        val existing = testDao.latestForTask(task.id, TestType.POST_LEARNING.name)
        if (existing != null) return existing.id
        return try {
            val test = createTestSession(
                languageCode = languageCode,
                type = TestType.POST_LEARNING,
                vocabularyIds = session.vocabularyIds,
                date = date,
                dailyTaskId = task.id,
                learningSessionId = session.id,
                threshold = null
            )
            updateTask(task.id) {
                it.copy(
                    newLearningStatus = NewLearningStatus.COMPLETED,
                    postTestStatus = PostTestStatus.PENDING,
                    overallStatus = OverallStatus.NEW_WORDS_DONE,
                    completedNewWords = session.passedIds.size,
                    updatedAt = dateProvider.nowMillis()
                )
            }
            test.id
        } catch (error: IllegalStateException) {
            null
        }
    }

    // -------------------------------------------------------------------- tests

    suspend fun startRandomTest(languageCode: String, count: Int, date: String = today()): Result<String> {
        val learned = learningDao.learnedRecords(languageCode).map { it.vocabularyId }
        if (learned.size < 2) {
            return Result.failure(IllegalStateException("Learn at least two words before taking a random test."))
        }
        val size = count.coerceIn(1, learned.size - 1)
        val words = learned.shuffled(random).take(size)
        return try {
            val session = createTestSession(
                languageCode = languageCode,
                type = TestType.RANDOM,
                vocabularyIds = words,
                date = date,
                dailyTaskId = null,
                learningSessionId = null,
                threshold = null
            )
            Result.success(session.id)
        } catch (error: IllegalStateException) {
            Result.failure(IllegalStateException(error.message ?: "Unable to build the test."))
        }
    }

    suspend fun startSavedWordsTest(languageCode: String, count: Int, date: String = today()): Result<String> {
        val words = learningDao.savedWords(languageCode).map { it.vocabularyId }
        if (words.isEmpty()) {
            return Result.failure(IllegalStateException("No saved words to test yet."))
        }
        val size = count.coerceIn(1, words.size)
        val selected = words.shuffled(random).take(size)
        return try {
            val session = createTestSession(
                languageCode = languageCode,
                type = TestType.SAVED_WORDS,
                vocabularyIds = selected,
                date = date,
                dailyTaskId = null,
                learningSessionId = null,
                threshold = null
            )
            Result.success(session.id)
        } catch (error: IllegalStateException) {
            Result.failure(IllegalStateException(error.message ?: "Unable to build the test."))
        }
    }

    // ------------------------------------------------------------------ answering

    suspend fun answerQuestion(sessionId: String, questionId: String, selectedIndex: Int): AnswerOutcome? =
        database.withTransaction {
            val session = testDao.session(sessionId) ?: return@withTransaction null
            if (session.status != TestStatus.IN_PROGRESS) return@withTransaction null
            val question = testDao.question(questionId) ?: return@withTransaction null
            if (question.selectedIndex != null) return@withTransaction null

            val correct = selectedIndex == question.correctIndex
            val answered = question.copy(selectedIndex = selectedIndex, isCorrect = correct)
            testDao.upsertQuestion(answered)
            if (learningDao.record(question.vocabularyId) != null) {
                learningDao.recordAttempt(
                    vocabularyId = question.vocabularyId,
                    correct = if (correct) 1 else 0,
                    incorrect = if (correct) 0 else 1,
                    date = today()
                )
            }
            if (!correct) {
                testDao.insertQuestions(
                    listOf(
                        question.copy(
                            id = UUID.randomUUID().toString(),
                            selectedIndex = null,
                            isCorrect = null,
                            retryCount = question.retryCount + 1,
                            isReviewCopy = true,
                            groupIndex = question.groupIndex + 1,
                            orderInGroup = Int.MAX_VALUE
                        )
                    )
                )
            }

            var updated = session.copy(
                firstAttemptTotal = session.firstAttemptTotal + if (question.retryCount == 0) 1 else 0,
                firstAttemptCorrect = session.firstAttemptCorrect +
                    if (question.retryCount == 0 && correct) 1 else 0
            )

            val unanswered = testDao.unansweredCount(sessionId)
            if (unanswered == 0) {
                updated = finalizeSession(updated)
                val result = buildResult(updated)
                AnswerOutcome(answered, correct, updated, dictionary.bundle(answered.vocabularyId), result)
            } else {
                var group = session.currentGroup
                val groupHasUnanswered = testDao.questionsInGroup(sessionId, group).any { it.selectedIndex == null }
                if (!groupHasUnanswered) {
                    group = testDao.firstUnansweredGroup(sessionId) ?: (group + 1)
                    shuffleGroup(sessionId, group)
                }
                val groupAnswered = testDao.questionsInGroup(sessionId, group).count { it.selectedIndex != null }
                updated = updated.copy(currentGroup = group, currentQuestion = groupAnswered)
                testDao.upsertSession(updated)
                AnswerOutcome(answered, correct, updated, dictionary.bundle(answered.vocabularyId), null)
            }
        }

    private suspend fun finalizeSession(session: TestSessionEntity): TestSessionEntity {
        val total = session.firstAttemptTotal
        val score = if (total > 0) session.firstAttemptCorrect * 100.0 / total else 0.0
        val threshold = session.passingThreshold ?: settingsRepository.current().passingThreshold
        val isPreliminary = session.testType == TestType.PRELIMINARY
        val passed = if (isPreliminary) score >= threshold else true
        var updated = session.copy(
            status = if (isPreliminary && !passed) TestStatus.FAILED else TestStatus.COMPLETED,
            scorePercent = score,
            passed = passed,
            completedAt = dateProvider.nowMillis()
        )
        testDao.upsertSession(updated)

        when (session.testType) {
            TestType.PRELIMINARY -> {
                session.dailyTaskId?.let { taskId ->
                    updateTask(taskId) { task ->
                        val preliminary = if (passed) PreliminaryStatus.PASSED else PreliminaryStatus.FAILED
                        task.copy(
                            preliminaryStatus = preliminary,
                            overallStatus = if (passed) OverallStatus.READY else OverallStatus.PRELIMINARY_REQUIRED,
                            updatedAt = dateProvider.nowMillis()
                        )
                    }
                }
            }

            TestType.POST_LEARNING -> {
                learningDao.markLearned(session.vocabularyIds, LearningStatus.LEARNED, session.sessionDate)
                session.dailyTaskId?.let { taskId ->
                    updateTask(taskId) { task ->
                        task.copy(
                            newLearningStatus = NewLearningStatus.COMPLETED,
                            postTestStatus = PostTestStatus.COMPLETED,
                            completedNewWords = session.vocabularyIds.size,
                            overallStatus = OverallStatus.COMPLETED,
                            updatedAt = dateProvider.nowMillis()
                        )
                    }
                }
            }

            TestType.GROUP_QUIZ -> completeLearningRound(updated)

            TestType.RANDOM, TestType.SAVED_WORDS -> Unit
        }
        return updated
    }

    private suspend fun completeLearningRound(quiz: TestSessionEntity) {
        val learningSessionId = quiz.learningSessionId ?: return
        var session = learningDao.learningSession(learningSessionId) ?: return
        if (session.activeQuizSessionId != quiz.id) return
        val questions = testDao.questions(quiz.id)
        val failed = questions.filter { it.isCorrect == false }.map { it.vocabularyId }.distinct()
        val newlyPassed = quiz.vocabularyIds.filter { it !in failed }
        val passed = (session.passedIds + newlyPassed).distinct()
        val pending = failed.filter { it !in passed }
        val groupEnd = minOf(((session.cardCursor / 10) + 1) * 10, session.vocabularyIds.size)

        session = session.copy(
            passedIds = passed,
            pendingQuizIds = pending,
            activeQuizSessionId = null,
            quizRound = session.quizRound + 1,
            updatedAt = dateProvider.nowMillis()
        )
        val task = learningDao.dailyTask(session.languageCode, session.learningDate)
        if (task != null) {
            updateTask(task.id) {
                it.copy(completedNewWords = passed.size, updatedAt = dateProvider.nowMillis())
            }
        }

        when {
            groupEnd < session.vocabularyIds.size -> {
                learningDao.upsertLearningSession(
                    session.copy(cardCursor = groupEnd, phase = LearningPhase.CARDS)
                )
            }

            pending.isNotEmpty() -> {
                val reviewQuiz = createTestSession(
                    languageCode = session.languageCode,
                    type = TestType.GROUP_QUIZ,
                    vocabularyIds = pending,
                    date = session.learningDate,
                    dailyTaskId = task?.id,
                    learningSessionId = session.id,
                    threshold = null
                )
                learningDao.upsertLearningSession(
                    session.copy(
                        cardCursor = session.vocabularyIds.size,
                        phase = LearningPhase.QUIZ,
                        activeQuizSessionId = reviewQuiz.id
                    )
                )
            }

            else -> {
                learningDao.upsertLearningSession(
                    session.copy(
                        cardCursor = session.vocabularyIds.size,
                        phase = LearningPhase.COMPLETE,
                        completedOn = today()
                    )
                )
                finishLearning(session)
            }
        }
    }

    private suspend fun finishLearning(session: LearningSessionEntity) {
        val task = learningDao.dailyTask(session.languageCode, session.learningDate) ?: return
        ensurePostLearningSession(session, task)
        updateTask(task.id) {
            it.copy(
                newLearningStatus = NewLearningStatus.COMPLETED,
                postTestStatus = PostTestStatus.PENDING,
                overallStatus = OverallStatus.NEW_WORDS_DONE,
                completedNewWords = session.passedIds.size,
                plannedNewWords = session.targetCount,
                updatedAt = dateProvider.nowMillis()
            )
        }
    }

    private suspend fun ensurePostLearningSession(session: LearningSessionEntity, task: DailyTaskEntity): TestSessionEntity? {
        val existing = testDao.latestForTask(task.id, TestType.POST_LEARNING.name)
        if (existing != null) return existing
        return try {
            createTestSession(
                languageCode = session.languageCode,
                type = TestType.POST_LEARNING,
                vocabularyIds = session.vocabularyIds,
                date = session.learningDate,
                dailyTaskId = task.id,
                learningSessionId = session.id,
                threshold = null
            )
        } catch (error: IllegalStateException) {
            null
        }
    }

    private suspend fun reconcileLearningSession(session: LearningSessionEntity): LearningSessionEntity {
        val quizId = session.activeQuizSessionId ?: return session
        val quiz = testDao.session(quizId) ?: return session
        if (quiz.status == TestStatus.IN_PROGRESS) return session
        database.withTransaction { completeLearningRound(quiz) }
        return learningDao.learningSession(session.id) ?: session
    }

    // ------------------------------------------------------------------ read models

    suspend fun snapshot(sessionId: String): SessionSnapshot? {
        val session = testDao.session(sessionId) ?: return null
        val language = languageDao.get(session.languageCode) ?: return null
        val nextUnanswered = testDao.nextUnanswered(sessionId)
        val sessionQuestions = testDao.questions(sessionId)
        val next = nextUnanswered?.let { current ->
            if (current.questionType == QuestionType.MORPHOLOGY) {
                sessionQuestions.firstOrNull { candidate ->
                    candidate.selectedIndex == null &&
                        candidate.vocabularyId == current.vocabularyId &&
                        (candidate.questionType == QuestionType.CONTEXT ||
                            candidate.questionType == QuestionType.FORM_MEANING)
                } ?: current
            } else if (current.questionType == QuestionType.FORM_PARSE) {
                sessionQuestions.firstOrNull { candidate ->
                    candidate.selectedIndex == null &&
                        candidate.vocabularyId == current.vocabularyId &&
                        candidate.questionType == QuestionType.FORM_MEANING
                } ?: current
            } else {
                current
            }
        }
        val totalQuestions = testDao.questionCount(sessionId)
        val unanswered = testDao.unansweredCount(sessionId)
        val correct = testDao.correctCount(sessionId)
        val incorrect = testDao.incorrectCount(sessionId)
        val groupIndex = next?.groupIndex ?: session.currentGroup
        val groupQuestions = testDao.questionsInGroup(sessionId, groupIndex)
        val answeredInGroup = groupQuestions.count { it.selectedIndex != null }
        val progress = TestProgress(
            groupIndex = groupIndex,
            groupNumber = groupIndex + 1,
            positionInGroup = (answeredInGroup + 1).coerceAtMost(groupQuestions.size.coerceAtLeast(1)),
            questionsInGroup = groupQuestions.size,
            totalQuestions = totalQuestions,
            answered = totalQuestions - unanswered,
            correct = correct,
            incorrect = incorrect,
            remaining = unanswered
        )
        return SessionSnapshot(
            session = session,
            language = language,
            question = next,
            secondaryQuestion = next?.let { primary ->
                val preferred = when (primary.questionType) {
                    QuestionType.FORM_MEANING -> listOf(QuestionType.FORM_PARSE)
                    QuestionType.CONTEXT -> listOf(QuestionType.MORPHOLOGY, QuestionType.SEMANTIC)
                    QuestionType.MORPHOLOGY -> listOf(QuestionType.CONTEXT, QuestionType.FORM_MEANING)
                    QuestionType.SEMANTIC -> listOf(QuestionType.CONTEXT, QuestionType.MORPHOLOGY)
                    QuestionType.CLOZE -> emptyList()
                    QuestionType.FORM_PARSE -> listOf(QuestionType.FORM_MEANING)
                }
                preferred.firstNotNullOfOrNull { type ->
                    sessionQuestions.firstOrNull { candidate ->
                        candidate.id != primary.id &&
                            candidate.vocabularyId == primary.vocabularyId &&
                            candidate.selectedIndex == null &&
                            candidate.questionType == type
                    }
                }
            },
            bundle = next?.let { dictionary.bundle(it.vocabularyId) },
            progress = progress
        )
    }

    suspend fun result(sessionId: String): SessionResult? {
        val session = testDao.session(sessionId) ?: return null
        return buildResult(session)
    }

    private suspend fun buildResult(session: TestSessionEntity): SessionResult? {
        val language = languageDao.get(session.languageCode) ?: return null
        val questions = testDao.questions(session.id)
        val missedIds = questions.filter { it.isCorrect == false }.map { it.vocabularyId }.distinct()
        val missed = dictionary.bundles(missedIds)
        return SessionResult(
            session = session,
            language = language,
            missedWords = missed,
            correct = questions.count { it.isCorrect == true },
            incorrect = questions.count { it.isCorrect == false },
            totalQuestions = questions.size,
            firstAttemptScore = session.scorePercent ?: 0.0
        )
    }

    suspend fun session(sessionId: String): TestSessionEntity? = testDao.session(sessionId)

    suspend fun inProgressTests(language: String): List<TestSessionEntity> = testDao.inProgress(language)

    fun observeDailyTask(language: String, date: String) = learningDao.observeDailyTask(language, date)

    fun observeLearnedCount(language: String) = learningDao.observeLearnedCount(language)

    fun observeSavedCount(language: String) = learningDao.observeSavedCount(language)

    fun observeDailyTasks(language: String) = learningDao.observeDailyTasks(language)

    fun observeUnfinishedSessions(language: String) = learningDao.observeUnfinishedSessions(language)

    fun observeLearningSession(learningSessionId: String) = learningDao.observeLearningSession(learningSessionId)

    fun observeInProgressTests(language: String) = testDao.observeInProgress(language)

    fun observeTestHistory(language: String) = testDao.observeHistory(language)

    fun observeDailyTaskFlow(language: String, date: String) = learningDao.observeDailyTask(language, date)

    fun observeAllRecords(language: String) = learningDao.observeRecords(language)

    fun observeCompletedTestCount() = testDao.observeCompletedTestCount()

    suspend fun resetProgress() {
        database.withTransaction {
            testDao.deleteAllQuestions()
            testDao.deleteAllSessions()
            learningDao.deleteAllLearningSessions()
            learningDao.deleteAllDailyTasks()
            learningDao.deleteAllRecords()
        }
    }

    // ---------------------------------------------------------------- saved words

    fun observeSaved(language: String) = learningDao.observeSavedVocabulary(language)

    fun observeSavedIds(language: String) = learningDao.observeSaved(language)

    fun observeIsSaved(vocabularyId: String) = learningDao.observeIsSaved(vocabularyId)

    fun observeRecord(vocabularyId: String) = learningDao.observeRecord(vocabularyId)

    suspend fun isSaved(vocabularyId: String): Boolean = learningDao.savedEntry(vocabularyId) != null

    suspend fun progressRecord(vocabularyId: String): LearningRecordEntity? = learningDao.record(vocabularyId)

    suspend fun savedWords(language: String): List<com.baytalhikmah.data.local.SavedWordEntity> =
        learningDao.savedWords(language)

    suspend fun toggleSaved(vocabularyId: String) {
        val language = dictionary.vocabularyById(vocabularyId)?.languageCode ?: return
        val existing = learningDao.savedEntry(vocabularyId)
        if (existing != null) {
            learningDao.removeSavedWord(vocabularyId)
        } else {
            learningDao.saveWord(
                com.baytalhikmah.data.local.SavedWordEntity(
                    vocabularyId = vocabularyId,
                    languageCode = language,
                    savedOn = today()
                )
            )
        }
    }

    suspend fun toggleDifficult(vocabularyId: String) {
        val word = dictionary.vocabularyById(vocabularyId) ?: return
        val existing = learningDao.record(vocabularyId)
        if (existing == null) {
            learningDao.upsertRecord(
                LearningRecordEntity(
                    vocabularyId = vocabularyId,
                    languageCode = word.languageCode,
                    status = LearningStatus.NOT_STARTED,
                    introducedOn = null,
                    firstLearnedOn = null,
                    lastReviewedOn = null,
                    totalAttempts = 0,
                    correctAttempts = 0,
                    incorrectAttempts = 0,
                    difficult = true
                )
            )
        } else {
            learningDao.setDifficult(vocabularyId, !existing.difficult)
        }
    }

    // ---------------------------------------------------------------- test creation

    private suspend fun createTestSession(
        languageCode: String,
        type: TestType,
        vocabularyIds: List<String>,
        date: String,
        dailyTaskId: String?,
        learningSessionId: String?,
        threshold: Int?
    ): TestSessionEntity {
        require(vocabularyIds.isNotEmpty()) { "No vocabulary selected for this test." }
        val language = languageDao.get(languageCode)
            ?: throw IllegalStateException("The selected language is not installed.")
        val bundles = dictionary.bundles(vocabularyIds)
        if (bundles.size != vocabularyIds.size) {
            throw IllegalStateException("Some vocabulary entries are unavailable in the database.")
        }
        val byId = bundles.associateBy { it.vocabulary.id }
        val pool = dictionary.distractorPool(languageCode)
        val factory = QuestionFactory(language.morphology, random)
        val sessionId = UUID.randomUUID().toString()
        val chunkSize = if (type == TestType.GROUP_QUIZ) vocabularyIds.size else 10
        val questions = mutableListOf<TestQuestionEntity>()

        vocabularyIds.chunked(chunkSize.coerceAtLeast(1)).forEachIndexed { groupIndex, chunk ->
            val chunkBundles = chunk.mapNotNull { byId[it] }
            val drafts: List<QuestionDraft> = if (type == TestType.GROUP_QUIZ) {
                factory.forGroupQuiz(chunkBundles, pool)
            } else {
                factory.forTest(chunkBundles, pool)
            }
            val covered = drafts.map { it.vocabularyId }.toSet()
            val missing = chunk.filter { it !in covered }
            if (missing.isNotEmpty()) {
                throw IllegalStateException(
                    "The vocabulary dataset lacks enough verified data to test: ${missing.joinToString(", ")}."
                )
            }
            drafts.shuffled(random).forEachIndexed { order, draft ->
                questions += TestQuestionEntity(
                    id = UUID.randomUUID().toString(),
                    sessionId = sessionId,
                    vocabularyId = draft.vocabularyId,
                    questionType = draft.type,
                    prompt = draft.prompt,
                    contextTranslation = draft.contextTranslation,
                    highlightText = draft.highlightText,
                    options = draft.options,
                    correctIndex = draft.correctIndex,
                    selectedIndex = null,
                    isCorrect = null,
                    retryCount = 0,
                    isReviewCopy = false,
                    groupIndex = groupIndex,
                    orderInGroup = order
                )
            }
        }
        if (questions.isEmpty()) throw IllegalStateException("No questions could be generated.")
        val session = TestSessionEntity(
            id = sessionId,
            languageCode = languageCode,
            testType = type,
            sessionDate = date,
            createdAt = dateProvider.nowMillis(),
            completedAt = null,
            totalVocabularyItems = vocabularyIds.size,
            currentGroup = 0,
            currentQuestion = 0,
            status = TestStatus.IN_PROGRESS,
            scorePercent = null,
            passed = null,
            dailyTaskId = dailyTaskId,
            learningSessionId = learningSessionId,
            vocabularyIds = vocabularyIds,
            firstAttemptCorrect = 0,
            firstAttemptTotal = 0,
            passingThreshold = threshold
        )
        testDao.insertSession(session)
        testDao.insertQuestions(questions)
        return session
    }

    private suspend fun shuffleGroup(sessionId: String, groupIndex: Int) {
        val questions = testDao.questionsInGroup(sessionId, groupIndex)
        if (questions.size < 2) return
        questions.shuffled(random).forEachIndexed { index, question ->
            testDao.upsertQuestion(question.copy(orderInGroup = index))
        }
    }

    // ------------------------------------------------------------------- helpers

    private suspend fun updateTask(taskId: String, transform: (DailyTaskEntity) -> DailyTaskEntity) {
        val task = learningDao.dailyTaskById(taskId) ?: return
        learningDao.upsertDailyTask(transform(task))
    }

    private fun taskId(language: String, date: String) = "$language|$date"

    private fun learningSessionId(language: String, date: String) = "$language|$date"

    private companion object {
        const val PRELIMINARY_MIN_WORDS = 30
    }
}

data class LearningState(
    val daily: DailyState,
    val bundle: VocabBundle?,
    val groupNumber: Int,
    val groupPosition: Int,
    val groupSize: Int,
    val isLastCardOfGroup: Boolean,
    val availableNewWords: Int
)

data class LanguageStatistics(
    val languageCode: String,
    val totalWords: Int,
    val learned: Int,
    val inProgress: Int,
    val difficult: Int,
    val saved: Int,
    val attempts: Int,
    val correctAttempts: Int,
    val completedDays: Int
) {
    val accuracy: Double get() = if (attempts == 0) 0.0 else correctAttempts * 100.0 / attempts
}
