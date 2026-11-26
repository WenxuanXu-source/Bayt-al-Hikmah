package com.baytalhikmah.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.baytalhikmah.data.local.dao.LanguageDao;
import com.baytalhikmah.data.local.dao.LanguageDao_Impl;
import com.baytalhikmah.data.local.dao.LearningDao;
import com.baytalhikmah.data.local.dao.LearningDao_Impl;
import com.baytalhikmah.data.local.dao.ReadingDao;
import com.baytalhikmah.data.local.dao.ReadingDao_Impl;
import com.baytalhikmah.data.local.dao.TestDao;
import com.baytalhikmah.data.local.dao.TestDao_Impl;
import com.baytalhikmah.data.local.dao.VocabularyDao;
import com.baytalhikmah.data.local.dao.VocabularyDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile LanguageDao _languageDao;

  private volatile VocabularyDao _vocabularyDao;

  private volatile LearningDao _learningDao;

  private volatile TestDao _testDao;

  private volatile ReadingDao _readingDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(5) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `languages` (`code` TEXT NOT NULL, `name` TEXT NOT NULL, `nativeName` TEXT NOT NULL, `script` TEXT NOT NULL, `enabled` INTEGER NOT NULL, `datasetVersion` INTEGER NOT NULL, `coverageNote` TEXT NOT NULL, `licenseNote` TEXT NOT NULL, `sources` TEXT NOT NULL, `morphology` TEXT NOT NULL, `sortOrder` INTEGER NOT NULL, PRIMARY KEY(`code`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `vocabulary` (`id` TEXT NOT NULL, `languageCode` TEXT NOT NULL, `headword` TEXT NOT NULL, `normalizedHeadword` TEXT NOT NULL, `pos` TEXT NOT NULL, `gender` TEXT, `book` TEXT NOT NULL DEFAULT 'BASIC', `definitions` TEXT NOT NULL, `grammaticalClass` TEXT NOT NULL, `inflectionType` TEXT NOT NULL, `principalParts` TEXT NOT NULL, `irregularForms` TEXT NOT NULL, `etymology` TEXT, `etymologyNote` TEXT, `sourceReference` TEXT, `frequencyRank` INTEGER, `datasetVersion` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_vocabulary_languageCode` ON `vocabulary` (`languageCode`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_vocabulary_normalizedHeadword` ON `vocabulary` (`normalizedHeadword`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_vocabulary_pos` ON `vocabulary` (`pos`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_vocabulary_languageCode_book` ON `vocabulary` (`languageCode`, `book`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `inflected_forms` (`id` TEXT NOT NULL, `vocabularyId` TEXT NOT NULL, `writtenForm` TEXT NOT NULL, `normalizedForm` TEXT NOT NULL, `dimensions` TEXT NOT NULL, `quizEligible` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_inflected_forms_vocabularyId` ON `inflected_forms` (`vocabularyId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_inflected_forms_normalizedForm` ON `inflected_forms` (`normalizedForm`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `example_sentences` (`id` TEXT NOT NULL, `vocabularyId` TEXT NOT NULL, `originalText` TEXT NOT NULL, `translation` TEXT NOT NULL, `author` TEXT, `work` TEXT, `passageReference` TEXT, `isAuthentic` INTEGER NOT NULL, `targetForm` TEXT, `targetFormNormalized` TEXT, `targetDimensions` TEXT NOT NULL DEFAULT '', PRIMARY KEY(`id`))");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_example_sentences_vocabularyId` ON `example_sentences` (`vocabularyId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `learning_records` (`vocabularyId` TEXT NOT NULL, `languageCode` TEXT NOT NULL, `status` TEXT NOT NULL, `introducedOn` TEXT, `firstLearnedOn` TEXT, `lastReviewedOn` TEXT, `totalAttempts` INTEGER NOT NULL, `correctAttempts` INTEGER NOT NULL, `incorrectAttempts` INTEGER NOT NULL, `difficult` INTEGER NOT NULL, `mastered` INTEGER NOT NULL, PRIMARY KEY(`vocabularyId`))");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_learning_records_languageCode` ON `learning_records` (`languageCode`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_learning_records_status` ON `learning_records` (`status`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `daily_tasks` (`id` TEXT NOT NULL, `languageCode` TEXT NOT NULL, `learningDate` TEXT NOT NULL, `book` TEXT NOT NULL DEFAULT 'BASIC', `plannedNewWords` INTEGER NOT NULL, `completedNewWords` INTEGER NOT NULL, `preliminaryStatus` TEXT NOT NULL, `newLearningStatus` TEXT NOT NULL, `postTestStatus` TEXT NOT NULL, `overallStatus` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_daily_tasks_languageCode` ON `daily_tasks` (`languageCode`)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_daily_tasks_languageCode_book_learningDate` ON `daily_tasks` (`languageCode`, `book`, `learningDate`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `saved_words` (`vocabularyId` TEXT NOT NULL, `languageCode` TEXT NOT NULL, `savedOn` TEXT NOT NULL, PRIMARY KEY(`vocabularyId`))");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_saved_words_languageCode` ON `saved_words` (`languageCode`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `learning_sessions` (`id` TEXT NOT NULL, `languageCode` TEXT NOT NULL, `learningDate` TEXT NOT NULL, `book` TEXT NOT NULL DEFAULT 'BASIC', `vocabularyIds` TEXT NOT NULL, `targetCount` INTEGER NOT NULL, `cardCursor` INTEGER NOT NULL, `quizRound` INTEGER NOT NULL, `passedIds` TEXT NOT NULL, `pendingQuizIds` TEXT NOT NULL, `phase` TEXT NOT NULL, `activeQuizSessionId` TEXT, `completedOn` TEXT, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_learning_sessions_languageCode` ON `learning_sessions` (`languageCode`)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_learning_sessions_languageCode_book_learningDate` ON `learning_sessions` (`languageCode`, `book`, `learningDate`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `test_sessions` (`id` TEXT NOT NULL, `languageCode` TEXT NOT NULL, `book` TEXT NOT NULL DEFAULT 'BASIC', `testType` TEXT NOT NULL, `sessionDate` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `completedAt` INTEGER, `totalVocabularyItems` INTEGER NOT NULL, `currentGroup` INTEGER NOT NULL, `currentQuestion` INTEGER NOT NULL, `status` TEXT NOT NULL, `scorePercent` REAL, `passed` INTEGER, `dailyTaskId` TEXT, `learningSessionId` TEXT, `vocabularyIds` TEXT NOT NULL, `firstAttemptCorrect` INTEGER NOT NULL, `firstAttemptTotal` INTEGER NOT NULL, `passingThreshold` INTEGER, PRIMARY KEY(`id`))");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_test_sessions_languageCode` ON `test_sessions` (`languageCode`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_test_sessions_sessionDate` ON `test_sessions` (`sessionDate`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_test_sessions_status` ON `test_sessions` (`status`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_test_sessions_testType` ON `test_sessions` (`testType`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_test_sessions_languageCode_book` ON `test_sessions` (`languageCode`, `book`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `test_questions` (`id` TEXT NOT NULL, `sessionId` TEXT NOT NULL, `vocabularyId` TEXT NOT NULL, `questionType` TEXT NOT NULL, `prompt` TEXT NOT NULL, `contextTranslation` TEXT, `highlightText` TEXT, `options` TEXT NOT NULL, `correctIndex` INTEGER NOT NULL, `selectedIndex` INTEGER, `isCorrect` INTEGER, `retryCount` INTEGER NOT NULL, `isReviewCopy` INTEGER NOT NULL, `groupIndex` INTEGER NOT NULL, `orderInGroup` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_test_questions_sessionId` ON `test_questions` (`sessionId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_test_questions_vocabularyId` ON `test_questions` (`vocabularyId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_test_questions_groupIndex` ON `test_questions` (`groupIndex`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `legacy_word_keys` (`vocabularyId` TEXT NOT NULL, `languageCode` TEXT NOT NULL, `normalizedHeadword` TEXT NOT NULL, `pos` TEXT NOT NULL, PRIMARY KEY(`vocabularyId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `reading_works` (`id` TEXT NOT NULL, `languageCode` TEXT NOT NULL, `author` TEXT NOT NULL, `title` TEXT NOT NULL, `summary` TEXT NOT NULL, `textSource` TEXT NOT NULL, `translationSource` TEXT NOT NULL, `bookCount` INTEGER NOT NULL, `sortOrder` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_reading_works_languageCode` ON `reading_works` (`languageCode`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `reading_lexemes` (`id` TEXT NOT NULL, `workId` TEXT NOT NULL, `surface` TEXT NOT NULL, `lemma` TEXT NOT NULL, `pos` TEXT NOT NULL, `gloss` TEXT NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_reading_lexemes_workId` ON `reading_lexemes` (`workId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `reading_sections` (`id` TEXT NOT NULL, `workId` TEXT NOT NULL, `bookNumber` INTEGER NOT NULL, `bookLabel` TEXT NOT NULL, `sectionLabel` TEXT NOT NULL, `heading` TEXT NOT NULL, `originalText` TEXT NOT NULL, `translationText` TEXT NOT NULL, `newWordIds` TEXT NOT NULL, `sortOrder` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_reading_sections_workId` ON `reading_sections` (`workId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_reading_sections_workId_bookNumber` ON `reading_sections` (`workId`, `bookNumber`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `reading_progress` (`workId` TEXT NOT NULL, `status` TEXT NOT NULL, `startedAt` INTEGER, `finishedAt` INTEGER, `lastTestedAt` INTEGER, `attempts` INTEGER NOT NULL, `lastCorrect` INTEGER, `lastTotal` INTEGER, `bestCorrect` INTEGER NOT NULL, PRIMARY KEY(`workId`))");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_reading_progress_status` ON `reading_progress` (`status`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `reading_section_progress` (`sectionId` TEXT NOT NULL, `workId` TEXT NOT NULL, `status` TEXT NOT NULL, `startedAt` INTEGER, `finishedAt` INTEGER, `lastTestedAt` INTEGER, `attempts` INTEGER NOT NULL, `lastCorrect` INTEGER, `lastTotal` INTEGER, `bestCorrect` INTEGER NOT NULL, PRIMARY KEY(`sectionId`))");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_reading_section_progress_workId` ON `reading_section_progress` (`workId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_reading_section_progress_status` ON `reading_section_progress` (`status`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '3de3277a0b9650a6c360f91b2370f3c5')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `languages`");
        db.execSQL("DROP TABLE IF EXISTS `vocabulary`");
        db.execSQL("DROP TABLE IF EXISTS `inflected_forms`");
        db.execSQL("DROP TABLE IF EXISTS `example_sentences`");
        db.execSQL("DROP TABLE IF EXISTS `learning_records`");
        db.execSQL("DROP TABLE IF EXISTS `daily_tasks`");
        db.execSQL("DROP TABLE IF EXISTS `saved_words`");
        db.execSQL("DROP TABLE IF EXISTS `learning_sessions`");
        db.execSQL("DROP TABLE IF EXISTS `test_sessions`");
        db.execSQL("DROP TABLE IF EXISTS `test_questions`");
        db.execSQL("DROP TABLE IF EXISTS `legacy_word_keys`");
        db.execSQL("DROP TABLE IF EXISTS `reading_works`");
        db.execSQL("DROP TABLE IF EXISTS `reading_lexemes`");
        db.execSQL("DROP TABLE IF EXISTS `reading_sections`");
        db.execSQL("DROP TABLE IF EXISTS `reading_progress`");
        db.execSQL("DROP TABLE IF EXISTS `reading_section_progress`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsLanguages = new HashMap<String, TableInfo.Column>(11);
        _columnsLanguages.put("code", new TableInfo.Column("code", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLanguages.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLanguages.put("nativeName", new TableInfo.Column("nativeName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLanguages.put("script", new TableInfo.Column("script", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLanguages.put("enabled", new TableInfo.Column("enabled", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLanguages.put("datasetVersion", new TableInfo.Column("datasetVersion", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLanguages.put("coverageNote", new TableInfo.Column("coverageNote", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLanguages.put("licenseNote", new TableInfo.Column("licenseNote", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLanguages.put("sources", new TableInfo.Column("sources", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLanguages.put("morphology", new TableInfo.Column("morphology", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLanguages.put("sortOrder", new TableInfo.Column("sortOrder", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysLanguages = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesLanguages = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoLanguages = new TableInfo("languages", _columnsLanguages, _foreignKeysLanguages, _indicesLanguages);
        final TableInfo _existingLanguages = TableInfo.read(db, "languages");
        if (!_infoLanguages.equals(_existingLanguages)) {
          return new RoomOpenHelper.ValidationResult(false, "languages(com.baytalhikmah.data.local.LanguageEntity).\n"
                  + " Expected:\n" + _infoLanguages + "\n"
                  + " Found:\n" + _existingLanguages);
        }
        final HashMap<String, TableInfo.Column> _columnsVocabulary = new HashMap<String, TableInfo.Column>(17);
        _columnsVocabulary.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVocabulary.put("languageCode", new TableInfo.Column("languageCode", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVocabulary.put("headword", new TableInfo.Column("headword", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVocabulary.put("normalizedHeadword", new TableInfo.Column("normalizedHeadword", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVocabulary.put("pos", new TableInfo.Column("pos", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVocabulary.put("gender", new TableInfo.Column("gender", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVocabulary.put("book", new TableInfo.Column("book", "TEXT", true, 0, "'BASIC'", TableInfo.CREATED_FROM_ENTITY));
        _columnsVocabulary.put("definitions", new TableInfo.Column("definitions", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVocabulary.put("grammaticalClass", new TableInfo.Column("grammaticalClass", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVocabulary.put("inflectionType", new TableInfo.Column("inflectionType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVocabulary.put("principalParts", new TableInfo.Column("principalParts", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVocabulary.put("irregularForms", new TableInfo.Column("irregularForms", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVocabulary.put("etymology", new TableInfo.Column("etymology", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVocabulary.put("etymologyNote", new TableInfo.Column("etymologyNote", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVocabulary.put("sourceReference", new TableInfo.Column("sourceReference", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVocabulary.put("frequencyRank", new TableInfo.Column("frequencyRank", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVocabulary.put("datasetVersion", new TableInfo.Column("datasetVersion", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysVocabulary = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesVocabulary = new HashSet<TableInfo.Index>(4);
        _indicesVocabulary.add(new TableInfo.Index("index_vocabulary_languageCode", false, Arrays.asList("languageCode"), Arrays.asList("ASC")));
        _indicesVocabulary.add(new TableInfo.Index("index_vocabulary_normalizedHeadword", false, Arrays.asList("normalizedHeadword"), Arrays.asList("ASC")));
        _indicesVocabulary.add(new TableInfo.Index("index_vocabulary_pos", false, Arrays.asList("pos"), Arrays.asList("ASC")));
        _indicesVocabulary.add(new TableInfo.Index("index_vocabulary_languageCode_book", false, Arrays.asList("languageCode", "book"), Arrays.asList("ASC", "ASC")));
        final TableInfo _infoVocabulary = new TableInfo("vocabulary", _columnsVocabulary, _foreignKeysVocabulary, _indicesVocabulary);
        final TableInfo _existingVocabulary = TableInfo.read(db, "vocabulary");
        if (!_infoVocabulary.equals(_existingVocabulary)) {
          return new RoomOpenHelper.ValidationResult(false, "vocabulary(com.baytalhikmah.data.local.VocabularyEntity).\n"
                  + " Expected:\n" + _infoVocabulary + "\n"
                  + " Found:\n" + _existingVocabulary);
        }
        final HashMap<String, TableInfo.Column> _columnsInflectedForms = new HashMap<String, TableInfo.Column>(6);
        _columnsInflectedForms.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInflectedForms.put("vocabularyId", new TableInfo.Column("vocabularyId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInflectedForms.put("writtenForm", new TableInfo.Column("writtenForm", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInflectedForms.put("normalizedForm", new TableInfo.Column("normalizedForm", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInflectedForms.put("dimensions", new TableInfo.Column("dimensions", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInflectedForms.put("quizEligible", new TableInfo.Column("quizEligible", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysInflectedForms = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesInflectedForms = new HashSet<TableInfo.Index>(2);
        _indicesInflectedForms.add(new TableInfo.Index("index_inflected_forms_vocabularyId", false, Arrays.asList("vocabularyId"), Arrays.asList("ASC")));
        _indicesInflectedForms.add(new TableInfo.Index("index_inflected_forms_normalizedForm", false, Arrays.asList("normalizedForm"), Arrays.asList("ASC")));
        final TableInfo _infoInflectedForms = new TableInfo("inflected_forms", _columnsInflectedForms, _foreignKeysInflectedForms, _indicesInflectedForms);
        final TableInfo _existingInflectedForms = TableInfo.read(db, "inflected_forms");
        if (!_infoInflectedForms.equals(_existingInflectedForms)) {
          return new RoomOpenHelper.ValidationResult(false, "inflected_forms(com.baytalhikmah.data.local.InflectedFormEntity).\n"
                  + " Expected:\n" + _infoInflectedForms + "\n"
                  + " Found:\n" + _existingInflectedForms);
        }
        final HashMap<String, TableInfo.Column> _columnsExampleSentences = new HashMap<String, TableInfo.Column>(11);
        _columnsExampleSentences.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExampleSentences.put("vocabularyId", new TableInfo.Column("vocabularyId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExampleSentences.put("originalText", new TableInfo.Column("originalText", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExampleSentences.put("translation", new TableInfo.Column("translation", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExampleSentences.put("author", new TableInfo.Column("author", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExampleSentences.put("work", new TableInfo.Column("work", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExampleSentences.put("passageReference", new TableInfo.Column("passageReference", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExampleSentences.put("isAuthentic", new TableInfo.Column("isAuthentic", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExampleSentences.put("targetForm", new TableInfo.Column("targetForm", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExampleSentences.put("targetFormNormalized", new TableInfo.Column("targetFormNormalized", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExampleSentences.put("targetDimensions", new TableInfo.Column("targetDimensions", "TEXT", true, 0, "''", TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysExampleSentences = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesExampleSentences = new HashSet<TableInfo.Index>(1);
        _indicesExampleSentences.add(new TableInfo.Index("index_example_sentences_vocabularyId", false, Arrays.asList("vocabularyId"), Arrays.asList("ASC")));
        final TableInfo _infoExampleSentences = new TableInfo("example_sentences", _columnsExampleSentences, _foreignKeysExampleSentences, _indicesExampleSentences);
        final TableInfo _existingExampleSentences = TableInfo.read(db, "example_sentences");
        if (!_infoExampleSentences.equals(_existingExampleSentences)) {
          return new RoomOpenHelper.ValidationResult(false, "example_sentences(com.baytalhikmah.data.local.ExampleSentenceEntity).\n"
                  + " Expected:\n" + _infoExampleSentences + "\n"
                  + " Found:\n" + _existingExampleSentences);
        }
        final HashMap<String, TableInfo.Column> _columnsLearningRecords = new HashMap<String, TableInfo.Column>(11);
        _columnsLearningRecords.put("vocabularyId", new TableInfo.Column("vocabularyId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLearningRecords.put("languageCode", new TableInfo.Column("languageCode", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLearningRecords.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLearningRecords.put("introducedOn", new TableInfo.Column("introducedOn", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLearningRecords.put("firstLearnedOn", new TableInfo.Column("firstLearnedOn", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLearningRecords.put("lastReviewedOn", new TableInfo.Column("lastReviewedOn", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLearningRecords.put("totalAttempts", new TableInfo.Column("totalAttempts", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLearningRecords.put("correctAttempts", new TableInfo.Column("correctAttempts", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLearningRecords.put("incorrectAttempts", new TableInfo.Column("incorrectAttempts", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLearningRecords.put("difficult", new TableInfo.Column("difficult", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLearningRecords.put("mastered", new TableInfo.Column("mastered", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysLearningRecords = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesLearningRecords = new HashSet<TableInfo.Index>(2);
        _indicesLearningRecords.add(new TableInfo.Index("index_learning_records_languageCode", false, Arrays.asList("languageCode"), Arrays.asList("ASC")));
        _indicesLearningRecords.add(new TableInfo.Index("index_learning_records_status", false, Arrays.asList("status"), Arrays.asList("ASC")));
        final TableInfo _infoLearningRecords = new TableInfo("learning_records", _columnsLearningRecords, _foreignKeysLearningRecords, _indicesLearningRecords);
        final TableInfo _existingLearningRecords = TableInfo.read(db, "learning_records");
        if (!_infoLearningRecords.equals(_existingLearningRecords)) {
          return new RoomOpenHelper.ValidationResult(false, "learning_records(com.baytalhikmah.data.local.LearningRecordEntity).\n"
                  + " Expected:\n" + _infoLearningRecords + "\n"
                  + " Found:\n" + _existingLearningRecords);
        }
        final HashMap<String, TableInfo.Column> _columnsDailyTasks = new HashMap<String, TableInfo.Column>(12);
        _columnsDailyTasks.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyTasks.put("languageCode", new TableInfo.Column("languageCode", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyTasks.put("learningDate", new TableInfo.Column("learningDate", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyTasks.put("book", new TableInfo.Column("book", "TEXT", true, 0, "'BASIC'", TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyTasks.put("plannedNewWords", new TableInfo.Column("plannedNewWords", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyTasks.put("completedNewWords", new TableInfo.Column("completedNewWords", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyTasks.put("preliminaryStatus", new TableInfo.Column("preliminaryStatus", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyTasks.put("newLearningStatus", new TableInfo.Column("newLearningStatus", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyTasks.put("postTestStatus", new TableInfo.Column("postTestStatus", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyTasks.put("overallStatus", new TableInfo.Column("overallStatus", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyTasks.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyTasks.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDailyTasks = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesDailyTasks = new HashSet<TableInfo.Index>(2);
        _indicesDailyTasks.add(new TableInfo.Index("index_daily_tasks_languageCode", false, Arrays.asList("languageCode"), Arrays.asList("ASC")));
        _indicesDailyTasks.add(new TableInfo.Index("index_daily_tasks_languageCode_book_learningDate", true, Arrays.asList("languageCode", "book", "learningDate"), Arrays.asList("ASC", "ASC", "ASC")));
        final TableInfo _infoDailyTasks = new TableInfo("daily_tasks", _columnsDailyTasks, _foreignKeysDailyTasks, _indicesDailyTasks);
        final TableInfo _existingDailyTasks = TableInfo.read(db, "daily_tasks");
        if (!_infoDailyTasks.equals(_existingDailyTasks)) {
          return new RoomOpenHelper.ValidationResult(false, "daily_tasks(com.baytalhikmah.data.local.DailyTaskEntity).\n"
                  + " Expected:\n" + _infoDailyTasks + "\n"
                  + " Found:\n" + _existingDailyTasks);
        }
        final HashMap<String, TableInfo.Column> _columnsSavedWords = new HashMap<String, TableInfo.Column>(3);
        _columnsSavedWords.put("vocabularyId", new TableInfo.Column("vocabularyId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSavedWords.put("languageCode", new TableInfo.Column("languageCode", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSavedWords.put("savedOn", new TableInfo.Column("savedOn", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSavedWords = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSavedWords = new HashSet<TableInfo.Index>(1);
        _indicesSavedWords.add(new TableInfo.Index("index_saved_words_languageCode", false, Arrays.asList("languageCode"), Arrays.asList("ASC")));
        final TableInfo _infoSavedWords = new TableInfo("saved_words", _columnsSavedWords, _foreignKeysSavedWords, _indicesSavedWords);
        final TableInfo _existingSavedWords = TableInfo.read(db, "saved_words");
        if (!_infoSavedWords.equals(_existingSavedWords)) {
          return new RoomOpenHelper.ValidationResult(false, "saved_words(com.baytalhikmah.data.local.SavedWordEntity).\n"
                  + " Expected:\n" + _infoSavedWords + "\n"
                  + " Found:\n" + _existingSavedWords);
        }
        final HashMap<String, TableInfo.Column> _columnsLearningSessions = new HashMap<String, TableInfo.Column>(15);
        _columnsLearningSessions.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLearningSessions.put("languageCode", new TableInfo.Column("languageCode", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLearningSessions.put("learningDate", new TableInfo.Column("learningDate", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLearningSessions.put("book", new TableInfo.Column("book", "TEXT", true, 0, "'BASIC'", TableInfo.CREATED_FROM_ENTITY));
        _columnsLearningSessions.put("vocabularyIds", new TableInfo.Column("vocabularyIds", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLearningSessions.put("targetCount", new TableInfo.Column("targetCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLearningSessions.put("cardCursor", new TableInfo.Column("cardCursor", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLearningSessions.put("quizRound", new TableInfo.Column("quizRound", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLearningSessions.put("passedIds", new TableInfo.Column("passedIds", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLearningSessions.put("pendingQuizIds", new TableInfo.Column("pendingQuizIds", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLearningSessions.put("phase", new TableInfo.Column("phase", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLearningSessions.put("activeQuizSessionId", new TableInfo.Column("activeQuizSessionId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLearningSessions.put("completedOn", new TableInfo.Column("completedOn", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLearningSessions.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLearningSessions.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysLearningSessions = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesLearningSessions = new HashSet<TableInfo.Index>(2);
        _indicesLearningSessions.add(new TableInfo.Index("index_learning_sessions_languageCode", false, Arrays.asList("languageCode"), Arrays.asList("ASC")));
        _indicesLearningSessions.add(new TableInfo.Index("index_learning_sessions_languageCode_book_learningDate", true, Arrays.asList("languageCode", "book", "learningDate"), Arrays.asList("ASC", "ASC", "ASC")));
        final TableInfo _infoLearningSessions = new TableInfo("learning_sessions", _columnsLearningSessions, _foreignKeysLearningSessions, _indicesLearningSessions);
        final TableInfo _existingLearningSessions = TableInfo.read(db, "learning_sessions");
        if (!_infoLearningSessions.equals(_existingLearningSessions)) {
          return new RoomOpenHelper.ValidationResult(false, "learning_sessions(com.baytalhikmah.data.local.LearningSessionEntity).\n"
                  + " Expected:\n" + _infoLearningSessions + "\n"
                  + " Found:\n" + _existingLearningSessions);
        }
        final HashMap<String, TableInfo.Column> _columnsTestSessions = new HashMap<String, TableInfo.Column>(19);
        _columnsTestSessions.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTestSessions.put("languageCode", new TableInfo.Column("languageCode", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTestSessions.put("book", new TableInfo.Column("book", "TEXT", true, 0, "'BASIC'", TableInfo.CREATED_FROM_ENTITY));
        _columnsTestSessions.put("testType", new TableInfo.Column("testType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTestSessions.put("sessionDate", new TableInfo.Column("sessionDate", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTestSessions.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTestSessions.put("completedAt", new TableInfo.Column("completedAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTestSessions.put("totalVocabularyItems", new TableInfo.Column("totalVocabularyItems", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTestSessions.put("currentGroup", new TableInfo.Column("currentGroup", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTestSessions.put("currentQuestion", new TableInfo.Column("currentQuestion", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTestSessions.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTestSessions.put("scorePercent", new TableInfo.Column("scorePercent", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTestSessions.put("passed", new TableInfo.Column("passed", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTestSessions.put("dailyTaskId", new TableInfo.Column("dailyTaskId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTestSessions.put("learningSessionId", new TableInfo.Column("learningSessionId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTestSessions.put("vocabularyIds", new TableInfo.Column("vocabularyIds", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTestSessions.put("firstAttemptCorrect", new TableInfo.Column("firstAttemptCorrect", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTestSessions.put("firstAttemptTotal", new TableInfo.Column("firstAttemptTotal", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTestSessions.put("passingThreshold", new TableInfo.Column("passingThreshold", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTestSessions = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTestSessions = new HashSet<TableInfo.Index>(5);
        _indicesTestSessions.add(new TableInfo.Index("index_test_sessions_languageCode", false, Arrays.asList("languageCode"), Arrays.asList("ASC")));
        _indicesTestSessions.add(new TableInfo.Index("index_test_sessions_sessionDate", false, Arrays.asList("sessionDate"), Arrays.asList("ASC")));
        _indicesTestSessions.add(new TableInfo.Index("index_test_sessions_status", false, Arrays.asList("status"), Arrays.asList("ASC")));
        _indicesTestSessions.add(new TableInfo.Index("index_test_sessions_testType", false, Arrays.asList("testType"), Arrays.asList("ASC")));
        _indicesTestSessions.add(new TableInfo.Index("index_test_sessions_languageCode_book", false, Arrays.asList("languageCode", "book"), Arrays.asList("ASC", "ASC")));
        final TableInfo _infoTestSessions = new TableInfo("test_sessions", _columnsTestSessions, _foreignKeysTestSessions, _indicesTestSessions);
        final TableInfo _existingTestSessions = TableInfo.read(db, "test_sessions");
        if (!_infoTestSessions.equals(_existingTestSessions)) {
          return new RoomOpenHelper.ValidationResult(false, "test_sessions(com.baytalhikmah.data.local.TestSessionEntity).\n"
                  + " Expected:\n" + _infoTestSessions + "\n"
                  + " Found:\n" + _existingTestSessions);
        }
        final HashMap<String, TableInfo.Column> _columnsTestQuestions = new HashMap<String, TableInfo.Column>(15);
        _columnsTestQuestions.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTestQuestions.put("sessionId", new TableInfo.Column("sessionId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTestQuestions.put("vocabularyId", new TableInfo.Column("vocabularyId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTestQuestions.put("questionType", new TableInfo.Column("questionType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTestQuestions.put("prompt", new TableInfo.Column("prompt", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTestQuestions.put("contextTranslation", new TableInfo.Column("contextTranslation", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTestQuestions.put("highlightText", new TableInfo.Column("highlightText", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTestQuestions.put("options", new TableInfo.Column("options", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTestQuestions.put("correctIndex", new TableInfo.Column("correctIndex", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTestQuestions.put("selectedIndex", new TableInfo.Column("selectedIndex", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTestQuestions.put("isCorrect", new TableInfo.Column("isCorrect", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTestQuestions.put("retryCount", new TableInfo.Column("retryCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTestQuestions.put("isReviewCopy", new TableInfo.Column("isReviewCopy", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTestQuestions.put("groupIndex", new TableInfo.Column("groupIndex", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTestQuestions.put("orderInGroup", new TableInfo.Column("orderInGroup", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTestQuestions = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTestQuestions = new HashSet<TableInfo.Index>(3);
        _indicesTestQuestions.add(new TableInfo.Index("index_test_questions_sessionId", false, Arrays.asList("sessionId"), Arrays.asList("ASC")));
        _indicesTestQuestions.add(new TableInfo.Index("index_test_questions_vocabularyId", false, Arrays.asList("vocabularyId"), Arrays.asList("ASC")));
        _indicesTestQuestions.add(new TableInfo.Index("index_test_questions_groupIndex", false, Arrays.asList("groupIndex"), Arrays.asList("ASC")));
        final TableInfo _infoTestQuestions = new TableInfo("test_questions", _columnsTestQuestions, _foreignKeysTestQuestions, _indicesTestQuestions);
        final TableInfo _existingTestQuestions = TableInfo.read(db, "test_questions");
        if (!_infoTestQuestions.equals(_existingTestQuestions)) {
          return new RoomOpenHelper.ValidationResult(false, "test_questions(com.baytalhikmah.data.local.TestQuestionEntity).\n"
                  + " Expected:\n" + _infoTestQuestions + "\n"
                  + " Found:\n" + _existingTestQuestions);
        }
        final HashMap<String, TableInfo.Column> _columnsLegacyWordKeys = new HashMap<String, TableInfo.Column>(4);
        _columnsLegacyWordKeys.put("vocabularyId", new TableInfo.Column("vocabularyId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLegacyWordKeys.put("languageCode", new TableInfo.Column("languageCode", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLegacyWordKeys.put("normalizedHeadword", new TableInfo.Column("normalizedHeadword", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLegacyWordKeys.put("pos", new TableInfo.Column("pos", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysLegacyWordKeys = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesLegacyWordKeys = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoLegacyWordKeys = new TableInfo("legacy_word_keys", _columnsLegacyWordKeys, _foreignKeysLegacyWordKeys, _indicesLegacyWordKeys);
        final TableInfo _existingLegacyWordKeys = TableInfo.read(db, "legacy_word_keys");
        if (!_infoLegacyWordKeys.equals(_existingLegacyWordKeys)) {
          return new RoomOpenHelper.ValidationResult(false, "legacy_word_keys(com.baytalhikmah.data.local.LegacyWordKeyEntity).\n"
                  + " Expected:\n" + _infoLegacyWordKeys + "\n"
                  + " Found:\n" + _existingLegacyWordKeys);
        }
        final HashMap<String, TableInfo.Column> _columnsReadingWorks = new HashMap<String, TableInfo.Column>(9);
        _columnsReadingWorks.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReadingWorks.put("languageCode", new TableInfo.Column("languageCode", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReadingWorks.put("author", new TableInfo.Column("author", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReadingWorks.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReadingWorks.put("summary", new TableInfo.Column("summary", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReadingWorks.put("textSource", new TableInfo.Column("textSource", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReadingWorks.put("translationSource", new TableInfo.Column("translationSource", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReadingWorks.put("bookCount", new TableInfo.Column("bookCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReadingWorks.put("sortOrder", new TableInfo.Column("sortOrder", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysReadingWorks = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesReadingWorks = new HashSet<TableInfo.Index>(1);
        _indicesReadingWorks.add(new TableInfo.Index("index_reading_works_languageCode", false, Arrays.asList("languageCode"), Arrays.asList("ASC")));
        final TableInfo _infoReadingWorks = new TableInfo("reading_works", _columnsReadingWorks, _foreignKeysReadingWorks, _indicesReadingWorks);
        final TableInfo _existingReadingWorks = TableInfo.read(db, "reading_works");
        if (!_infoReadingWorks.equals(_existingReadingWorks)) {
          return new RoomOpenHelper.ValidationResult(false, "reading_works(com.baytalhikmah.data.local.ReadingWorkEntity).\n"
                  + " Expected:\n" + _infoReadingWorks + "\n"
                  + " Found:\n" + _existingReadingWorks);
        }
        final HashMap<String, TableInfo.Column> _columnsReadingLexemes = new HashMap<String, TableInfo.Column>(6);
        _columnsReadingLexemes.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReadingLexemes.put("workId", new TableInfo.Column("workId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReadingLexemes.put("surface", new TableInfo.Column("surface", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReadingLexemes.put("lemma", new TableInfo.Column("lemma", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReadingLexemes.put("pos", new TableInfo.Column("pos", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReadingLexemes.put("gloss", new TableInfo.Column("gloss", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysReadingLexemes = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesReadingLexemes = new HashSet<TableInfo.Index>(1);
        _indicesReadingLexemes.add(new TableInfo.Index("index_reading_lexemes_workId", false, Arrays.asList("workId"), Arrays.asList("ASC")));
        final TableInfo _infoReadingLexemes = new TableInfo("reading_lexemes", _columnsReadingLexemes, _foreignKeysReadingLexemes, _indicesReadingLexemes);
        final TableInfo _existingReadingLexemes = TableInfo.read(db, "reading_lexemes");
        if (!_infoReadingLexemes.equals(_existingReadingLexemes)) {
          return new RoomOpenHelper.ValidationResult(false, "reading_lexemes(com.baytalhikmah.data.local.ReadingLexemeEntity).\n"
                  + " Expected:\n" + _infoReadingLexemes + "\n"
                  + " Found:\n" + _existingReadingLexemes);
        }
        final HashMap<String, TableInfo.Column> _columnsReadingSections = new HashMap<String, TableInfo.Column>(10);
        _columnsReadingSections.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReadingSections.put("workId", new TableInfo.Column("workId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReadingSections.put("bookNumber", new TableInfo.Column("bookNumber", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReadingSections.put("bookLabel", new TableInfo.Column("bookLabel", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReadingSections.put("sectionLabel", new TableInfo.Column("sectionLabel", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReadingSections.put("heading", new TableInfo.Column("heading", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReadingSections.put("originalText", new TableInfo.Column("originalText", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReadingSections.put("translationText", new TableInfo.Column("translationText", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReadingSections.put("newWordIds", new TableInfo.Column("newWordIds", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReadingSections.put("sortOrder", new TableInfo.Column("sortOrder", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysReadingSections = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesReadingSections = new HashSet<TableInfo.Index>(2);
        _indicesReadingSections.add(new TableInfo.Index("index_reading_sections_workId", false, Arrays.asList("workId"), Arrays.asList("ASC")));
        _indicesReadingSections.add(new TableInfo.Index("index_reading_sections_workId_bookNumber", false, Arrays.asList("workId", "bookNumber"), Arrays.asList("ASC", "ASC")));
        final TableInfo _infoReadingSections = new TableInfo("reading_sections", _columnsReadingSections, _foreignKeysReadingSections, _indicesReadingSections);
        final TableInfo _existingReadingSections = TableInfo.read(db, "reading_sections");
        if (!_infoReadingSections.equals(_existingReadingSections)) {
          return new RoomOpenHelper.ValidationResult(false, "reading_sections(com.baytalhikmah.data.local.ReadingSectionEntity).\n"
                  + " Expected:\n" + _infoReadingSections + "\n"
                  + " Found:\n" + _existingReadingSections);
        }
        final HashMap<String, TableInfo.Column> _columnsReadingProgress = new HashMap<String, TableInfo.Column>(9);
        _columnsReadingProgress.put("workId", new TableInfo.Column("workId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReadingProgress.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReadingProgress.put("startedAt", new TableInfo.Column("startedAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReadingProgress.put("finishedAt", new TableInfo.Column("finishedAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReadingProgress.put("lastTestedAt", new TableInfo.Column("lastTestedAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReadingProgress.put("attempts", new TableInfo.Column("attempts", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReadingProgress.put("lastCorrect", new TableInfo.Column("lastCorrect", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReadingProgress.put("lastTotal", new TableInfo.Column("lastTotal", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReadingProgress.put("bestCorrect", new TableInfo.Column("bestCorrect", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysReadingProgress = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesReadingProgress = new HashSet<TableInfo.Index>(1);
        _indicesReadingProgress.add(new TableInfo.Index("index_reading_progress_status", false, Arrays.asList("status"), Arrays.asList("ASC")));
        final TableInfo _infoReadingProgress = new TableInfo("reading_progress", _columnsReadingProgress, _foreignKeysReadingProgress, _indicesReadingProgress);
        final TableInfo _existingReadingProgress = TableInfo.read(db, "reading_progress");
        if (!_infoReadingProgress.equals(_existingReadingProgress)) {
          return new RoomOpenHelper.ValidationResult(false, "reading_progress(com.baytalhikmah.data.local.ReadingProgressEntity).\n"
                  + " Expected:\n" + _infoReadingProgress + "\n"
                  + " Found:\n" + _existingReadingProgress);
        }
        final HashMap<String, TableInfo.Column> _columnsReadingSectionProgress = new HashMap<String, TableInfo.Column>(10);
        _columnsReadingSectionProgress.put("sectionId", new TableInfo.Column("sectionId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReadingSectionProgress.put("workId", new TableInfo.Column("workId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReadingSectionProgress.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReadingSectionProgress.put("startedAt", new TableInfo.Column("startedAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReadingSectionProgress.put("finishedAt", new TableInfo.Column("finishedAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReadingSectionProgress.put("lastTestedAt", new TableInfo.Column("lastTestedAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReadingSectionProgress.put("attempts", new TableInfo.Column("attempts", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReadingSectionProgress.put("lastCorrect", new TableInfo.Column("lastCorrect", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReadingSectionProgress.put("lastTotal", new TableInfo.Column("lastTotal", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReadingSectionProgress.put("bestCorrect", new TableInfo.Column("bestCorrect", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysReadingSectionProgress = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesReadingSectionProgress = new HashSet<TableInfo.Index>(2);
        _indicesReadingSectionProgress.add(new TableInfo.Index("index_reading_section_progress_workId", false, Arrays.asList("workId"), Arrays.asList("ASC")));
        _indicesReadingSectionProgress.add(new TableInfo.Index("index_reading_section_progress_status", false, Arrays.asList("status"), Arrays.asList("ASC")));
        final TableInfo _infoReadingSectionProgress = new TableInfo("reading_section_progress", _columnsReadingSectionProgress, _foreignKeysReadingSectionProgress, _indicesReadingSectionProgress);
        final TableInfo _existingReadingSectionProgress = TableInfo.read(db, "reading_section_progress");
        if (!_infoReadingSectionProgress.equals(_existingReadingSectionProgress)) {
          return new RoomOpenHelper.ValidationResult(false, "reading_section_progress(com.baytalhikmah.data.local.ReadingSectionProgressEntity).\n"
                  + " Expected:\n" + _infoReadingSectionProgress + "\n"
                  + " Found:\n" + _existingReadingSectionProgress);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "3de3277a0b9650a6c360f91b2370f3c5", "8a694670fae2991c1dd59583be06aa65");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "languages","vocabulary","inflected_forms","example_sentences","learning_records","daily_tasks","saved_words","learning_sessions","test_sessions","test_questions","legacy_word_keys","reading_works","reading_lexemes","reading_sections","reading_progress","reading_section_progress");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `languages`");
      _db.execSQL("DELETE FROM `vocabulary`");
      _db.execSQL("DELETE FROM `inflected_forms`");
      _db.execSQL("DELETE FROM `example_sentences`");
      _db.execSQL("DELETE FROM `learning_records`");
      _db.execSQL("DELETE FROM `daily_tasks`");
      _db.execSQL("DELETE FROM `saved_words`");
      _db.execSQL("DELETE FROM `learning_sessions`");
      _db.execSQL("DELETE FROM `test_sessions`");
      _db.execSQL("DELETE FROM `test_questions`");
      _db.execSQL("DELETE FROM `legacy_word_keys`");
      _db.execSQL("DELETE FROM `reading_works`");
      _db.execSQL("DELETE FROM `reading_lexemes`");
      _db.execSQL("DELETE FROM `reading_sections`");
      _db.execSQL("DELETE FROM `reading_progress`");
      _db.execSQL("DELETE FROM `reading_section_progress`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(LanguageDao.class, LanguageDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(VocabularyDao.class, VocabularyDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(LearningDao.class, LearningDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(TestDao.class, TestDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ReadingDao.class, ReadingDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public LanguageDao languageDao() {
    if (_languageDao != null) {
      return _languageDao;
    } else {
      synchronized(this) {
        if(_languageDao == null) {
          _languageDao = new LanguageDao_Impl(this);
        }
        return _languageDao;
      }
    }
  }

  @Override
  public VocabularyDao vocabularyDao() {
    if (_vocabularyDao != null) {
      return _vocabularyDao;
    } else {
      synchronized(this) {
        if(_vocabularyDao == null) {
          _vocabularyDao = new VocabularyDao_Impl(this);
        }
        return _vocabularyDao;
      }
    }
  }

  @Override
  public LearningDao learningDao() {
    if (_learningDao != null) {
      return _learningDao;
    } else {
      synchronized(this) {
        if(_learningDao == null) {
          _learningDao = new LearningDao_Impl(this);
        }
        return _learningDao;
      }
    }
  }

  @Override
  public TestDao testDao() {
    if (_testDao != null) {
      return _testDao;
    } else {
      synchronized(this) {
        if(_testDao == null) {
          _testDao = new TestDao_Impl(this);
        }
        return _testDao;
      }
    }
  }

  @Override
  public ReadingDao readingDao() {
    if (_readingDao != null) {
      return _readingDao;
    } else {
      synchronized(this) {
        if(_readingDao == null) {
          _readingDao = new ReadingDao_Impl(this);
        }
        return _readingDao;
      }
    }
  }
}
