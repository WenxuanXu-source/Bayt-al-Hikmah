package com.baytalhikmah.data

import android.content.Context
import android.net.Uri
import androidx.room.withTransaction
import com.baytalhikmah.data.local.AppDatabase
import com.baytalhikmah.data.local.LanguageEntity
import com.baytalhikmah.data.model.EntryDto
import com.baytalhikmah.data.model.LanguagePack
import com.baytalhikmah.domain.SearchNormalizer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

data class ImportReport(
    val languageCode: String,
    val languageName: String,
    val added: Int,
    val updated: Int,
    val skipped: Int,
    val invalid: Int,
    val newLanguage: Boolean,
    val warnings: List<String>
) {
    fun summary(): String = buildString {
        append("$languageName ($languageCode): ")
        append("$added added, $updated updated, $skipped skipped, $invalid invalid.")
        if (newLanguage) append(" New language registered.")
        if (warnings.isNotEmpty()) {
            append("\n\n")
            warnings.take(8).forEach { append("• $it\n") }
            if (warnings.size > 8) append("• … ${warnings.size - 8} more warnings")
        }
    }
}

class DatasetService(
    private val context: Context,
    private val database: AppDatabase
) {
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    private val languageDao = database.languageDao()
    private val vocabularyDao = database.vocabularyDao()

    suspend fun exportLanguage(languageCode: String): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val language = languageDao.get(languageCode)
                ?: error("Language $languageCode is not installed.")
            val vocabulary = vocabularyDao.getAll(languageCode)
            val ids = vocabulary.map { it.id }
            val forms = ids.chunked(EXPORT_CHUNK).flatMap { vocabularyDao.formsForIds(it) }
            val examples = ids.chunked(EXPORT_CHUNK).flatMap { vocabularyDao.examplesForIds(it) }
            val pack = DatasetMapper.toLanguagePack(language, vocabulary, forms, examples)
            json.encodeToString(LanguagePack.serializer(), pack)
        }
    }

    suspend fun exportTo(languageCode: String, uri: Uri): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val text = exportLanguage(languageCode).getOrThrow()
            DocumentIo.writeText(context, uri, text)
            val size = text.toByteArray().size
            "Exported ${languageCode.uppercase()} vocabulary (${size / 1024} KB)."
        }
    }

    suspend fun importFrom(uri: Uri): Result<ImportReport> = withContext(Dispatchers.IO) {
        runCatching {
            val text = DocumentIo.readText(context, uri)
            importJson(text).getOrThrow()
        }
    }

    suspend fun importJson(text: String): Result<ImportReport> = withContext(Dispatchers.IO) {
        runCatching {
            val pack = runCatching { json.decodeFromString(LanguagePack.serializer(), text) }
                .getOrElse { error -> throw IllegalArgumentException("Invalid dataset file: ${error.message}") }
            val structuralErrors = DatasetMapper.validate(pack).filter {
                it.startsWith("Unsupported schema") || it.startsWith("Language code") ||
                    it.startsWith("Language name") || it.startsWith("The dataset contains no entries")
            }
            if (structuralErrors.isNotEmpty()) {
                throw IllegalArgumentException(structuralErrors.joinToString("\n"))
            }
            val warnings = mutableListOf<String>()
            val existingLanguage = languageDao.get(pack.language.code)
            val existingVocabulary = vocabularyDao.getAllUnsorted(pack.language.code)
            val byId = existingVocabulary.associateBy { it.id }
            val byHeadword = existingVocabulary.associateBy {
                it.normalizedHeadword + "|" + it.pos.name
            }
            val seenIds = mutableSetOf<String>()
            val seenKeys = mutableSetOf<String>()
            var added = 0
            var updated = 0
            var skipped = 0
            var invalid = 0
            val datasetVersion = maxOf(
                pack.language.datasetVersion,
                (existingLanguage?.datasetVersion ?: 0) + 1
            )
            val language = DatasetMapper.toLanguageEntity(pack, existingLanguage?.sortOrder ?: nextSortOrder())
                .copy(datasetVersion = datasetVersion, enabled = existingLanguage?.enabled ?: true)

            database.withTransaction {
                languageDao.upsert(language)
                pack.entries.forEach { entry ->
                    if (!isValidEntry(entry)) {
                        invalid++
                        warnings += "${entry.id.ifBlank { entry.headword }}: missing required fields."
                        return@forEach
                    }
                    if (!seenIds.add(entry.id)) {
                        skipped++
                        warnings += "${entry.id}: duplicate id inside the file."
                        return@forEach
                    }
                    val vocabulary = DatasetMapper.toVocabulary(entry, language.code, datasetVersion)
                    val key = vocabulary.normalizedHeadword + "|" + vocabulary.pos.name
                    if (!seenKeys.add(key)) {
                        skipped++
                        warnings += "${entry.headword}: duplicate form and part of speech inside the file."
                        return@forEach
                    }
                    val differentEntry = byHeadword[key]
                    if (differentEntry != null && differentEntry.id != vocabulary.id) {
                        skipped++
                        warnings += "${entry.headword}: a different entry with the same form and part of speech exists."
                        return@forEach
                    }
                    if (byId.containsKey(vocabulary.id)) updated++ else added++
                    vocabularyDao.upsert(vocabulary)
                    vocabularyDao.deleteForms(vocabulary.id)
                    vocabularyDao.deleteExamples(vocabulary.id)
                    val forms = DatasetMapper.toForms(entry)
                    if (forms.isNotEmpty()) vocabularyDao.upsertForms(forms)
                    val examples = DatasetMapper.toExamples(entry)
                    if (examples.isNotEmpty()) vocabularyDao.upsertExamples(examples)
                }
            }
            ImportReport(
                languageCode = language.code,
                languageName = language.name,
                added = added,
                updated = updated,
                skipped = skipped,
                invalid = invalid,
                newLanguage = existingLanguage == null,
                warnings = warnings
            )
        }
    }

    private fun isValidEntry(entry: EntryDto): Boolean =
        entry.id.isNotBlank() && entry.headword.isNotBlank() &&
            entry.definitions.any { it.isNotBlank() } &&
            SearchNormalizer.normalize(entry.headword).isNotBlank()

    private suspend fun nextSortOrder(): Int = languageDao.nextSortOrder()

    private companion object {
        const val EXPORT_CHUNK = 400
    }
}
