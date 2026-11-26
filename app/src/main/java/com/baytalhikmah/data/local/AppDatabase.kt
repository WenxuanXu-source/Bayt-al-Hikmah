package com.baytalhikmah.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.baytalhikmah.data.local.dao.LanguageDao
import com.baytalhikmah.data.local.dao.LearningDao
import com.baytalhikmah.data.local.dao.ReadingDao
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
        TestQuestionEntity::class,
        LegacyWordKeyEntity::class,
        ReadingWorkEntity::class,
        ReadingLexemeEntity::class,
        ReadingSectionEntity::class,
        ReadingProgressEntity::class,
        ReadingSectionProgressEntity::class
    ],
    version = 5,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun languageDao(): LanguageDao
    abstract fun vocabularyDao(): VocabularyDao
    abstract fun learningDao(): LearningDao
    abstract fun testDao(): TestDao
    abstract fun readingDao(): ReadingDao

    companion object {
        /**
         * A vocabulary entry used to be identified by its position in the pack, so
         * rebuilding a wordbook orphans every learning record. The upgrade snapshots
         * headword plus part of speech for each identifier first, which is what
         * LegacyRecordMigrator re-attaches the records against.
         */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) = with(database) {
                execSQL("ALTER TABLE `vocabulary` ADD COLUMN `book` TEXT NOT NULL DEFAULT 'BASIC'")
                execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_vocabulary_languageCode_book` " +
                        "ON `vocabulary` (`languageCode`, `book`)"
                )
                execSQL("ALTER TABLE `example_sentences` ADD COLUMN `targetDimensions` TEXT NOT NULL DEFAULT ''")
                execSQL("ALTER TABLE `daily_tasks` ADD COLUMN `book` TEXT NOT NULL DEFAULT 'BASIC'")
                execSQL("ALTER TABLE `learning_sessions` ADD COLUMN `book` TEXT NOT NULL DEFAULT 'BASIC'")
                execSQL("ALTER TABLE `test_sessions` ADD COLUMN `book` TEXT NOT NULL DEFAULT 'BASIC'")
                execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_test_sessions_languageCode_book` " +
                        "ON `test_sessions` (`languageCode`, `book`)"
                )

                // Both books keep their own daily plan, so the old day-per-language
                // uniqueness has to make room for a book column.
                execSQL("DROP INDEX IF EXISTS `index_daily_tasks_languageCode_learningDate`")
                execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS `index_daily_tasks_languageCode_book_learningDate` " +
                        "ON `daily_tasks` (`languageCode`, `book`, `learningDate`)"
                )
                execSQL("DROP INDEX IF EXISTS `index_learning_sessions_languageCode_learningDate`")
                execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS `index_learning_sessions_languageCode_book_learningDate` " +
                        "ON `learning_sessions` (`languageCode`, `book`, `learningDate`)"
                )

                execSQL(
                    "CREATE TABLE IF NOT EXISTS `reading_works` (`id` TEXT NOT NULL, " +
                        "`languageCode` TEXT NOT NULL, `author` TEXT NOT NULL, `title` TEXT NOT NULL, " +
                        "`summary` TEXT NOT NULL, `textSource` TEXT NOT NULL, `translationSource` TEXT NOT NULL, " +
                        "`bookCount` INTEGER NOT NULL, `sortOrder` INTEGER NOT NULL, PRIMARY KEY(`id`))"
                )
                execSQL("CREATE INDEX IF NOT EXISTS `index_reading_works_languageCode` ON `reading_works` (`languageCode`)")
                execSQL(
                    "CREATE TABLE IF NOT EXISTS `reading_lexemes` (`id` TEXT NOT NULL, `workId` TEXT NOT NULL, " +
                        "`surface` TEXT NOT NULL, `lemma` TEXT NOT NULL, `pos` TEXT NOT NULL, `gloss` TEXT NOT NULL, " +
                        "PRIMARY KEY(`id`))"
                )
                execSQL("CREATE INDEX IF NOT EXISTS `index_reading_lexemes_workId` ON `reading_lexemes` (`workId`)")
                execSQL(
                    "CREATE TABLE IF NOT EXISTS `reading_sections` (`id` TEXT NOT NULL, `workId` TEXT NOT NULL, " +
                        "`bookNumber` INTEGER NOT NULL, `bookLabel` TEXT NOT NULL, `sectionLabel` TEXT NOT NULL, " +
                        "`heading` TEXT NOT NULL, `originalText` TEXT NOT NULL, `translationText` TEXT NOT NULL, " +
                        "`newWordIds` TEXT NOT NULL, `sortOrder` INTEGER NOT NULL, PRIMARY KEY(`id`))"
                )
                execSQL("CREATE INDEX IF NOT EXISTS `index_reading_sections_workId` ON `reading_sections` (`workId`)")
                execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_reading_sections_workId_bookNumber` " +
                        "ON `reading_sections` (`workId`, `bookNumber`)"
                )

                execSQL(
                    "CREATE TABLE IF NOT EXISTS `legacy_word_keys` (`vocabularyId` TEXT NOT NULL, " +
                        "`languageCode` TEXT NOT NULL, `normalizedHeadword` TEXT NOT NULL, `pos` TEXT NOT NULL, " +
                        "PRIMARY KEY(`vocabularyId`))"
                )
                execSQL(
                    "INSERT INTO `legacy_word_keys` (vocabularyId, languageCode, normalizedHeadword, pos) " +
                        "SELECT id, languageCode, normalizedHeadword, pos FROM `vocabulary`"
                )
            }
        }

        /**
         * Version 3 makes the upgrade snapshot a declared Room table. Devices that
         * already migrated to the earlier version 2 schema have the same columns,
         * so the second step only has to create them when missing.
         */
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) = with(database) {
                execSQL(
                    "CREATE TABLE IF NOT EXISTS `legacy_word_keys` (`vocabularyId` TEXT NOT NULL, " +
                        "`languageCode` TEXT NOT NULL, `normalizedHeadword` TEXT NOT NULL, " +
                        "`pos` TEXT NOT NULL, PRIMARY KEY(`vocabularyId`))"
                )
            }
        }

        /**
         * Version 4 records per-work reading state. A work is Unread until a
         * progress row exists, Reading once the row is started, and Finished
         * only when the two-question reading test has been passed.
         */
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) = with(database) {
                execSQL(
                    "CREATE TABLE IF NOT EXISTS `reading_progress` (`workId` TEXT NOT NULL, " +
                        "`status` TEXT NOT NULL, `startedAt` INTEGER, `finishedAt` INTEGER, " +
                        "`lastTestedAt` INTEGER, `attempts` INTEGER NOT NULL, " +
                        "`lastCorrect` INTEGER, `lastTotal` INTEGER, `bestCorrect` INTEGER NOT NULL, " +
                        "PRIMARY KEY(`workId`))"
                )
                execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_reading_progress_status` " +
                        "ON `reading_progress` (`status`)"
                )
            }
        }

        /**
         * Version 5 moves reading state to the section and records explicit
         * mastery. A finished work from version 4 marks all of its sections
         * finished; a work in progress marks its first section Reading.
         */
        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) = with(database) {
                execSQL(
                    "ALTER TABLE `learning_records` ADD COLUMN `mastered` INTEGER NOT NULL DEFAULT 0"
                )
                execSQL(
                    "CREATE TABLE IF NOT EXISTS `reading_section_progress` (" +
                        "`sectionId` TEXT NOT NULL, `workId` TEXT NOT NULL, `status` TEXT NOT NULL, " +
                        "`startedAt` INTEGER, `finishedAt` INTEGER, `lastTestedAt` INTEGER, " +
                        "`attempts` INTEGER NOT NULL, `lastCorrect` INTEGER, `lastTotal` INTEGER, " +
                        "`bestCorrect` INTEGER NOT NULL, PRIMARY KEY(`sectionId`))"
                )
                execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_reading_section_progress_workId` " +
                        "ON `reading_section_progress` (`workId`)"
                )
                execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_reading_section_progress_status` " +
                        "ON `reading_section_progress` (`status`)"
                )
                execSQL(
                    "INSERT OR IGNORE INTO `reading_section_progress` " +
                        "(`sectionId`, `workId`, `status`, `startedAt`, `finishedAt`, " +
                        "`lastTestedAt`, `attempts`, `lastCorrect`, `lastTotal`, `bestCorrect`) " +
                        "SELECT s.id, s.workId, 'FINISHED', p.startedAt, p.finishedAt, " +
                        "p.lastTestedAt, p.attempts, p.lastCorrect, p.lastTotal, p.bestCorrect " +
                        "FROM `reading_sections` s JOIN `reading_progress` p ON p.workId = s.workId " +
                        "WHERE p.status = 'FINISHED'"
                )
                execSQL(
                    "INSERT OR IGNORE INTO `reading_section_progress` " +
                        "(`sectionId`, `workId`, `status`, `startedAt`, `finishedAt`, " +
                        "`lastTestedAt`, `attempts`, `lastCorrect`, `lastTotal`, `bestCorrect`) " +
                        "SELECT s.id, s.workId, 'READING', p.startedAt, NULL, NULL, 0, NULL, NULL, 0 " +
                        "FROM `reading_sections` s JOIN `reading_progress` p ON p.workId = s.workId " +
                        "WHERE p.status = 'READING' AND s.sortOrder = " +
                        "(SELECT MIN(s2.sortOrder) FROM `reading_sections` s2 WHERE s2.workId = s.workId)"
                )
            }
        }

        fun build(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, "bayt-al-hikmah.db")
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                .build()
    }
}
