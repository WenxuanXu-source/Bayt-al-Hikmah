package com.baytalhikmah.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.baytalhikmah.data.local.Converters;
import com.baytalhikmah.data.local.ExampleSentenceEntity;
import com.baytalhikmah.data.local.InflectedFormEntity;
import com.baytalhikmah.data.local.VocabularyEntity;
import com.baytalhikmah.data.model.Gender;
import com.baytalhikmah.data.model.Pos;
import com.baytalhikmah.data.model.VocabBook;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class VocabularyDao_Impl implements VocabularyDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<VocabularyEntity> __insertionAdapterOfVocabularyEntity;

  private final Converters __converters = new Converters();

  private final EntityInsertionAdapter<InflectedFormEntity> __insertionAdapterOfInflectedFormEntity;

  private final EntityInsertionAdapter<ExampleSentenceEntity> __insertionAdapterOfExampleSentenceEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteFormsByPattern;

  private final SharedSQLiteStatement __preparedStmtOfDeleteExamplesByPattern;

  private final SharedSQLiteStatement __preparedStmtOfDeleteVocabularyByPattern;

  private final SharedSQLiteStatement __preparedStmtOfDeleteForms;

  private final SharedSQLiteStatement __preparedStmtOfDeleteExamples;

  private final SharedSQLiteStatement __preparedStmtOfDeleteLanguageForms;

  private final SharedSQLiteStatement __preparedStmtOfDeleteLanguageExamples;

  private final SharedSQLiteStatement __preparedStmtOfDeleteLanguageVocabulary;

  private final SharedSQLiteStatement __preparedStmtOfDelete;

  public VocabularyDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfVocabularyEntity = new EntityInsertionAdapter<VocabularyEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `vocabulary` (`id`,`languageCode`,`headword`,`normalizedHeadword`,`pos`,`gender`,`book`,`definitions`,`grammaticalClass`,`inflectionType`,`principalParts`,`irregularForms`,`etymology`,`etymologyNote`,`sourceReference`,`frequencyRank`,`datasetVersion`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final VocabularyEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getLanguageCode());
        statement.bindString(3, entity.getHeadword());
        statement.bindString(4, entity.getNormalizedHeadword());
        final String _tmp = __converters.posToString(entity.getPos());
        statement.bindString(5, _tmp);
        final String _tmp_1 = __converters.genderToString(entity.getGender());
        if (_tmp_1 == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, _tmp_1);
        }
        final String _tmp_2 = __converters.vocabBookToString(entity.getBook());
        statement.bindString(7, _tmp_2);
        final String _tmp_3 = __converters.stringListToJson(entity.getDefinitions());
        statement.bindString(8, _tmp_3);
        statement.bindString(9, entity.getGrammaticalClass());
        statement.bindString(10, entity.getInflectionType());
        final String _tmp_4 = __converters.stringListToJson(entity.getPrincipalParts());
        statement.bindString(11, _tmp_4);
        final String _tmp_5 = __converters.stringListToJson(entity.getIrregularForms());
        statement.bindString(12, _tmp_5);
        if (entity.getEtymology() == null) {
          statement.bindNull(13);
        } else {
          statement.bindString(13, entity.getEtymology());
        }
        if (entity.getEtymologyNote() == null) {
          statement.bindNull(14);
        } else {
          statement.bindString(14, entity.getEtymologyNote());
        }
        if (entity.getSourceReference() == null) {
          statement.bindNull(15);
        } else {
          statement.bindString(15, entity.getSourceReference());
        }
        if (entity.getFrequencyRank() == null) {
          statement.bindNull(16);
        } else {
          statement.bindLong(16, entity.getFrequencyRank());
        }
        statement.bindLong(17, entity.getDatasetVersion());
      }
    };
    this.__insertionAdapterOfInflectedFormEntity = new EntityInsertionAdapter<InflectedFormEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `inflected_forms` (`id`,`vocabularyId`,`writtenForm`,`normalizedForm`,`dimensions`,`quizEligible`) VALUES (?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final InflectedFormEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getVocabularyId());
        statement.bindString(3, entity.getWrittenForm());
        statement.bindString(4, entity.getNormalizedForm());
        final String _tmp = __converters.stringMapToJson(entity.getDimensions());
        statement.bindString(5, _tmp);
        final int _tmp_1 = entity.getQuizEligible() ? 1 : 0;
        statement.bindLong(6, _tmp_1);
      }
    };
    this.__insertionAdapterOfExampleSentenceEntity = new EntityInsertionAdapter<ExampleSentenceEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `example_sentences` (`id`,`vocabularyId`,`originalText`,`translation`,`author`,`work`,`passageReference`,`isAuthentic`,`targetForm`,`targetFormNormalized`,`targetDimensions`) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ExampleSentenceEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getVocabularyId());
        statement.bindString(3, entity.getOriginalText());
        statement.bindString(4, entity.getTranslation());
        if (entity.getAuthor() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getAuthor());
        }
        if (entity.getWork() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getWork());
        }
        if (entity.getPassageReference() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getPassageReference());
        }
        final int _tmp = entity.isAuthentic() ? 1 : 0;
        statement.bindLong(8, _tmp);
        if (entity.getTargetForm() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getTargetForm());
        }
        if (entity.getTargetFormNormalized() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getTargetFormNormalized());
        }
        final String _tmp_1 = __converters.stringMapToJson(entity.getTargetDimensions());
        statement.bindString(11, _tmp_1);
      }
    };
    this.__preparedStmtOfDeleteFormsByPattern = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM inflected_forms WHERE vocabularyId LIKE ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteExamplesByPattern = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM example_sentences WHERE vocabularyId LIKE ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteVocabularyByPattern = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM vocabulary WHERE id LIKE ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteForms = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM inflected_forms WHERE vocabularyId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteExamples = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM example_sentences WHERE vocabularyId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteLanguageForms = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM inflected_forms WHERE vocabularyId IN (SELECT id FROM vocabulary WHERE languageCode = ?)";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteLanguageExamples = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM example_sentences WHERE vocabularyId IN (SELECT id FROM vocabulary WHERE languageCode = ?)";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteLanguageVocabulary = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM vocabulary WHERE languageCode = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDelete = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM vocabulary WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object upsertAll(final List<VocabularyEntity> items,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfVocabularyEntity.insert(items);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object upsert(final VocabularyEntity item, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfVocabularyEntity.insert(item);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object upsertForms(final List<InflectedFormEntity> items,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfInflectedFormEntity.insert(items);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object upsertExamples(final List<ExampleSentenceEntity> items,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfExampleSentenceEntity.insert(items);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteFormsByPattern(final String pattern,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteFormsByPattern.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, pattern);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteFormsByPattern.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteExamplesByPattern(final String pattern,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteExamplesByPattern.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, pattern);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteExamplesByPattern.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteVocabularyByPattern(final String pattern,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteVocabularyByPattern.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, pattern);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteVocabularyByPattern.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteForms(final String vocabularyId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteForms.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, vocabularyId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteForms.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteExamples(final String vocabularyId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteExamples.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, vocabularyId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteExamples.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteLanguageForms(final String language,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteLanguageForms.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, language);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteLanguageForms.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteLanguageExamples(final String language,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteLanguageExamples.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, language);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteLanguageExamples.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteLanguageVocabulary(final String language,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteLanguageVocabulary.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, language);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteLanguageVocabulary.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final String id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDelete.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDelete.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<VocabularyEntity>> observeAll(final String language) {
    final String _sql = "SELECT * FROM vocabulary WHERE languageCode = ? ORDER BY normalizedHeadword";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, language);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"vocabulary"}, new Callable<List<VocabularyEntity>>() {
      @Override
      @NonNull
      public List<VocabularyEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLanguageCode = CursorUtil.getColumnIndexOrThrow(_cursor, "languageCode");
          final int _cursorIndexOfHeadword = CursorUtil.getColumnIndexOrThrow(_cursor, "headword");
          final int _cursorIndexOfNormalizedHeadword = CursorUtil.getColumnIndexOrThrow(_cursor, "normalizedHeadword");
          final int _cursorIndexOfPos = CursorUtil.getColumnIndexOrThrow(_cursor, "pos");
          final int _cursorIndexOfGender = CursorUtil.getColumnIndexOrThrow(_cursor, "gender");
          final int _cursorIndexOfBook = CursorUtil.getColumnIndexOrThrow(_cursor, "book");
          final int _cursorIndexOfDefinitions = CursorUtil.getColumnIndexOrThrow(_cursor, "definitions");
          final int _cursorIndexOfGrammaticalClass = CursorUtil.getColumnIndexOrThrow(_cursor, "grammaticalClass");
          final int _cursorIndexOfInflectionType = CursorUtil.getColumnIndexOrThrow(_cursor, "inflectionType");
          final int _cursorIndexOfPrincipalParts = CursorUtil.getColumnIndexOrThrow(_cursor, "principalParts");
          final int _cursorIndexOfIrregularForms = CursorUtil.getColumnIndexOrThrow(_cursor, "irregularForms");
          final int _cursorIndexOfEtymology = CursorUtil.getColumnIndexOrThrow(_cursor, "etymology");
          final int _cursorIndexOfEtymologyNote = CursorUtil.getColumnIndexOrThrow(_cursor, "etymologyNote");
          final int _cursorIndexOfSourceReference = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceReference");
          final int _cursorIndexOfFrequencyRank = CursorUtil.getColumnIndexOrThrow(_cursor, "frequencyRank");
          final int _cursorIndexOfDatasetVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "datasetVersion");
          final List<VocabularyEntity> _result = new ArrayList<VocabularyEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final VocabularyEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpLanguageCode;
            _tmpLanguageCode = _cursor.getString(_cursorIndexOfLanguageCode);
            final String _tmpHeadword;
            _tmpHeadword = _cursor.getString(_cursorIndexOfHeadword);
            final String _tmpNormalizedHeadword;
            _tmpNormalizedHeadword = _cursor.getString(_cursorIndexOfNormalizedHeadword);
            final Pos _tmpPos;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfPos);
            _tmpPos = __converters.stringToPos(_tmp);
            final Gender _tmpGender;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfGender)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfGender);
            }
            _tmpGender = __converters.stringToGender(_tmp_1);
            final VocabBook _tmpBook;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfBook);
            _tmpBook = __converters.stringToVocabBook(_tmp_2);
            final List<String> _tmpDefinitions;
            final String _tmp_3;
            _tmp_3 = _cursor.getString(_cursorIndexOfDefinitions);
            _tmpDefinitions = __converters.jsonToStringList(_tmp_3);
            final String _tmpGrammaticalClass;
            _tmpGrammaticalClass = _cursor.getString(_cursorIndexOfGrammaticalClass);
            final String _tmpInflectionType;
            _tmpInflectionType = _cursor.getString(_cursorIndexOfInflectionType);
            final List<String> _tmpPrincipalParts;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfPrincipalParts);
            _tmpPrincipalParts = __converters.jsonToStringList(_tmp_4);
            final List<String> _tmpIrregularForms;
            final String _tmp_5;
            _tmp_5 = _cursor.getString(_cursorIndexOfIrregularForms);
            _tmpIrregularForms = __converters.jsonToStringList(_tmp_5);
            final String _tmpEtymology;
            if (_cursor.isNull(_cursorIndexOfEtymology)) {
              _tmpEtymology = null;
            } else {
              _tmpEtymology = _cursor.getString(_cursorIndexOfEtymology);
            }
            final String _tmpEtymologyNote;
            if (_cursor.isNull(_cursorIndexOfEtymologyNote)) {
              _tmpEtymologyNote = null;
            } else {
              _tmpEtymologyNote = _cursor.getString(_cursorIndexOfEtymologyNote);
            }
            final String _tmpSourceReference;
            if (_cursor.isNull(_cursorIndexOfSourceReference)) {
              _tmpSourceReference = null;
            } else {
              _tmpSourceReference = _cursor.getString(_cursorIndexOfSourceReference);
            }
            final Integer _tmpFrequencyRank;
            if (_cursor.isNull(_cursorIndexOfFrequencyRank)) {
              _tmpFrequencyRank = null;
            } else {
              _tmpFrequencyRank = _cursor.getInt(_cursorIndexOfFrequencyRank);
            }
            final int _tmpDatasetVersion;
            _tmpDatasetVersion = _cursor.getInt(_cursorIndexOfDatasetVersion);
            _item = new VocabularyEntity(_tmpId,_tmpLanguageCode,_tmpHeadword,_tmpNormalizedHeadword,_tmpPos,_tmpGender,_tmpBook,_tmpDefinitions,_tmpGrammaticalClass,_tmpInflectionType,_tmpPrincipalParts,_tmpIrregularForms,_tmpEtymology,_tmpEtymologyNote,_tmpSourceReference,_tmpFrequencyRank,_tmpDatasetVersion);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getAll(final String language,
      final Continuation<? super List<VocabularyEntity>> $completion) {
    final String _sql = "SELECT * FROM vocabulary WHERE languageCode = ? ORDER BY normalizedHeadword";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, language);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<VocabularyEntity>>() {
      @Override
      @NonNull
      public List<VocabularyEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLanguageCode = CursorUtil.getColumnIndexOrThrow(_cursor, "languageCode");
          final int _cursorIndexOfHeadword = CursorUtil.getColumnIndexOrThrow(_cursor, "headword");
          final int _cursorIndexOfNormalizedHeadword = CursorUtil.getColumnIndexOrThrow(_cursor, "normalizedHeadword");
          final int _cursorIndexOfPos = CursorUtil.getColumnIndexOrThrow(_cursor, "pos");
          final int _cursorIndexOfGender = CursorUtil.getColumnIndexOrThrow(_cursor, "gender");
          final int _cursorIndexOfBook = CursorUtil.getColumnIndexOrThrow(_cursor, "book");
          final int _cursorIndexOfDefinitions = CursorUtil.getColumnIndexOrThrow(_cursor, "definitions");
          final int _cursorIndexOfGrammaticalClass = CursorUtil.getColumnIndexOrThrow(_cursor, "grammaticalClass");
          final int _cursorIndexOfInflectionType = CursorUtil.getColumnIndexOrThrow(_cursor, "inflectionType");
          final int _cursorIndexOfPrincipalParts = CursorUtil.getColumnIndexOrThrow(_cursor, "principalParts");
          final int _cursorIndexOfIrregularForms = CursorUtil.getColumnIndexOrThrow(_cursor, "irregularForms");
          final int _cursorIndexOfEtymology = CursorUtil.getColumnIndexOrThrow(_cursor, "etymology");
          final int _cursorIndexOfEtymologyNote = CursorUtil.getColumnIndexOrThrow(_cursor, "etymologyNote");
          final int _cursorIndexOfSourceReference = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceReference");
          final int _cursorIndexOfFrequencyRank = CursorUtil.getColumnIndexOrThrow(_cursor, "frequencyRank");
          final int _cursorIndexOfDatasetVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "datasetVersion");
          final List<VocabularyEntity> _result = new ArrayList<VocabularyEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final VocabularyEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpLanguageCode;
            _tmpLanguageCode = _cursor.getString(_cursorIndexOfLanguageCode);
            final String _tmpHeadword;
            _tmpHeadword = _cursor.getString(_cursorIndexOfHeadword);
            final String _tmpNormalizedHeadword;
            _tmpNormalizedHeadword = _cursor.getString(_cursorIndexOfNormalizedHeadword);
            final Pos _tmpPos;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfPos);
            _tmpPos = __converters.stringToPos(_tmp);
            final Gender _tmpGender;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfGender)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfGender);
            }
            _tmpGender = __converters.stringToGender(_tmp_1);
            final VocabBook _tmpBook;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfBook);
            _tmpBook = __converters.stringToVocabBook(_tmp_2);
            final List<String> _tmpDefinitions;
            final String _tmp_3;
            _tmp_3 = _cursor.getString(_cursorIndexOfDefinitions);
            _tmpDefinitions = __converters.jsonToStringList(_tmp_3);
            final String _tmpGrammaticalClass;
            _tmpGrammaticalClass = _cursor.getString(_cursorIndexOfGrammaticalClass);
            final String _tmpInflectionType;
            _tmpInflectionType = _cursor.getString(_cursorIndexOfInflectionType);
            final List<String> _tmpPrincipalParts;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfPrincipalParts);
            _tmpPrincipalParts = __converters.jsonToStringList(_tmp_4);
            final List<String> _tmpIrregularForms;
            final String _tmp_5;
            _tmp_5 = _cursor.getString(_cursorIndexOfIrregularForms);
            _tmpIrregularForms = __converters.jsonToStringList(_tmp_5);
            final String _tmpEtymology;
            if (_cursor.isNull(_cursorIndexOfEtymology)) {
              _tmpEtymology = null;
            } else {
              _tmpEtymology = _cursor.getString(_cursorIndexOfEtymology);
            }
            final String _tmpEtymologyNote;
            if (_cursor.isNull(_cursorIndexOfEtymologyNote)) {
              _tmpEtymologyNote = null;
            } else {
              _tmpEtymologyNote = _cursor.getString(_cursorIndexOfEtymologyNote);
            }
            final String _tmpSourceReference;
            if (_cursor.isNull(_cursorIndexOfSourceReference)) {
              _tmpSourceReference = null;
            } else {
              _tmpSourceReference = _cursor.getString(_cursorIndexOfSourceReference);
            }
            final Integer _tmpFrequencyRank;
            if (_cursor.isNull(_cursorIndexOfFrequencyRank)) {
              _tmpFrequencyRank = null;
            } else {
              _tmpFrequencyRank = _cursor.getInt(_cursorIndexOfFrequencyRank);
            }
            final int _tmpDatasetVersion;
            _tmpDatasetVersion = _cursor.getInt(_cursorIndexOfDatasetVersion);
            _item = new VocabularyEntity(_tmpId,_tmpLanguageCode,_tmpHeadword,_tmpNormalizedHeadword,_tmpPos,_tmpGender,_tmpBook,_tmpDefinitions,_tmpGrammaticalClass,_tmpInflectionType,_tmpPrincipalParts,_tmpIrregularForms,_tmpEtymology,_tmpEtymologyNote,_tmpSourceReference,_tmpFrequencyRank,_tmpDatasetVersion);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAllUnsorted(final String language,
      final Continuation<? super List<VocabularyEntity>> $completion) {
    final String _sql = "SELECT * FROM vocabulary WHERE languageCode = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, language);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<VocabularyEntity>>() {
      @Override
      @NonNull
      public List<VocabularyEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLanguageCode = CursorUtil.getColumnIndexOrThrow(_cursor, "languageCode");
          final int _cursorIndexOfHeadword = CursorUtil.getColumnIndexOrThrow(_cursor, "headword");
          final int _cursorIndexOfNormalizedHeadword = CursorUtil.getColumnIndexOrThrow(_cursor, "normalizedHeadword");
          final int _cursorIndexOfPos = CursorUtil.getColumnIndexOrThrow(_cursor, "pos");
          final int _cursorIndexOfGender = CursorUtil.getColumnIndexOrThrow(_cursor, "gender");
          final int _cursorIndexOfBook = CursorUtil.getColumnIndexOrThrow(_cursor, "book");
          final int _cursorIndexOfDefinitions = CursorUtil.getColumnIndexOrThrow(_cursor, "definitions");
          final int _cursorIndexOfGrammaticalClass = CursorUtil.getColumnIndexOrThrow(_cursor, "grammaticalClass");
          final int _cursorIndexOfInflectionType = CursorUtil.getColumnIndexOrThrow(_cursor, "inflectionType");
          final int _cursorIndexOfPrincipalParts = CursorUtil.getColumnIndexOrThrow(_cursor, "principalParts");
          final int _cursorIndexOfIrregularForms = CursorUtil.getColumnIndexOrThrow(_cursor, "irregularForms");
          final int _cursorIndexOfEtymology = CursorUtil.getColumnIndexOrThrow(_cursor, "etymology");
          final int _cursorIndexOfEtymologyNote = CursorUtil.getColumnIndexOrThrow(_cursor, "etymologyNote");
          final int _cursorIndexOfSourceReference = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceReference");
          final int _cursorIndexOfFrequencyRank = CursorUtil.getColumnIndexOrThrow(_cursor, "frequencyRank");
          final int _cursorIndexOfDatasetVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "datasetVersion");
          final List<VocabularyEntity> _result = new ArrayList<VocabularyEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final VocabularyEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpLanguageCode;
            _tmpLanguageCode = _cursor.getString(_cursorIndexOfLanguageCode);
            final String _tmpHeadword;
            _tmpHeadword = _cursor.getString(_cursorIndexOfHeadword);
            final String _tmpNormalizedHeadword;
            _tmpNormalizedHeadword = _cursor.getString(_cursorIndexOfNormalizedHeadword);
            final Pos _tmpPos;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfPos);
            _tmpPos = __converters.stringToPos(_tmp);
            final Gender _tmpGender;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfGender)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfGender);
            }
            _tmpGender = __converters.stringToGender(_tmp_1);
            final VocabBook _tmpBook;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfBook);
            _tmpBook = __converters.stringToVocabBook(_tmp_2);
            final List<String> _tmpDefinitions;
            final String _tmp_3;
            _tmp_3 = _cursor.getString(_cursorIndexOfDefinitions);
            _tmpDefinitions = __converters.jsonToStringList(_tmp_3);
            final String _tmpGrammaticalClass;
            _tmpGrammaticalClass = _cursor.getString(_cursorIndexOfGrammaticalClass);
            final String _tmpInflectionType;
            _tmpInflectionType = _cursor.getString(_cursorIndexOfInflectionType);
            final List<String> _tmpPrincipalParts;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfPrincipalParts);
            _tmpPrincipalParts = __converters.jsonToStringList(_tmp_4);
            final List<String> _tmpIrregularForms;
            final String _tmp_5;
            _tmp_5 = _cursor.getString(_cursorIndexOfIrregularForms);
            _tmpIrregularForms = __converters.jsonToStringList(_tmp_5);
            final String _tmpEtymology;
            if (_cursor.isNull(_cursorIndexOfEtymology)) {
              _tmpEtymology = null;
            } else {
              _tmpEtymology = _cursor.getString(_cursorIndexOfEtymology);
            }
            final String _tmpEtymologyNote;
            if (_cursor.isNull(_cursorIndexOfEtymologyNote)) {
              _tmpEtymologyNote = null;
            } else {
              _tmpEtymologyNote = _cursor.getString(_cursorIndexOfEtymologyNote);
            }
            final String _tmpSourceReference;
            if (_cursor.isNull(_cursorIndexOfSourceReference)) {
              _tmpSourceReference = null;
            } else {
              _tmpSourceReference = _cursor.getString(_cursorIndexOfSourceReference);
            }
            final Integer _tmpFrequencyRank;
            if (_cursor.isNull(_cursorIndexOfFrequencyRank)) {
              _tmpFrequencyRank = null;
            } else {
              _tmpFrequencyRank = _cursor.getInt(_cursorIndexOfFrequencyRank);
            }
            final int _tmpDatasetVersion;
            _tmpDatasetVersion = _cursor.getInt(_cursorIndexOfDatasetVersion);
            _item = new VocabularyEntity(_tmpId,_tmpLanguageCode,_tmpHeadword,_tmpNormalizedHeadword,_tmpPos,_tmpGender,_tmpBook,_tmpDefinitions,_tmpGrammaticalClass,_tmpInflectionType,_tmpPrincipalParts,_tmpIrregularForms,_tmpEtymology,_tmpEtymologyNote,_tmpSourceReference,_tmpFrequencyRank,_tmpDatasetVersion);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object get(final String id, final Continuation<? super VocabularyEntity> $completion) {
    final String _sql = "SELECT * FROM vocabulary WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<VocabularyEntity>() {
      @Override
      @Nullable
      public VocabularyEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLanguageCode = CursorUtil.getColumnIndexOrThrow(_cursor, "languageCode");
          final int _cursorIndexOfHeadword = CursorUtil.getColumnIndexOrThrow(_cursor, "headword");
          final int _cursorIndexOfNormalizedHeadword = CursorUtil.getColumnIndexOrThrow(_cursor, "normalizedHeadword");
          final int _cursorIndexOfPos = CursorUtil.getColumnIndexOrThrow(_cursor, "pos");
          final int _cursorIndexOfGender = CursorUtil.getColumnIndexOrThrow(_cursor, "gender");
          final int _cursorIndexOfBook = CursorUtil.getColumnIndexOrThrow(_cursor, "book");
          final int _cursorIndexOfDefinitions = CursorUtil.getColumnIndexOrThrow(_cursor, "definitions");
          final int _cursorIndexOfGrammaticalClass = CursorUtil.getColumnIndexOrThrow(_cursor, "grammaticalClass");
          final int _cursorIndexOfInflectionType = CursorUtil.getColumnIndexOrThrow(_cursor, "inflectionType");
          final int _cursorIndexOfPrincipalParts = CursorUtil.getColumnIndexOrThrow(_cursor, "principalParts");
          final int _cursorIndexOfIrregularForms = CursorUtil.getColumnIndexOrThrow(_cursor, "irregularForms");
          final int _cursorIndexOfEtymology = CursorUtil.getColumnIndexOrThrow(_cursor, "etymology");
          final int _cursorIndexOfEtymologyNote = CursorUtil.getColumnIndexOrThrow(_cursor, "etymologyNote");
          final int _cursorIndexOfSourceReference = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceReference");
          final int _cursorIndexOfFrequencyRank = CursorUtil.getColumnIndexOrThrow(_cursor, "frequencyRank");
          final int _cursorIndexOfDatasetVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "datasetVersion");
          final VocabularyEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpLanguageCode;
            _tmpLanguageCode = _cursor.getString(_cursorIndexOfLanguageCode);
            final String _tmpHeadword;
            _tmpHeadword = _cursor.getString(_cursorIndexOfHeadword);
            final String _tmpNormalizedHeadword;
            _tmpNormalizedHeadword = _cursor.getString(_cursorIndexOfNormalizedHeadword);
            final Pos _tmpPos;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfPos);
            _tmpPos = __converters.stringToPos(_tmp);
            final Gender _tmpGender;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfGender)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfGender);
            }
            _tmpGender = __converters.stringToGender(_tmp_1);
            final VocabBook _tmpBook;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfBook);
            _tmpBook = __converters.stringToVocabBook(_tmp_2);
            final List<String> _tmpDefinitions;
            final String _tmp_3;
            _tmp_3 = _cursor.getString(_cursorIndexOfDefinitions);
            _tmpDefinitions = __converters.jsonToStringList(_tmp_3);
            final String _tmpGrammaticalClass;
            _tmpGrammaticalClass = _cursor.getString(_cursorIndexOfGrammaticalClass);
            final String _tmpInflectionType;
            _tmpInflectionType = _cursor.getString(_cursorIndexOfInflectionType);
            final List<String> _tmpPrincipalParts;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfPrincipalParts);
            _tmpPrincipalParts = __converters.jsonToStringList(_tmp_4);
            final List<String> _tmpIrregularForms;
            final String _tmp_5;
            _tmp_5 = _cursor.getString(_cursorIndexOfIrregularForms);
            _tmpIrregularForms = __converters.jsonToStringList(_tmp_5);
            final String _tmpEtymology;
            if (_cursor.isNull(_cursorIndexOfEtymology)) {
              _tmpEtymology = null;
            } else {
              _tmpEtymology = _cursor.getString(_cursorIndexOfEtymology);
            }
            final String _tmpEtymologyNote;
            if (_cursor.isNull(_cursorIndexOfEtymologyNote)) {
              _tmpEtymologyNote = null;
            } else {
              _tmpEtymologyNote = _cursor.getString(_cursorIndexOfEtymologyNote);
            }
            final String _tmpSourceReference;
            if (_cursor.isNull(_cursorIndexOfSourceReference)) {
              _tmpSourceReference = null;
            } else {
              _tmpSourceReference = _cursor.getString(_cursorIndexOfSourceReference);
            }
            final Integer _tmpFrequencyRank;
            if (_cursor.isNull(_cursorIndexOfFrequencyRank)) {
              _tmpFrequencyRank = null;
            } else {
              _tmpFrequencyRank = _cursor.getInt(_cursorIndexOfFrequencyRank);
            }
            final int _tmpDatasetVersion;
            _tmpDatasetVersion = _cursor.getInt(_cursorIndexOfDatasetVersion);
            _result = new VocabularyEntity(_tmpId,_tmpLanguageCode,_tmpHeadword,_tmpNormalizedHeadword,_tmpPos,_tmpGender,_tmpBook,_tmpDefinitions,_tmpGrammaticalClass,_tmpInflectionType,_tmpPrincipalParts,_tmpIrregularForms,_tmpEtymology,_tmpEtymologyNote,_tmpSourceReference,_tmpFrequencyRank,_tmpDatasetVersion);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getByIds(final List<String> ids,
      final Continuation<? super List<VocabularyEntity>> $completion) {
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT * FROM vocabulary WHERE id IN (");
    final int _inputSize = ids.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize;
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (String _item : ids) {
      _statement.bindString(_argIndex, _item);
      _argIndex++;
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<VocabularyEntity>>() {
      @Override
      @NonNull
      public List<VocabularyEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLanguageCode = CursorUtil.getColumnIndexOrThrow(_cursor, "languageCode");
          final int _cursorIndexOfHeadword = CursorUtil.getColumnIndexOrThrow(_cursor, "headword");
          final int _cursorIndexOfNormalizedHeadword = CursorUtil.getColumnIndexOrThrow(_cursor, "normalizedHeadword");
          final int _cursorIndexOfPos = CursorUtil.getColumnIndexOrThrow(_cursor, "pos");
          final int _cursorIndexOfGender = CursorUtil.getColumnIndexOrThrow(_cursor, "gender");
          final int _cursorIndexOfBook = CursorUtil.getColumnIndexOrThrow(_cursor, "book");
          final int _cursorIndexOfDefinitions = CursorUtil.getColumnIndexOrThrow(_cursor, "definitions");
          final int _cursorIndexOfGrammaticalClass = CursorUtil.getColumnIndexOrThrow(_cursor, "grammaticalClass");
          final int _cursorIndexOfInflectionType = CursorUtil.getColumnIndexOrThrow(_cursor, "inflectionType");
          final int _cursorIndexOfPrincipalParts = CursorUtil.getColumnIndexOrThrow(_cursor, "principalParts");
          final int _cursorIndexOfIrregularForms = CursorUtil.getColumnIndexOrThrow(_cursor, "irregularForms");
          final int _cursorIndexOfEtymology = CursorUtil.getColumnIndexOrThrow(_cursor, "etymology");
          final int _cursorIndexOfEtymologyNote = CursorUtil.getColumnIndexOrThrow(_cursor, "etymologyNote");
          final int _cursorIndexOfSourceReference = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceReference");
          final int _cursorIndexOfFrequencyRank = CursorUtil.getColumnIndexOrThrow(_cursor, "frequencyRank");
          final int _cursorIndexOfDatasetVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "datasetVersion");
          final List<VocabularyEntity> _result = new ArrayList<VocabularyEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final VocabularyEntity _item_1;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpLanguageCode;
            _tmpLanguageCode = _cursor.getString(_cursorIndexOfLanguageCode);
            final String _tmpHeadword;
            _tmpHeadword = _cursor.getString(_cursorIndexOfHeadword);
            final String _tmpNormalizedHeadword;
            _tmpNormalizedHeadword = _cursor.getString(_cursorIndexOfNormalizedHeadword);
            final Pos _tmpPos;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfPos);
            _tmpPos = __converters.stringToPos(_tmp);
            final Gender _tmpGender;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfGender)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfGender);
            }
            _tmpGender = __converters.stringToGender(_tmp_1);
            final VocabBook _tmpBook;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfBook);
            _tmpBook = __converters.stringToVocabBook(_tmp_2);
            final List<String> _tmpDefinitions;
            final String _tmp_3;
            _tmp_3 = _cursor.getString(_cursorIndexOfDefinitions);
            _tmpDefinitions = __converters.jsonToStringList(_tmp_3);
            final String _tmpGrammaticalClass;
            _tmpGrammaticalClass = _cursor.getString(_cursorIndexOfGrammaticalClass);
            final String _tmpInflectionType;
            _tmpInflectionType = _cursor.getString(_cursorIndexOfInflectionType);
            final List<String> _tmpPrincipalParts;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfPrincipalParts);
            _tmpPrincipalParts = __converters.jsonToStringList(_tmp_4);
            final List<String> _tmpIrregularForms;
            final String _tmp_5;
            _tmp_5 = _cursor.getString(_cursorIndexOfIrregularForms);
            _tmpIrregularForms = __converters.jsonToStringList(_tmp_5);
            final String _tmpEtymology;
            if (_cursor.isNull(_cursorIndexOfEtymology)) {
              _tmpEtymology = null;
            } else {
              _tmpEtymology = _cursor.getString(_cursorIndexOfEtymology);
            }
            final String _tmpEtymologyNote;
            if (_cursor.isNull(_cursorIndexOfEtymologyNote)) {
              _tmpEtymologyNote = null;
            } else {
              _tmpEtymologyNote = _cursor.getString(_cursorIndexOfEtymologyNote);
            }
            final String _tmpSourceReference;
            if (_cursor.isNull(_cursorIndexOfSourceReference)) {
              _tmpSourceReference = null;
            } else {
              _tmpSourceReference = _cursor.getString(_cursorIndexOfSourceReference);
            }
            final Integer _tmpFrequencyRank;
            if (_cursor.isNull(_cursorIndexOfFrequencyRank)) {
              _tmpFrequencyRank = null;
            } else {
              _tmpFrequencyRank = _cursor.getInt(_cursorIndexOfFrequencyRank);
            }
            final int _tmpDatasetVersion;
            _tmpDatasetVersion = _cursor.getInt(_cursorIndexOfDatasetVersion);
            _item_1 = new VocabularyEntity(_tmpId,_tmpLanguageCode,_tmpHeadword,_tmpNormalizedHeadword,_tmpPos,_tmpGender,_tmpBook,_tmpDefinitions,_tmpGrammaticalClass,_tmpInflectionType,_tmpPrincipalParts,_tmpIrregularForms,_tmpEtymology,_tmpEtymologyNote,_tmpSourceReference,_tmpFrequencyRank,_tmpDatasetVersion);
            _result.add(_item_1);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object count(final String language, final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM vocabulary WHERE languageCode = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, language);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object countBook(final String language, final VocabBook book,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM vocabulary WHERE languageCode = ? AND book = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, language);
    _argIndex = 2;
    final String _tmp = __converters.vocabBookToString(book);
    _statement.bindString(_argIndex, _tmp);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(0);
            _result = _tmp_1;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<Integer> observeCount(final String language) {
    final String _sql = "SELECT COUNT(*) FROM vocabulary WHERE languageCode = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, language);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"vocabulary"}, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<Integer> observeBookCount(final String language, final VocabBook book) {
    final String _sql = "SELECT COUNT(*) FROM vocabulary WHERE languageCode = ? AND book = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, language);
    _argIndex = 2;
    final String _tmp = __converters.vocabBookToString(book);
    _statement.bindString(_argIndex, _tmp);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"vocabulary"}, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(0);
            _result = _tmp_1;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getAllByFrequency(final String language, final VocabBook book,
      final Continuation<? super List<VocabularyEntity>> $completion) {
    final String _sql = "SELECT * FROM vocabulary WHERE languageCode = ? AND book = ? ORDER BY COALESCE(frequencyRank, 1000000), normalizedHeadword";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, language);
    _argIndex = 2;
    final String _tmp = __converters.vocabBookToString(book);
    _statement.bindString(_argIndex, _tmp);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<VocabularyEntity>>() {
      @Override
      @NonNull
      public List<VocabularyEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLanguageCode = CursorUtil.getColumnIndexOrThrow(_cursor, "languageCode");
          final int _cursorIndexOfHeadword = CursorUtil.getColumnIndexOrThrow(_cursor, "headword");
          final int _cursorIndexOfNormalizedHeadword = CursorUtil.getColumnIndexOrThrow(_cursor, "normalizedHeadword");
          final int _cursorIndexOfPos = CursorUtil.getColumnIndexOrThrow(_cursor, "pos");
          final int _cursorIndexOfGender = CursorUtil.getColumnIndexOrThrow(_cursor, "gender");
          final int _cursorIndexOfBook = CursorUtil.getColumnIndexOrThrow(_cursor, "book");
          final int _cursorIndexOfDefinitions = CursorUtil.getColumnIndexOrThrow(_cursor, "definitions");
          final int _cursorIndexOfGrammaticalClass = CursorUtil.getColumnIndexOrThrow(_cursor, "grammaticalClass");
          final int _cursorIndexOfInflectionType = CursorUtil.getColumnIndexOrThrow(_cursor, "inflectionType");
          final int _cursorIndexOfPrincipalParts = CursorUtil.getColumnIndexOrThrow(_cursor, "principalParts");
          final int _cursorIndexOfIrregularForms = CursorUtil.getColumnIndexOrThrow(_cursor, "irregularForms");
          final int _cursorIndexOfEtymology = CursorUtil.getColumnIndexOrThrow(_cursor, "etymology");
          final int _cursorIndexOfEtymologyNote = CursorUtil.getColumnIndexOrThrow(_cursor, "etymologyNote");
          final int _cursorIndexOfSourceReference = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceReference");
          final int _cursorIndexOfFrequencyRank = CursorUtil.getColumnIndexOrThrow(_cursor, "frequencyRank");
          final int _cursorIndexOfDatasetVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "datasetVersion");
          final List<VocabularyEntity> _result = new ArrayList<VocabularyEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final VocabularyEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpLanguageCode;
            _tmpLanguageCode = _cursor.getString(_cursorIndexOfLanguageCode);
            final String _tmpHeadword;
            _tmpHeadword = _cursor.getString(_cursorIndexOfHeadword);
            final String _tmpNormalizedHeadword;
            _tmpNormalizedHeadword = _cursor.getString(_cursorIndexOfNormalizedHeadword);
            final Pos _tmpPos;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfPos);
            _tmpPos = __converters.stringToPos(_tmp_1);
            final Gender _tmpGender;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfGender)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfGender);
            }
            _tmpGender = __converters.stringToGender(_tmp_2);
            final VocabBook _tmpBook;
            final String _tmp_3;
            _tmp_3 = _cursor.getString(_cursorIndexOfBook);
            _tmpBook = __converters.stringToVocabBook(_tmp_3);
            final List<String> _tmpDefinitions;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfDefinitions);
            _tmpDefinitions = __converters.jsonToStringList(_tmp_4);
            final String _tmpGrammaticalClass;
            _tmpGrammaticalClass = _cursor.getString(_cursorIndexOfGrammaticalClass);
            final String _tmpInflectionType;
            _tmpInflectionType = _cursor.getString(_cursorIndexOfInflectionType);
            final List<String> _tmpPrincipalParts;
            final String _tmp_5;
            _tmp_5 = _cursor.getString(_cursorIndexOfPrincipalParts);
            _tmpPrincipalParts = __converters.jsonToStringList(_tmp_5);
            final List<String> _tmpIrregularForms;
            final String _tmp_6;
            _tmp_6 = _cursor.getString(_cursorIndexOfIrregularForms);
            _tmpIrregularForms = __converters.jsonToStringList(_tmp_6);
            final String _tmpEtymology;
            if (_cursor.isNull(_cursorIndexOfEtymology)) {
              _tmpEtymology = null;
            } else {
              _tmpEtymology = _cursor.getString(_cursorIndexOfEtymology);
            }
            final String _tmpEtymologyNote;
            if (_cursor.isNull(_cursorIndexOfEtymologyNote)) {
              _tmpEtymologyNote = null;
            } else {
              _tmpEtymologyNote = _cursor.getString(_cursorIndexOfEtymologyNote);
            }
            final String _tmpSourceReference;
            if (_cursor.isNull(_cursorIndexOfSourceReference)) {
              _tmpSourceReference = null;
            } else {
              _tmpSourceReference = _cursor.getString(_cursorIndexOfSourceReference);
            }
            final Integer _tmpFrequencyRank;
            if (_cursor.isNull(_cursorIndexOfFrequencyRank)) {
              _tmpFrequencyRank = null;
            } else {
              _tmpFrequencyRank = _cursor.getInt(_cursorIndexOfFrequencyRank);
            }
            final int _tmpDatasetVersion;
            _tmpDatasetVersion = _cursor.getInt(_cursorIndexOfDatasetVersion);
            _item = new VocabularyEntity(_tmpId,_tmpLanguageCode,_tmpHeadword,_tmpNormalizedHeadword,_tmpPos,_tmpGender,_tmpBook,_tmpDefinitions,_tmpGrammaticalClass,_tmpInflectionType,_tmpPrincipalParts,_tmpIrregularForms,_tmpEtymology,_tmpEtymologyNote,_tmpSourceReference,_tmpFrequencyRank,_tmpDatasetVersion);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<VocabularyEntity>> observeBookByFrequency(final String language,
      final VocabBook book) {
    final String _sql = "SELECT * FROM vocabulary WHERE languageCode = ? AND book = ? ORDER BY COALESCE(frequencyRank, 1000000), normalizedHeadword";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, language);
    _argIndex = 2;
    final String _tmp = __converters.vocabBookToString(book);
    _statement.bindString(_argIndex, _tmp);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"vocabulary"}, new Callable<List<VocabularyEntity>>() {
      @Override
      @NonNull
      public List<VocabularyEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLanguageCode = CursorUtil.getColumnIndexOrThrow(_cursor, "languageCode");
          final int _cursorIndexOfHeadword = CursorUtil.getColumnIndexOrThrow(_cursor, "headword");
          final int _cursorIndexOfNormalizedHeadword = CursorUtil.getColumnIndexOrThrow(_cursor, "normalizedHeadword");
          final int _cursorIndexOfPos = CursorUtil.getColumnIndexOrThrow(_cursor, "pos");
          final int _cursorIndexOfGender = CursorUtil.getColumnIndexOrThrow(_cursor, "gender");
          final int _cursorIndexOfBook = CursorUtil.getColumnIndexOrThrow(_cursor, "book");
          final int _cursorIndexOfDefinitions = CursorUtil.getColumnIndexOrThrow(_cursor, "definitions");
          final int _cursorIndexOfGrammaticalClass = CursorUtil.getColumnIndexOrThrow(_cursor, "grammaticalClass");
          final int _cursorIndexOfInflectionType = CursorUtil.getColumnIndexOrThrow(_cursor, "inflectionType");
          final int _cursorIndexOfPrincipalParts = CursorUtil.getColumnIndexOrThrow(_cursor, "principalParts");
          final int _cursorIndexOfIrregularForms = CursorUtil.getColumnIndexOrThrow(_cursor, "irregularForms");
          final int _cursorIndexOfEtymology = CursorUtil.getColumnIndexOrThrow(_cursor, "etymology");
          final int _cursorIndexOfEtymologyNote = CursorUtil.getColumnIndexOrThrow(_cursor, "etymologyNote");
          final int _cursorIndexOfSourceReference = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceReference");
          final int _cursorIndexOfFrequencyRank = CursorUtil.getColumnIndexOrThrow(_cursor, "frequencyRank");
          final int _cursorIndexOfDatasetVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "datasetVersion");
          final List<VocabularyEntity> _result = new ArrayList<VocabularyEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final VocabularyEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpLanguageCode;
            _tmpLanguageCode = _cursor.getString(_cursorIndexOfLanguageCode);
            final String _tmpHeadword;
            _tmpHeadword = _cursor.getString(_cursorIndexOfHeadword);
            final String _tmpNormalizedHeadword;
            _tmpNormalizedHeadword = _cursor.getString(_cursorIndexOfNormalizedHeadword);
            final Pos _tmpPos;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfPos);
            _tmpPos = __converters.stringToPos(_tmp_1);
            final Gender _tmpGender;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfGender)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfGender);
            }
            _tmpGender = __converters.stringToGender(_tmp_2);
            final VocabBook _tmpBook;
            final String _tmp_3;
            _tmp_3 = _cursor.getString(_cursorIndexOfBook);
            _tmpBook = __converters.stringToVocabBook(_tmp_3);
            final List<String> _tmpDefinitions;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfDefinitions);
            _tmpDefinitions = __converters.jsonToStringList(_tmp_4);
            final String _tmpGrammaticalClass;
            _tmpGrammaticalClass = _cursor.getString(_cursorIndexOfGrammaticalClass);
            final String _tmpInflectionType;
            _tmpInflectionType = _cursor.getString(_cursorIndexOfInflectionType);
            final List<String> _tmpPrincipalParts;
            final String _tmp_5;
            _tmp_5 = _cursor.getString(_cursorIndexOfPrincipalParts);
            _tmpPrincipalParts = __converters.jsonToStringList(_tmp_5);
            final List<String> _tmpIrregularForms;
            final String _tmp_6;
            _tmp_6 = _cursor.getString(_cursorIndexOfIrregularForms);
            _tmpIrregularForms = __converters.jsonToStringList(_tmp_6);
            final String _tmpEtymology;
            if (_cursor.isNull(_cursorIndexOfEtymology)) {
              _tmpEtymology = null;
            } else {
              _tmpEtymology = _cursor.getString(_cursorIndexOfEtymology);
            }
            final String _tmpEtymologyNote;
            if (_cursor.isNull(_cursorIndexOfEtymologyNote)) {
              _tmpEtymologyNote = null;
            } else {
              _tmpEtymologyNote = _cursor.getString(_cursorIndexOfEtymologyNote);
            }
            final String _tmpSourceReference;
            if (_cursor.isNull(_cursorIndexOfSourceReference)) {
              _tmpSourceReference = null;
            } else {
              _tmpSourceReference = _cursor.getString(_cursorIndexOfSourceReference);
            }
            final Integer _tmpFrequencyRank;
            if (_cursor.isNull(_cursorIndexOfFrequencyRank)) {
              _tmpFrequencyRank = null;
            } else {
              _tmpFrequencyRank = _cursor.getInt(_cursorIndexOfFrequencyRank);
            }
            final int _tmpDatasetVersion;
            _tmpDatasetVersion = _cursor.getInt(_cursorIndexOfDatasetVersion);
            _item = new VocabularyEntity(_tmpId,_tmpLanguageCode,_tmpHeadword,_tmpNormalizedHeadword,_tmpPos,_tmpGender,_tmpBook,_tmpDefinitions,_tmpGrammaticalClass,_tmpInflectionType,_tmpPrincipalParts,_tmpIrregularForms,_tmpEtymology,_tmpEtymologyNote,_tmpSourceReference,_tmpFrequencyRank,_tmpDatasetVersion);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object formsFor(final String vocabularyId,
      final Continuation<? super List<InflectedFormEntity>> $completion) {
    final String _sql = "SELECT * FROM inflected_forms WHERE vocabularyId = ? ORDER BY id";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, vocabularyId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<InflectedFormEntity>>() {
      @Override
      @NonNull
      public List<InflectedFormEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfVocabularyId = CursorUtil.getColumnIndexOrThrow(_cursor, "vocabularyId");
          final int _cursorIndexOfWrittenForm = CursorUtil.getColumnIndexOrThrow(_cursor, "writtenForm");
          final int _cursorIndexOfNormalizedForm = CursorUtil.getColumnIndexOrThrow(_cursor, "normalizedForm");
          final int _cursorIndexOfDimensions = CursorUtil.getColumnIndexOrThrow(_cursor, "dimensions");
          final int _cursorIndexOfQuizEligible = CursorUtil.getColumnIndexOrThrow(_cursor, "quizEligible");
          final List<InflectedFormEntity> _result = new ArrayList<InflectedFormEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final InflectedFormEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpVocabularyId;
            _tmpVocabularyId = _cursor.getString(_cursorIndexOfVocabularyId);
            final String _tmpWrittenForm;
            _tmpWrittenForm = _cursor.getString(_cursorIndexOfWrittenForm);
            final String _tmpNormalizedForm;
            _tmpNormalizedForm = _cursor.getString(_cursorIndexOfNormalizedForm);
            final Map<String, String> _tmpDimensions;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfDimensions);
            _tmpDimensions = __converters.jsonToStringMap(_tmp);
            final boolean _tmpQuizEligible;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfQuizEligible);
            _tmpQuizEligible = _tmp_1 != 0;
            _item = new InflectedFormEntity(_tmpId,_tmpVocabularyId,_tmpWrittenForm,_tmpNormalizedForm,_tmpDimensions,_tmpQuizEligible);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object formsForIds(final List<String> vocabularyIds,
      final Continuation<? super List<InflectedFormEntity>> $completion) {
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT * FROM inflected_forms WHERE vocabularyId IN (");
    final int _inputSize = vocabularyIds.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize;
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (String _item : vocabularyIds) {
      _statement.bindString(_argIndex, _item);
      _argIndex++;
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<InflectedFormEntity>>() {
      @Override
      @NonNull
      public List<InflectedFormEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfVocabularyId = CursorUtil.getColumnIndexOrThrow(_cursor, "vocabularyId");
          final int _cursorIndexOfWrittenForm = CursorUtil.getColumnIndexOrThrow(_cursor, "writtenForm");
          final int _cursorIndexOfNormalizedForm = CursorUtil.getColumnIndexOrThrow(_cursor, "normalizedForm");
          final int _cursorIndexOfDimensions = CursorUtil.getColumnIndexOrThrow(_cursor, "dimensions");
          final int _cursorIndexOfQuizEligible = CursorUtil.getColumnIndexOrThrow(_cursor, "quizEligible");
          final List<InflectedFormEntity> _result = new ArrayList<InflectedFormEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final InflectedFormEntity _item_1;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpVocabularyId;
            _tmpVocabularyId = _cursor.getString(_cursorIndexOfVocabularyId);
            final String _tmpWrittenForm;
            _tmpWrittenForm = _cursor.getString(_cursorIndexOfWrittenForm);
            final String _tmpNormalizedForm;
            _tmpNormalizedForm = _cursor.getString(_cursorIndexOfNormalizedForm);
            final Map<String, String> _tmpDimensions;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfDimensions);
            _tmpDimensions = __converters.jsonToStringMap(_tmp);
            final boolean _tmpQuizEligible;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfQuizEligible);
            _tmpQuizEligible = _tmp_1 != 0;
            _item_1 = new InflectedFormEntity(_tmpId,_tmpVocabularyId,_tmpWrittenForm,_tmpNormalizedForm,_tmpDimensions,_tmpQuizEligible);
            _result.add(_item_1);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object examplesFor(final String vocabularyId,
      final Continuation<? super List<ExampleSentenceEntity>> $completion) {
    final String _sql = "SELECT * FROM example_sentences WHERE vocabularyId = ? ORDER BY id";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, vocabularyId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ExampleSentenceEntity>>() {
      @Override
      @NonNull
      public List<ExampleSentenceEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfVocabularyId = CursorUtil.getColumnIndexOrThrow(_cursor, "vocabularyId");
          final int _cursorIndexOfOriginalText = CursorUtil.getColumnIndexOrThrow(_cursor, "originalText");
          final int _cursorIndexOfTranslation = CursorUtil.getColumnIndexOrThrow(_cursor, "translation");
          final int _cursorIndexOfAuthor = CursorUtil.getColumnIndexOrThrow(_cursor, "author");
          final int _cursorIndexOfWork = CursorUtil.getColumnIndexOrThrow(_cursor, "work");
          final int _cursorIndexOfPassageReference = CursorUtil.getColumnIndexOrThrow(_cursor, "passageReference");
          final int _cursorIndexOfIsAuthentic = CursorUtil.getColumnIndexOrThrow(_cursor, "isAuthentic");
          final int _cursorIndexOfTargetForm = CursorUtil.getColumnIndexOrThrow(_cursor, "targetForm");
          final int _cursorIndexOfTargetFormNormalized = CursorUtil.getColumnIndexOrThrow(_cursor, "targetFormNormalized");
          final int _cursorIndexOfTargetDimensions = CursorUtil.getColumnIndexOrThrow(_cursor, "targetDimensions");
          final List<ExampleSentenceEntity> _result = new ArrayList<ExampleSentenceEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ExampleSentenceEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpVocabularyId;
            _tmpVocabularyId = _cursor.getString(_cursorIndexOfVocabularyId);
            final String _tmpOriginalText;
            _tmpOriginalText = _cursor.getString(_cursorIndexOfOriginalText);
            final String _tmpTranslation;
            _tmpTranslation = _cursor.getString(_cursorIndexOfTranslation);
            final String _tmpAuthor;
            if (_cursor.isNull(_cursorIndexOfAuthor)) {
              _tmpAuthor = null;
            } else {
              _tmpAuthor = _cursor.getString(_cursorIndexOfAuthor);
            }
            final String _tmpWork;
            if (_cursor.isNull(_cursorIndexOfWork)) {
              _tmpWork = null;
            } else {
              _tmpWork = _cursor.getString(_cursorIndexOfWork);
            }
            final String _tmpPassageReference;
            if (_cursor.isNull(_cursorIndexOfPassageReference)) {
              _tmpPassageReference = null;
            } else {
              _tmpPassageReference = _cursor.getString(_cursorIndexOfPassageReference);
            }
            final boolean _tmpIsAuthentic;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsAuthentic);
            _tmpIsAuthentic = _tmp != 0;
            final String _tmpTargetForm;
            if (_cursor.isNull(_cursorIndexOfTargetForm)) {
              _tmpTargetForm = null;
            } else {
              _tmpTargetForm = _cursor.getString(_cursorIndexOfTargetForm);
            }
            final String _tmpTargetFormNormalized;
            if (_cursor.isNull(_cursorIndexOfTargetFormNormalized)) {
              _tmpTargetFormNormalized = null;
            } else {
              _tmpTargetFormNormalized = _cursor.getString(_cursorIndexOfTargetFormNormalized);
            }
            final Map<String, String> _tmpTargetDimensions;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfTargetDimensions);
            _tmpTargetDimensions = __converters.jsonToStringMap(_tmp_1);
            _item = new ExampleSentenceEntity(_tmpId,_tmpVocabularyId,_tmpOriginalText,_tmpTranslation,_tmpAuthor,_tmpWork,_tmpPassageReference,_tmpIsAuthentic,_tmpTargetForm,_tmpTargetFormNormalized,_tmpTargetDimensions);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object examplesForIds(final List<String> vocabularyIds,
      final Continuation<? super List<ExampleSentenceEntity>> $completion) {
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT * FROM example_sentences WHERE vocabularyId IN (");
    final int _inputSize = vocabularyIds.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize;
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (String _item : vocabularyIds) {
      _statement.bindString(_argIndex, _item);
      _argIndex++;
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ExampleSentenceEntity>>() {
      @Override
      @NonNull
      public List<ExampleSentenceEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfVocabularyId = CursorUtil.getColumnIndexOrThrow(_cursor, "vocabularyId");
          final int _cursorIndexOfOriginalText = CursorUtil.getColumnIndexOrThrow(_cursor, "originalText");
          final int _cursorIndexOfTranslation = CursorUtil.getColumnIndexOrThrow(_cursor, "translation");
          final int _cursorIndexOfAuthor = CursorUtil.getColumnIndexOrThrow(_cursor, "author");
          final int _cursorIndexOfWork = CursorUtil.getColumnIndexOrThrow(_cursor, "work");
          final int _cursorIndexOfPassageReference = CursorUtil.getColumnIndexOrThrow(_cursor, "passageReference");
          final int _cursorIndexOfIsAuthentic = CursorUtil.getColumnIndexOrThrow(_cursor, "isAuthentic");
          final int _cursorIndexOfTargetForm = CursorUtil.getColumnIndexOrThrow(_cursor, "targetForm");
          final int _cursorIndexOfTargetFormNormalized = CursorUtil.getColumnIndexOrThrow(_cursor, "targetFormNormalized");
          final int _cursorIndexOfTargetDimensions = CursorUtil.getColumnIndexOrThrow(_cursor, "targetDimensions");
          final List<ExampleSentenceEntity> _result = new ArrayList<ExampleSentenceEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ExampleSentenceEntity _item_1;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpVocabularyId;
            _tmpVocabularyId = _cursor.getString(_cursorIndexOfVocabularyId);
            final String _tmpOriginalText;
            _tmpOriginalText = _cursor.getString(_cursorIndexOfOriginalText);
            final String _tmpTranslation;
            _tmpTranslation = _cursor.getString(_cursorIndexOfTranslation);
            final String _tmpAuthor;
            if (_cursor.isNull(_cursorIndexOfAuthor)) {
              _tmpAuthor = null;
            } else {
              _tmpAuthor = _cursor.getString(_cursorIndexOfAuthor);
            }
            final String _tmpWork;
            if (_cursor.isNull(_cursorIndexOfWork)) {
              _tmpWork = null;
            } else {
              _tmpWork = _cursor.getString(_cursorIndexOfWork);
            }
            final String _tmpPassageReference;
            if (_cursor.isNull(_cursorIndexOfPassageReference)) {
              _tmpPassageReference = null;
            } else {
              _tmpPassageReference = _cursor.getString(_cursorIndexOfPassageReference);
            }
            final boolean _tmpIsAuthentic;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsAuthentic);
            _tmpIsAuthentic = _tmp != 0;
            final String _tmpTargetForm;
            if (_cursor.isNull(_cursorIndexOfTargetForm)) {
              _tmpTargetForm = null;
            } else {
              _tmpTargetForm = _cursor.getString(_cursorIndexOfTargetForm);
            }
            final String _tmpTargetFormNormalized;
            if (_cursor.isNull(_cursorIndexOfTargetFormNormalized)) {
              _tmpTargetFormNormalized = null;
            } else {
              _tmpTargetFormNormalized = _cursor.getString(_cursorIndexOfTargetFormNormalized);
            }
            final Map<String, String> _tmpTargetDimensions;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfTargetDimensions);
            _tmpTargetDimensions = __converters.jsonToStringMap(_tmp_1);
            _item_1 = new ExampleSentenceEntity(_tmpId,_tmpVocabularyId,_tmpOriginalText,_tmpTranslation,_tmpAuthor,_tmpWork,_tmpPassageReference,_tmpIsAuthentic,_tmpTargetForm,_tmpTargetFormNormalized,_tmpTargetDimensions);
            _result.add(_item_1);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<VocabularyListRow>> search(final String language, final String pattern,
      final VocabBook book, final String pos, final int onlyLearned, final int onlySaved,
      final int limit) {
    final String _sql = "\n"
            + "        SELECT v.id AS id, v.headword AS headword, v.pos AS pos, v.gender AS gender,\n"
            + "               v.definitions AS definitions, v.frequencyRank AS frequencyRank, v.book AS book,\n"
            + "               CASE WHEN r.status = 'LEARNED' THEN 1 ELSE 0 END AS learned,\n"
            + "               CASE WHEN s.vocabularyId IS NOT NULL THEN 1 ELSE 0 END AS saved\n"
            + "        FROM vocabulary v\n"
            + "        LEFT JOIN learning_records r ON r.vocabularyId = v.id\n"
            + "        LEFT JOIN saved_words s ON s.vocabularyId = v.id\n"
            + "        WHERE v.languageCode = ?\n"
            + "          AND (? IS NULL OR v.book = ?)\n"
            + "          AND (? IS NULL OR v.pos = ?)\n"
            + "          AND (? = 0 OR r.status = 'LEARNED')\n"
            + "          AND (? = 0 OR s.vocabularyId IS NOT NULL)\n"
            + "          AND (\n"
            + "                ? = '%'\n"
            + "                OR v.normalizedHeadword LIKE ?\n"
            + "                OR LOWER(v.headword) LIKE ?\n"
            + "                OR LOWER(v.definitions) LIKE ?\n"
            + "                OR EXISTS (\n"
            + "                    SELECT 1 FROM inflected_forms f\n"
            + "                    WHERE f.vocabularyId = v.id AND f.normalizedForm LIKE ?\n"
            + "                )\n"
            + "          )\n"
            + "        ORDER BY v.normalizedHeadword\n"
            + "        LIMIT ?\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 13);
    int _argIndex = 1;
    _statement.bindString(_argIndex, language);
    _argIndex = 2;
    final String _tmp;
    if (book == null) {
      _tmp = null;
    } else {
      _tmp = __converters.vocabBookToString(book);
    }
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp);
    }
    _argIndex = 3;
    final String _tmp_1;
    if (book == null) {
      _tmp_1 = null;
    } else {
      _tmp_1 = __converters.vocabBookToString(book);
    }
    if (_tmp_1 == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp_1);
    }
    _argIndex = 4;
    if (pos == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, pos);
    }
    _argIndex = 5;
    if (pos == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, pos);
    }
    _argIndex = 6;
    _statement.bindLong(_argIndex, onlyLearned);
    _argIndex = 7;
    _statement.bindLong(_argIndex, onlySaved);
    _argIndex = 8;
    _statement.bindString(_argIndex, pattern);
    _argIndex = 9;
    _statement.bindString(_argIndex, pattern);
    _argIndex = 10;
    _statement.bindString(_argIndex, pattern);
    _argIndex = 11;
    _statement.bindString(_argIndex, pattern);
    _argIndex = 12;
    _statement.bindString(_argIndex, pattern);
    _argIndex = 13;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"vocabulary", "learning_records",
        "saved_words", "inflected_forms"}, new Callable<List<VocabularyListRow>>() {
      @Override
      @NonNull
      public List<VocabularyListRow> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfHeadword = 1;
          final int _cursorIndexOfPos = 2;
          final int _cursorIndexOfGender = 3;
          final int _cursorIndexOfDefinitions = 4;
          final int _cursorIndexOfFrequencyRank = 5;
          final int _cursorIndexOfBook = 6;
          final int _cursorIndexOfLearned = 7;
          final int _cursorIndexOfSaved = 8;
          final List<VocabularyListRow> _result = new ArrayList<VocabularyListRow>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final VocabularyListRow _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpHeadword;
            _tmpHeadword = _cursor.getString(_cursorIndexOfHeadword);
            final Pos _tmpPos;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfPos);
            _tmpPos = __converters.stringToPos(_tmp_2);
            final Gender _tmpGender;
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfGender)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfGender);
            }
            _tmpGender = __converters.stringToGender(_tmp_3);
            final List<String> _tmpDefinitions;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfDefinitions);
            _tmpDefinitions = __converters.jsonToStringList(_tmp_4);
            final Integer _tmpFrequencyRank;
            if (_cursor.isNull(_cursorIndexOfFrequencyRank)) {
              _tmpFrequencyRank = null;
            } else {
              _tmpFrequencyRank = _cursor.getInt(_cursorIndexOfFrequencyRank);
            }
            final VocabBook _tmpBook;
            final String _tmp_5;
            _tmp_5 = _cursor.getString(_cursorIndexOfBook);
            _tmpBook = __converters.stringToVocabBook(_tmp_5);
            final boolean _tmpLearned;
            final int _tmp_6;
            _tmp_6 = _cursor.getInt(_cursorIndexOfLearned);
            _tmpLearned = _tmp_6 != 0;
            final boolean _tmpSaved;
            final int _tmp_7;
            _tmp_7 = _cursor.getInt(_cursorIndexOfSaved);
            _tmpSaved = _tmp_7 != 0;
            _item = new VocabularyListRow(_tmpId,_tmpHeadword,_tmpPos,_tmpGender,_tmpDefinitions,_tmpFrequencyRank,_tmpBook,_tmpLearned,_tmpSaved);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object searchByNormalized(final String language, final String pattern,
      final Continuation<? super List<VocabularyEntity>> $completion) {
    final String _sql = "SELECT * FROM vocabulary WHERE languageCode = ? AND normalizedHeadword LIKE ? ORDER BY normalizedHeadword";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, language);
    _argIndex = 2;
    _statement.bindString(_argIndex, pattern);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<VocabularyEntity>>() {
      @Override
      @NonNull
      public List<VocabularyEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLanguageCode = CursorUtil.getColumnIndexOrThrow(_cursor, "languageCode");
          final int _cursorIndexOfHeadword = CursorUtil.getColumnIndexOrThrow(_cursor, "headword");
          final int _cursorIndexOfNormalizedHeadword = CursorUtil.getColumnIndexOrThrow(_cursor, "normalizedHeadword");
          final int _cursorIndexOfPos = CursorUtil.getColumnIndexOrThrow(_cursor, "pos");
          final int _cursorIndexOfGender = CursorUtil.getColumnIndexOrThrow(_cursor, "gender");
          final int _cursorIndexOfBook = CursorUtil.getColumnIndexOrThrow(_cursor, "book");
          final int _cursorIndexOfDefinitions = CursorUtil.getColumnIndexOrThrow(_cursor, "definitions");
          final int _cursorIndexOfGrammaticalClass = CursorUtil.getColumnIndexOrThrow(_cursor, "grammaticalClass");
          final int _cursorIndexOfInflectionType = CursorUtil.getColumnIndexOrThrow(_cursor, "inflectionType");
          final int _cursorIndexOfPrincipalParts = CursorUtil.getColumnIndexOrThrow(_cursor, "principalParts");
          final int _cursorIndexOfIrregularForms = CursorUtil.getColumnIndexOrThrow(_cursor, "irregularForms");
          final int _cursorIndexOfEtymology = CursorUtil.getColumnIndexOrThrow(_cursor, "etymology");
          final int _cursorIndexOfEtymologyNote = CursorUtil.getColumnIndexOrThrow(_cursor, "etymologyNote");
          final int _cursorIndexOfSourceReference = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceReference");
          final int _cursorIndexOfFrequencyRank = CursorUtil.getColumnIndexOrThrow(_cursor, "frequencyRank");
          final int _cursorIndexOfDatasetVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "datasetVersion");
          final List<VocabularyEntity> _result = new ArrayList<VocabularyEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final VocabularyEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpLanguageCode;
            _tmpLanguageCode = _cursor.getString(_cursorIndexOfLanguageCode);
            final String _tmpHeadword;
            _tmpHeadword = _cursor.getString(_cursorIndexOfHeadword);
            final String _tmpNormalizedHeadword;
            _tmpNormalizedHeadword = _cursor.getString(_cursorIndexOfNormalizedHeadword);
            final Pos _tmpPos;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfPos);
            _tmpPos = __converters.stringToPos(_tmp);
            final Gender _tmpGender;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfGender)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfGender);
            }
            _tmpGender = __converters.stringToGender(_tmp_1);
            final VocabBook _tmpBook;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfBook);
            _tmpBook = __converters.stringToVocabBook(_tmp_2);
            final List<String> _tmpDefinitions;
            final String _tmp_3;
            _tmp_3 = _cursor.getString(_cursorIndexOfDefinitions);
            _tmpDefinitions = __converters.jsonToStringList(_tmp_3);
            final String _tmpGrammaticalClass;
            _tmpGrammaticalClass = _cursor.getString(_cursorIndexOfGrammaticalClass);
            final String _tmpInflectionType;
            _tmpInflectionType = _cursor.getString(_cursorIndexOfInflectionType);
            final List<String> _tmpPrincipalParts;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfPrincipalParts);
            _tmpPrincipalParts = __converters.jsonToStringList(_tmp_4);
            final List<String> _tmpIrregularForms;
            final String _tmp_5;
            _tmp_5 = _cursor.getString(_cursorIndexOfIrregularForms);
            _tmpIrregularForms = __converters.jsonToStringList(_tmp_5);
            final String _tmpEtymology;
            if (_cursor.isNull(_cursorIndexOfEtymology)) {
              _tmpEtymology = null;
            } else {
              _tmpEtymology = _cursor.getString(_cursorIndexOfEtymology);
            }
            final String _tmpEtymologyNote;
            if (_cursor.isNull(_cursorIndexOfEtymologyNote)) {
              _tmpEtymologyNote = null;
            } else {
              _tmpEtymologyNote = _cursor.getString(_cursorIndexOfEtymologyNote);
            }
            final String _tmpSourceReference;
            if (_cursor.isNull(_cursorIndexOfSourceReference)) {
              _tmpSourceReference = null;
            } else {
              _tmpSourceReference = _cursor.getString(_cursorIndexOfSourceReference);
            }
            final Integer _tmpFrequencyRank;
            if (_cursor.isNull(_cursorIndexOfFrequencyRank)) {
              _tmpFrequencyRank = null;
            } else {
              _tmpFrequencyRank = _cursor.getInt(_cursorIndexOfFrequencyRank);
            }
            final int _tmpDatasetVersion;
            _tmpDatasetVersion = _cursor.getInt(_cursorIndexOfDatasetVersion);
            _item = new VocabularyEntity(_tmpId,_tmpLanguageCode,_tmpHeadword,_tmpNormalizedHeadword,_tmpPos,_tmpGender,_tmpBook,_tmpDefinitions,_tmpGrammaticalClass,_tmpInflectionType,_tmpPrincipalParts,_tmpIrregularForms,_tmpEtymology,_tmpEtymologyNote,_tmpSourceReference,_tmpFrequencyRank,_tmpDatasetVersion);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object nextUnintroduced(final String language, final VocabBook book, final int limit,
      final Continuation<? super List<VocabularyEntity>> $completion) {
    final String _sql = "\n"
            + "        SELECT v.* FROM vocabulary v\n"
            + "        WHERE v.languageCode = ?\n"
            + "          AND v.book = ?\n"
            + "          AND NOT EXISTS (SELECT 1 FROM learning_records r WHERE r.vocabularyId = v.id)\n"
            + "        ORDER BY RANDOM()\n"
            + "        LIMIT ?\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindString(_argIndex, language);
    _argIndex = 2;
    final String _tmp = __converters.vocabBookToString(book);
    _statement.bindString(_argIndex, _tmp);
    _argIndex = 3;
    _statement.bindLong(_argIndex, limit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<VocabularyEntity>>() {
      @Override
      @NonNull
      public List<VocabularyEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLanguageCode = CursorUtil.getColumnIndexOrThrow(_cursor, "languageCode");
          final int _cursorIndexOfHeadword = CursorUtil.getColumnIndexOrThrow(_cursor, "headword");
          final int _cursorIndexOfNormalizedHeadword = CursorUtil.getColumnIndexOrThrow(_cursor, "normalizedHeadword");
          final int _cursorIndexOfPos = CursorUtil.getColumnIndexOrThrow(_cursor, "pos");
          final int _cursorIndexOfGender = CursorUtil.getColumnIndexOrThrow(_cursor, "gender");
          final int _cursorIndexOfBook = CursorUtil.getColumnIndexOrThrow(_cursor, "book");
          final int _cursorIndexOfDefinitions = CursorUtil.getColumnIndexOrThrow(_cursor, "definitions");
          final int _cursorIndexOfGrammaticalClass = CursorUtil.getColumnIndexOrThrow(_cursor, "grammaticalClass");
          final int _cursorIndexOfInflectionType = CursorUtil.getColumnIndexOrThrow(_cursor, "inflectionType");
          final int _cursorIndexOfPrincipalParts = CursorUtil.getColumnIndexOrThrow(_cursor, "principalParts");
          final int _cursorIndexOfIrregularForms = CursorUtil.getColumnIndexOrThrow(_cursor, "irregularForms");
          final int _cursorIndexOfEtymology = CursorUtil.getColumnIndexOrThrow(_cursor, "etymology");
          final int _cursorIndexOfEtymologyNote = CursorUtil.getColumnIndexOrThrow(_cursor, "etymologyNote");
          final int _cursorIndexOfSourceReference = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceReference");
          final int _cursorIndexOfFrequencyRank = CursorUtil.getColumnIndexOrThrow(_cursor, "frequencyRank");
          final int _cursorIndexOfDatasetVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "datasetVersion");
          final List<VocabularyEntity> _result = new ArrayList<VocabularyEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final VocabularyEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpLanguageCode;
            _tmpLanguageCode = _cursor.getString(_cursorIndexOfLanguageCode);
            final String _tmpHeadword;
            _tmpHeadword = _cursor.getString(_cursorIndexOfHeadword);
            final String _tmpNormalizedHeadword;
            _tmpNormalizedHeadword = _cursor.getString(_cursorIndexOfNormalizedHeadword);
            final Pos _tmpPos;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfPos);
            _tmpPos = __converters.stringToPos(_tmp_1);
            final Gender _tmpGender;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfGender)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfGender);
            }
            _tmpGender = __converters.stringToGender(_tmp_2);
            final VocabBook _tmpBook;
            final String _tmp_3;
            _tmp_3 = _cursor.getString(_cursorIndexOfBook);
            _tmpBook = __converters.stringToVocabBook(_tmp_3);
            final List<String> _tmpDefinitions;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfDefinitions);
            _tmpDefinitions = __converters.jsonToStringList(_tmp_4);
            final String _tmpGrammaticalClass;
            _tmpGrammaticalClass = _cursor.getString(_cursorIndexOfGrammaticalClass);
            final String _tmpInflectionType;
            _tmpInflectionType = _cursor.getString(_cursorIndexOfInflectionType);
            final List<String> _tmpPrincipalParts;
            final String _tmp_5;
            _tmp_5 = _cursor.getString(_cursorIndexOfPrincipalParts);
            _tmpPrincipalParts = __converters.jsonToStringList(_tmp_5);
            final List<String> _tmpIrregularForms;
            final String _tmp_6;
            _tmp_6 = _cursor.getString(_cursorIndexOfIrregularForms);
            _tmpIrregularForms = __converters.jsonToStringList(_tmp_6);
            final String _tmpEtymology;
            if (_cursor.isNull(_cursorIndexOfEtymology)) {
              _tmpEtymology = null;
            } else {
              _tmpEtymology = _cursor.getString(_cursorIndexOfEtymology);
            }
            final String _tmpEtymologyNote;
            if (_cursor.isNull(_cursorIndexOfEtymologyNote)) {
              _tmpEtymologyNote = null;
            } else {
              _tmpEtymologyNote = _cursor.getString(_cursorIndexOfEtymologyNote);
            }
            final String _tmpSourceReference;
            if (_cursor.isNull(_cursorIndexOfSourceReference)) {
              _tmpSourceReference = null;
            } else {
              _tmpSourceReference = _cursor.getString(_cursorIndexOfSourceReference);
            }
            final Integer _tmpFrequencyRank;
            if (_cursor.isNull(_cursorIndexOfFrequencyRank)) {
              _tmpFrequencyRank = null;
            } else {
              _tmpFrequencyRank = _cursor.getInt(_cursorIndexOfFrequencyRank);
            }
            final int _tmpDatasetVersion;
            _tmpDatasetVersion = _cursor.getInt(_cursorIndexOfDatasetVersion);
            _item = new VocabularyEntity(_tmpId,_tmpLanguageCode,_tmpHeadword,_tmpNormalizedHeadword,_tmpPos,_tmpGender,_tmpBook,_tmpDefinitions,_tmpGrammaticalClass,_tmpInflectionType,_tmpPrincipalParts,_tmpIrregularForms,_tmpEtymology,_tmpEtymologyNote,_tmpSourceReference,_tmpFrequencyRank,_tmpDatasetVersion);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object randomPool(final String language, final int limit,
      final Continuation<? super List<VocabularyEntity>> $completion) {
    final String _sql = "SELECT * FROM vocabulary WHERE languageCode = ? ORDER BY RANDOM() LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, language);
    _argIndex = 2;
    _statement.bindLong(_argIndex, limit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<VocabularyEntity>>() {
      @Override
      @NonNull
      public List<VocabularyEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLanguageCode = CursorUtil.getColumnIndexOrThrow(_cursor, "languageCode");
          final int _cursorIndexOfHeadword = CursorUtil.getColumnIndexOrThrow(_cursor, "headword");
          final int _cursorIndexOfNormalizedHeadword = CursorUtil.getColumnIndexOrThrow(_cursor, "normalizedHeadword");
          final int _cursorIndexOfPos = CursorUtil.getColumnIndexOrThrow(_cursor, "pos");
          final int _cursorIndexOfGender = CursorUtil.getColumnIndexOrThrow(_cursor, "gender");
          final int _cursorIndexOfBook = CursorUtil.getColumnIndexOrThrow(_cursor, "book");
          final int _cursorIndexOfDefinitions = CursorUtil.getColumnIndexOrThrow(_cursor, "definitions");
          final int _cursorIndexOfGrammaticalClass = CursorUtil.getColumnIndexOrThrow(_cursor, "grammaticalClass");
          final int _cursorIndexOfInflectionType = CursorUtil.getColumnIndexOrThrow(_cursor, "inflectionType");
          final int _cursorIndexOfPrincipalParts = CursorUtil.getColumnIndexOrThrow(_cursor, "principalParts");
          final int _cursorIndexOfIrregularForms = CursorUtil.getColumnIndexOrThrow(_cursor, "irregularForms");
          final int _cursorIndexOfEtymology = CursorUtil.getColumnIndexOrThrow(_cursor, "etymology");
          final int _cursorIndexOfEtymologyNote = CursorUtil.getColumnIndexOrThrow(_cursor, "etymologyNote");
          final int _cursorIndexOfSourceReference = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceReference");
          final int _cursorIndexOfFrequencyRank = CursorUtil.getColumnIndexOrThrow(_cursor, "frequencyRank");
          final int _cursorIndexOfDatasetVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "datasetVersion");
          final List<VocabularyEntity> _result = new ArrayList<VocabularyEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final VocabularyEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpLanguageCode;
            _tmpLanguageCode = _cursor.getString(_cursorIndexOfLanguageCode);
            final String _tmpHeadword;
            _tmpHeadword = _cursor.getString(_cursorIndexOfHeadword);
            final String _tmpNormalizedHeadword;
            _tmpNormalizedHeadword = _cursor.getString(_cursorIndexOfNormalizedHeadword);
            final Pos _tmpPos;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfPos);
            _tmpPos = __converters.stringToPos(_tmp);
            final Gender _tmpGender;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfGender)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfGender);
            }
            _tmpGender = __converters.stringToGender(_tmp_1);
            final VocabBook _tmpBook;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfBook);
            _tmpBook = __converters.stringToVocabBook(_tmp_2);
            final List<String> _tmpDefinitions;
            final String _tmp_3;
            _tmp_3 = _cursor.getString(_cursorIndexOfDefinitions);
            _tmpDefinitions = __converters.jsonToStringList(_tmp_3);
            final String _tmpGrammaticalClass;
            _tmpGrammaticalClass = _cursor.getString(_cursorIndexOfGrammaticalClass);
            final String _tmpInflectionType;
            _tmpInflectionType = _cursor.getString(_cursorIndexOfInflectionType);
            final List<String> _tmpPrincipalParts;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfPrincipalParts);
            _tmpPrincipalParts = __converters.jsonToStringList(_tmp_4);
            final List<String> _tmpIrregularForms;
            final String _tmp_5;
            _tmp_5 = _cursor.getString(_cursorIndexOfIrregularForms);
            _tmpIrregularForms = __converters.jsonToStringList(_tmp_5);
            final String _tmpEtymology;
            if (_cursor.isNull(_cursorIndexOfEtymology)) {
              _tmpEtymology = null;
            } else {
              _tmpEtymology = _cursor.getString(_cursorIndexOfEtymology);
            }
            final String _tmpEtymologyNote;
            if (_cursor.isNull(_cursorIndexOfEtymologyNote)) {
              _tmpEtymologyNote = null;
            } else {
              _tmpEtymologyNote = _cursor.getString(_cursorIndexOfEtymologyNote);
            }
            final String _tmpSourceReference;
            if (_cursor.isNull(_cursorIndexOfSourceReference)) {
              _tmpSourceReference = null;
            } else {
              _tmpSourceReference = _cursor.getString(_cursorIndexOfSourceReference);
            }
            final Integer _tmpFrequencyRank;
            if (_cursor.isNull(_cursorIndexOfFrequencyRank)) {
              _tmpFrequencyRank = null;
            } else {
              _tmpFrequencyRank = _cursor.getInt(_cursorIndexOfFrequencyRank);
            }
            final int _tmpDatasetVersion;
            _tmpDatasetVersion = _cursor.getInt(_cursorIndexOfDatasetVersion);
            _item = new VocabularyEntity(_tmpId,_tmpLanguageCode,_tmpHeadword,_tmpNormalizedHeadword,_tmpPos,_tmpGender,_tmpBook,_tmpDefinitions,_tmpGrammaticalClass,_tmpInflectionType,_tmpPrincipalParts,_tmpIrregularForms,_tmpEtymology,_tmpEtymologyNote,_tmpSourceReference,_tmpFrequencyRank,_tmpDatasetVersion);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object lookupForm(final String language, final String normalized, final int limit,
      final Continuation<? super List<FormLookupRow>> $completion) {
    final String _sql = "\n"
            + "        SELECT v.id AS vocabularyId, v.headword AS headword, v.pos AS pos, v.gender AS gender,\n"
            + "               v.book AS book, v.definitions AS definitions, v.grammaticalClass AS grammaticalClass,\n"
            + "               v.inflectionType AS inflectionType, f.writtenForm AS writtenForm, f.dimensions AS dimensions\n"
            + "        FROM inflected_forms f\n"
            + "        JOIN vocabulary v ON v.id = f.vocabularyId\n"
            + "        WHERE v.languageCode = ? AND f.normalizedForm = ?\n"
            + "        ORDER BY v.frequencyRank IS NULL, v.frequencyRank, v.headword\n"
            + "        LIMIT ?\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindString(_argIndex, language);
    _argIndex = 2;
    _statement.bindString(_argIndex, normalized);
    _argIndex = 3;
    _statement.bindLong(_argIndex, limit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<FormLookupRow>>() {
      @Override
      @NonNull
      public List<FormLookupRow> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfVocabularyId = 0;
          final int _cursorIndexOfHeadword = 1;
          final int _cursorIndexOfPos = 2;
          final int _cursorIndexOfGender = 3;
          final int _cursorIndexOfBook = 4;
          final int _cursorIndexOfDefinitions = 5;
          final int _cursorIndexOfGrammaticalClass = 6;
          final int _cursorIndexOfInflectionType = 7;
          final int _cursorIndexOfWrittenForm = 8;
          final int _cursorIndexOfDimensions = 9;
          final List<FormLookupRow> _result = new ArrayList<FormLookupRow>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FormLookupRow _item;
            final String _tmpVocabularyId;
            _tmpVocabularyId = _cursor.getString(_cursorIndexOfVocabularyId);
            final String _tmpHeadword;
            _tmpHeadword = _cursor.getString(_cursorIndexOfHeadword);
            final Pos _tmpPos;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfPos);
            _tmpPos = __converters.stringToPos(_tmp);
            final Gender _tmpGender;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfGender)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfGender);
            }
            _tmpGender = __converters.stringToGender(_tmp_1);
            final VocabBook _tmpBook;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfBook);
            _tmpBook = __converters.stringToVocabBook(_tmp_2);
            final List<String> _tmpDefinitions;
            final String _tmp_3;
            _tmp_3 = _cursor.getString(_cursorIndexOfDefinitions);
            _tmpDefinitions = __converters.jsonToStringList(_tmp_3);
            final String _tmpGrammaticalClass;
            _tmpGrammaticalClass = _cursor.getString(_cursorIndexOfGrammaticalClass);
            final String _tmpInflectionType;
            _tmpInflectionType = _cursor.getString(_cursorIndexOfInflectionType);
            final String _tmpWrittenForm;
            _tmpWrittenForm = _cursor.getString(_cursorIndexOfWrittenForm);
            final Map<String, String> _tmpDimensions;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfDimensions);
            _tmpDimensions = __converters.jsonToStringMap(_tmp_4);
            _item = new FormLookupRow(_tmpVocabularyId,_tmpHeadword,_tmpPos,_tmpGender,_tmpBook,_tmpDefinitions,_tmpGrammaticalClass,_tmpInflectionType,_tmpWrittenForm,_tmpDimensions);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object lookupHeadword(final String language, final String normalized, final int limit,
      final Continuation<? super List<FormLookupRow>> $completion) {
    final String _sql = "\n"
            + "        SELECT v.id AS vocabularyId, v.headword AS headword, v.pos AS pos, v.gender AS gender,\n"
            + "               v.book AS book, v.definitions AS definitions, v.grammaticalClass AS grammaticalClass,\n"
            + "               v.inflectionType AS inflectionType, v.headword AS writtenForm, '' AS dimensions\n"
            + "        FROM vocabulary v\n"
            + "        WHERE v.languageCode = ? AND v.normalizedHeadword = ?\n"
            + "        ORDER BY v.frequencyRank IS NULL, v.frequencyRank\n"
            + "        LIMIT ?\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindString(_argIndex, language);
    _argIndex = 2;
    _statement.bindString(_argIndex, normalized);
    _argIndex = 3;
    _statement.bindLong(_argIndex, limit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<FormLookupRow>>() {
      @Override
      @NonNull
      public List<FormLookupRow> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfVocabularyId = 0;
          final int _cursorIndexOfHeadword = 1;
          final int _cursorIndexOfPos = 2;
          final int _cursorIndexOfGender = 3;
          final int _cursorIndexOfBook = 4;
          final int _cursorIndexOfDefinitions = 5;
          final int _cursorIndexOfGrammaticalClass = 6;
          final int _cursorIndexOfInflectionType = 7;
          final int _cursorIndexOfWrittenForm = 8;
          final int _cursorIndexOfDimensions = 9;
          final List<FormLookupRow> _result = new ArrayList<FormLookupRow>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FormLookupRow _item;
            final String _tmpVocabularyId;
            _tmpVocabularyId = _cursor.getString(_cursorIndexOfVocabularyId);
            final String _tmpHeadword;
            _tmpHeadword = _cursor.getString(_cursorIndexOfHeadword);
            final Pos _tmpPos;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfPos);
            _tmpPos = __converters.stringToPos(_tmp);
            final Gender _tmpGender;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfGender)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfGender);
            }
            _tmpGender = __converters.stringToGender(_tmp_1);
            final VocabBook _tmpBook;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfBook);
            _tmpBook = __converters.stringToVocabBook(_tmp_2);
            final List<String> _tmpDefinitions;
            final String _tmp_3;
            _tmp_3 = _cursor.getString(_cursorIndexOfDefinitions);
            _tmpDefinitions = __converters.jsonToStringList(_tmp_3);
            final String _tmpGrammaticalClass;
            _tmpGrammaticalClass = _cursor.getString(_cursorIndexOfGrammaticalClass);
            final String _tmpInflectionType;
            _tmpInflectionType = _cursor.getString(_cursorIndexOfInflectionType);
            final String _tmpWrittenForm;
            _tmpWrittenForm = _cursor.getString(_cursorIndexOfWrittenForm);
            final Map<String, String> _tmpDimensions;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfDimensions);
            _tmpDimensions = __converters.jsonToStringMap(_tmp_4);
            _item = new FormLookupRow(_tmpVocabularyId,_tmpHeadword,_tmpPos,_tmpGender,_tmpBook,_tmpDefinitions,_tmpGrammaticalClass,_tmpInflectionType,_tmpWrittenForm,_tmpDimensions);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteByIds(final List<String> ids, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
        _stringBuilder.append("DELETE FROM vocabulary WHERE id IN (");
        final int _inputSize = ids.size();
        StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
        _stringBuilder.append(")");
        final String _sql = _stringBuilder.toString();
        final SupportSQLiteStatement _stmt = __db.compileStatement(_sql);
        int _argIndex = 1;
        for (String _item : ids) {
          _stmt.bindString(_argIndex, _item);
          _argIndex++;
        }
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteFormsByIds(final List<String> ids,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
        _stringBuilder.append("DELETE FROM inflected_forms WHERE vocabularyId IN (");
        final int _inputSize = ids.size();
        StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
        _stringBuilder.append(")");
        final String _sql = _stringBuilder.toString();
        final SupportSQLiteStatement _stmt = __db.compileStatement(_sql);
        int _argIndex = 1;
        for (String _item : ids) {
          _stmt.bindString(_argIndex, _item);
          _argIndex++;
        }
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteExamplesByIds(final List<String> ids,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
        _stringBuilder.append("DELETE FROM example_sentences WHERE vocabularyId IN (");
        final int _inputSize = ids.size();
        StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
        _stringBuilder.append(")");
        final String _sql = _stringBuilder.toString();
        final SupportSQLiteStatement _stmt = __db.compileStatement(_sql);
        int _argIndex = 1;
        for (String _item : ids) {
          _stmt.bindString(_argIndex, _item);
          _argIndex++;
        }
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteFormsForIds(final List<String> vocabularyIds,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
        _stringBuilder.append("DELETE FROM inflected_forms WHERE vocabularyId IN (");
        final int _inputSize = vocabularyIds.size();
        StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
        _stringBuilder.append(")");
        final String _sql = _stringBuilder.toString();
        final SupportSQLiteStatement _stmt = __db.compileStatement(_sql);
        int _argIndex = 1;
        for (String _item : vocabularyIds) {
          _stmt.bindString(_argIndex, _item);
          _argIndex++;
        }
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteExamplesForIds(final List<String> vocabularyIds,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
        _stringBuilder.append("DELETE FROM example_sentences WHERE vocabularyId IN (");
        final int _inputSize = vocabularyIds.size();
        StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
        _stringBuilder.append(")");
        final String _sql = _stringBuilder.toString();
        final SupportSQLiteStatement _stmt = __db.compileStatement(_sql);
        int _argIndex = 1;
        for (String _item : vocabularyIds) {
          _stmt.bindString(_argIndex, _item);
          _argIndex++;
        }
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
