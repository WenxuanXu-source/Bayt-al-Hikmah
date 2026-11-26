package com.baytalhikmah.data

import com.baytalhikmah.data.local.AppDatabase
import com.baytalhikmah.data.local.LanguageEntity
import com.baytalhikmah.data.local.VocabularyEntity
import com.baytalhikmah.data.local.dao.VocabularyListRow
import com.baytalhikmah.domain.VocabBundle
import kotlinx.coroutines.flow.Flow

class DictionaryRepository(database: AppDatabase) {
    private val languageDao = database.languageDao()
    private val vocabularyDao = database.vocabularyDao()

    fun observeLanguages(): Flow<List<LanguageEntity>> = languageDao.observeAll()

    suspend fun languages(): List<LanguageEntity> = languageDao.getEnabled()

    suspend fun language(code: String): LanguageEntity? = languageDao.get(code)

    fun observeVocabulary(language: String): Flow<List<VocabularyEntity>> =
        vocabularyDao.observeAll(language)

    fun observeCount(language: String): Flow<Int> = vocabularyDao.observeCount(language)

    fun search(
        language: String,
        query: String,
        pos: String?,
        onlyLearned: Boolean,
        onlySaved: Boolean
    ): Flow<List<VocabularyListRow>> = vocabularyDao.search(
        language = language,
        pattern = com.baytalhikmah.domain.SearchNormalizer.pattern(query),
        pos = pos,
        onlyLearned = if (onlyLearned) 1 else 0,
        onlySaved = if (onlySaved) 1 else 0,
        limit = SEARCH_LIMIT
    )

    suspend fun vocabulary(language: String): List<VocabularyEntity> =
        vocabularyDao.getAllByFrequency(language)

    suspend fun nextUnintroduced(language: String, limit: Int): List<VocabularyEntity> =
        vocabularyDao.nextUnintroduced(language, limit.coerceAtLeast(1))

    suspend fun vocabularyById(id: String): VocabularyEntity? = vocabularyDao.get(id)

    suspend fun count(language: String): Int = vocabularyDao.count(language)

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

    private suspend fun bundleOf(vocabulary: VocabularyEntity): VocabBundle =
        VocabBundle(
            vocabulary = vocabulary,
            forms = vocabularyDao.formsFor(vocabulary.id),
            examples = vocabularyDao.examplesFor(vocabulary.id)
        )

    companion object {
        const val SEARCH_LIMIT = 300
        const val POOL_LIMIT = 600
        private const val QUERY_CHUNK = 400
    }
}
