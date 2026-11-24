package com.baytalhikmah.data

import android.content.Context
import android.util.Log
import androidx.room.withTransaction
import com.baytalhikmah.data.local.AppDatabase
import com.baytalhikmah.data.model.DatasetManifest
import com.baytalhikmah.data.model.LanguagePack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class DatabaseSeeder(
    private val context: Context,
    private val database: AppDatabase
) {
    private val json = Json { ignoreUnknownKeys = true }
    private val languageDao = database.languageDao()
    private val vocabularyDao = database.vocabularyDao()

    suspend fun seedIfNeeded() = withContext(Dispatchers.IO) {
        val manifest = runCatching {
            val text = context.assets.open("dictionary/manifest.json").bufferedReader().use { it.readText() }
            json.decodeFromString(DatasetManifest.serializer(), text)
        }.getOrElse { error ->
            Log.e(TAG, "Unable to read dataset manifest", error)
            return@withContext
        }

        manifest.languages.forEachIndexed { index, entry ->
            val pack = runCatching {
                val text = context.assets.open(entry.asset).bufferedReader().use { it.readText() }
                json.decodeFromString(LanguagePack.serializer(), text)
            }.getOrElse { error ->
                Log.e(TAG, "Unable to read ${entry.asset}", error)
                return@forEachIndexed
            }
            val errors = DatasetMapper.validate(pack)
            if (errors.isNotEmpty()) {
                Log.e(TAG, "Dataset ${pack.language.code} has issues: ${errors.take(10)}")
            }
            seedPack(pack, index, entry.asset)
        }
    }

    private suspend fun seedPack(pack: LanguagePack, sortOrder: Int, assetName: String) {
        val code = pack.language.code
        val existing = languageDao.get(code)
        val existingCount = vocabularyDao.count(code)
        val shouldSeed = existing == null ||
            existingCount == 0 ||
            pack.language.datasetVersion > existing.datasetVersion
        if (!shouldSeed) return

        Log.i(TAG, "Seeding $code from $assetName (${pack.entries.size} entries)")
        if (pack.language.datasetVersion >= LEGACY_CLEANUP_VERSION) {
            val legacyPrefix = when (code) {
                "grc" -> "grc-lsj-"
                "lat" -> "lat-ls-"
                else -> null
            }
            if (legacyPrefix != null) {
                val pattern = "$legacyPrefix%"
                val db = database.openHelper.writableDatabase
                db.execSQL("DELETE FROM learning_sessions WHERE vocabularyIds LIKE ?", arrayOf("%$legacyPrefix%"))
                db.execSQL("DELETE FROM test_sessions WHERE vocabularyIds LIKE ?", arrayOf("%$legacyPrefix%"))
                db.execSQL("DELETE FROM test_questions WHERE vocabularyId LIKE ?", arrayOf(pattern))
                db.execSQL("DELETE FROM learning_records WHERE vocabularyId LIKE ?", arrayOf(pattern))
                db.execSQL("DELETE FROM saved_words WHERE vocabularyId LIKE ?", arrayOf(pattern))
                db.execSQL(
                    "UPDATE daily_tasks SET newLearningStatus='NOT_STARTED', postTestStatus='NOT_READY', " +
                        "completedNewWords=0, overallStatus='READY' WHERE languageCode=? AND newLearningStatus='IN_PROGRESS'",
                    arrayOf(code)
                )
                vocabularyDao.deleteFormsByPattern(pattern)
                vocabularyDao.deleteExamplesByPattern(pattern)
                vocabularyDao.deleteVocabularyByPattern(pattern)
            }
        }
        val language = DatasetMapper.toLanguageEntity(pack, existing?.sortOrder ?: sortOrder)

        database.withTransaction {
            languageDao.upsert(language)
            pack.entries.chunked(SEED_CHUNK).forEach { chunk ->
                val entries = chunk.filter {
                    it.id.isNotBlank() && it.headword.isNotBlank() && it.definitions.any(String::isNotBlank)
                }
                if (entries.isEmpty()) return@forEach
                vocabularyDao.upsertAll(
                    entries.map { DatasetMapper.toVocabulary(it, code, pack.language.datasetVersion) }
                )
                val ids = entries.map { it.id }
                ids.chunked(SEED_CHUNK).forEach {
                    vocabularyDao.deleteFormsForIds(it)
                    vocabularyDao.deleteExamplesForIds(it)
                }
                val forms = entries.flatMap(DatasetMapper::toForms)
                if (forms.isNotEmpty()) vocabularyDao.upsertForms(forms)
                val examples = entries.flatMap(DatasetMapper::toExamples)
                if (examples.isNotEmpty()) vocabularyDao.upsertExamples(examples)
            }
        }
    }

    private companion object {
        const val TAG = "DatabaseSeeder"
        const val SEED_CHUNK = 500
        const val LEGACY_CLEANUP_VERSION = 6
    }
}
