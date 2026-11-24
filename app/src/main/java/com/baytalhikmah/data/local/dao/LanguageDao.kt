package com.baytalhikmah.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.baytalhikmah.data.local.LanguageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LanguageDao {
    @Query("SELECT * FROM languages ORDER BY sortOrder, name")
    fun observeAll(): Flow<List<LanguageEntity>>

    @Query("SELECT * FROM languages WHERE enabled = 1 ORDER BY sortOrder, name")
    suspend fun getEnabled(): List<LanguageEntity>

    @Query("SELECT * FROM languages WHERE code = :code")
    suspend fun get(code: String): LanguageEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(language: LanguageEntity)

    @Query("SELECT COUNT(*) FROM languages")
    suspend fun count(): Int

    @Query("SELECT COALESCE(MAX(sortOrder), -1) + 1 FROM languages")
    suspend fun nextSortOrder(): Int
}
