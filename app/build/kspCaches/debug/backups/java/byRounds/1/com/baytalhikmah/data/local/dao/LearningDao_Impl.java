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
import com.baytalhikmah.data.local.DailyTaskEntity;
import com.baytalhikmah.data.local.LearningRecordEntity;
import com.baytalhikmah.data.local.LearningSessionEntity;
import com.baytalhikmah.data.local.SavedWordEntity;
import com.baytalhikmah.data.model.Gender;
import com.baytalhikmah.data.model.LearningPhase;
import com.baytalhikmah.data.model.LearningStatus;
import com.baytalhikmah.data.model.NewLearningStatus;
import com.baytalhikmah.data.model.OverallStatus;
import com.baytalhikmah.data.model.Pos;
import com.baytalhikmah.data.model.PostTestStatus;
import com.baytalhikmah.data.model.PreliminaryStatus;
import com.baytalhikmah.data.model.VocabBook;
import java.lang.Boolean;
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
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class LearningDao_Impl implements LearningDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<LearningRecordEntity> __insertionAdapterOfLearningRecordEntity;

  private final Converters __converters = new Converters();

  private final EntityInsertionAdapter<DailyTaskEntity> __insertionAdapterOfDailyTaskEntity;

  private final EntityInsertionAdapter<LearningSessionEntity> __insertionAdapterOfLearningSessionEntity;

  private final EntityInsertionAdapter<SavedWordEntity> __insertionAdapterOfSavedWordEntity;

  private final SharedSQLiteStatement __preparedStmtOfRepointRecord;

  private final SharedSQLiteStatement __preparedStmtOfDeleteRecord;

  private final SharedSQLiteStatement __preparedStmtOfRecordAttempt;

  private final SharedSQLiteStatement __preparedStmtOfSetDifficult;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllRecords;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllDailyTasks;

  private final SharedSQLiteStatement __preparedStmtOfDeleteLearningSession;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllLearningSessions;

  private final SharedSQLiteStatement __preparedStmtOfRemoveSavedWord;

  private final SharedSQLiteStatement __preparedStmtOfRepointSaved;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllSaved;

  public LearningDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfLearningRecordEntity = new EntityInsertionAdapter<LearningRecordEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `learning_records` (`vocabularyId`,`languageCode`,`status`,`introducedOn`,`firstLearnedOn`,`lastReviewedOn`,`totalAttempts`,`correctAttempts`,`incorrectAttempts`,`difficult`,`mastered`) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final LearningRecordEntity entity) {
        statement.bindString(1, entity.getVocabularyId());
        statement.bindString(2, entity.getLanguageCode());
        final String _tmp = __converters.learningStatusToString(entity.getStatus());
        statement.bindString(3, _tmp);
        if (entity.getIntroducedOn() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getIntroducedOn());
        }
        if (entity.getFirstLearnedOn() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getFirstLearnedOn());
        }
        if (entity.getLastReviewedOn() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getLastReviewedOn());
        }
        statement.bindLong(7, entity.getTotalAttempts());
        statement.bindLong(8, entity.getCorrectAttempts());
        statement.bindLong(9, entity.getIncorrectAttempts());
        final int _tmp_1 = entity.getDifficult() ? 1 : 0;
        statement.bindLong(10, _tmp_1);
        final int _tmp_2 = entity.getMastered() ? 1 : 0;
        statement.bindLong(11, _tmp_2);
      }
    };
    this.__insertionAdapterOfDailyTaskEntity = new EntityInsertionAdapter<DailyTaskEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `daily_tasks` (`id`,`languageCode`,`learningDate`,`book`,`plannedNewWords`,`completedNewWords`,`preliminaryStatus`,`newLearningStatus`,`postTestStatus`,`overallStatus`,`createdAt`,`updatedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DailyTaskEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getLanguageCode());
        statement.bindString(3, entity.getLearningDate());
        final String _tmp = __converters.vocabBookToString(entity.getBook());
        statement.bindString(4, _tmp);
        statement.bindLong(5, entity.getPlannedNewWords());
        statement.bindLong(6, entity.getCompletedNewWords());
        final String _tmp_1 = __converters.preliminaryToString(entity.getPreliminaryStatus());
        statement.bindString(7, _tmp_1);
        final String _tmp_2 = __converters.newLearningToString(entity.getNewLearningStatus());
        statement.bindString(8, _tmp_2);
        final String _tmp_3 = __converters.postTestToString(entity.getPostTestStatus());
        statement.bindString(9, _tmp_3);
        final String _tmp_4 = __converters.overallToString(entity.getOverallStatus());
        statement.bindString(10, _tmp_4);
        statement.bindLong(11, entity.getCreatedAt());
        statement.bindLong(12, entity.getUpdatedAt());
      }
    };
    this.__insertionAdapterOfLearningSessionEntity = new EntityInsertionAdapter<LearningSessionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `learning_sessions` (`id`,`languageCode`,`learningDate`,`book`,`vocabularyIds`,`targetCount`,`cardCursor`,`quizRound`,`passedIds`,`pendingQuizIds`,`phase`,`activeQuizSessionId`,`completedOn`,`createdAt`,`updatedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final LearningSessionEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getLanguageCode());
        statement.bindString(3, entity.getLearningDate());
        final String _tmp = __converters.vocabBookToString(entity.getBook());
        statement.bindString(4, _tmp);
        final String _tmp_1 = __converters.stringListToJson(entity.getVocabularyIds());
        statement.bindString(5, _tmp_1);
        statement.bindLong(6, entity.getTargetCount());
        statement.bindLong(7, entity.getCardCursor());
        statement.bindLong(8, entity.getQuizRound());
        final String _tmp_2 = __converters.stringListToJson(entity.getPassedIds());
        statement.bindString(9, _tmp_2);
        final String _tmp_3 = __converters.stringListToJson(entity.getPendingQuizIds());
        statement.bindString(10, _tmp_3);
        final String _tmp_4 = __converters.learningPhaseToString(entity.getPhase());
        statement.bindString(11, _tmp_4);
        if (entity.getActiveQuizSessionId() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getActiveQuizSessionId());
        }
        if (entity.getCompletedOn() == null) {
          statement.bindNull(13);
        } else {
          statement.bindString(13, entity.getCompletedOn());
        }
        statement.bindLong(14, entity.getCreatedAt());
        statement.bindLong(15, entity.getUpdatedAt());
      }
    };
    this.__insertionAdapterOfSavedWordEntity = new EntityInsertionAdapter<SavedWordEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `saved_words` (`vocabularyId`,`languageCode`,`savedOn`) VALUES (?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SavedWordEntity entity) {
        statement.bindString(1, entity.getVocabularyId());
        statement.bindString(2, entity.getLanguageCode());
        statement.bindString(3, entity.getSavedOn());
      }
    };
    this.__preparedStmtOfRepointRecord = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE learning_records SET vocabularyId = ? WHERE vocabularyId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteRecord = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM learning_records WHERE vocabularyId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfRecordAttempt = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "\n"
                + "        UPDATE learning_records\n"
                + "        SET totalAttempts = totalAttempts + 1,\n"
                + "            correctAttempts = correctAttempts + ?,\n"
                + "            incorrectAttempts = incorrectAttempts + ?,\n"
                + "            lastReviewedOn = ?\n"
                + "        WHERE vocabularyId = ?\n"
                + "        ";
        return _query;
      }
    };
    this.__preparedStmtOfSetDifficult = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE learning_records SET difficult = ? WHERE vocabularyId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAllRecords = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM learning_records";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAllDailyTasks = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM daily_tasks";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteLearningSession = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM learning_sessions WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAllLearningSessions = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM learning_sessions";
        return _query;
      }
    };
    this.__preparedStmtOfRemoveSavedWord = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM saved_words WHERE vocabularyId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfRepointSaved = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE saved_words SET vocabularyId = ? WHERE vocabularyId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAllSaved = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM saved_words";
        return _query;
      }
    };
  }

  @Override
  public Object upsertRecord(final LearningRecordEntity record,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfLearningRecordEntity.insert(record);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object upsertRecords(final List<LearningRecordEntity> records,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfLearningRecordEntity.insert(records);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object upsertDailyTask(final DailyTaskEntity task,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfDailyTaskEntity.insert(task);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object upsertDailyTasks(final List<DailyTaskEntity> tasks,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfDailyTaskEntity.insert(tasks);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object upsertLearningSession(final LearningSessionEntity session,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfLearningSessionEntity.insert(session);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object upsertLearningSessions(final List<LearningSessionEntity> sessions,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfLearningSessionEntity.insert(sessions);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object saveWord(final SavedWordEntity word, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfSavedWordEntity.insert(word);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object saveWords(final List<SavedWordEntity> words,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfSavedWordEntity.insert(words);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object repointRecord(final String source, final String target,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfRepointRecord.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, target);
        _argIndex = 2;
        _stmt.bindString(_argIndex, source);
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
          __preparedStmtOfRepointRecord.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteRecord(final String vocabularyId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteRecord.acquire();
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
          __preparedStmtOfDeleteRecord.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object recordAttempt(final String vocabularyId, final int correct, final int incorrect,
      final String date, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfRecordAttempt.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, correct);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, incorrect);
        _argIndex = 3;
        _stmt.bindString(_argIndex, date);
        _argIndex = 4;
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
          __preparedStmtOfRecordAttempt.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object setDifficult(final String vocabularyId, final boolean difficult,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfSetDifficult.acquire();
        int _argIndex = 1;
        final int _tmp = difficult ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
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
          __preparedStmtOfSetDifficult.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAllRecords(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllRecords.acquire();
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
          __preparedStmtOfDeleteAllRecords.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAllDailyTasks(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllDailyTasks.acquire();
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
          __preparedStmtOfDeleteAllDailyTasks.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteLearningSession(final String id,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteLearningSession.acquire();
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
          __preparedStmtOfDeleteLearningSession.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAllLearningSessions(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllLearningSessions.acquire();
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
          __preparedStmtOfDeleteAllLearningSessions.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object removeSavedWord(final String vocabularyId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfRemoveSavedWord.acquire();
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
          __preparedStmtOfRemoveSavedWord.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object repointSaved(final String source, final String target,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfRepointSaved.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, target);
        _argIndex = 2;
        _stmt.bindString(_argIndex, source);
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
          __preparedStmtOfRepointSaved.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAllSaved(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllSaved.acquire();
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
          __preparedStmtOfDeleteAllSaved.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object record(final String vocabularyId,
      final Continuation<? super LearningRecordEntity> $completion) {
    final String _sql = "SELECT * FROM learning_records WHERE vocabularyId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, vocabularyId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<LearningRecordEntity>() {
      @Override
      @Nullable
      public LearningRecordEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfVocabularyId = CursorUtil.getColumnIndexOrThrow(_cursor, "vocabularyId");
          final int _cursorIndexOfLanguageCode = CursorUtil.getColumnIndexOrThrow(_cursor, "languageCode");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfIntroducedOn = CursorUtil.getColumnIndexOrThrow(_cursor, "introducedOn");
          final int _cursorIndexOfFirstLearnedOn = CursorUtil.getColumnIndexOrThrow(_cursor, "firstLearnedOn");
          final int _cursorIndexOfLastReviewedOn = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReviewedOn");
          final int _cursorIndexOfTotalAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "totalAttempts");
          final int _cursorIndexOfCorrectAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "correctAttempts");
          final int _cursorIndexOfIncorrectAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "incorrectAttempts");
          final int _cursorIndexOfDifficult = CursorUtil.getColumnIndexOrThrow(_cursor, "difficult");
          final int _cursorIndexOfMastered = CursorUtil.getColumnIndexOrThrow(_cursor, "mastered");
          final LearningRecordEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpVocabularyId;
            _tmpVocabularyId = _cursor.getString(_cursorIndexOfVocabularyId);
            final String _tmpLanguageCode;
            _tmpLanguageCode = _cursor.getString(_cursorIndexOfLanguageCode);
            final LearningStatus _tmpStatus;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.stringToLearningStatus(_tmp);
            final String _tmpIntroducedOn;
            if (_cursor.isNull(_cursorIndexOfIntroducedOn)) {
              _tmpIntroducedOn = null;
            } else {
              _tmpIntroducedOn = _cursor.getString(_cursorIndexOfIntroducedOn);
            }
            final String _tmpFirstLearnedOn;
            if (_cursor.isNull(_cursorIndexOfFirstLearnedOn)) {
              _tmpFirstLearnedOn = null;
            } else {
              _tmpFirstLearnedOn = _cursor.getString(_cursorIndexOfFirstLearnedOn);
            }
            final String _tmpLastReviewedOn;
            if (_cursor.isNull(_cursorIndexOfLastReviewedOn)) {
              _tmpLastReviewedOn = null;
            } else {
              _tmpLastReviewedOn = _cursor.getString(_cursorIndexOfLastReviewedOn);
            }
            final int _tmpTotalAttempts;
            _tmpTotalAttempts = _cursor.getInt(_cursorIndexOfTotalAttempts);
            final int _tmpCorrectAttempts;
            _tmpCorrectAttempts = _cursor.getInt(_cursorIndexOfCorrectAttempts);
            final int _tmpIncorrectAttempts;
            _tmpIncorrectAttempts = _cursor.getInt(_cursorIndexOfIncorrectAttempts);
            final boolean _tmpDifficult;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfDifficult);
            _tmpDifficult = _tmp_1 != 0;
            final boolean _tmpMastered;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfMastered);
            _tmpMastered = _tmp_2 != 0;
            _result = new LearningRecordEntity(_tmpVocabularyId,_tmpLanguageCode,_tmpStatus,_tmpIntroducedOn,_tmpFirstLearnedOn,_tmpLastReviewedOn,_tmpTotalAttempts,_tmpCorrectAttempts,_tmpIncorrectAttempts,_tmpDifficult,_tmpMastered);
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
  public Flow<LearningRecordEntity> observeRecord(final String vocabularyId) {
    final String _sql = "SELECT * FROM learning_records WHERE vocabularyId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, vocabularyId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"learning_records"}, new Callable<LearningRecordEntity>() {
      @Override
      @Nullable
      public LearningRecordEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfVocabularyId = CursorUtil.getColumnIndexOrThrow(_cursor, "vocabularyId");
          final int _cursorIndexOfLanguageCode = CursorUtil.getColumnIndexOrThrow(_cursor, "languageCode");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfIntroducedOn = CursorUtil.getColumnIndexOrThrow(_cursor, "introducedOn");
          final int _cursorIndexOfFirstLearnedOn = CursorUtil.getColumnIndexOrThrow(_cursor, "firstLearnedOn");
          final int _cursorIndexOfLastReviewedOn = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReviewedOn");
          final int _cursorIndexOfTotalAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "totalAttempts");
          final int _cursorIndexOfCorrectAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "correctAttempts");
          final int _cursorIndexOfIncorrectAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "incorrectAttempts");
          final int _cursorIndexOfDifficult = CursorUtil.getColumnIndexOrThrow(_cursor, "difficult");
          final int _cursorIndexOfMastered = CursorUtil.getColumnIndexOrThrow(_cursor, "mastered");
          final LearningRecordEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpVocabularyId;
            _tmpVocabularyId = _cursor.getString(_cursorIndexOfVocabularyId);
            final String _tmpLanguageCode;
            _tmpLanguageCode = _cursor.getString(_cursorIndexOfLanguageCode);
            final LearningStatus _tmpStatus;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.stringToLearningStatus(_tmp);
            final String _tmpIntroducedOn;
            if (_cursor.isNull(_cursorIndexOfIntroducedOn)) {
              _tmpIntroducedOn = null;
            } else {
              _tmpIntroducedOn = _cursor.getString(_cursorIndexOfIntroducedOn);
            }
            final String _tmpFirstLearnedOn;
            if (_cursor.isNull(_cursorIndexOfFirstLearnedOn)) {
              _tmpFirstLearnedOn = null;
            } else {
              _tmpFirstLearnedOn = _cursor.getString(_cursorIndexOfFirstLearnedOn);
            }
            final String _tmpLastReviewedOn;
            if (_cursor.isNull(_cursorIndexOfLastReviewedOn)) {
              _tmpLastReviewedOn = null;
            } else {
              _tmpLastReviewedOn = _cursor.getString(_cursorIndexOfLastReviewedOn);
            }
            final int _tmpTotalAttempts;
            _tmpTotalAttempts = _cursor.getInt(_cursorIndexOfTotalAttempts);
            final int _tmpCorrectAttempts;
            _tmpCorrectAttempts = _cursor.getInt(_cursorIndexOfCorrectAttempts);
            final int _tmpIncorrectAttempts;
            _tmpIncorrectAttempts = _cursor.getInt(_cursorIndexOfIncorrectAttempts);
            final boolean _tmpDifficult;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfDifficult);
            _tmpDifficult = _tmp_1 != 0;
            final boolean _tmpMastered;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfMastered);
            _tmpMastered = _tmp_2 != 0;
            _result = new LearningRecordEntity(_tmpVocabularyId,_tmpLanguageCode,_tmpStatus,_tmpIntroducedOn,_tmpFirstLearnedOn,_tmpLastReviewedOn,_tmpTotalAttempts,_tmpCorrectAttempts,_tmpIncorrectAttempts,_tmpDifficult,_tmpMastered);
          } else {
            _result = null;
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
  public Flow<List<LearningRecordEntity>> observeRecords(final String language) {
    final String _sql = "SELECT * FROM learning_records WHERE languageCode = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, language);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"learning_records"}, new Callable<List<LearningRecordEntity>>() {
      @Override
      @NonNull
      public List<LearningRecordEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfVocabularyId = CursorUtil.getColumnIndexOrThrow(_cursor, "vocabularyId");
          final int _cursorIndexOfLanguageCode = CursorUtil.getColumnIndexOrThrow(_cursor, "languageCode");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfIntroducedOn = CursorUtil.getColumnIndexOrThrow(_cursor, "introducedOn");
          final int _cursorIndexOfFirstLearnedOn = CursorUtil.getColumnIndexOrThrow(_cursor, "firstLearnedOn");
          final int _cursorIndexOfLastReviewedOn = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReviewedOn");
          final int _cursorIndexOfTotalAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "totalAttempts");
          final int _cursorIndexOfCorrectAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "correctAttempts");
          final int _cursorIndexOfIncorrectAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "incorrectAttempts");
          final int _cursorIndexOfDifficult = CursorUtil.getColumnIndexOrThrow(_cursor, "difficult");
          final int _cursorIndexOfMastered = CursorUtil.getColumnIndexOrThrow(_cursor, "mastered");
          final List<LearningRecordEntity> _result = new ArrayList<LearningRecordEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final LearningRecordEntity _item;
            final String _tmpVocabularyId;
            _tmpVocabularyId = _cursor.getString(_cursorIndexOfVocabularyId);
            final String _tmpLanguageCode;
            _tmpLanguageCode = _cursor.getString(_cursorIndexOfLanguageCode);
            final LearningStatus _tmpStatus;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.stringToLearningStatus(_tmp);
            final String _tmpIntroducedOn;
            if (_cursor.isNull(_cursorIndexOfIntroducedOn)) {
              _tmpIntroducedOn = null;
            } else {
              _tmpIntroducedOn = _cursor.getString(_cursorIndexOfIntroducedOn);
            }
            final String _tmpFirstLearnedOn;
            if (_cursor.isNull(_cursorIndexOfFirstLearnedOn)) {
              _tmpFirstLearnedOn = null;
            } else {
              _tmpFirstLearnedOn = _cursor.getString(_cursorIndexOfFirstLearnedOn);
            }
            final String _tmpLastReviewedOn;
            if (_cursor.isNull(_cursorIndexOfLastReviewedOn)) {
              _tmpLastReviewedOn = null;
            } else {
              _tmpLastReviewedOn = _cursor.getString(_cursorIndexOfLastReviewedOn);
            }
            final int _tmpTotalAttempts;
            _tmpTotalAttempts = _cursor.getInt(_cursorIndexOfTotalAttempts);
            final int _tmpCorrectAttempts;
            _tmpCorrectAttempts = _cursor.getInt(_cursorIndexOfCorrectAttempts);
            final int _tmpIncorrectAttempts;
            _tmpIncorrectAttempts = _cursor.getInt(_cursorIndexOfIncorrectAttempts);
            final boolean _tmpDifficult;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfDifficult);
            _tmpDifficult = _tmp_1 != 0;
            final boolean _tmpMastered;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfMastered);
            _tmpMastered = _tmp_2 != 0;
            _item = new LearningRecordEntity(_tmpVocabularyId,_tmpLanguageCode,_tmpStatus,_tmpIntroducedOn,_tmpFirstLearnedOn,_tmpLastReviewedOn,_tmpTotalAttempts,_tmpCorrectAttempts,_tmpIncorrectAttempts,_tmpDifficult,_tmpMastered);
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
  public Object records(final String language,
      final Continuation<? super List<LearningRecordEntity>> $completion) {
    final String _sql = "SELECT * FROM learning_records WHERE languageCode = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, language);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<LearningRecordEntity>>() {
      @Override
      @NonNull
      public List<LearningRecordEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfVocabularyId = CursorUtil.getColumnIndexOrThrow(_cursor, "vocabularyId");
          final int _cursorIndexOfLanguageCode = CursorUtil.getColumnIndexOrThrow(_cursor, "languageCode");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfIntroducedOn = CursorUtil.getColumnIndexOrThrow(_cursor, "introducedOn");
          final int _cursorIndexOfFirstLearnedOn = CursorUtil.getColumnIndexOrThrow(_cursor, "firstLearnedOn");
          final int _cursorIndexOfLastReviewedOn = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReviewedOn");
          final int _cursorIndexOfTotalAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "totalAttempts");
          final int _cursorIndexOfCorrectAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "correctAttempts");
          final int _cursorIndexOfIncorrectAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "incorrectAttempts");
          final int _cursorIndexOfDifficult = CursorUtil.getColumnIndexOrThrow(_cursor, "difficult");
          final int _cursorIndexOfMastered = CursorUtil.getColumnIndexOrThrow(_cursor, "mastered");
          final List<LearningRecordEntity> _result = new ArrayList<LearningRecordEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final LearningRecordEntity _item;
            final String _tmpVocabularyId;
            _tmpVocabularyId = _cursor.getString(_cursorIndexOfVocabularyId);
            final String _tmpLanguageCode;
            _tmpLanguageCode = _cursor.getString(_cursorIndexOfLanguageCode);
            final LearningStatus _tmpStatus;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.stringToLearningStatus(_tmp);
            final String _tmpIntroducedOn;
            if (_cursor.isNull(_cursorIndexOfIntroducedOn)) {
              _tmpIntroducedOn = null;
            } else {
              _tmpIntroducedOn = _cursor.getString(_cursorIndexOfIntroducedOn);
            }
            final String _tmpFirstLearnedOn;
            if (_cursor.isNull(_cursorIndexOfFirstLearnedOn)) {
              _tmpFirstLearnedOn = null;
            } else {
              _tmpFirstLearnedOn = _cursor.getString(_cursorIndexOfFirstLearnedOn);
            }
            final String _tmpLastReviewedOn;
            if (_cursor.isNull(_cursorIndexOfLastReviewedOn)) {
              _tmpLastReviewedOn = null;
            } else {
              _tmpLastReviewedOn = _cursor.getString(_cursorIndexOfLastReviewedOn);
            }
            final int _tmpTotalAttempts;
            _tmpTotalAttempts = _cursor.getInt(_cursorIndexOfTotalAttempts);
            final int _tmpCorrectAttempts;
            _tmpCorrectAttempts = _cursor.getInt(_cursorIndexOfCorrectAttempts);
            final int _tmpIncorrectAttempts;
            _tmpIncorrectAttempts = _cursor.getInt(_cursorIndexOfIncorrectAttempts);
            final boolean _tmpDifficult;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfDifficult);
            _tmpDifficult = _tmp_1 != 0;
            final boolean _tmpMastered;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfMastered);
            _tmpMastered = _tmp_2 != 0;
            _item = new LearningRecordEntity(_tmpVocabularyId,_tmpLanguageCode,_tmpStatus,_tmpIntroducedOn,_tmpFirstLearnedOn,_tmpLastReviewedOn,_tmpTotalAttempts,_tmpCorrectAttempts,_tmpIncorrectAttempts,_tmpDifficult,_tmpMastered);
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
  public Object records(final String language, final VocabBook book,
      final Continuation<? super List<LearningRecordEntity>> $completion) {
    final String _sql = "\n"
            + "        SELECT r.* FROM learning_records r JOIN vocabulary v ON v.id = r.vocabularyId\n"
            + "        WHERE r.languageCode = ? AND v.book = ?\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, language);
    _argIndex = 2;
    final String _tmp = __converters.vocabBookToString(book);
    _statement.bindString(_argIndex, _tmp);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<LearningRecordEntity>>() {
      @Override
      @NonNull
      public List<LearningRecordEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfVocabularyId = CursorUtil.getColumnIndexOrThrow(_cursor, "vocabularyId");
          final int _cursorIndexOfLanguageCode = CursorUtil.getColumnIndexOrThrow(_cursor, "languageCode");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfIntroducedOn = CursorUtil.getColumnIndexOrThrow(_cursor, "introducedOn");
          final int _cursorIndexOfFirstLearnedOn = CursorUtil.getColumnIndexOrThrow(_cursor, "firstLearnedOn");
          final int _cursorIndexOfLastReviewedOn = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReviewedOn");
          final int _cursorIndexOfTotalAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "totalAttempts");
          final int _cursorIndexOfCorrectAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "correctAttempts");
          final int _cursorIndexOfIncorrectAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "incorrectAttempts");
          final int _cursorIndexOfDifficult = CursorUtil.getColumnIndexOrThrow(_cursor, "difficult");
          final int _cursorIndexOfMastered = CursorUtil.getColumnIndexOrThrow(_cursor, "mastered");
          final List<LearningRecordEntity> _result = new ArrayList<LearningRecordEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final LearningRecordEntity _item;
            final String _tmpVocabularyId;
            _tmpVocabularyId = _cursor.getString(_cursorIndexOfVocabularyId);
            final String _tmpLanguageCode;
            _tmpLanguageCode = _cursor.getString(_cursorIndexOfLanguageCode);
            final LearningStatus _tmpStatus;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.stringToLearningStatus(_tmp_1);
            final String _tmpIntroducedOn;
            if (_cursor.isNull(_cursorIndexOfIntroducedOn)) {
              _tmpIntroducedOn = null;
            } else {
              _tmpIntroducedOn = _cursor.getString(_cursorIndexOfIntroducedOn);
            }
            final String _tmpFirstLearnedOn;
            if (_cursor.isNull(_cursorIndexOfFirstLearnedOn)) {
              _tmpFirstLearnedOn = null;
            } else {
              _tmpFirstLearnedOn = _cursor.getString(_cursorIndexOfFirstLearnedOn);
            }
            final String _tmpLastReviewedOn;
            if (_cursor.isNull(_cursorIndexOfLastReviewedOn)) {
              _tmpLastReviewedOn = null;
            } else {
              _tmpLastReviewedOn = _cursor.getString(_cursorIndexOfLastReviewedOn);
            }
            final int _tmpTotalAttempts;
            _tmpTotalAttempts = _cursor.getInt(_cursorIndexOfTotalAttempts);
            final int _tmpCorrectAttempts;
            _tmpCorrectAttempts = _cursor.getInt(_cursorIndexOfCorrectAttempts);
            final int _tmpIncorrectAttempts;
            _tmpIncorrectAttempts = _cursor.getInt(_cursorIndexOfIncorrectAttempts);
            final boolean _tmpDifficult;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfDifficult);
            _tmpDifficult = _tmp_2 != 0;
            final boolean _tmpMastered;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfMastered);
            _tmpMastered = _tmp_3 != 0;
            _item = new LearningRecordEntity(_tmpVocabularyId,_tmpLanguageCode,_tmpStatus,_tmpIntroducedOn,_tmpFirstLearnedOn,_tmpLastReviewedOn,_tmpTotalAttempts,_tmpCorrectAttempts,_tmpIncorrectAttempts,_tmpDifficult,_tmpMastered);
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
  public Object recordsForIds(final List<String> ids,
      final Continuation<? super List<LearningRecordEntity>> $completion) {
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT * FROM learning_records WHERE vocabularyId IN (");
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
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<LearningRecordEntity>>() {
      @Override
      @NonNull
      public List<LearningRecordEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfVocabularyId = CursorUtil.getColumnIndexOrThrow(_cursor, "vocabularyId");
          final int _cursorIndexOfLanguageCode = CursorUtil.getColumnIndexOrThrow(_cursor, "languageCode");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfIntroducedOn = CursorUtil.getColumnIndexOrThrow(_cursor, "introducedOn");
          final int _cursorIndexOfFirstLearnedOn = CursorUtil.getColumnIndexOrThrow(_cursor, "firstLearnedOn");
          final int _cursorIndexOfLastReviewedOn = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReviewedOn");
          final int _cursorIndexOfTotalAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "totalAttempts");
          final int _cursorIndexOfCorrectAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "correctAttempts");
          final int _cursorIndexOfIncorrectAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "incorrectAttempts");
          final int _cursorIndexOfDifficult = CursorUtil.getColumnIndexOrThrow(_cursor, "difficult");
          final int _cursorIndexOfMastered = CursorUtil.getColumnIndexOrThrow(_cursor, "mastered");
          final List<LearningRecordEntity> _result = new ArrayList<LearningRecordEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final LearningRecordEntity _item_1;
            final String _tmpVocabularyId;
            _tmpVocabularyId = _cursor.getString(_cursorIndexOfVocabularyId);
            final String _tmpLanguageCode;
            _tmpLanguageCode = _cursor.getString(_cursorIndexOfLanguageCode);
            final LearningStatus _tmpStatus;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.stringToLearningStatus(_tmp);
            final String _tmpIntroducedOn;
            if (_cursor.isNull(_cursorIndexOfIntroducedOn)) {
              _tmpIntroducedOn = null;
            } else {
              _tmpIntroducedOn = _cursor.getString(_cursorIndexOfIntroducedOn);
            }
            final String _tmpFirstLearnedOn;
            if (_cursor.isNull(_cursorIndexOfFirstLearnedOn)) {
              _tmpFirstLearnedOn = null;
            } else {
              _tmpFirstLearnedOn = _cursor.getString(_cursorIndexOfFirstLearnedOn);
            }
            final String _tmpLastReviewedOn;
            if (_cursor.isNull(_cursorIndexOfLastReviewedOn)) {
              _tmpLastReviewedOn = null;
            } else {
              _tmpLastReviewedOn = _cursor.getString(_cursorIndexOfLastReviewedOn);
            }
            final int _tmpTotalAttempts;
            _tmpTotalAttempts = _cursor.getInt(_cursorIndexOfTotalAttempts);
            final int _tmpCorrectAttempts;
            _tmpCorrectAttempts = _cursor.getInt(_cursorIndexOfCorrectAttempts);
            final int _tmpIncorrectAttempts;
            _tmpIncorrectAttempts = _cursor.getInt(_cursorIndexOfIncorrectAttempts);
            final boolean _tmpDifficult;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfDifficult);
            _tmpDifficult = _tmp_1 != 0;
            final boolean _tmpMastered;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfMastered);
            _tmpMastered = _tmp_2 != 0;
            _item_1 = new LearningRecordEntity(_tmpVocabularyId,_tmpLanguageCode,_tmpStatus,_tmpIntroducedOn,_tmpFirstLearnedOn,_tmpLastReviewedOn,_tmpTotalAttempts,_tmpCorrectAttempts,_tmpIncorrectAttempts,_tmpDifficult,_tmpMastered);
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
  public Flow<Integer> observeLearnedCount(final String language, final VocabBook book) {
    final String _sql = "\n"
            + "        SELECT COUNT(*) FROM learning_records r JOIN vocabulary v ON v.id = r.vocabularyId\n"
            + "        WHERE r.languageCode = ? AND v.book = ? AND r.status = 'LEARNED'\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, language);
    _argIndex = 2;
    final String _tmp = __converters.vocabBookToString(book);
    _statement.bindString(_argIndex, _tmp);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"learning_records",
        "vocabulary"}, new Callable<Integer>() {
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
  public Object learnedCount(final String language, final VocabBook book,
      final Continuation<? super Integer> $completion) {
    final String _sql = "\n"
            + "        SELECT COUNT(*) FROM learning_records r JOIN vocabulary v ON v.id = r.vocabularyId\n"
            + "        WHERE r.languageCode = ? AND v.book = ? AND r.status = 'LEARNED'\n"
            + "        ";
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
  public Object learnedRecords(final String language, final VocabBook book,
      final Continuation<? super List<LearningRecordEntity>> $completion) {
    final String _sql = "\n"
            + "        SELECT r.* FROM learning_records r JOIN vocabulary v ON v.id = r.vocabularyId\n"
            + "        WHERE r.languageCode = ? AND v.book = ? AND r.status = 'LEARNED'\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, language);
    _argIndex = 2;
    final String _tmp = __converters.vocabBookToString(book);
    _statement.bindString(_argIndex, _tmp);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<LearningRecordEntity>>() {
      @Override
      @NonNull
      public List<LearningRecordEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfVocabularyId = CursorUtil.getColumnIndexOrThrow(_cursor, "vocabularyId");
          final int _cursorIndexOfLanguageCode = CursorUtil.getColumnIndexOrThrow(_cursor, "languageCode");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfIntroducedOn = CursorUtil.getColumnIndexOrThrow(_cursor, "introducedOn");
          final int _cursorIndexOfFirstLearnedOn = CursorUtil.getColumnIndexOrThrow(_cursor, "firstLearnedOn");
          final int _cursorIndexOfLastReviewedOn = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReviewedOn");
          final int _cursorIndexOfTotalAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "totalAttempts");
          final int _cursorIndexOfCorrectAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "correctAttempts");
          final int _cursorIndexOfIncorrectAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "incorrectAttempts");
          final int _cursorIndexOfDifficult = CursorUtil.getColumnIndexOrThrow(_cursor, "difficult");
          final int _cursorIndexOfMastered = CursorUtil.getColumnIndexOrThrow(_cursor, "mastered");
          final List<LearningRecordEntity> _result = new ArrayList<LearningRecordEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final LearningRecordEntity _item;
            final String _tmpVocabularyId;
            _tmpVocabularyId = _cursor.getString(_cursorIndexOfVocabularyId);
            final String _tmpLanguageCode;
            _tmpLanguageCode = _cursor.getString(_cursorIndexOfLanguageCode);
            final LearningStatus _tmpStatus;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.stringToLearningStatus(_tmp_1);
            final String _tmpIntroducedOn;
            if (_cursor.isNull(_cursorIndexOfIntroducedOn)) {
              _tmpIntroducedOn = null;
            } else {
              _tmpIntroducedOn = _cursor.getString(_cursorIndexOfIntroducedOn);
            }
            final String _tmpFirstLearnedOn;
            if (_cursor.isNull(_cursorIndexOfFirstLearnedOn)) {
              _tmpFirstLearnedOn = null;
            } else {
              _tmpFirstLearnedOn = _cursor.getString(_cursorIndexOfFirstLearnedOn);
            }
            final String _tmpLastReviewedOn;
            if (_cursor.isNull(_cursorIndexOfLastReviewedOn)) {
              _tmpLastReviewedOn = null;
            } else {
              _tmpLastReviewedOn = _cursor.getString(_cursorIndexOfLastReviewedOn);
            }
            final int _tmpTotalAttempts;
            _tmpTotalAttempts = _cursor.getInt(_cursorIndexOfTotalAttempts);
            final int _tmpCorrectAttempts;
            _tmpCorrectAttempts = _cursor.getInt(_cursorIndexOfCorrectAttempts);
            final int _tmpIncorrectAttempts;
            _tmpIncorrectAttempts = _cursor.getInt(_cursorIndexOfIncorrectAttempts);
            final boolean _tmpDifficult;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfDifficult);
            _tmpDifficult = _tmp_2 != 0;
            final boolean _tmpMastered;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfMastered);
            _tmpMastered = _tmp_3 != 0;
            _item = new LearningRecordEntity(_tmpVocabularyId,_tmpLanguageCode,_tmpStatus,_tmpIntroducedOn,_tmpFirstLearnedOn,_tmpLastReviewedOn,_tmpTotalAttempts,_tmpCorrectAttempts,_tmpIncorrectAttempts,_tmpDifficult,_tmpMastered);
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
  public Object inProgressRecords(final String language, final VocabBook book,
      final Continuation<? super List<LearningRecordEntity>> $completion) {
    final String _sql = "\n"
            + "        SELECT r.* FROM learning_records r JOIN vocabulary v ON v.id = r.vocabularyId\n"
            + "        WHERE r.languageCode = ? AND v.book = ? AND r.status = 'IN_PROGRESS'\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, language);
    _argIndex = 2;
    final String _tmp = __converters.vocabBookToString(book);
    _statement.bindString(_argIndex, _tmp);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<LearningRecordEntity>>() {
      @Override
      @NonNull
      public List<LearningRecordEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfVocabularyId = CursorUtil.getColumnIndexOrThrow(_cursor, "vocabularyId");
          final int _cursorIndexOfLanguageCode = CursorUtil.getColumnIndexOrThrow(_cursor, "languageCode");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfIntroducedOn = CursorUtil.getColumnIndexOrThrow(_cursor, "introducedOn");
          final int _cursorIndexOfFirstLearnedOn = CursorUtil.getColumnIndexOrThrow(_cursor, "firstLearnedOn");
          final int _cursorIndexOfLastReviewedOn = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReviewedOn");
          final int _cursorIndexOfTotalAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "totalAttempts");
          final int _cursorIndexOfCorrectAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "correctAttempts");
          final int _cursorIndexOfIncorrectAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "incorrectAttempts");
          final int _cursorIndexOfDifficult = CursorUtil.getColumnIndexOrThrow(_cursor, "difficult");
          final int _cursorIndexOfMastered = CursorUtil.getColumnIndexOrThrow(_cursor, "mastered");
          final List<LearningRecordEntity> _result = new ArrayList<LearningRecordEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final LearningRecordEntity _item;
            final String _tmpVocabularyId;
            _tmpVocabularyId = _cursor.getString(_cursorIndexOfVocabularyId);
            final String _tmpLanguageCode;
            _tmpLanguageCode = _cursor.getString(_cursorIndexOfLanguageCode);
            final LearningStatus _tmpStatus;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.stringToLearningStatus(_tmp_1);
            final String _tmpIntroducedOn;
            if (_cursor.isNull(_cursorIndexOfIntroducedOn)) {
              _tmpIntroducedOn = null;
            } else {
              _tmpIntroducedOn = _cursor.getString(_cursorIndexOfIntroducedOn);
            }
            final String _tmpFirstLearnedOn;
            if (_cursor.isNull(_cursorIndexOfFirstLearnedOn)) {
              _tmpFirstLearnedOn = null;
            } else {
              _tmpFirstLearnedOn = _cursor.getString(_cursorIndexOfFirstLearnedOn);
            }
            final String _tmpLastReviewedOn;
            if (_cursor.isNull(_cursorIndexOfLastReviewedOn)) {
              _tmpLastReviewedOn = null;
            } else {
              _tmpLastReviewedOn = _cursor.getString(_cursorIndexOfLastReviewedOn);
            }
            final int _tmpTotalAttempts;
            _tmpTotalAttempts = _cursor.getInt(_cursorIndexOfTotalAttempts);
            final int _tmpCorrectAttempts;
            _tmpCorrectAttempts = _cursor.getInt(_cursorIndexOfCorrectAttempts);
            final int _tmpIncorrectAttempts;
            _tmpIncorrectAttempts = _cursor.getInt(_cursorIndexOfIncorrectAttempts);
            final boolean _tmpDifficult;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfDifficult);
            _tmpDifficult = _tmp_2 != 0;
            final boolean _tmpMastered;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfMastered);
            _tmpMastered = _tmp_3 != 0;
            _item = new LearningRecordEntity(_tmpVocabularyId,_tmpLanguageCode,_tmpStatus,_tmpIntroducedOn,_tmpFirstLearnedOn,_tmpLastReviewedOn,_tmpTotalAttempts,_tmpCorrectAttempts,_tmpIncorrectAttempts,_tmpDifficult,_tmpMastered);
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
  public Object introducedIds(final String language, final VocabBook book,
      final Continuation<? super List<String>> $completion) {
    final String _sql = "\n"
            + "        SELECT r.vocabularyId FROM learning_records r JOIN vocabulary v ON v.id = r.vocabularyId\n"
            + "        WHERE r.languageCode = ? AND v.book = ? AND r.status IN ('IN_PROGRESS', 'LEARNED')\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, language);
    _argIndex = 2;
    final String _tmp = __converters.vocabBookToString(book);
    _statement.bindString(_argIndex, _tmp);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<String>>() {
      @Override
      @NonNull
      public List<String> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final List<String> _result = new ArrayList<String>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final String _item;
            _item = _cursor.getString(0);
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
  public Object allRecords(final Continuation<? super List<LearningRecordEntity>> $completion) {
    final String _sql = "SELECT * FROM learning_records";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<LearningRecordEntity>>() {
      @Override
      @NonNull
      public List<LearningRecordEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfVocabularyId = CursorUtil.getColumnIndexOrThrow(_cursor, "vocabularyId");
          final int _cursorIndexOfLanguageCode = CursorUtil.getColumnIndexOrThrow(_cursor, "languageCode");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfIntroducedOn = CursorUtil.getColumnIndexOrThrow(_cursor, "introducedOn");
          final int _cursorIndexOfFirstLearnedOn = CursorUtil.getColumnIndexOrThrow(_cursor, "firstLearnedOn");
          final int _cursorIndexOfLastReviewedOn = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReviewedOn");
          final int _cursorIndexOfTotalAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "totalAttempts");
          final int _cursorIndexOfCorrectAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "correctAttempts");
          final int _cursorIndexOfIncorrectAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "incorrectAttempts");
          final int _cursorIndexOfDifficult = CursorUtil.getColumnIndexOrThrow(_cursor, "difficult");
          final int _cursorIndexOfMastered = CursorUtil.getColumnIndexOrThrow(_cursor, "mastered");
          final List<LearningRecordEntity> _result = new ArrayList<LearningRecordEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final LearningRecordEntity _item;
            final String _tmpVocabularyId;
            _tmpVocabularyId = _cursor.getString(_cursorIndexOfVocabularyId);
            final String _tmpLanguageCode;
            _tmpLanguageCode = _cursor.getString(_cursorIndexOfLanguageCode);
            final LearningStatus _tmpStatus;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.stringToLearningStatus(_tmp);
            final String _tmpIntroducedOn;
            if (_cursor.isNull(_cursorIndexOfIntroducedOn)) {
              _tmpIntroducedOn = null;
            } else {
              _tmpIntroducedOn = _cursor.getString(_cursorIndexOfIntroducedOn);
            }
            final String _tmpFirstLearnedOn;
            if (_cursor.isNull(_cursorIndexOfFirstLearnedOn)) {
              _tmpFirstLearnedOn = null;
            } else {
              _tmpFirstLearnedOn = _cursor.getString(_cursorIndexOfFirstLearnedOn);
            }
            final String _tmpLastReviewedOn;
            if (_cursor.isNull(_cursorIndexOfLastReviewedOn)) {
              _tmpLastReviewedOn = null;
            } else {
              _tmpLastReviewedOn = _cursor.getString(_cursorIndexOfLastReviewedOn);
            }
            final int _tmpTotalAttempts;
            _tmpTotalAttempts = _cursor.getInt(_cursorIndexOfTotalAttempts);
            final int _tmpCorrectAttempts;
            _tmpCorrectAttempts = _cursor.getInt(_cursorIndexOfCorrectAttempts);
            final int _tmpIncorrectAttempts;
            _tmpIncorrectAttempts = _cursor.getInt(_cursorIndexOfIncorrectAttempts);
            final boolean _tmpDifficult;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfDifficult);
            _tmpDifficult = _tmp_1 != 0;
            final boolean _tmpMastered;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfMastered);
            _tmpMastered = _tmp_2 != 0;
            _item = new LearningRecordEntity(_tmpVocabularyId,_tmpLanguageCode,_tmpStatus,_tmpIntroducedOn,_tmpFirstLearnedOn,_tmpLastReviewedOn,_tmpTotalAttempts,_tmpCorrectAttempts,_tmpIncorrectAttempts,_tmpDifficult,_tmpMastered);
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
  public Object dailyTask(final String language, final String date, final VocabBook book,
      final Continuation<? super DailyTaskEntity> $completion) {
    final String _sql = "SELECT * FROM daily_tasks WHERE languageCode = ? AND book = ? AND learningDate = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindString(_argIndex, language);
    _argIndex = 2;
    final String _tmp = __converters.vocabBookToString(book);
    _statement.bindString(_argIndex, _tmp);
    _argIndex = 3;
    _statement.bindString(_argIndex, date);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<DailyTaskEntity>() {
      @Override
      @Nullable
      public DailyTaskEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLanguageCode = CursorUtil.getColumnIndexOrThrow(_cursor, "languageCode");
          final int _cursorIndexOfLearningDate = CursorUtil.getColumnIndexOrThrow(_cursor, "learningDate");
          final int _cursorIndexOfBook = CursorUtil.getColumnIndexOrThrow(_cursor, "book");
          final int _cursorIndexOfPlannedNewWords = CursorUtil.getColumnIndexOrThrow(_cursor, "plannedNewWords");
          final int _cursorIndexOfCompletedNewWords = CursorUtil.getColumnIndexOrThrow(_cursor, "completedNewWords");
          final int _cursorIndexOfPreliminaryStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "preliminaryStatus");
          final int _cursorIndexOfNewLearningStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "newLearningStatus");
          final int _cursorIndexOfPostTestStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "postTestStatus");
          final int _cursorIndexOfOverallStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "overallStatus");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final DailyTaskEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpLanguageCode;
            _tmpLanguageCode = _cursor.getString(_cursorIndexOfLanguageCode);
            final String _tmpLearningDate;
            _tmpLearningDate = _cursor.getString(_cursorIndexOfLearningDate);
            final VocabBook _tmpBook;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfBook);
            _tmpBook = __converters.stringToVocabBook(_tmp_1);
            final int _tmpPlannedNewWords;
            _tmpPlannedNewWords = _cursor.getInt(_cursorIndexOfPlannedNewWords);
            final int _tmpCompletedNewWords;
            _tmpCompletedNewWords = _cursor.getInt(_cursorIndexOfCompletedNewWords);
            final PreliminaryStatus _tmpPreliminaryStatus;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfPreliminaryStatus);
            _tmpPreliminaryStatus = __converters.stringToPreliminary(_tmp_2);
            final NewLearningStatus _tmpNewLearningStatus;
            final String _tmp_3;
            _tmp_3 = _cursor.getString(_cursorIndexOfNewLearningStatus);
            _tmpNewLearningStatus = __converters.stringToNewLearning(_tmp_3);
            final PostTestStatus _tmpPostTestStatus;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfPostTestStatus);
            _tmpPostTestStatus = __converters.stringToPostTest(_tmp_4);
            final OverallStatus _tmpOverallStatus;
            final String _tmp_5;
            _tmp_5 = _cursor.getString(_cursorIndexOfOverallStatus);
            _tmpOverallStatus = __converters.stringToOverall(_tmp_5);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new DailyTaskEntity(_tmpId,_tmpLanguageCode,_tmpLearningDate,_tmpBook,_tmpPlannedNewWords,_tmpCompletedNewWords,_tmpPreliminaryStatus,_tmpNewLearningStatus,_tmpPostTestStatus,_tmpOverallStatus,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object dailyTaskById(final String id,
      final Continuation<? super DailyTaskEntity> $completion) {
    final String _sql = "SELECT * FROM daily_tasks WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<DailyTaskEntity>() {
      @Override
      @Nullable
      public DailyTaskEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLanguageCode = CursorUtil.getColumnIndexOrThrow(_cursor, "languageCode");
          final int _cursorIndexOfLearningDate = CursorUtil.getColumnIndexOrThrow(_cursor, "learningDate");
          final int _cursorIndexOfBook = CursorUtil.getColumnIndexOrThrow(_cursor, "book");
          final int _cursorIndexOfPlannedNewWords = CursorUtil.getColumnIndexOrThrow(_cursor, "plannedNewWords");
          final int _cursorIndexOfCompletedNewWords = CursorUtil.getColumnIndexOrThrow(_cursor, "completedNewWords");
          final int _cursorIndexOfPreliminaryStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "preliminaryStatus");
          final int _cursorIndexOfNewLearningStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "newLearningStatus");
          final int _cursorIndexOfPostTestStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "postTestStatus");
          final int _cursorIndexOfOverallStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "overallStatus");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final DailyTaskEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpLanguageCode;
            _tmpLanguageCode = _cursor.getString(_cursorIndexOfLanguageCode);
            final String _tmpLearningDate;
            _tmpLearningDate = _cursor.getString(_cursorIndexOfLearningDate);
            final VocabBook _tmpBook;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfBook);
            _tmpBook = __converters.stringToVocabBook(_tmp);
            final int _tmpPlannedNewWords;
            _tmpPlannedNewWords = _cursor.getInt(_cursorIndexOfPlannedNewWords);
            final int _tmpCompletedNewWords;
            _tmpCompletedNewWords = _cursor.getInt(_cursorIndexOfCompletedNewWords);
            final PreliminaryStatus _tmpPreliminaryStatus;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfPreliminaryStatus);
            _tmpPreliminaryStatus = __converters.stringToPreliminary(_tmp_1);
            final NewLearningStatus _tmpNewLearningStatus;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfNewLearningStatus);
            _tmpNewLearningStatus = __converters.stringToNewLearning(_tmp_2);
            final PostTestStatus _tmpPostTestStatus;
            final String _tmp_3;
            _tmp_3 = _cursor.getString(_cursorIndexOfPostTestStatus);
            _tmpPostTestStatus = __converters.stringToPostTest(_tmp_3);
            final OverallStatus _tmpOverallStatus;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfOverallStatus);
            _tmpOverallStatus = __converters.stringToOverall(_tmp_4);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new DailyTaskEntity(_tmpId,_tmpLanguageCode,_tmpLearningDate,_tmpBook,_tmpPlannedNewWords,_tmpCompletedNewWords,_tmpPreliminaryStatus,_tmpNewLearningStatus,_tmpPostTestStatus,_tmpOverallStatus,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<DailyTaskEntity> observeDailyTask(final String language, final String date,
      final VocabBook book) {
    final String _sql = "SELECT * FROM daily_tasks WHERE languageCode = ? AND book = ? AND learningDate = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindString(_argIndex, language);
    _argIndex = 2;
    final String _tmp = __converters.vocabBookToString(book);
    _statement.bindString(_argIndex, _tmp);
    _argIndex = 3;
    _statement.bindString(_argIndex, date);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"daily_tasks"}, new Callable<DailyTaskEntity>() {
      @Override
      @Nullable
      public DailyTaskEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLanguageCode = CursorUtil.getColumnIndexOrThrow(_cursor, "languageCode");
          final int _cursorIndexOfLearningDate = CursorUtil.getColumnIndexOrThrow(_cursor, "learningDate");
          final int _cursorIndexOfBook = CursorUtil.getColumnIndexOrThrow(_cursor, "book");
          final int _cursorIndexOfPlannedNewWords = CursorUtil.getColumnIndexOrThrow(_cursor, "plannedNewWords");
          final int _cursorIndexOfCompletedNewWords = CursorUtil.getColumnIndexOrThrow(_cursor, "completedNewWords");
          final int _cursorIndexOfPreliminaryStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "preliminaryStatus");
          final int _cursorIndexOfNewLearningStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "newLearningStatus");
          final int _cursorIndexOfPostTestStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "postTestStatus");
          final int _cursorIndexOfOverallStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "overallStatus");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final DailyTaskEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpLanguageCode;
            _tmpLanguageCode = _cursor.getString(_cursorIndexOfLanguageCode);
            final String _tmpLearningDate;
            _tmpLearningDate = _cursor.getString(_cursorIndexOfLearningDate);
            final VocabBook _tmpBook;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfBook);
            _tmpBook = __converters.stringToVocabBook(_tmp_1);
            final int _tmpPlannedNewWords;
            _tmpPlannedNewWords = _cursor.getInt(_cursorIndexOfPlannedNewWords);
            final int _tmpCompletedNewWords;
            _tmpCompletedNewWords = _cursor.getInt(_cursorIndexOfCompletedNewWords);
            final PreliminaryStatus _tmpPreliminaryStatus;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfPreliminaryStatus);
            _tmpPreliminaryStatus = __converters.stringToPreliminary(_tmp_2);
            final NewLearningStatus _tmpNewLearningStatus;
            final String _tmp_3;
            _tmp_3 = _cursor.getString(_cursorIndexOfNewLearningStatus);
            _tmpNewLearningStatus = __converters.stringToNewLearning(_tmp_3);
            final PostTestStatus _tmpPostTestStatus;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfPostTestStatus);
            _tmpPostTestStatus = __converters.stringToPostTest(_tmp_4);
            final OverallStatus _tmpOverallStatus;
            final String _tmp_5;
            _tmp_5 = _cursor.getString(_cursorIndexOfOverallStatus);
            _tmpOverallStatus = __converters.stringToOverall(_tmp_5);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new DailyTaskEntity(_tmpId,_tmpLanguageCode,_tmpLearningDate,_tmpBook,_tmpPlannedNewWords,_tmpCompletedNewWords,_tmpPreliminaryStatus,_tmpNewLearningStatus,_tmpPostTestStatus,_tmpOverallStatus,_tmpCreatedAt,_tmpUpdatedAt);
          } else {
            _result = null;
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
  public Flow<List<DailyTaskEntity>> observeDailyTasks(final String language,
      final VocabBook book) {
    final String _sql = "SELECT * FROM daily_tasks WHERE languageCode = ? AND book = ? ORDER BY learningDate DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, language);
    _argIndex = 2;
    final String _tmp = __converters.vocabBookToString(book);
    _statement.bindString(_argIndex, _tmp);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"daily_tasks"}, new Callable<List<DailyTaskEntity>>() {
      @Override
      @NonNull
      public List<DailyTaskEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLanguageCode = CursorUtil.getColumnIndexOrThrow(_cursor, "languageCode");
          final int _cursorIndexOfLearningDate = CursorUtil.getColumnIndexOrThrow(_cursor, "learningDate");
          final int _cursorIndexOfBook = CursorUtil.getColumnIndexOrThrow(_cursor, "book");
          final int _cursorIndexOfPlannedNewWords = CursorUtil.getColumnIndexOrThrow(_cursor, "plannedNewWords");
          final int _cursorIndexOfCompletedNewWords = CursorUtil.getColumnIndexOrThrow(_cursor, "completedNewWords");
          final int _cursorIndexOfPreliminaryStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "preliminaryStatus");
          final int _cursorIndexOfNewLearningStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "newLearningStatus");
          final int _cursorIndexOfPostTestStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "postTestStatus");
          final int _cursorIndexOfOverallStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "overallStatus");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<DailyTaskEntity> _result = new ArrayList<DailyTaskEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DailyTaskEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpLanguageCode;
            _tmpLanguageCode = _cursor.getString(_cursorIndexOfLanguageCode);
            final String _tmpLearningDate;
            _tmpLearningDate = _cursor.getString(_cursorIndexOfLearningDate);
            final VocabBook _tmpBook;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfBook);
            _tmpBook = __converters.stringToVocabBook(_tmp_1);
            final int _tmpPlannedNewWords;
            _tmpPlannedNewWords = _cursor.getInt(_cursorIndexOfPlannedNewWords);
            final int _tmpCompletedNewWords;
            _tmpCompletedNewWords = _cursor.getInt(_cursorIndexOfCompletedNewWords);
            final PreliminaryStatus _tmpPreliminaryStatus;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfPreliminaryStatus);
            _tmpPreliminaryStatus = __converters.stringToPreliminary(_tmp_2);
            final NewLearningStatus _tmpNewLearningStatus;
            final String _tmp_3;
            _tmp_3 = _cursor.getString(_cursorIndexOfNewLearningStatus);
            _tmpNewLearningStatus = __converters.stringToNewLearning(_tmp_3);
            final PostTestStatus _tmpPostTestStatus;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfPostTestStatus);
            _tmpPostTestStatus = __converters.stringToPostTest(_tmp_4);
            final OverallStatus _tmpOverallStatus;
            final String _tmp_5;
            _tmp_5 = _cursor.getString(_cursorIndexOfOverallStatus);
            _tmpOverallStatus = __converters.stringToOverall(_tmp_5);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new DailyTaskEntity(_tmpId,_tmpLanguageCode,_tmpLearningDate,_tmpBook,_tmpPlannedNewWords,_tmpCompletedNewWords,_tmpPreliminaryStatus,_tmpNewLearningStatus,_tmpPostTestStatus,_tmpOverallStatus,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object recentDailyTasks(final String language, final VocabBook book, final int limit,
      final Continuation<? super List<DailyTaskEntity>> $completion) {
    final String _sql = "SELECT * FROM daily_tasks WHERE languageCode = ? AND book = ? ORDER BY learningDate DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindString(_argIndex, language);
    _argIndex = 2;
    final String _tmp = __converters.vocabBookToString(book);
    _statement.bindString(_argIndex, _tmp);
    _argIndex = 3;
    _statement.bindLong(_argIndex, limit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<DailyTaskEntity>>() {
      @Override
      @NonNull
      public List<DailyTaskEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLanguageCode = CursorUtil.getColumnIndexOrThrow(_cursor, "languageCode");
          final int _cursorIndexOfLearningDate = CursorUtil.getColumnIndexOrThrow(_cursor, "learningDate");
          final int _cursorIndexOfBook = CursorUtil.getColumnIndexOrThrow(_cursor, "book");
          final int _cursorIndexOfPlannedNewWords = CursorUtil.getColumnIndexOrThrow(_cursor, "plannedNewWords");
          final int _cursorIndexOfCompletedNewWords = CursorUtil.getColumnIndexOrThrow(_cursor, "completedNewWords");
          final int _cursorIndexOfPreliminaryStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "preliminaryStatus");
          final int _cursorIndexOfNewLearningStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "newLearningStatus");
          final int _cursorIndexOfPostTestStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "postTestStatus");
          final int _cursorIndexOfOverallStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "overallStatus");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<DailyTaskEntity> _result = new ArrayList<DailyTaskEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DailyTaskEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpLanguageCode;
            _tmpLanguageCode = _cursor.getString(_cursorIndexOfLanguageCode);
            final String _tmpLearningDate;
            _tmpLearningDate = _cursor.getString(_cursorIndexOfLearningDate);
            final VocabBook _tmpBook;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfBook);
            _tmpBook = __converters.stringToVocabBook(_tmp_1);
            final int _tmpPlannedNewWords;
            _tmpPlannedNewWords = _cursor.getInt(_cursorIndexOfPlannedNewWords);
            final int _tmpCompletedNewWords;
            _tmpCompletedNewWords = _cursor.getInt(_cursorIndexOfCompletedNewWords);
            final PreliminaryStatus _tmpPreliminaryStatus;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfPreliminaryStatus);
            _tmpPreliminaryStatus = __converters.stringToPreliminary(_tmp_2);
            final NewLearningStatus _tmpNewLearningStatus;
            final String _tmp_3;
            _tmp_3 = _cursor.getString(_cursorIndexOfNewLearningStatus);
            _tmpNewLearningStatus = __converters.stringToNewLearning(_tmp_3);
            final PostTestStatus _tmpPostTestStatus;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfPostTestStatus);
            _tmpPostTestStatus = __converters.stringToPostTest(_tmp_4);
            final OverallStatus _tmpOverallStatus;
            final String _tmp_5;
            _tmp_5 = _cursor.getString(_cursorIndexOfOverallStatus);
            _tmpOverallStatus = __converters.stringToOverall(_tmp_5);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new DailyTaskEntity(_tmpId,_tmpLanguageCode,_tmpLearningDate,_tmpBook,_tmpPlannedNewWords,_tmpCompletedNewWords,_tmpPreliminaryStatus,_tmpNewLearningStatus,_tmpPostTestStatus,_tmpOverallStatus,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object completedDailyTasks(final String language, final VocabBook book,
      final Continuation<? super List<DailyTaskEntity>> $completion) {
    final String _sql = "SELECT * FROM daily_tasks WHERE languageCode = ? AND book = ? AND overallStatus = 'COMPLETED'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, language);
    _argIndex = 2;
    final String _tmp = __converters.vocabBookToString(book);
    _statement.bindString(_argIndex, _tmp);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<DailyTaskEntity>>() {
      @Override
      @NonNull
      public List<DailyTaskEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLanguageCode = CursorUtil.getColumnIndexOrThrow(_cursor, "languageCode");
          final int _cursorIndexOfLearningDate = CursorUtil.getColumnIndexOrThrow(_cursor, "learningDate");
          final int _cursorIndexOfBook = CursorUtil.getColumnIndexOrThrow(_cursor, "book");
          final int _cursorIndexOfPlannedNewWords = CursorUtil.getColumnIndexOrThrow(_cursor, "plannedNewWords");
          final int _cursorIndexOfCompletedNewWords = CursorUtil.getColumnIndexOrThrow(_cursor, "completedNewWords");
          final int _cursorIndexOfPreliminaryStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "preliminaryStatus");
          final int _cursorIndexOfNewLearningStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "newLearningStatus");
          final int _cursorIndexOfPostTestStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "postTestStatus");
          final int _cursorIndexOfOverallStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "overallStatus");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<DailyTaskEntity> _result = new ArrayList<DailyTaskEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DailyTaskEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpLanguageCode;
            _tmpLanguageCode = _cursor.getString(_cursorIndexOfLanguageCode);
            final String _tmpLearningDate;
            _tmpLearningDate = _cursor.getString(_cursorIndexOfLearningDate);
            final VocabBook _tmpBook;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfBook);
            _tmpBook = __converters.stringToVocabBook(_tmp_1);
            final int _tmpPlannedNewWords;
            _tmpPlannedNewWords = _cursor.getInt(_cursorIndexOfPlannedNewWords);
            final int _tmpCompletedNewWords;
            _tmpCompletedNewWords = _cursor.getInt(_cursorIndexOfCompletedNewWords);
            final PreliminaryStatus _tmpPreliminaryStatus;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfPreliminaryStatus);
            _tmpPreliminaryStatus = __converters.stringToPreliminary(_tmp_2);
            final NewLearningStatus _tmpNewLearningStatus;
            final String _tmp_3;
            _tmp_3 = _cursor.getString(_cursorIndexOfNewLearningStatus);
            _tmpNewLearningStatus = __converters.stringToNewLearning(_tmp_3);
            final PostTestStatus _tmpPostTestStatus;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfPostTestStatus);
            _tmpPostTestStatus = __converters.stringToPostTest(_tmp_4);
            final OverallStatus _tmpOverallStatus;
            final String _tmp_5;
            _tmp_5 = _cursor.getString(_cursorIndexOfOverallStatus);
            _tmpOverallStatus = __converters.stringToOverall(_tmp_5);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new DailyTaskEntity(_tmpId,_tmpLanguageCode,_tmpLearningDate,_tmpBook,_tmpPlannedNewWords,_tmpCompletedNewWords,_tmpPreliminaryStatus,_tmpNewLearningStatus,_tmpPostTestStatus,_tmpOverallStatus,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object allDailyTasks(final Continuation<? super List<DailyTaskEntity>> $completion) {
    final String _sql = "SELECT * FROM daily_tasks";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<DailyTaskEntity>>() {
      @Override
      @NonNull
      public List<DailyTaskEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLanguageCode = CursorUtil.getColumnIndexOrThrow(_cursor, "languageCode");
          final int _cursorIndexOfLearningDate = CursorUtil.getColumnIndexOrThrow(_cursor, "learningDate");
          final int _cursorIndexOfBook = CursorUtil.getColumnIndexOrThrow(_cursor, "book");
          final int _cursorIndexOfPlannedNewWords = CursorUtil.getColumnIndexOrThrow(_cursor, "plannedNewWords");
          final int _cursorIndexOfCompletedNewWords = CursorUtil.getColumnIndexOrThrow(_cursor, "completedNewWords");
          final int _cursorIndexOfPreliminaryStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "preliminaryStatus");
          final int _cursorIndexOfNewLearningStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "newLearningStatus");
          final int _cursorIndexOfPostTestStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "postTestStatus");
          final int _cursorIndexOfOverallStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "overallStatus");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<DailyTaskEntity> _result = new ArrayList<DailyTaskEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DailyTaskEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpLanguageCode;
            _tmpLanguageCode = _cursor.getString(_cursorIndexOfLanguageCode);
            final String _tmpLearningDate;
            _tmpLearningDate = _cursor.getString(_cursorIndexOfLearningDate);
            final VocabBook _tmpBook;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfBook);
            _tmpBook = __converters.stringToVocabBook(_tmp);
            final int _tmpPlannedNewWords;
            _tmpPlannedNewWords = _cursor.getInt(_cursorIndexOfPlannedNewWords);
            final int _tmpCompletedNewWords;
            _tmpCompletedNewWords = _cursor.getInt(_cursorIndexOfCompletedNewWords);
            final PreliminaryStatus _tmpPreliminaryStatus;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfPreliminaryStatus);
            _tmpPreliminaryStatus = __converters.stringToPreliminary(_tmp_1);
            final NewLearningStatus _tmpNewLearningStatus;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfNewLearningStatus);
            _tmpNewLearningStatus = __converters.stringToNewLearning(_tmp_2);
            final PostTestStatus _tmpPostTestStatus;
            final String _tmp_3;
            _tmp_3 = _cursor.getString(_cursorIndexOfPostTestStatus);
            _tmpPostTestStatus = __converters.stringToPostTest(_tmp_3);
            final OverallStatus _tmpOverallStatus;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfOverallStatus);
            _tmpOverallStatus = __converters.stringToOverall(_tmp_4);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new DailyTaskEntity(_tmpId,_tmpLanguageCode,_tmpLearningDate,_tmpBook,_tmpPlannedNewWords,_tmpCompletedNewWords,_tmpPreliminaryStatus,_tmpNewLearningStatus,_tmpPostTestStatus,_tmpOverallStatus,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object learningSession(final String id,
      final Continuation<? super LearningSessionEntity> $completion) {
    final String _sql = "SELECT * FROM learning_sessions WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<LearningSessionEntity>() {
      @Override
      @Nullable
      public LearningSessionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLanguageCode = CursorUtil.getColumnIndexOrThrow(_cursor, "languageCode");
          final int _cursorIndexOfLearningDate = CursorUtil.getColumnIndexOrThrow(_cursor, "learningDate");
          final int _cursorIndexOfBook = CursorUtil.getColumnIndexOrThrow(_cursor, "book");
          final int _cursorIndexOfVocabularyIds = CursorUtil.getColumnIndexOrThrow(_cursor, "vocabularyIds");
          final int _cursorIndexOfTargetCount = CursorUtil.getColumnIndexOrThrow(_cursor, "targetCount");
          final int _cursorIndexOfCardCursor = CursorUtil.getColumnIndexOrThrow(_cursor, "cardCursor");
          final int _cursorIndexOfQuizRound = CursorUtil.getColumnIndexOrThrow(_cursor, "quizRound");
          final int _cursorIndexOfPassedIds = CursorUtil.getColumnIndexOrThrow(_cursor, "passedIds");
          final int _cursorIndexOfPendingQuizIds = CursorUtil.getColumnIndexOrThrow(_cursor, "pendingQuizIds");
          final int _cursorIndexOfPhase = CursorUtil.getColumnIndexOrThrow(_cursor, "phase");
          final int _cursorIndexOfActiveQuizSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "activeQuizSessionId");
          final int _cursorIndexOfCompletedOn = CursorUtil.getColumnIndexOrThrow(_cursor, "completedOn");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final LearningSessionEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpLanguageCode;
            _tmpLanguageCode = _cursor.getString(_cursorIndexOfLanguageCode);
            final String _tmpLearningDate;
            _tmpLearningDate = _cursor.getString(_cursorIndexOfLearningDate);
            final VocabBook _tmpBook;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfBook);
            _tmpBook = __converters.stringToVocabBook(_tmp);
            final List<String> _tmpVocabularyIds;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfVocabularyIds);
            _tmpVocabularyIds = __converters.jsonToStringList(_tmp_1);
            final int _tmpTargetCount;
            _tmpTargetCount = _cursor.getInt(_cursorIndexOfTargetCount);
            final int _tmpCardCursor;
            _tmpCardCursor = _cursor.getInt(_cursorIndexOfCardCursor);
            final int _tmpQuizRound;
            _tmpQuizRound = _cursor.getInt(_cursorIndexOfQuizRound);
            final List<String> _tmpPassedIds;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfPassedIds);
            _tmpPassedIds = __converters.jsonToStringList(_tmp_2);
            final List<String> _tmpPendingQuizIds;
            final String _tmp_3;
            _tmp_3 = _cursor.getString(_cursorIndexOfPendingQuizIds);
            _tmpPendingQuizIds = __converters.jsonToStringList(_tmp_3);
            final LearningPhase _tmpPhase;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfPhase);
            _tmpPhase = __converters.stringToLearningPhase(_tmp_4);
            final String _tmpActiveQuizSessionId;
            if (_cursor.isNull(_cursorIndexOfActiveQuizSessionId)) {
              _tmpActiveQuizSessionId = null;
            } else {
              _tmpActiveQuizSessionId = _cursor.getString(_cursorIndexOfActiveQuizSessionId);
            }
            final String _tmpCompletedOn;
            if (_cursor.isNull(_cursorIndexOfCompletedOn)) {
              _tmpCompletedOn = null;
            } else {
              _tmpCompletedOn = _cursor.getString(_cursorIndexOfCompletedOn);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new LearningSessionEntity(_tmpId,_tmpLanguageCode,_tmpLearningDate,_tmpBook,_tmpVocabularyIds,_tmpTargetCount,_tmpCardCursor,_tmpQuizRound,_tmpPassedIds,_tmpPendingQuizIds,_tmpPhase,_tmpActiveQuizSessionId,_tmpCompletedOn,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<LearningSessionEntity> observeLearningSession(final String id) {
    final String _sql = "SELECT * FROM learning_sessions WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"learning_sessions"}, new Callable<LearningSessionEntity>() {
      @Override
      @Nullable
      public LearningSessionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLanguageCode = CursorUtil.getColumnIndexOrThrow(_cursor, "languageCode");
          final int _cursorIndexOfLearningDate = CursorUtil.getColumnIndexOrThrow(_cursor, "learningDate");
          final int _cursorIndexOfBook = CursorUtil.getColumnIndexOrThrow(_cursor, "book");
          final int _cursorIndexOfVocabularyIds = CursorUtil.getColumnIndexOrThrow(_cursor, "vocabularyIds");
          final int _cursorIndexOfTargetCount = CursorUtil.getColumnIndexOrThrow(_cursor, "targetCount");
          final int _cursorIndexOfCardCursor = CursorUtil.getColumnIndexOrThrow(_cursor, "cardCursor");
          final int _cursorIndexOfQuizRound = CursorUtil.getColumnIndexOrThrow(_cursor, "quizRound");
          final int _cursorIndexOfPassedIds = CursorUtil.getColumnIndexOrThrow(_cursor, "passedIds");
          final int _cursorIndexOfPendingQuizIds = CursorUtil.getColumnIndexOrThrow(_cursor, "pendingQuizIds");
          final int _cursorIndexOfPhase = CursorUtil.getColumnIndexOrThrow(_cursor, "phase");
          final int _cursorIndexOfActiveQuizSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "activeQuizSessionId");
          final int _cursorIndexOfCompletedOn = CursorUtil.getColumnIndexOrThrow(_cursor, "completedOn");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final LearningSessionEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpLanguageCode;
            _tmpLanguageCode = _cursor.getString(_cursorIndexOfLanguageCode);
            final String _tmpLearningDate;
            _tmpLearningDate = _cursor.getString(_cursorIndexOfLearningDate);
            final VocabBook _tmpBook;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfBook);
            _tmpBook = __converters.stringToVocabBook(_tmp);
            final List<String> _tmpVocabularyIds;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfVocabularyIds);
            _tmpVocabularyIds = __converters.jsonToStringList(_tmp_1);
            final int _tmpTargetCount;
            _tmpTargetCount = _cursor.getInt(_cursorIndexOfTargetCount);
            final int _tmpCardCursor;
            _tmpCardCursor = _cursor.getInt(_cursorIndexOfCardCursor);
            final int _tmpQuizRound;
            _tmpQuizRound = _cursor.getInt(_cursorIndexOfQuizRound);
            final List<String> _tmpPassedIds;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfPassedIds);
            _tmpPassedIds = __converters.jsonToStringList(_tmp_2);
            final List<String> _tmpPendingQuizIds;
            final String _tmp_3;
            _tmp_3 = _cursor.getString(_cursorIndexOfPendingQuizIds);
            _tmpPendingQuizIds = __converters.jsonToStringList(_tmp_3);
            final LearningPhase _tmpPhase;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfPhase);
            _tmpPhase = __converters.stringToLearningPhase(_tmp_4);
            final String _tmpActiveQuizSessionId;
            if (_cursor.isNull(_cursorIndexOfActiveQuizSessionId)) {
              _tmpActiveQuizSessionId = null;
            } else {
              _tmpActiveQuizSessionId = _cursor.getString(_cursorIndexOfActiveQuizSessionId);
            }
            final String _tmpCompletedOn;
            if (_cursor.isNull(_cursorIndexOfCompletedOn)) {
              _tmpCompletedOn = null;
            } else {
              _tmpCompletedOn = _cursor.getString(_cursorIndexOfCompletedOn);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new LearningSessionEntity(_tmpId,_tmpLanguageCode,_tmpLearningDate,_tmpBook,_tmpVocabularyIds,_tmpTargetCount,_tmpCardCursor,_tmpQuizRound,_tmpPassedIds,_tmpPendingQuizIds,_tmpPhase,_tmpActiveQuizSessionId,_tmpCompletedOn,_tmpCreatedAt,_tmpUpdatedAt);
          } else {
            _result = null;
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
  public Object learningSessionForDate(final String language, final String date,
      final VocabBook book, final Continuation<? super LearningSessionEntity> $completion) {
    final String _sql = "SELECT * FROM learning_sessions WHERE languageCode = ? AND book = ? AND learningDate = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindString(_argIndex, language);
    _argIndex = 2;
    final String _tmp = __converters.vocabBookToString(book);
    _statement.bindString(_argIndex, _tmp);
    _argIndex = 3;
    _statement.bindString(_argIndex, date);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<LearningSessionEntity>() {
      @Override
      @Nullable
      public LearningSessionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLanguageCode = CursorUtil.getColumnIndexOrThrow(_cursor, "languageCode");
          final int _cursorIndexOfLearningDate = CursorUtil.getColumnIndexOrThrow(_cursor, "learningDate");
          final int _cursorIndexOfBook = CursorUtil.getColumnIndexOrThrow(_cursor, "book");
          final int _cursorIndexOfVocabularyIds = CursorUtil.getColumnIndexOrThrow(_cursor, "vocabularyIds");
          final int _cursorIndexOfTargetCount = CursorUtil.getColumnIndexOrThrow(_cursor, "targetCount");
          final int _cursorIndexOfCardCursor = CursorUtil.getColumnIndexOrThrow(_cursor, "cardCursor");
          final int _cursorIndexOfQuizRound = CursorUtil.getColumnIndexOrThrow(_cursor, "quizRound");
          final int _cursorIndexOfPassedIds = CursorUtil.getColumnIndexOrThrow(_cursor, "passedIds");
          final int _cursorIndexOfPendingQuizIds = CursorUtil.getColumnIndexOrThrow(_cursor, "pendingQuizIds");
          final int _cursorIndexOfPhase = CursorUtil.getColumnIndexOrThrow(_cursor, "phase");
          final int _cursorIndexOfActiveQuizSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "activeQuizSessionId");
          final int _cursorIndexOfCompletedOn = CursorUtil.getColumnIndexOrThrow(_cursor, "completedOn");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final LearningSessionEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpLanguageCode;
            _tmpLanguageCode = _cursor.getString(_cursorIndexOfLanguageCode);
            final String _tmpLearningDate;
            _tmpLearningDate = _cursor.getString(_cursorIndexOfLearningDate);
            final VocabBook _tmpBook;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfBook);
            _tmpBook = __converters.stringToVocabBook(_tmp_1);
            final List<String> _tmpVocabularyIds;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfVocabularyIds);
            _tmpVocabularyIds = __converters.jsonToStringList(_tmp_2);
            final int _tmpTargetCount;
            _tmpTargetCount = _cursor.getInt(_cursorIndexOfTargetCount);
            final int _tmpCardCursor;
            _tmpCardCursor = _cursor.getInt(_cursorIndexOfCardCursor);
            final int _tmpQuizRound;
            _tmpQuizRound = _cursor.getInt(_cursorIndexOfQuizRound);
            final List<String> _tmpPassedIds;
            final String _tmp_3;
            _tmp_3 = _cursor.getString(_cursorIndexOfPassedIds);
            _tmpPassedIds = __converters.jsonToStringList(_tmp_3);
            final List<String> _tmpPendingQuizIds;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfPendingQuizIds);
            _tmpPendingQuizIds = __converters.jsonToStringList(_tmp_4);
            final LearningPhase _tmpPhase;
            final String _tmp_5;
            _tmp_5 = _cursor.getString(_cursorIndexOfPhase);
            _tmpPhase = __converters.stringToLearningPhase(_tmp_5);
            final String _tmpActiveQuizSessionId;
            if (_cursor.isNull(_cursorIndexOfActiveQuizSessionId)) {
              _tmpActiveQuizSessionId = null;
            } else {
              _tmpActiveQuizSessionId = _cursor.getString(_cursorIndexOfActiveQuizSessionId);
            }
            final String _tmpCompletedOn;
            if (_cursor.isNull(_cursorIndexOfCompletedOn)) {
              _tmpCompletedOn = null;
            } else {
              _tmpCompletedOn = _cursor.getString(_cursorIndexOfCompletedOn);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new LearningSessionEntity(_tmpId,_tmpLanguageCode,_tmpLearningDate,_tmpBook,_tmpVocabularyIds,_tmpTargetCount,_tmpCardCursor,_tmpQuizRound,_tmpPassedIds,_tmpPendingQuizIds,_tmpPhase,_tmpActiveQuizSessionId,_tmpCompletedOn,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<List<LearningSessionEntity>> observeUnfinishedSessions(final String language,
      final VocabBook book) {
    final String _sql = "SELECT * FROM learning_sessions WHERE languageCode = ? AND book = ? AND phase != 'COMPLETE' ORDER BY learningDate DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, language);
    _argIndex = 2;
    final String _tmp = __converters.vocabBookToString(book);
    _statement.bindString(_argIndex, _tmp);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"learning_sessions"}, new Callable<List<LearningSessionEntity>>() {
      @Override
      @NonNull
      public List<LearningSessionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLanguageCode = CursorUtil.getColumnIndexOrThrow(_cursor, "languageCode");
          final int _cursorIndexOfLearningDate = CursorUtil.getColumnIndexOrThrow(_cursor, "learningDate");
          final int _cursorIndexOfBook = CursorUtil.getColumnIndexOrThrow(_cursor, "book");
          final int _cursorIndexOfVocabularyIds = CursorUtil.getColumnIndexOrThrow(_cursor, "vocabularyIds");
          final int _cursorIndexOfTargetCount = CursorUtil.getColumnIndexOrThrow(_cursor, "targetCount");
          final int _cursorIndexOfCardCursor = CursorUtil.getColumnIndexOrThrow(_cursor, "cardCursor");
          final int _cursorIndexOfQuizRound = CursorUtil.getColumnIndexOrThrow(_cursor, "quizRound");
          final int _cursorIndexOfPassedIds = CursorUtil.getColumnIndexOrThrow(_cursor, "passedIds");
          final int _cursorIndexOfPendingQuizIds = CursorUtil.getColumnIndexOrThrow(_cursor, "pendingQuizIds");
          final int _cursorIndexOfPhase = CursorUtil.getColumnIndexOrThrow(_cursor, "phase");
          final int _cursorIndexOfActiveQuizSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "activeQuizSessionId");
          final int _cursorIndexOfCompletedOn = CursorUtil.getColumnIndexOrThrow(_cursor, "completedOn");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<LearningSessionEntity> _result = new ArrayList<LearningSessionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final LearningSessionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpLanguageCode;
            _tmpLanguageCode = _cursor.getString(_cursorIndexOfLanguageCode);
            final String _tmpLearningDate;
            _tmpLearningDate = _cursor.getString(_cursorIndexOfLearningDate);
            final VocabBook _tmpBook;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfBook);
            _tmpBook = __converters.stringToVocabBook(_tmp_1);
            final List<String> _tmpVocabularyIds;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfVocabularyIds);
            _tmpVocabularyIds = __converters.jsonToStringList(_tmp_2);
            final int _tmpTargetCount;
            _tmpTargetCount = _cursor.getInt(_cursorIndexOfTargetCount);
            final int _tmpCardCursor;
            _tmpCardCursor = _cursor.getInt(_cursorIndexOfCardCursor);
            final int _tmpQuizRound;
            _tmpQuizRound = _cursor.getInt(_cursorIndexOfQuizRound);
            final List<String> _tmpPassedIds;
            final String _tmp_3;
            _tmp_3 = _cursor.getString(_cursorIndexOfPassedIds);
            _tmpPassedIds = __converters.jsonToStringList(_tmp_3);
            final List<String> _tmpPendingQuizIds;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfPendingQuizIds);
            _tmpPendingQuizIds = __converters.jsonToStringList(_tmp_4);
            final LearningPhase _tmpPhase;
            final String _tmp_5;
            _tmp_5 = _cursor.getString(_cursorIndexOfPhase);
            _tmpPhase = __converters.stringToLearningPhase(_tmp_5);
            final String _tmpActiveQuizSessionId;
            if (_cursor.isNull(_cursorIndexOfActiveQuizSessionId)) {
              _tmpActiveQuizSessionId = null;
            } else {
              _tmpActiveQuizSessionId = _cursor.getString(_cursorIndexOfActiveQuizSessionId);
            }
            final String _tmpCompletedOn;
            if (_cursor.isNull(_cursorIndexOfCompletedOn)) {
              _tmpCompletedOn = null;
            } else {
              _tmpCompletedOn = _cursor.getString(_cursorIndexOfCompletedOn);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new LearningSessionEntity(_tmpId,_tmpLanguageCode,_tmpLearningDate,_tmpBook,_tmpVocabularyIds,_tmpTargetCount,_tmpCardCursor,_tmpQuizRound,_tmpPassedIds,_tmpPendingQuizIds,_tmpPhase,_tmpActiveQuizSessionId,_tmpCompletedOn,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object allUnfinishedSessions(
      final Continuation<? super List<LearningSessionEntity>> $completion) {
    final String _sql = "SELECT * FROM learning_sessions WHERE phase != 'COMPLETE'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<LearningSessionEntity>>() {
      @Override
      @NonNull
      public List<LearningSessionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLanguageCode = CursorUtil.getColumnIndexOrThrow(_cursor, "languageCode");
          final int _cursorIndexOfLearningDate = CursorUtil.getColumnIndexOrThrow(_cursor, "learningDate");
          final int _cursorIndexOfBook = CursorUtil.getColumnIndexOrThrow(_cursor, "book");
          final int _cursorIndexOfVocabularyIds = CursorUtil.getColumnIndexOrThrow(_cursor, "vocabularyIds");
          final int _cursorIndexOfTargetCount = CursorUtil.getColumnIndexOrThrow(_cursor, "targetCount");
          final int _cursorIndexOfCardCursor = CursorUtil.getColumnIndexOrThrow(_cursor, "cardCursor");
          final int _cursorIndexOfQuizRound = CursorUtil.getColumnIndexOrThrow(_cursor, "quizRound");
          final int _cursorIndexOfPassedIds = CursorUtil.getColumnIndexOrThrow(_cursor, "passedIds");
          final int _cursorIndexOfPendingQuizIds = CursorUtil.getColumnIndexOrThrow(_cursor, "pendingQuizIds");
          final int _cursorIndexOfPhase = CursorUtil.getColumnIndexOrThrow(_cursor, "phase");
          final int _cursorIndexOfActiveQuizSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "activeQuizSessionId");
          final int _cursorIndexOfCompletedOn = CursorUtil.getColumnIndexOrThrow(_cursor, "completedOn");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<LearningSessionEntity> _result = new ArrayList<LearningSessionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final LearningSessionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpLanguageCode;
            _tmpLanguageCode = _cursor.getString(_cursorIndexOfLanguageCode);
            final String _tmpLearningDate;
            _tmpLearningDate = _cursor.getString(_cursorIndexOfLearningDate);
            final VocabBook _tmpBook;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfBook);
            _tmpBook = __converters.stringToVocabBook(_tmp);
            final List<String> _tmpVocabularyIds;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfVocabularyIds);
            _tmpVocabularyIds = __converters.jsonToStringList(_tmp_1);
            final int _tmpTargetCount;
            _tmpTargetCount = _cursor.getInt(_cursorIndexOfTargetCount);
            final int _tmpCardCursor;
            _tmpCardCursor = _cursor.getInt(_cursorIndexOfCardCursor);
            final int _tmpQuizRound;
            _tmpQuizRound = _cursor.getInt(_cursorIndexOfQuizRound);
            final List<String> _tmpPassedIds;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfPassedIds);
            _tmpPassedIds = __converters.jsonToStringList(_tmp_2);
            final List<String> _tmpPendingQuizIds;
            final String _tmp_3;
            _tmp_3 = _cursor.getString(_cursorIndexOfPendingQuizIds);
            _tmpPendingQuizIds = __converters.jsonToStringList(_tmp_3);
            final LearningPhase _tmpPhase;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfPhase);
            _tmpPhase = __converters.stringToLearningPhase(_tmp_4);
            final String _tmpActiveQuizSessionId;
            if (_cursor.isNull(_cursorIndexOfActiveQuizSessionId)) {
              _tmpActiveQuizSessionId = null;
            } else {
              _tmpActiveQuizSessionId = _cursor.getString(_cursorIndexOfActiveQuizSessionId);
            }
            final String _tmpCompletedOn;
            if (_cursor.isNull(_cursorIndexOfCompletedOn)) {
              _tmpCompletedOn = null;
            } else {
              _tmpCompletedOn = _cursor.getString(_cursorIndexOfCompletedOn);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new LearningSessionEntity(_tmpId,_tmpLanguageCode,_tmpLearningDate,_tmpBook,_tmpVocabularyIds,_tmpTargetCount,_tmpCardCursor,_tmpQuizRound,_tmpPassedIds,_tmpPendingQuizIds,_tmpPhase,_tmpActiveQuizSessionId,_tmpCompletedOn,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object unfinishedSessionsBefore(final String language, final String date,
      final VocabBook book, final Continuation<? super List<LearningSessionEntity>> $completion) {
    final String _sql = "SELECT * FROM learning_sessions WHERE languageCode = ? AND book = ? AND phase != 'COMPLETE' AND learningDate != ? ORDER BY learningDate DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindString(_argIndex, language);
    _argIndex = 2;
    final String _tmp = __converters.vocabBookToString(book);
    _statement.bindString(_argIndex, _tmp);
    _argIndex = 3;
    _statement.bindString(_argIndex, date);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<LearningSessionEntity>>() {
      @Override
      @NonNull
      public List<LearningSessionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLanguageCode = CursorUtil.getColumnIndexOrThrow(_cursor, "languageCode");
          final int _cursorIndexOfLearningDate = CursorUtil.getColumnIndexOrThrow(_cursor, "learningDate");
          final int _cursorIndexOfBook = CursorUtil.getColumnIndexOrThrow(_cursor, "book");
          final int _cursorIndexOfVocabularyIds = CursorUtil.getColumnIndexOrThrow(_cursor, "vocabularyIds");
          final int _cursorIndexOfTargetCount = CursorUtil.getColumnIndexOrThrow(_cursor, "targetCount");
          final int _cursorIndexOfCardCursor = CursorUtil.getColumnIndexOrThrow(_cursor, "cardCursor");
          final int _cursorIndexOfQuizRound = CursorUtil.getColumnIndexOrThrow(_cursor, "quizRound");
          final int _cursorIndexOfPassedIds = CursorUtil.getColumnIndexOrThrow(_cursor, "passedIds");
          final int _cursorIndexOfPendingQuizIds = CursorUtil.getColumnIndexOrThrow(_cursor, "pendingQuizIds");
          final int _cursorIndexOfPhase = CursorUtil.getColumnIndexOrThrow(_cursor, "phase");
          final int _cursorIndexOfActiveQuizSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "activeQuizSessionId");
          final int _cursorIndexOfCompletedOn = CursorUtil.getColumnIndexOrThrow(_cursor, "completedOn");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<LearningSessionEntity> _result = new ArrayList<LearningSessionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final LearningSessionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpLanguageCode;
            _tmpLanguageCode = _cursor.getString(_cursorIndexOfLanguageCode);
            final String _tmpLearningDate;
            _tmpLearningDate = _cursor.getString(_cursorIndexOfLearningDate);
            final VocabBook _tmpBook;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfBook);
            _tmpBook = __converters.stringToVocabBook(_tmp_1);
            final List<String> _tmpVocabularyIds;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfVocabularyIds);
            _tmpVocabularyIds = __converters.jsonToStringList(_tmp_2);
            final int _tmpTargetCount;
            _tmpTargetCount = _cursor.getInt(_cursorIndexOfTargetCount);
            final int _tmpCardCursor;
            _tmpCardCursor = _cursor.getInt(_cursorIndexOfCardCursor);
            final int _tmpQuizRound;
            _tmpQuizRound = _cursor.getInt(_cursorIndexOfQuizRound);
            final List<String> _tmpPassedIds;
            final String _tmp_3;
            _tmp_3 = _cursor.getString(_cursorIndexOfPassedIds);
            _tmpPassedIds = __converters.jsonToStringList(_tmp_3);
            final List<String> _tmpPendingQuizIds;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfPendingQuizIds);
            _tmpPendingQuizIds = __converters.jsonToStringList(_tmp_4);
            final LearningPhase _tmpPhase;
            final String _tmp_5;
            _tmp_5 = _cursor.getString(_cursorIndexOfPhase);
            _tmpPhase = __converters.stringToLearningPhase(_tmp_5);
            final String _tmpActiveQuizSessionId;
            if (_cursor.isNull(_cursorIndexOfActiveQuizSessionId)) {
              _tmpActiveQuizSessionId = null;
            } else {
              _tmpActiveQuizSessionId = _cursor.getString(_cursorIndexOfActiveQuizSessionId);
            }
            final String _tmpCompletedOn;
            if (_cursor.isNull(_cursorIndexOfCompletedOn)) {
              _tmpCompletedOn = null;
            } else {
              _tmpCompletedOn = _cursor.getString(_cursorIndexOfCompletedOn);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new LearningSessionEntity(_tmpId,_tmpLanguageCode,_tmpLearningDate,_tmpBook,_tmpVocabularyIds,_tmpTargetCount,_tmpCardCursor,_tmpQuizRound,_tmpPassedIds,_tmpPendingQuizIds,_tmpPhase,_tmpActiveQuizSessionId,_tmpCompletedOn,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object allLearningSessions(
      final Continuation<? super List<LearningSessionEntity>> $completion) {
    final String _sql = "SELECT * FROM learning_sessions";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<LearningSessionEntity>>() {
      @Override
      @NonNull
      public List<LearningSessionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLanguageCode = CursorUtil.getColumnIndexOrThrow(_cursor, "languageCode");
          final int _cursorIndexOfLearningDate = CursorUtil.getColumnIndexOrThrow(_cursor, "learningDate");
          final int _cursorIndexOfBook = CursorUtil.getColumnIndexOrThrow(_cursor, "book");
          final int _cursorIndexOfVocabularyIds = CursorUtil.getColumnIndexOrThrow(_cursor, "vocabularyIds");
          final int _cursorIndexOfTargetCount = CursorUtil.getColumnIndexOrThrow(_cursor, "targetCount");
          final int _cursorIndexOfCardCursor = CursorUtil.getColumnIndexOrThrow(_cursor, "cardCursor");
          final int _cursorIndexOfQuizRound = CursorUtil.getColumnIndexOrThrow(_cursor, "quizRound");
          final int _cursorIndexOfPassedIds = CursorUtil.getColumnIndexOrThrow(_cursor, "passedIds");
          final int _cursorIndexOfPendingQuizIds = CursorUtil.getColumnIndexOrThrow(_cursor, "pendingQuizIds");
          final int _cursorIndexOfPhase = CursorUtil.getColumnIndexOrThrow(_cursor, "phase");
          final int _cursorIndexOfActiveQuizSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "activeQuizSessionId");
          final int _cursorIndexOfCompletedOn = CursorUtil.getColumnIndexOrThrow(_cursor, "completedOn");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<LearningSessionEntity> _result = new ArrayList<LearningSessionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final LearningSessionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpLanguageCode;
            _tmpLanguageCode = _cursor.getString(_cursorIndexOfLanguageCode);
            final String _tmpLearningDate;
            _tmpLearningDate = _cursor.getString(_cursorIndexOfLearningDate);
            final VocabBook _tmpBook;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfBook);
            _tmpBook = __converters.stringToVocabBook(_tmp);
            final List<String> _tmpVocabularyIds;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfVocabularyIds);
            _tmpVocabularyIds = __converters.jsonToStringList(_tmp_1);
            final int _tmpTargetCount;
            _tmpTargetCount = _cursor.getInt(_cursorIndexOfTargetCount);
            final int _tmpCardCursor;
            _tmpCardCursor = _cursor.getInt(_cursorIndexOfCardCursor);
            final int _tmpQuizRound;
            _tmpQuizRound = _cursor.getInt(_cursorIndexOfQuizRound);
            final List<String> _tmpPassedIds;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfPassedIds);
            _tmpPassedIds = __converters.jsonToStringList(_tmp_2);
            final List<String> _tmpPendingQuizIds;
            final String _tmp_3;
            _tmp_3 = _cursor.getString(_cursorIndexOfPendingQuizIds);
            _tmpPendingQuizIds = __converters.jsonToStringList(_tmp_3);
            final LearningPhase _tmpPhase;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfPhase);
            _tmpPhase = __converters.stringToLearningPhase(_tmp_4);
            final String _tmpActiveQuizSessionId;
            if (_cursor.isNull(_cursorIndexOfActiveQuizSessionId)) {
              _tmpActiveQuizSessionId = null;
            } else {
              _tmpActiveQuizSessionId = _cursor.getString(_cursorIndexOfActiveQuizSessionId);
            }
            final String _tmpCompletedOn;
            if (_cursor.isNull(_cursorIndexOfCompletedOn)) {
              _tmpCompletedOn = null;
            } else {
              _tmpCompletedOn = _cursor.getString(_cursorIndexOfCompletedOn);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new LearningSessionEntity(_tmpId,_tmpLanguageCode,_tmpLearningDate,_tmpBook,_tmpVocabularyIds,_tmpTargetCount,_tmpCardCursor,_tmpQuizRound,_tmpPassedIds,_tmpPendingQuizIds,_tmpPhase,_tmpActiveQuizSessionId,_tmpCompletedOn,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<List<SavedWordEntity>> observeSaved(final String language) {
    final String _sql = "SELECT * FROM saved_words WHERE languageCode = ? ORDER BY savedOn DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, language);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"saved_words"}, new Callable<List<SavedWordEntity>>() {
      @Override
      @NonNull
      public List<SavedWordEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfVocabularyId = CursorUtil.getColumnIndexOrThrow(_cursor, "vocabularyId");
          final int _cursorIndexOfLanguageCode = CursorUtil.getColumnIndexOrThrow(_cursor, "languageCode");
          final int _cursorIndexOfSavedOn = CursorUtil.getColumnIndexOrThrow(_cursor, "savedOn");
          final List<SavedWordEntity> _result = new ArrayList<SavedWordEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SavedWordEntity _item;
            final String _tmpVocabularyId;
            _tmpVocabularyId = _cursor.getString(_cursorIndexOfVocabularyId);
            final String _tmpLanguageCode;
            _tmpLanguageCode = _cursor.getString(_cursorIndexOfLanguageCode);
            final String _tmpSavedOn;
            _tmpSavedOn = _cursor.getString(_cursorIndexOfSavedOn);
            _item = new SavedWordEntity(_tmpVocabularyId,_tmpLanguageCode,_tmpSavedOn);
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
  public Flow<List<SavedVocabularyRow>> observeSavedVocabulary(final String language) {
    final String _sql = "\n"
            + "        SELECT v.id AS id, v.headword AS headword, v.pos AS pos, v.gender AS gender,\n"
            + "               v.definitions AS definitions, s.savedOn AS savedOn\n"
            + "        FROM saved_words s\n"
            + "        JOIN vocabulary v ON v.id = s.vocabularyId\n"
            + "        WHERE s.languageCode = ?\n"
            + "        ORDER BY v.normalizedHeadword\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, language);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"saved_words",
        "vocabulary"}, new Callable<List<SavedVocabularyRow>>() {
      @Override
      @NonNull
      public List<SavedVocabularyRow> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfHeadword = 1;
          final int _cursorIndexOfPos = 2;
          final int _cursorIndexOfGender = 3;
          final int _cursorIndexOfDefinitions = 4;
          final int _cursorIndexOfSavedOn = 5;
          final List<SavedVocabularyRow> _result = new ArrayList<SavedVocabularyRow>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SavedVocabularyRow _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
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
            final List<String> _tmpDefinitions;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfDefinitions);
            _tmpDefinitions = __converters.jsonToStringList(_tmp_2);
            final String _tmpSavedOn;
            _tmpSavedOn = _cursor.getString(_cursorIndexOfSavedOn);
            _item = new SavedVocabularyRow(_tmpId,_tmpHeadword,_tmpPos,_tmpGender,_tmpDefinitions,_tmpSavedOn);
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
  public Object allSaved(final Continuation<? super List<SavedWordEntity>> $completion) {
    final String _sql = "SELECT * FROM saved_words";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<SavedWordEntity>>() {
      @Override
      @NonNull
      public List<SavedWordEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfVocabularyId = CursorUtil.getColumnIndexOrThrow(_cursor, "vocabularyId");
          final int _cursorIndexOfLanguageCode = CursorUtil.getColumnIndexOrThrow(_cursor, "languageCode");
          final int _cursorIndexOfSavedOn = CursorUtil.getColumnIndexOrThrow(_cursor, "savedOn");
          final List<SavedWordEntity> _result = new ArrayList<SavedWordEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SavedWordEntity _item;
            final String _tmpVocabularyId;
            _tmpVocabularyId = _cursor.getString(_cursorIndexOfVocabularyId);
            final String _tmpLanguageCode;
            _tmpLanguageCode = _cursor.getString(_cursorIndexOfLanguageCode);
            final String _tmpSavedOn;
            _tmpSavedOn = _cursor.getString(_cursorIndexOfSavedOn);
            _item = new SavedWordEntity(_tmpVocabularyId,_tmpLanguageCode,_tmpSavedOn);
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
  public Object savedWords(final String language,
      final Continuation<? super List<SavedWordEntity>> $completion) {
    final String _sql = "SELECT * FROM saved_words WHERE languageCode = ? ORDER BY savedOn DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, language);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<SavedWordEntity>>() {
      @Override
      @NonNull
      public List<SavedWordEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfVocabularyId = CursorUtil.getColumnIndexOrThrow(_cursor, "vocabularyId");
          final int _cursorIndexOfLanguageCode = CursorUtil.getColumnIndexOrThrow(_cursor, "languageCode");
          final int _cursorIndexOfSavedOn = CursorUtil.getColumnIndexOrThrow(_cursor, "savedOn");
          final List<SavedWordEntity> _result = new ArrayList<SavedWordEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SavedWordEntity _item;
            final String _tmpVocabularyId;
            _tmpVocabularyId = _cursor.getString(_cursorIndexOfVocabularyId);
            final String _tmpLanguageCode;
            _tmpLanguageCode = _cursor.getString(_cursorIndexOfLanguageCode);
            final String _tmpSavedOn;
            _tmpSavedOn = _cursor.getString(_cursorIndexOfSavedOn);
            _item = new SavedWordEntity(_tmpVocabularyId,_tmpLanguageCode,_tmpSavedOn);
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
  public Object savedCount(final String language, final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM saved_words WHERE languageCode = ?";
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
  public Object savedEntry(final String vocabularyId,
      final Continuation<? super SavedWordEntity> $completion) {
    final String _sql = "SELECT * FROM saved_words WHERE vocabularyId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, vocabularyId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<SavedWordEntity>() {
      @Override
      @Nullable
      public SavedWordEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfVocabularyId = CursorUtil.getColumnIndexOrThrow(_cursor, "vocabularyId");
          final int _cursorIndexOfLanguageCode = CursorUtil.getColumnIndexOrThrow(_cursor, "languageCode");
          final int _cursorIndexOfSavedOn = CursorUtil.getColumnIndexOrThrow(_cursor, "savedOn");
          final SavedWordEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpVocabularyId;
            _tmpVocabularyId = _cursor.getString(_cursorIndexOfVocabularyId);
            final String _tmpLanguageCode;
            _tmpLanguageCode = _cursor.getString(_cursorIndexOfLanguageCode);
            final String _tmpSavedOn;
            _tmpSavedOn = _cursor.getString(_cursorIndexOfSavedOn);
            _result = new SavedWordEntity(_tmpVocabularyId,_tmpLanguageCode,_tmpSavedOn);
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
  public Flow<Boolean> observeIsSaved(final String vocabularyId) {
    final String _sql = "SELECT EXISTS(SELECT 1 FROM saved_words WHERE vocabularyId = ?)";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, vocabularyId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"saved_words"}, new Callable<Boolean>() {
      @Override
      @NonNull
      public Boolean call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Boolean _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp != 0;
          } else {
            _result = false;
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
  public Flow<Integer> observeSavedCount(final String language) {
    final String _sql = "SELECT COUNT(*) FROM saved_words WHERE languageCode = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, language);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"saved_words"}, new Callable<Integer>() {
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
  public Object savedCountAll(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM saved_words";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
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
  public Object markLearned(final List<String> ids, final LearningStatus status, final String date,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
        _stringBuilder.append("UPDATE learning_records SET status = ");
        _stringBuilder.append("?");
        _stringBuilder.append(", firstLearnedOn = COALESCE(firstLearnedOn, ");
        _stringBuilder.append("?");
        _stringBuilder.append(") WHERE vocabularyId IN (");
        final int _inputSize = ids.size();
        StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
        _stringBuilder.append(")");
        final String _sql = _stringBuilder.toString();
        final SupportSQLiteStatement _stmt = __db.compileStatement(_sql);
        int _argIndex = 1;
        final String _tmp = __converters.learningStatusToString(status);
        _stmt.bindString(_argIndex, _tmp);
        _argIndex = 2;
        _stmt.bindString(_argIndex, date);
        _argIndex = 3;
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
  public Object markMastered(final List<String> ids, final String date,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
        _stringBuilder.append("UPDATE learning_records SET mastered = 1, status = 'LEARNED', firstLearnedOn = COALESCE(firstLearnedOn, ");
        _stringBuilder.append("?");
        _stringBuilder.append(") WHERE vocabularyId IN (");
        final int _inputSize = ids.size();
        StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
        _stringBuilder.append(")");
        final String _sql = _stringBuilder.toString();
        final SupportSQLiteStatement _stmt = __db.compileStatement(_sql);
        int _argIndex = 1;
        _stmt.bindString(_argIndex, date);
        _argIndex = 2;
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
