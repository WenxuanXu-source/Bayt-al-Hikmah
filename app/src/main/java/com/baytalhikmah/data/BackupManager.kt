package com.baytalhikmah.data

import android.content.Context
import android.net.Uri
import androidx.room.withTransaction
import com.baytalhikmah.data.local.AppDatabase
import com.baytalhikmah.data.local.DailyTaskEntity
import com.baytalhikmah.data.local.LearningRecordEntity
import com.baytalhikmah.data.local.LearningSessionEntity
import com.baytalhikmah.data.local.SavedWordEntity
import com.baytalhikmah.data.local.TestQuestionEntity
import com.baytalhikmah.data.local.TestSessionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class BackupSettings(
    val theme: String = "SYSTEM",
    val selectedLanguage: String = UserSettings.DEFAULT_LANGUAGE,
    val dailyTarget: Int = UserSettings.DEFAULT_TARGET,
    val passingThreshold: Int = UserSettings.DEFAULT_THRESHOLD,
    val fontScale: Float = 1f
)

@Serializable
data class BackupPayload(
    val schemaVersion: Int = 1,
    val exportedAt: String,
    val learningRecords: List<LearningRecordEntity> = emptyList(),
    val dailyTasks: List<DailyTaskEntity> = emptyList(),
    val savedWords: List<SavedWordEntity> = emptyList(),
    val learningSessions: List<LearningSessionEntity> = emptyList(),
    val testSessions: List<TestSessionEntity> = emptyList(),
    val testQuestions: List<TestQuestionEntity> = emptyList(),
    val settings: BackupSettings = BackupSettings()
)

class BackupManager(
    private val context: Context,
    private val database: AppDatabase,
    private val settingsRepository: SettingsRepository,
    private val dateProvider: com.baytalhikmah.domain.DateProvider
) {
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    suspend fun export(uri: Uri): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val learningDao = database.learningDao()
            val testDao = database.testDao()
            val current = settingsRepository.current()
            val payload = BackupPayload(
                exportedAt = dateProvider.today().toString(),
                learningRecords = learningDao.allRecords(),
                dailyTasks = learningDao.allDailyTasks(),
                savedWords = learningDao.allSaved(),
                learningSessions = learningDao.allLearningSessions(),
                testSessions = testDao.allSessions(),
                testQuestions = testDao.allQuestions(),
                settings = BackupSettings(
                    theme = current.theme.name,
                    selectedLanguage = current.selectedLanguage,
                    dailyTarget = current.dailyTarget,
                    passingThreshold = current.passingThreshold,
                    fontScale = current.fontScale
                )
            )
            DocumentIo.writeText(context, uri, json.encodeToString(BackupPayload.serializer(), payload))
            "Backup written: ${payload.learningRecords.size} learning records, " +
                "${payload.dailyTasks.size} daily tasks, ${payload.testSessions.size} test sessions."
        }
    }

    suspend fun restore(uri: Uri): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val text = DocumentIo.readText(context, uri)
            val payload = json.decodeFromString(BackupPayload.serializer(), text)
            require(payload.schemaVersion == 1) { "Unsupported backup version ${payload.schemaVersion}." }
            val learningDao = database.learningDao()
            val testDao = database.testDao()
            database.withTransaction {
                testDao.deleteAllQuestions()
                testDao.deleteAllSessions()
                learningDao.deleteAllLearningSessions()
                learningDao.deleteAllSaved()
                learningDao.deleteAllDailyTasks()
                learningDao.deleteAllRecords()

                if (payload.learningRecords.isNotEmpty()) learningDao.upsertRecords(payload.learningRecords)
                if (payload.dailyTasks.isNotEmpty()) learningDao.upsertDailyTasks(payload.dailyTasks)
                if (payload.savedWords.isNotEmpty()) learningDao.saveWords(payload.savedWords)
                if (payload.learningSessions.isNotEmpty()) learningDao.upsertLearningSessions(payload.learningSessions)
                if (payload.testSessions.isNotEmpty()) testDao.insertSessionsForRestore(payload.testSessions)
                if (payload.testQuestions.isNotEmpty()) testDao.insertQuestionsForRestore(payload.testQuestions)
            }
            val restored = payload.settings
            settingsRepository.applySettings(
                UserSettings(
                    theme = com.baytalhikmah.data.model.ThemeMode.entries.firstOrNull { it.name == restored.theme }
                        ?: com.baytalhikmah.data.model.ThemeMode.SYSTEM,
                    selectedLanguage = restored.selectedLanguage,
                    dailyTarget = restored.dailyTarget,
                    passingThreshold = restored.passingThreshold,
                    fontScale = restored.fontScale
                )
            )
            "Backup restored: ${payload.learningRecords.size} learning records, " +
                "${payload.savedWords.size} saved words, ${payload.testSessions.size} test sessions."
        }
    }
}
