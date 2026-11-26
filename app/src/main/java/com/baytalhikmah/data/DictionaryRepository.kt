package com.baytalhikmah.data

import com.baytalhikmah.data.local.AppDatabase
import com.baytalhikmah.data.local.LanguageEntity
import com.baytalhikmah.data.local.ReadingLexemeEntity
import com.baytalhikmah.data.local.ReadingProgressEntity
import com.baytalhikmah.data.local.ReadingSectionEntity
import com.baytalhikmah.data.local.ReadingSectionProgressEntity
import com.baytalhikmah.data.local.ReadingWorkEntity
import com.baytalhikmah.data.local.VocabularyEntity
import com.baytalhikmah.data.local.dao.FormLookupRow
import com.baytalhikmah.data.local.dao.ReadingBookRow
import com.baytalhikmah.data.local.dao.VocabularyListRow
import com.baytalhikmah.domain.SearchNormalizer
import com.baytalhikmah.domain.VocabBundle
import com.baytalhikmah.data.model.ReadingStatus
import com.baytalhikmah.data.model.VocabBook
import kotlinx.coroutines.flow.Flow

/** What a reader sees after tapping one word in a reading text. */
data class WordInsight(
    val matches: List<FormLookupRow>,
    /** Null when the entry exists but carries no parse for this form. */
    val bundle: VocabBundle?
)

class DictionaryRepository(database: AppDatabase) {
    private val languageDao = database.languageDao()
    private val vocabularyDao = database.vocabularyDao()
    private val readingDao = database.readingDao()

    fun observeLanguages(): Flow<List<LanguageEntity>> = languageDao.observeAll()

    suspend fun languages(): List<LanguageEntity> = languageDao.getEnabled()

    suspend fun language(code: String): LanguageEntity? = languageDao.get(code)

    fun observeVocabulary(language: String): Flow<List<VocabularyEntity>> =
        vocabularyDao.observeAll(language)

    fun observeCount(language: String): Flow<Int> = vocabularyDao.observeCount(language)

    fun observeBookCount(language: String, book: VocabBook): Flow<Int> =
        vocabularyDao.observeBookCount(language, book)

    /**
     * The reference shelf covers the whole dictionary; passing a book narrows it to
     * one wordlist.
     */
    fun search(
        language: String,
        query: String,
        book: VocabBook? = null,
        pos: String?,
        onlyLearned: Boolean,
        onlySaved: Boolean
    ): Flow<List<VocabularyListRow>> = vocabularyDao.search(
        language = language,
        pattern = SearchNormalizer.pattern(query),
        book = book,
        pos = pos,
        onlyLearned = if (onlyLearned) 1 else 0,
        onlySaved = if (onlySaved) 1 else 0,
        limit = SEARCH_LIMIT
    )

    suspend fun vocabulary(language: String, book: VocabBook): List<VocabularyEntity> =
        vocabularyDao.getAllByFrequency(language, book)

    fun observeBook(language: String, book: VocabBook): Flow<List<VocabularyEntity>> =
        vocabularyDao.observeBookByFrequency(language, book)

    suspend fun nextUnintroduced(language: String, book: VocabBook, limit: Int): List<VocabularyEntity> =
        vocabularyDao.nextUnintroduced(language, book, limit.coerceAtLeast(1))

    suspend fun vocabularyById(id: String): VocabularyEntity? = vocabularyDao.get(id)

    suspend fun vocabularyByIds(ids: List<String>): List<VocabularyEntity> =
        if (ids.isEmpty()) emptyList()
        else ids.chunked(QUERY_CHUNK).flatMap { vocabularyDao.getByIds(it) }

    suspend fun count(language: String): Int = vocabularyDao.count(language)

    suspend fun count(language: String, book: VocabBook): Int =
        vocabularyDao.countBook(language, book)

    suspend fun bundle(vocabularyId: String): VocabBundle? {
        val vocabulary = vocabularyDao.get(vocabularyId) ?: return null
        return bundleOf(vocabulary)
    }

    suspend fun bundles(vocabularyIds: List<String>): List<VocabBundle> {
        if (vocabularyIds.isEmpty()) return emptyList()
        val vocabulary = vocabularyIds.chunked(QUERY_CHUNK)
            .flatMap { vocabularyDao.getByIds(it) }
            .associateBy { it.id }
        val forms = vocabularyIds.chunked(QUERY_CHUNK)
            .flatMap { vocabularyDao.formsForIds(it) }
            .groupBy { it.vocabularyId }
        val examples = vocabularyIds.chunked(QUERY_CHUNK)
            .flatMap { vocabularyDao.examplesForIds(it) }
            .groupBy { it.vocabularyId }
        return vocabularyIds.mapNotNull { id ->
            vocabulary[id]?.let { vocab ->
                VocabBundle(
                    vocabulary = vocab,
                    forms = forms[id].orEmpty(),
                    examples = examples[id].orEmpty()
                )
            }
        }
    }

    suspend fun distractorPool(language: String, limit: Int = POOL_LIMIT): List<VocabBundle> {
        val vocabulary = vocabularyDao.randomPool(language, limit.coerceAtLeast(1))
        if (vocabulary.isEmpty()) return emptyList()
        val ids = vocabulary.map { it.id }
        val forms = ids.chunked(QUERY_CHUNK)
            .flatMap { vocabularyDao.formsForIds(it) }
            .groupBy { it.vocabularyId }
        return vocabulary.map { vocab ->
            VocabBundle(vocabulary = vocab, forms = forms[vocab.id].orEmpty(), examples = emptyList())
        }
    }

    suspend fun formsFor(vocabularyId: String) = vocabularyDao.formsFor(vocabularyId)

    suspend fun examplesFor(vocabularyId: String) = vocabularyDao.examplesFor(vocabularyId)

    // ---------------------------------------------------------------- reading

    fun observeReadingWorks(language: String): Flow<List<ReadingWorkEntity>> =
        readingDao.observeWorks(language)

    suspend fun readingWork(id: String): ReadingWorkEntity? = readingDao.getWork(id)

    suspend fun readingBooks(workId: String): List<ReadingBookRow> = readingDao.books(workId)

    suspend fun readingSections(workId: String, bookNumber: Int): List<ReadingSectionEntity> =
        readingDao.sectionsInBook(workId, bookNumber)

    suspend fun allReadingSections(workId: String): List<ReadingSectionEntity> =
        readingDao.sections(workId)

    suspend fun readingSection(id: String): ReadingSectionEntity? = readingDao.getSection(id)

    suspend fun readingLexemes(ids: List<String>): List<ReadingLexemeEntity> =
        if (ids.isEmpty()) emptyList() else ids.chunked(QUERY_CHUNK).flatMap { readingDao.lexemesByIds(it) }

    fun observeReadingProgress(): Flow<List<ReadingProgressEntity>> = readingDao.observeProgress()

    suspend fun readingProgress(workId: String): ReadingProgressEntity? = readingDao.progress(workId)

    /**
     * Opening a work starts it without ever downgrading a finished work. The
     * first opening time is kept for the audit trail.
     */
    suspend fun startReading(workId: String, now: Long) {
        val existing = readingDao.progress(workId)
        if (existing?.status == ReadingStatus.FINISHED) return
        readingDao.upsertProgress(
            existing?.copy(
                status = ReadingStatus.READING,
                startedAt = existing.startedAt ?: now
            ) ?: ReadingProgressEntity(
                workId = workId,
                status = ReadingStatus.READING,
                startedAt = now,
                finishedAt = null,
                lastTestedAt = null,
                attempts = 0,
                lastCorrect = null,
                lastTotal = null,
                bestCorrect = 0
            )
        )
    }

    fun observeReadingSectionProgress() = readingDao.observeSectionProgress()

    fun observeReadingWorkSummary() = readingDao.observeWorkSectionSummary()

    suspend fun readingSectionProgress(sectionId: String) =
        readingDao.sectionProgress(sectionId)

    /** Opening a section marks it Reading without downgrading a finished one. */
    suspend fun startReadingSection(sectionId: String, workId: String, now: Long) {
        val existing = readingDao.sectionProgress(sectionId)
        if (existing?.status == ReadingStatus.FINISHED) return
        readingDao.upsertSectionProgress(
            existing?.copy(
                status = ReadingStatus.READING,
                startedAt = existing.startedAt ?: now
            ) ?: ReadingSectionProgressEntity(
                sectionId = sectionId,
                workId = workId,
                status = ReadingStatus.READING,
                startedAt = now,
                finishedAt = null,
                lastTestedAt = null,
                attempts = 0,
                lastCorrect = null,
                lastTotal = null,
                bestCorrect = 0
            )
        )
    }

    /**
     * A section test needs every answer correct. A failed attempt leaves the
     * section Reading; a later failed attempt never demotes a finished section.
     */
    suspend fun recordReadingSectionTest(
        sectionId: String,
        workId: String,
        correct: Int,
        total: Int,
        now: Long
    ) {
        val existing = readingDao.sectionProgress(sectionId)
        val passed = total > 0 && correct == total
        val finished = passed || existing?.status == ReadingStatus.FINISHED
        readingDao.upsertSectionProgress(
            ReadingSectionProgressEntity(
                sectionId = sectionId,
                workId = workId,
                status = if (finished) ReadingStatus.FINISHED else ReadingStatus.READING,
                startedAt = existing?.startedAt ?: now,
                finishedAt = when {
                    existing?.finishedAt != null -> existing.finishedAt
                    passed -> now
                    else -> null
                },
                lastTestedAt = now,
                attempts = (existing?.attempts ?: 0) + 1,
                lastCorrect = correct,
                lastTotal = total,
                bestCorrect = maxOf(existing?.bestCorrect ?: 0, correct)
            )
        )
    }

    /**
     * Legacy work-level recording, retained for upgrade compatibility.
     */
    suspend fun recordReadingTest(workId: String, correct: Int, total: Int, now: Long) {
        val existing = readingDao.progress(workId)
        val passed = total > 0 && correct == total
        val finished = passed || existing?.status == ReadingStatus.FINISHED
        readingDao.upsertProgress(
            ReadingProgressEntity(
                workId = workId,
                status = if (finished) ReadingStatus.FINISHED else ReadingStatus.READING,
                startedAt = existing?.startedAt ?: now,
                finishedAt = when {
                    existing?.finishedAt != null -> existing.finishedAt
                    passed -> now
                    else -> null
                },
                lastTestedAt = now,
                attempts = (existing?.attempts ?: 0) + 1,
                lastCorrect = correct,
                lastTotal = total,
                bestCorrect = maxOf(existing?.bestCorrect ?: 0, correct)
            )
        )
    }

    /**
     * Tapping a word resolves the surface form against the inflected-form table,
     * which is the same table the morphology quizzes use, so a parse shown while
     * reading cannot disagree with the word card.
     */
    suspend fun lookup(language: String, surface: String): WordInsight {
        val normalized = SearchNormalizer.normalize(surface)
        val matches = vocabularyDao.lookupForm(language, normalized, LOOKUP_LIMIT)
            .ifEmpty { vocabularyDao.lookupHeadword(language, normalized, LOOKUP_LIMIT) }
        return WordInsight(
            matches = matches,
            bundle = matches.firstOrNull()?.let { bundle(it.vocabularyId) }
        )
    }

    private suspend fun bundleOf(vocabulary: VocabularyEntity): VocabBundle =
        VocabBundle(
            vocabulary = vocabulary,
            forms = vocabularyDao.formsFor(vocabulary.id),
            examples = vocabularyDao.examplesFor(vocabulary.id)
        )

    companion object {
        const val SEARCH_LIMIT = 300
        const val POOL_LIMIT = 600
        const val LOOKUP_LIMIT = 6
        private const val QUERY_CHUNK = 400
    }
}
