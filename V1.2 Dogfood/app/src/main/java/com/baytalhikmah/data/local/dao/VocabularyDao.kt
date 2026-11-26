package com.baytalhikmah.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.baytalhikmah.data.local.ExampleSentenceEntity
import com.baytalhikmah.data.local.InflectedFormEntity
import com.baytalhikmah.data.local.VocabularyEntity
import com.baytalhikmah.data.model.Gender
import com.baytalhikmah.data.model.Pos
import kotlinx.coroutines.flow.Flow

data class VocabularyListRow(
    val id: String,
    val headword: String,
    val pos: Pos,
    val gender: Gender?,
    val definitions: List<String>,
    val frequencyRank: Int?,
    val learned: Boolean,
    val saved: Boolean
)

@Dao
interface VocabularyDao {
    @Query("SELECT * FROM vocabulary WHERE languageCode = :language ORDER BY normalizedHeadword")
    fun observeAll(language: String): Flow<List<VocabularyEntity>>

    @Query("SELECT * FROM vocabulary WHERE languageCode = :language ORDER BY normalizedHeadword")
    suspend fun getAll(language: String): List<VocabularyEntity>

    @Query("SELECT * FROM vocabulary WHERE languageCode = :language")
    suspend fun getAllUnsorted(language: String): List<VocabularyEntity>

    @Query("SELECT * FROM vocabulary WHERE id = :id")
    suspend fun get(id: String): VocabularyEntity?

    @Query("SELECT * FROM vocabulary WHERE id IN (:ids)")
    suspend fun getByIds(ids: List<String>): List<VocabularyEntity>

    @Query("SELECT COUNT(*) FROM vocabulary WHERE languageCode = :language")
    suspend fun count(language: String): Int

    @Query("SELECT COUNT(*) FROM vocabulary WHERE languageCode = :language")
    fun observeCount(language: String): Flow<Int>

    @Query("SELECT * FROM vocabulary WHERE languageCode = :language ORDER BY COALESCE(frequencyRank, 1000000), normalizedHeadword")
    suspend fun getAllByFrequency(language: String): List<VocabularyEntity>

    @Query("SELECT * FROM inflected_forms WHERE vocabularyId = :vocabularyId ORDER BY id")
    suspend fun formsFor(vocabularyId: String): List<InflectedFormEntity>

    @Query("SELECT * FROM inflected_forms WHERE vocabularyId IN (:vocabularyIds)")
    suspend fun formsForIds(vocabularyIds: List<String>): List<InflectedFormEntity>

    @Query("SELECT * FROM example_sentences WHERE vocabularyId = :vocabularyId ORDER BY id")
    suspend fun examplesFor(vocabularyId: String): List<ExampleSentenceEntity>

    @Query("SELECT * FROM example_sentences WHERE vocabularyId IN (:vocabularyIds)")
    suspend fun examplesForIds(vocabularyIds: List<String>): List<ExampleSentenceEntity>

    @Query(
        """
        SELECT v.id AS id, v.headword AS headword, v.pos AS pos, v.gender AS gender,
               v.definitions AS definitions, v.frequencyRank AS frequencyRank,
               CASE WHEN r.status = 'LEARNED' THEN 1 ELSE 0 END AS learned,
               CASE WHEN s.vocabularyId IS NOT NULL THEN 1 ELSE 0 END AS saved
        FROM vocabulary v
        LEFT JOIN learning_records r ON r.vocabularyId = v.id
        LEFT JOIN saved_words s ON s.vocabularyId = v.id
        WHERE v.languageCode = :language
          AND (:pos IS NULL OR v.pos = :pos)
          AND (:onlyLearned = 0 OR r.status = 'LEARNED')
          AND (:onlySaved = 0 OR s.vocabularyId IS NOT NULL)
          AND (
                :pattern = '%'
                OR v.normalizedHeadword LIKE :pattern
                OR LOWER(v.headword) LIKE :pattern
                OR LOWER(v.definitions) LIKE :pattern
                OR EXISTS (
                    SELECT 1 FROM inflected_forms f
                    WHERE f.vocabularyId = v.id AND f.normalizedForm LIKE :pattern
                )
          )
        ORDER BY v.normalizedHeadword
        LIMIT :limit
        """
    )
    fun search(
        language: String,
        pattern: String,
        pos: String?,
        onlyLearned: Int,
        onlySaved: Int,
        limit: Int
    ): Flow<List<VocabularyListRow>>

    @Query("SELECT * FROM vocabulary WHERE languageCode = :language AND normalizedHeadword LIKE :pattern ORDER BY normalizedHeadword")
    suspend fun searchByNormalized(language: String, pattern: String): List<VocabularyEntity>

    @Query(
        """
        SELECT v.* FROM vocabulary v
        WHERE v.languageCode = :language
          AND NOT EXISTS (SELECT 1 FROM learning_records r WHERE r.vocabularyId = v.id)
        ORDER BY RANDOM()
        LIMIT :limit
        """
    )
    suspend fun nextUnintroduced(language: String, limit: Int): List<VocabularyEntity>

    @Query("SELECT * FROM vocabulary WHERE languageCode = :language ORDER BY RANDOM() LIMIT :limit")
    suspend fun randomPool(language: String, limit: Int): List<VocabularyEntity>

    @Query("DELETE FROM inflected_forms WHERE vocabularyId LIKE :pattern")
    suspend fun deleteFormsByPattern(pattern: String)

    @Query("DELETE FROM example_sentences WHERE vocabularyId LIKE :pattern")
    suspend fun deleteExamplesByPattern(pattern: String)

    @Query("DELETE FROM vocabulary WHERE id LIKE :pattern")
    suspend fun deleteVocabularyByPattern(pattern: String)

    @Query("DELETE FROM inflected_forms WHERE vocabularyId IN (:vocabularyIds)")
    suspend fun deleteFormsForIds(vocabularyIds: List<String>)

    @Query("DELETE FROM example_sentences WHERE vocabularyId IN (:vocabularyIds)")
    suspend fun deleteExamplesForIds(vocabularyIds: List<String>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<VocabularyEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: VocabularyEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertForms(items: List<InflectedFormEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertExamples(items: List<ExampleSentenceEntity>)

    @Query("DELETE FROM inflected_forms WHERE vocabularyId = :vocabularyId")
    suspend fun deleteForms(vocabularyId: String)

    @Query("DELETE FROM example_sentences WHERE vocabularyId = :vocabularyId")
    suspend fun deleteExamples(vocabularyId: String)

    @Query("DELETE FROM vocabulary WHERE languageCode = :language")
    suspend fun deleteLanguageVocabulary(language: String)

    @Query("DELETE FROM vocabulary WHERE id = :id")
    suspend fun delete(id: String)
}
