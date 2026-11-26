package com.baytalhikmah.data

import android.content.Context
import com.baytalhikmah.data.local.AppDatabase
import com.baytalhikmah.domain.SystemDateProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppContainer(private val context: Context) {
    val database: AppDatabase = AppDatabase.build(context)
    val dictionaryRepository = DictionaryRepository(database)
    val settingsRepository = SettingsRepository(context)
    val studyRepository = StudyRepository(database, dictionaryRepository, settingsRepository, SystemDateProvider)
    val referenceRepository = ReferenceRepository(context)
    val readingQuestionRepository = ReadingQuestionRepository(context)
    val readingWordIndexRepository = ReadingWordIndexRepository(context)
    val backupManager = BackupManager(context, database, settingsRepository, SystemDateProvider)
    val datasetService = DatasetService(context, database)
    private val seeder = DatabaseSeeder(context, database)

    private val _ready = MutableStateFlow(false)
    val ready: StateFlow<Boolean> = _ready.asStateFlow()

    suspend fun initialize() {
        try {
            seeder.seedIfNeeded()
        } finally {
            _ready.value = true
        }
    }
}
