package com.baytalhikmah.data

import android.content.Context
import android.util.Log
import androidx.room.withTransaction
import com.baytalhikmah.data.local.AppDatabase
import com.baytalhikmah.data.model.DatasetManifest
import com.baytalhikmah.data.model.LanguagePack
import com.baytalhikmah.data.model.ReadingPack
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
    private val readingDao = database.readingDao()
    private val legacyRecords = LegacyRecordMigrator(database)

    suspend fun seedIfNeeded() = withContext(Dispatchers.IO) {
        val manifest = runCatching {
            val text = context.assets.open("dictionary/manifest.json").bufferedReader().use { it.readText() }
            json.decodeFromString(DatasetManifest.serializer(), text)
        }.getOrElse { error ->
            Log.e(TAG, "Unable to read dataset manifest", error)
            return@withContext
        }

        val keys = legacyRecords.readKeys()
        val processedLanguages = mutableSetOf<String>()
        var moved = 0
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
                return@forEachIndexed
            }
            moved += seedPack(pack, index, entry.asset, keys)
            processedLanguages += pack.language.code
        }

        val readingSeed = context.getSharedPreferences("bayt_reading", Context.MODE_PRIVATE)
        if (readingSeed.getInt("version", 0) < READING_SEED_VERSION) {
            var seeded = true
            manifest.reading.forEachIndexed { index, entry ->
                seeded = seedReading(entry.asset, index) && seeded
            }
            if (seeded) {
                readingSeed.edit().putInt("version", READING_SEED_VERSION).apply()
            }
        }
        legacyRecords.clearKeys(processedLanguages)
        if (moved > 0) Log.i(TAG, "Re-attached $moved records from V1.2 identifiers")
    }

    private suspend fun seedPack(
        pack: LanguagePack,
        sortOrder: Int,
        assetName: String,
        legacyKeys: List<LegacyRecordMigrator.LegacyKey>
    ): Int {
        val code = pack.language.code
        val existing = languageDao.get(code)
        val validEntries = pack.entries.filter {
            it.id.isNotBlank() && it.headword.isNotBlank() && it.definitions.any(String::isNotBlank)
        }
        val entries = validEntries.map {
            DatasetMapper.toVocabulary(it, code, pack.language.datasetVersion)
        }
        val existingCount = vocabularyDao.count(code)
        val hasLegacyKeys = legacyKeys.any { it.languageCode == code }
        val shouldSeed = existing == null ||
            existingCount == 0 ||
            pack.language.datasetVersion > existing.datasetVersion ||
            (pack.language.datasetVersion == existing.datasetVersion &&
                (existingCount != entries.size || hasLegacyKeys))
        if (!shouldSeed) return 0

        Log.i(TAG, "Seeding $code from $assetName (${entries.size} entries)")
        val language = DatasetMapper.toLanguageEntity(pack, existing?.sortOrder ?: sortOrder)
        val forms = validEntries.flatMap(DatasetMapper::toForms)
        val examples = validEntries.flatMap(DatasetMapper::toExamples)

        val moved = database.withTransaction {
            vocabularyDao.deleteLanguageForms(code)
            vocabularyDao.deleteLanguageExamples(code)
            vocabularyDao.deleteLanguageVocabulary(code)
            languageDao.upsert(language)
            entries.chunked(SEED_CHUNK).forEach { vocabularyDao.upsertAll(it) }
            forms.chunked(SEED_CHUNK).forEach { vocabularyDao.upsertForms(it) }
            examples.chunked(SEED_CHUNK).forEach { vocabularyDao.upsertExamples(it) }
            legacyRecords.reattachWithinTransaction(legacyKeys.filter { it.languageCode == code }, entries)
        }
        Log.i(TAG, "Seeded $code: ${entries.size} entries, ${forms.size} forms, ${examples.size} examples")
        return moved
    }

    private suspend fun seedReading(assetName: String, sortOrder: Int): Boolean {
        val pack = runCatching {
            val text = context.assets.open(assetName).bufferedReader().use { it.readText() }
            json.decodeFromString(ReadingPack.serializer(), text)
        }.getOrElse { error ->
            Log.e(TAG, "Unable to read $assetName", error)
            return false
        }
        val errors = DatasetMapper.validateReading(pack)
        if (errors.isNotEmpty()) {
            Log.e(TAG, "Reading pack $assetName has issues: ${errors.take(10)}")
            return false
        }
        val work = DatasetMapper.toReadingWork(pack, sortOrder)
        val lexemes = DatasetMapper.toReadingLexemes(pack)
        val sections = DatasetMapper.toReadingSections(pack)
        database.withTransaction {
            readingDao.upsertWorks(listOf(work))
            readingDao.deleteLexemes(work.id)
            lexemes.chunked(SEED_CHUNK).forEach { readingDao.upsertLexemes(it) }
            readingDao.deleteSections(work.id)
            sections.chunked(SEED_CHUNK).forEach { readingDao.upsertSections(it) }
        }
        Log.i(TAG, "Seeded reading ${work.title}: ${sections.size} paragraphs, ${lexemes.size} unlisted words")
        return true
    }

    private companion object {
        const val TAG = "DatabaseSeeder"
        const val SEED_CHUNK = 500
        const val READING_SEED_VERSION = 3
    }
}
