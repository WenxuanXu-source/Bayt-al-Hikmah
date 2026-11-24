package com.baytalhikmah.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.baytalhikmah.data.local.dao.LanguageDao
import com.baytalhikmah.data.local.dao.LearningDao
import com.baytalhikmah.data.local.dao.TestDao
import com.baytalhikmah.data.local.dao.VocabularyDao

@Database(
    entities = [
        LanguageEntity::class,
        VocabularyEntity::class,
        InflectedFormEntity::class,
        ExampleSentenceEntity::class,
        LearningRecordEntity::class,
        DailyTaskEntity::class,
        SavedWordEntity::class,
        LearningSessionEntity::class,
        TestSessionEntity::class,
        TestQuestionEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun languageDao(): LanguageDao
    abstract fun vocabularyDao(): VocabularyDao
    abstract fun learningDao(): LearningDao
    abstract fun testDao(): TestDao

    companion object {
        fun build(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, "bayt-al-hikmah.db")
                .build()
    }
}
