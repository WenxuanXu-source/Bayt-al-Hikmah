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
import androidx.sqlite.db.SupportSQLiteStatement;
import com.baytalhikmah.data.local.Converters;
import com.baytalhikmah.data.local.TestQuestionEntity;
import com.baytalhikmah.data.local.TestSessionEntity;
import com.baytalhikmah.data.model.QuestionType;
import com.baytalhikmah.data.model.TestStatus;
import com.baytalhikmah.data.model.TestType;
import com.baytalhikmah.data.model.VocabBook;
import java.lang.Boolean;
import java.lang.Class;
import java.lang.Double;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
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
public final class TestDao_Impl implements TestDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<TestSessionEntity> __insertionAdapterOfTestSessionEntity;

  private final Converters __converters = new Converters();

  private final EntityInsertionAdapter<TestQuestionEntity> __insertionAdapterOfTestQuestionEntity;

  private final EntityInsertionAdapter<TestSessionEntity> __insertionAdapterOfTestSessionEntity_1;

  private final EntityInsertionAdapter<TestQuestionEntity> __insertionAdapterOfTestQuestionEntity_1;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllQuestions;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllSessions;

  private final SharedSQLiteStatement __preparedStmtOfDeleteSession;

  private final SharedSQLiteStatement __preparedStmtOfDeleteQuestion;

  private final SharedSQLiteStatement __preparedStmtOfDeleteQuestionsForSession;

  private final SharedSQLiteStatement __preparedStmtOfDeleteUnansweredForVocabulary;

  private final SharedSQLiteStatement __preparedStmtOfDeleteOrphanQuestions;

  public TestDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTestSessionEntity = new EntityInsertionAdapter<TestSessionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `test_sessions` (`id`,`languageCode`,`book`,`testType`,`sessionDate`,`createdAt`,`completedAt`,`totalVocabularyItems`,`currentGroup`,`currentQuestion`,`status`,`scorePercent`,`passed`,`dailyTaskId`,`learningSessionId`,`vocabularyIds`,`firstAttemptCorrect`,`firstAttemptTotal`,`passingThreshold`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TestSessionEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getLanguageCode());
        final String _tmp = __converters.vocabBookToString(entity.getBook());
        statement.bindString(3, _tmp);
        final String _tmp_1 = __converters.testTypeToString(entity.getTestType());
        statement.bindString(4, _tmp_1);
        statement.bindString(5, entity.getSessionDate());
        statement.bindLong(6, entity.getCreatedAt());
        if (entity.getCompletedAt() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getCompletedAt());
        }
        statement.bindLong(8, entity.getTotalVocabularyItems());
        statement.bindLong(9, entity.getCurrentGroup());
        statement.bindLong(10, entity.getCurrentQuestion());
        final String _tmp_2 = __converters.testStatusToString(entity.getStatus());
        statement.bindString(11, _tmp_2);
        if (entity.getScorePercent() == null) {
          statement.bindNull(12);
        } else {
          statement.bindDouble(12, entity.getScorePercent());
        }
        final Integer _tmp_3 = entity.getPassed() == null ? null : (entity.getPassed() ? 1 : 0);
        if (_tmp_3 == null) {
          statement.bindNull(13);
        } else {
          statement.bindLong(13, _tmp_3);
        }
        if (entity.getDailyTaskId() == null) {
          statement.bindNull(14);
        } else {
          statement.bindString(14, entity.getDailyTaskId());
        }
        if (entity.getLearningSessionId() == null) {
          statement.bindNull(15);
        } else {
          statement.bindString(15, entity.getLearningSessionId());
        }
        final String _tmp_4 = __converters.stringListToJson(entity.getVocabularyIds());
        statement.bindString(16, _tmp_4);
        statement.bindLong(17, entity.getFirstAttemptCorrect());
        statement.bindLong(18, entity.getFirstAttemptTotal());
        if (entity.getPassingThreshold() == null) {
          statement.bindNull(19);
        } else {
          statement.bindLong(19, entity.getPassingThreshold());
        }
      }
    };
    this.__insertionAdapterOfTestQuestionEntity = new EntityInsertionAdapter<TestQuestionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `test_questions` (`id`,`sessionId`,`vocabularyId`,`questionType`,`prompt`,`contextTranslation`,`highlightText`,`options`,`correctIndex`,`selectedIndex`,`isCorrect`,`retryCount`,`isReviewCopy`,`groupIndex`,`orderInGroup`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TestQuestionEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getSessionId());
        statement.bindString(3, entity.getVocabularyId());
        final String _tmp = __converters.questionTypeToString(entity.getQuestionType());
        statement.bindString(4, _tmp);
        statement.bindString(5, entity.getPrompt());
        if (entity.getContextTranslation() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getContextTranslation());
        }
        if (entity.getHighlightText() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getHighlightText());
        }
        final String _tmp_1 = __converters.stringListToJson(entity.getOptions());
        statement.bindString(8, _tmp_1);
        statement.bindLong(9, entity.getCorrectIndex());
        if (entity.getSelectedIndex() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getSelectedIndex());
        }
        final Integer _tmp_2 = entity.isCorrect() == null ? null : (entity.isCorrect() ? 1 : 0);
        if (_tmp_2 == null) {
          statement.bindNull(11);
        } else {
          statement.bindLong(11, _tmp_2);
        }
        statement.bindLong(12, entity.getRetryCount());
        final int _tmp_3 = entity.isReviewCopy() ? 1 : 0;
        statement.bindLong(13, _tmp_3);
        statement.bindLong(14, entity.getGroupIndex());
        statement.bindLong(15, entity.getOrderInGroup());
      }
    };
    this.__insertionAdapterOfTestSessionEntity_1 = new EntityInsertionAdapter<TestSessionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `test_sessions` (`id`,`languageCode`,`book`,`testType`,`sessionDate`,`createdAt`,`completedAt`,`totalVocabularyItems`,`currentGroup`,`currentQuestion`,`status`,`scorePercent`,`passed`,`dailyTaskId`,`learningSessionId`,`vocabularyIds`,`firstAttemptCorrect`,`firstAttemptTotal`,`passingThreshold`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TestSessionEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getLanguageCode());
        final String _tmp = __converters.vocabBookToString(entity.getBook());
        statement.bindString(3, _tmp);
        final String _tmp_1 = __converters.testTypeToString(entity.getTestType());
        statement.bindString(4, _tmp_1);
        statement.bindString(5, entity.getSessionDate());
        statement.bindLong(6, entity.getCreatedAt());
        if (entity.getCompletedAt() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getCompletedAt());
        }
        statement.bindLong(8, entity.getTotalVocabularyItems());
        statement.bindLong(9, entity.getCurrentGroup());
        statement.bindLong(10, entity.getCurrentQuestion());
        final String _tmp_2 = __converters.testStatusToString(entity.getStatus());
        statement.bindString(11, _tmp_2);
        if (entity.getScorePercent() == null) {
          statement.bindNull(12);
        } else {
          statement.bindDouble(12, entity.getScorePercent());
        }
        final Integer _tmp_3 = entity.getPassed() == null ? null : (entity.getPassed() ? 1 : 0);
        if (_tmp_3 == null) {
          statement.bindNull(13);
        } else {
          statement.bindLong(13, _tmp_3);
        }
        if (entity.getDailyTaskId() == null) {
          statement.bindNull(14);
        } else {
          statement.bindString(14, entity.getDailyTaskId());
        }
        if (entity.getLearningSessionId() == null) {
          statement.bindNull(15);
        } else {
          statement.bindString(15, entity.getLearningSessionId());
        }
        final String _tmp_4 = __converters.stringListToJson(entity.getVocabularyIds());
        statement.bindString(16, _tmp_4);
        statement.bindLong(17, entity.getFirstAttemptCorrect());
        statement.bindLong(18, entity.getFirstAttemptTotal());
        if (entity.getPassingThreshold() == null) {
          statement.bindNull(19);
        } else {
          statement.bindLong(19, entity.getPassingThreshold());
        }
      }
    };
    this.__insertionAdapterOfTestQuestionEntity_1 = new EntityInsertionAdapter<TestQuestionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `test_questions` (`id`,`sessionId`,`vocabularyId`,`questionType`,`prompt`,`contextTranslation`,`highlightText`,`options`,`correctIndex`,`selectedIndex`,`isCorrect`,`retryCount`,`isReviewCopy`,`groupIndex`,`orderInGroup`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TestQuestionEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getSessionId());
        statement.bindString(3, entity.getVocabularyId());
        final String _tmp = __converters.questionTypeToString(entity.getQuestionType());
        statement.bindString(4, _tmp);
        statement.bindString(5, entity.getPrompt());
        if (entity.getContextTranslation() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getContextTranslation());
        }
        if (entity.getHighlightText() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getHighlightText());
        }
        final String _tmp_1 = __converters.stringListToJson(entity.getOptions());
        statement.bindString(8, _tmp_1);
        statement.bindLong(9, entity.getCorrectIndex());
        if (entity.getSelectedIndex() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getSelectedIndex());
        }
        final Integer _tmp_2 = entity.isCorrect() == null ? null : (entity.isCorrect() ? 1 : 0);
        if (_tmp_2 == null) {
          statement.bindNull(11);
        } else {
          statement.bindLong(11, _tmp_2);
        }
        statement.bindLong(12, entity.getRetryCount());
        final int _tmp_3 = entity.isReviewCopy() ? 1 : 0;
        statement.bindLong(13, _tmp_3);
        statement.bindLong(14, entity.getGroupIndex());
        statement.bindLong(15, entity.getOrderInGroup());
      }
    };
    this.__preparedStmtOfDeleteAllQuestions = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM test_questions";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAllSessions = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM test_sessions";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteSession = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM test_sessions WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteQuestion = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM test_questions WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteQuestionsForSession = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM test_questions WHERE sessionId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteUnansweredForVocabulary = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM test_questions WHERE sessionId = ? AND vocabularyId = ? AND isCorrect IS NULL";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteOrphanQuestions = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM test_questions WHERE sessionId NOT IN (SELECT id FROM test_sessions)";
        return _query;
      }
    };
  }

  @Override
  public Object insertSession(final TestSessionEntity session,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfTestSessionEntity.insert(session);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertQuestions(final List<TestQuestionEntity> questions,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfTestQuestionEntity.insert(questions);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertSessionsForRestore(final List<TestSessionEntity> sessions,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfTestSessionEntity_1.insert(sessions);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertQuestionsForRestore(final List<TestQuestionEntity> questions,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfTestQuestionEntity_1.insert(questions);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object upsertSession(final TestSessionEntity session,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfTestSessionEntity_1.insert(session);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object upsertQuestion(final TestQuestionEntity question,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfTestQuestionEntity_1.insert(question);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAllQuestions(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllQuestions.acquire();
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
          __preparedStmtOfDeleteAllQuestions.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAllSessions(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllSessions.acquire();
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
          __preparedStmtOfDeleteAllSessions.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteSession(final String sessionId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteSession.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, sessionId);
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
          __preparedStmtOfDeleteSession.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteQuestion(final String questionId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteQuestion.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, questionId);
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
          __preparedStmtOfDeleteQuestion.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteQuestionsForSession(final String sessionId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteQuestionsForSession.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, sessionId);
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
          __preparedStmtOfDeleteQuestionsForSession.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteUnansweredForVocabulary(final String sessionId, final String vocabularyId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteUnansweredForVocabulary.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, sessionId);
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
          __preparedStmtOfDeleteUnansweredForVocabulary.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteOrphanQuestions(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteOrphanQuestions.acquire();
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
          __preparedStmtOfDeleteOrphanQuestions.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object session(final String sessionId,
      final Continuation<? super TestSessionEntity> $completion) {
    final String _sql = "SELECT * FROM test_sessions WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, sessionId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<TestSessionEntity>() {
      @Override
      @Nullable
      public TestSessionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLanguageCode = CursorUtil.getColumnIndexOrThrow(_cursor, "languageCode");
          final int _cursorIndexOfBook = CursorUtil.getColumnIndexOrThrow(_cursor, "book");
          final int _cursorIndexOfTestType = CursorUtil.getColumnIndexOrThrow(_cursor, "testType");
          final int _cursorIndexOfSessionDate = CursorUtil.getColumnIndexOrThrow(_cursor, "sessionDate");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfTotalVocabularyItems = CursorUtil.getColumnIndexOrThrow(_cursor, "totalVocabularyItems");
          final int _cursorIndexOfCurrentGroup = CursorUtil.getColumnIndexOrThrow(_cursor, "currentGroup");
          final int _cursorIndexOfCurrentQuestion = CursorUtil.getColumnIndexOrThrow(_cursor, "currentQuestion");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfScorePercent = CursorUtil.getColumnIndexOrThrow(_cursor, "scorePercent");
          final int _cursorIndexOfPassed = CursorUtil.getColumnIndexOrThrow(_cursor, "passed");
          final int _cursorIndexOfDailyTaskId = CursorUtil.getColumnIndexOrThrow(_cursor, "dailyTaskId");
          final int _cursorIndexOfLearningSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "learningSessionId");
          final int _cursorIndexOfVocabularyIds = CursorUtil.getColumnIndexOrThrow(_cursor, "vocabularyIds");
          final int _cursorIndexOfFirstAttemptCorrect = CursorUtil.getColumnIndexOrThrow(_cursor, "firstAttemptCorrect");
          final int _cursorIndexOfFirstAttemptTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "firstAttemptTotal");
          final int _cursorIndexOfPassingThreshold = CursorUtil.getColumnIndexOrThrow(_cursor, "passingThreshold");
          final TestSessionEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpLanguageCode;
            _tmpLanguageCode = _cursor.getString(_cursorIndexOfLanguageCode);
            final VocabBook _tmpBook;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfBook);
            _tmpBook = __converters.stringToVocabBook(_tmp);
            final TestType _tmpTestType;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfTestType);
            _tmpTestType = __converters.stringToTestType(_tmp_1);
            final String _tmpSessionDate;
            _tmpSessionDate = _cursor.getString(_cursorIndexOfSessionDate);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final int _tmpTotalVocabularyItems;
            _tmpTotalVocabularyItems = _cursor.getInt(_cursorIndexOfTotalVocabularyItems);
            final int _tmpCurrentGroup;
            _tmpCurrentGroup = _cursor.getInt(_cursorIndexOfCurrentGroup);
            final int _tmpCurrentQuestion;
            _tmpCurrentQuestion = _cursor.getInt(_cursorIndexOfCurrentQuestion);
            final TestStatus _tmpStatus;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.stringToTestStatus(_tmp_2);
            final Double _tmpScorePercent;
            if (_cursor.isNull(_cursorIndexOfScorePercent)) {
              _tmpScorePercent = null;
            } else {
              _tmpScorePercent = _cursor.getDouble(_cursorIndexOfScorePercent);
            }
            final Boolean _tmpPassed;
            final Integer _tmp_3;
            if (_cursor.isNull(_cursorIndexOfPassed)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getInt(_cursorIndexOfPassed);
            }
            _tmpPassed = _tmp_3 == null ? null : _tmp_3 != 0;
            final String _tmpDailyTaskId;
            if (_cursor.isNull(_cursorIndexOfDailyTaskId)) {
              _tmpDailyTaskId = null;
            } else {
              _tmpDailyTaskId = _cursor.getString(_cursorIndexOfDailyTaskId);
            }
            final String _tmpLearningSessionId;
            if (_cursor.isNull(_cursorIndexOfLearningSessionId)) {
              _tmpLearningSessionId = null;
            } else {
              _tmpLearningSessionId = _cursor.getString(_cursorIndexOfLearningSessionId);
            }
            final List<String> _tmpVocabularyIds;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfVocabularyIds);
            _tmpVocabularyIds = __converters.jsonToStringList(_tmp_4);
            final int _tmpFirstAttemptCorrect;
            _tmpFirstAttemptCorrect = _cursor.getInt(_cursorIndexOfFirstAttemptCorrect);
            final int _tmpFirstAttemptTotal;
            _tmpFirstAttemptTotal = _cursor.getInt(_cursorIndexOfFirstAttemptTotal);
            final Integer _tmpPassingThreshold;
            if (_cursor.isNull(_cursorIndexOfPassingThreshold)) {
              _tmpPassingThreshold = null;
            } else {
              _tmpPassingThreshold = _cursor.getInt(_cursorIndexOfPassingThreshold);
            }
            _result = new TestSessionEntity(_tmpId,_tmpLanguageCode,_tmpBook,_tmpTestType,_tmpSessionDate,_tmpCreatedAt,_tmpCompletedAt,_tmpTotalVocabularyItems,_tmpCurrentGroup,_tmpCurrentQuestion,_tmpStatus,_tmpScorePercent,_tmpPassed,_tmpDailyTaskId,_tmpLearningSessionId,_tmpVocabularyIds,_tmpFirstAttemptCorrect,_tmpFirstAttemptTotal,_tmpPassingThreshold);
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
  public Flow<TestSessionEntity> observeSession(final String sessionId) {
    final String _sql = "SELECT * FROM test_sessions WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, sessionId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"test_sessions"}, new Callable<TestSessionEntity>() {
      @Override
      @Nullable
      public TestSessionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLanguageCode = CursorUtil.getColumnIndexOrThrow(_cursor, "languageCode");
          final int _cursorIndexOfBook = CursorUtil.getColumnIndexOrThrow(_cursor, "book");
          final int _cursorIndexOfTestType = CursorUtil.getColumnIndexOrThrow(_cursor, "testType");
          final int _cursorIndexOfSessionDate = CursorUtil.getColumnIndexOrThrow(_cursor, "sessionDate");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfTotalVocabularyItems = CursorUtil.getColumnIndexOrThrow(_cursor, "totalVocabularyItems");
          final int _cursorIndexOfCurrentGroup = CursorUtil.getColumnIndexOrThrow(_cursor, "currentGroup");
          final int _cursorIndexOfCurrentQuestion = CursorUtil.getColumnIndexOrThrow(_cursor, "currentQuestion");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfScorePercent = CursorUtil.getColumnIndexOrThrow(_cursor, "scorePercent");
          final int _cursorIndexOfPassed = CursorUtil.getColumnIndexOrThrow(_cursor, "passed");
          final int _cursorIndexOfDailyTaskId = CursorUtil.getColumnIndexOrThrow(_cursor, "dailyTaskId");
          final int _cursorIndexOfLearningSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "learningSessionId");
          final int _cursorIndexOfVocabularyIds = CursorUtil.getColumnIndexOrThrow(_cursor, "vocabularyIds");
          final int _cursorIndexOfFirstAttemptCorrect = CursorUtil.getColumnIndexOrThrow(_cursor, "firstAttemptCorrect");
          final int _cursorIndexOfFirstAttemptTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "firstAttemptTotal");
          final int _cursorIndexOfPassingThreshold = CursorUtil.getColumnIndexOrThrow(_cursor, "passingThreshold");
          final TestSessionEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpLanguageCode;
            _tmpLanguageCode = _cursor.getString(_cursorIndexOfLanguageCode);
            final VocabBook _tmpBook;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfBook);
            _tmpBook = __converters.stringToVocabBook(_tmp);
            final TestType _tmpTestType;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfTestType);
            _tmpTestType = __converters.stringToTestType(_tmp_1);
            final String _tmpSessionDate;
            _tmpSessionDate = _cursor.getString(_cursorIndexOfSessionDate);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final int _tmpTotalVocabularyItems;
            _tmpTotalVocabularyItems = _cursor.getInt(_cursorIndexOfTotalVocabularyItems);
            final int _tmpCurrentGroup;
            _tmpCurrentGroup = _cursor.getInt(_cursorIndexOfCurrentGroup);
            final int _tmpCurrentQuestion;
            _tmpCurrentQuestion = _cursor.getInt(_cursorIndexOfCurrentQuestion);
            final TestStatus _tmpStatus;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.stringToTestStatus(_tmp_2);
            final Double _tmpScorePercent;
            if (_cursor.isNull(_cursorIndexOfScorePercent)) {
              _tmpScorePercent = null;
            } else {
              _tmpScorePercent = _cursor.getDouble(_cursorIndexOfScorePercent);
            }
            final Boolean _tmpPassed;
            final Integer _tmp_3;
            if (_cursor.isNull(_cursorIndexOfPassed)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getInt(_cursorIndexOfPassed);
            }
            _tmpPassed = _tmp_3 == null ? null : _tmp_3 != 0;
            final String _tmpDailyTaskId;
            if (_cursor.isNull(_cursorIndexOfDailyTaskId)) {
              _tmpDailyTaskId = null;
            } else {
              _tmpDailyTaskId = _cursor.getString(_cursorIndexOfDailyTaskId);
            }
            final String _tmpLearningSessionId;
            if (_cursor.isNull(_cursorIndexOfLearningSessionId)) {
              _tmpLearningSessionId = null;
            } else {
              _tmpLearningSessionId = _cursor.getString(_cursorIndexOfLearningSessionId);
            }
            final List<String> _tmpVocabularyIds;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfVocabularyIds);
            _tmpVocabularyIds = __converters.jsonToStringList(_tmp_4);
            final int _tmpFirstAttemptCorrect;
            _tmpFirstAttemptCorrect = _cursor.getInt(_cursorIndexOfFirstAttemptCorrect);
            final int _tmpFirstAttemptTotal;
            _tmpFirstAttemptTotal = _cursor.getInt(_cursorIndexOfFirstAttemptTotal);
            final Integer _tmpPassingThreshold;
            if (_cursor.isNull(_cursorIndexOfPassingThreshold)) {
              _tmpPassingThreshold = null;
            } else {
              _tmpPassingThreshold = _cursor.getInt(_cursorIndexOfPassingThreshold);
            }
            _result = new TestSessionEntity(_tmpId,_tmpLanguageCode,_tmpBook,_tmpTestType,_tmpSessionDate,_tmpCreatedAt,_tmpCompletedAt,_tmpTotalVocabularyItems,_tmpCurrentGroup,_tmpCurrentQuestion,_tmpStatus,_tmpScorePercent,_tmpPassed,_tmpDailyTaskId,_tmpLearningSessionId,_tmpVocabularyIds,_tmpFirstAttemptCorrect,_tmpFirstAttemptTotal,_tmpPassingThreshold);
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
  public Object question(final String questionId,
      final Continuation<? super TestQuestionEntity> $completion) {
    final String _sql = "SELECT * FROM test_questions WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, questionId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<TestQuestionEntity>() {
      @Override
      @Nullable
      public TestQuestionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "sessionId");
          final int _cursorIndexOfVocabularyId = CursorUtil.getColumnIndexOrThrow(_cursor, "vocabularyId");
          final int _cursorIndexOfQuestionType = CursorUtil.getColumnIndexOrThrow(_cursor, "questionType");
          final int _cursorIndexOfPrompt = CursorUtil.getColumnIndexOrThrow(_cursor, "prompt");
          final int _cursorIndexOfContextTranslation = CursorUtil.getColumnIndexOrThrow(_cursor, "contextTranslation");
          final int _cursorIndexOfHighlightText = CursorUtil.getColumnIndexOrThrow(_cursor, "highlightText");
          final int _cursorIndexOfOptions = CursorUtil.getColumnIndexOrThrow(_cursor, "options");
          final int _cursorIndexOfCorrectIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "correctIndex");
          final int _cursorIndexOfSelectedIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "selectedIndex");
          final int _cursorIndexOfIsCorrect = CursorUtil.getColumnIndexOrThrow(_cursor, "isCorrect");
          final int _cursorIndexOfRetryCount = CursorUtil.getColumnIndexOrThrow(_cursor, "retryCount");
          final int _cursorIndexOfIsReviewCopy = CursorUtil.getColumnIndexOrThrow(_cursor, "isReviewCopy");
          final int _cursorIndexOfGroupIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "groupIndex");
          final int _cursorIndexOfOrderInGroup = CursorUtil.getColumnIndexOrThrow(_cursor, "orderInGroup");
          final TestQuestionEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpSessionId;
            _tmpSessionId = _cursor.getString(_cursorIndexOfSessionId);
            final String _tmpVocabularyId;
            _tmpVocabularyId = _cursor.getString(_cursorIndexOfVocabularyId);
            final QuestionType _tmpQuestionType;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfQuestionType);
            _tmpQuestionType = __converters.stringToQuestionType(_tmp);
            final String _tmpPrompt;
            _tmpPrompt = _cursor.getString(_cursorIndexOfPrompt);
            final String _tmpContextTranslation;
            if (_cursor.isNull(_cursorIndexOfContextTranslation)) {
              _tmpContextTranslation = null;
            } else {
              _tmpContextTranslation = _cursor.getString(_cursorIndexOfContextTranslation);
            }
            final String _tmpHighlightText;
            if (_cursor.isNull(_cursorIndexOfHighlightText)) {
              _tmpHighlightText = null;
            } else {
              _tmpHighlightText = _cursor.getString(_cursorIndexOfHighlightText);
            }
            final List<String> _tmpOptions;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfOptions);
            _tmpOptions = __converters.jsonToStringList(_tmp_1);
            final int _tmpCorrectIndex;
            _tmpCorrectIndex = _cursor.getInt(_cursorIndexOfCorrectIndex);
            final Integer _tmpSelectedIndex;
            if (_cursor.isNull(_cursorIndexOfSelectedIndex)) {
              _tmpSelectedIndex = null;
            } else {
              _tmpSelectedIndex = _cursor.getInt(_cursorIndexOfSelectedIndex);
            }
            final Boolean _tmpIsCorrect;
            final Integer _tmp_2;
            if (_cursor.isNull(_cursorIndexOfIsCorrect)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getInt(_cursorIndexOfIsCorrect);
            }
            _tmpIsCorrect = _tmp_2 == null ? null : _tmp_2 != 0;
            final int _tmpRetryCount;
            _tmpRetryCount = _cursor.getInt(_cursorIndexOfRetryCount);
            final boolean _tmpIsReviewCopy;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsReviewCopy);
            _tmpIsReviewCopy = _tmp_3 != 0;
            final int _tmpGroupIndex;
            _tmpGroupIndex = _cursor.getInt(_cursorIndexOfGroupIndex);
            final int _tmpOrderInGroup;
            _tmpOrderInGroup = _cursor.getInt(_cursorIndexOfOrderInGroup);
            _result = new TestQuestionEntity(_tmpId,_tmpSessionId,_tmpVocabularyId,_tmpQuestionType,_tmpPrompt,_tmpContextTranslation,_tmpHighlightText,_tmpOptions,_tmpCorrectIndex,_tmpSelectedIndex,_tmpIsCorrect,_tmpRetryCount,_tmpIsReviewCopy,_tmpGroupIndex,_tmpOrderInGroup);
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
  public Object questions(final String sessionId,
      final Continuation<? super List<TestQuestionEntity>> $completion) {
    final String _sql = "SELECT * FROM test_questions WHERE sessionId = ? ORDER BY groupIndex, orderInGroup, rowid";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, sessionId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<TestQuestionEntity>>() {
      @Override
      @NonNull
      public List<TestQuestionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "sessionId");
          final int _cursorIndexOfVocabularyId = CursorUtil.getColumnIndexOrThrow(_cursor, "vocabularyId");
          final int _cursorIndexOfQuestionType = CursorUtil.getColumnIndexOrThrow(_cursor, "questionType");
          final int _cursorIndexOfPrompt = CursorUtil.getColumnIndexOrThrow(_cursor, "prompt");
          final int _cursorIndexOfContextTranslation = CursorUtil.getColumnIndexOrThrow(_cursor, "contextTranslation");
          final int _cursorIndexOfHighlightText = CursorUtil.getColumnIndexOrThrow(_cursor, "highlightText");
          final int _cursorIndexOfOptions = CursorUtil.getColumnIndexOrThrow(_cursor, "options");
          final int _cursorIndexOfCorrectIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "correctIndex");
          final int _cursorIndexOfSelectedIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "selectedIndex");
          final int _cursorIndexOfIsCorrect = CursorUtil.getColumnIndexOrThrow(_cursor, "isCorrect");
          final int _cursorIndexOfRetryCount = CursorUtil.getColumnIndexOrThrow(_cursor, "retryCount");
          final int _cursorIndexOfIsReviewCopy = CursorUtil.getColumnIndexOrThrow(_cursor, "isReviewCopy");
          final int _cursorIndexOfGroupIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "groupIndex");
          final int _cursorIndexOfOrderInGroup = CursorUtil.getColumnIndexOrThrow(_cursor, "orderInGroup");
          final List<TestQuestionEntity> _result = new ArrayList<TestQuestionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TestQuestionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpSessionId;
            _tmpSessionId = _cursor.getString(_cursorIndexOfSessionId);
            final String _tmpVocabularyId;
            _tmpVocabularyId = _cursor.getString(_cursorIndexOfVocabularyId);
            final QuestionType _tmpQuestionType;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfQuestionType);
            _tmpQuestionType = __converters.stringToQuestionType(_tmp);
            final String _tmpPrompt;
            _tmpPrompt = _cursor.getString(_cursorIndexOfPrompt);
            final String _tmpContextTranslation;
            if (_cursor.isNull(_cursorIndexOfContextTranslation)) {
              _tmpContextTranslation = null;
            } else {
              _tmpContextTranslation = _cursor.getString(_cursorIndexOfContextTranslation);
            }
            final String _tmpHighlightText;
            if (_cursor.isNull(_cursorIndexOfHighlightText)) {
              _tmpHighlightText = null;
            } else {
              _tmpHighlightText = _cursor.getString(_cursorIndexOfHighlightText);
            }
            final List<String> _tmpOptions;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfOptions);
            _tmpOptions = __converters.jsonToStringList(_tmp_1);
            final int _tmpCorrectIndex;
            _tmpCorrectIndex = _cursor.getInt(_cursorIndexOfCorrectIndex);
            final Integer _tmpSelectedIndex;
            if (_cursor.isNull(_cursorIndexOfSelectedIndex)) {
              _tmpSelectedIndex = null;
            } else {
              _tmpSelectedIndex = _cursor.getInt(_cursorIndexOfSelectedIndex);
            }
            final Boolean _tmpIsCorrect;
            final Integer _tmp_2;
            if (_cursor.isNull(_cursorIndexOfIsCorrect)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getInt(_cursorIndexOfIsCorrect);
            }
            _tmpIsCorrect = _tmp_2 == null ? null : _tmp_2 != 0;
            final int _tmpRetryCount;
            _tmpRetryCount = _cursor.getInt(_cursorIndexOfRetryCount);
            final boolean _tmpIsReviewCopy;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsReviewCopy);
            _tmpIsReviewCopy = _tmp_3 != 0;
            final int _tmpGroupIndex;
            _tmpGroupIndex = _cursor.getInt(_cursorIndexOfGroupIndex);
            final int _tmpOrderInGroup;
            _tmpOrderInGroup = _cursor.getInt(_cursorIndexOfOrderInGroup);
            _item = new TestQuestionEntity(_tmpId,_tmpSessionId,_tmpVocabularyId,_tmpQuestionType,_tmpPrompt,_tmpContextTranslation,_tmpHighlightText,_tmpOptions,_tmpCorrectIndex,_tmpSelectedIndex,_tmpIsCorrect,_tmpRetryCount,_tmpIsReviewCopy,_tmpGroupIndex,_tmpOrderInGroup);
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
  public Flow<List<TestQuestionEntity>> observeQuestions(final String sessionId) {
    final String _sql = "SELECT * FROM test_questions WHERE sessionId = ? ORDER BY groupIndex, orderInGroup, rowid";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, sessionId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"test_questions"}, new Callable<List<TestQuestionEntity>>() {
      @Override
      @NonNull
      public List<TestQuestionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "sessionId");
          final int _cursorIndexOfVocabularyId = CursorUtil.getColumnIndexOrThrow(_cursor, "vocabularyId");
          final int _cursorIndexOfQuestionType = CursorUtil.getColumnIndexOrThrow(_cursor, "questionType");
          final int _cursorIndexOfPrompt = CursorUtil.getColumnIndexOrThrow(_cursor, "prompt");
          final int _cursorIndexOfContextTranslation = CursorUtil.getColumnIndexOrThrow(_cursor, "contextTranslation");
          final int _cursorIndexOfHighlightText = CursorUtil.getColumnIndexOrThrow(_cursor, "highlightText");
          final int _cursorIndexOfOptions = CursorUtil.getColumnIndexOrThrow(_cursor, "options");
          final int _cursorIndexOfCorrectIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "correctIndex");
          final int _cursorIndexOfSelectedIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "selectedIndex");
          final int _cursorIndexOfIsCorrect = CursorUtil.getColumnIndexOrThrow(_cursor, "isCorrect");
          final int _cursorIndexOfRetryCount = CursorUtil.getColumnIndexOrThrow(_cursor, "retryCount");
          final int _cursorIndexOfIsReviewCopy = CursorUtil.getColumnIndexOrThrow(_cursor, "isReviewCopy");
          final int _cursorIndexOfGroupIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "groupIndex");
          final int _cursorIndexOfOrderInGroup = CursorUtil.getColumnIndexOrThrow(_cursor, "orderInGroup");
          final List<TestQuestionEntity> _result = new ArrayList<TestQuestionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TestQuestionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpSessionId;
            _tmpSessionId = _cursor.getString(_cursorIndexOfSessionId);
            final String _tmpVocabularyId;
            _tmpVocabularyId = _cursor.getString(_cursorIndexOfVocabularyId);
            final QuestionType _tmpQuestionType;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfQuestionType);
            _tmpQuestionType = __converters.stringToQuestionType(_tmp);
            final String _tmpPrompt;
            _tmpPrompt = _cursor.getString(_cursorIndexOfPrompt);
            final String _tmpContextTranslation;
            if (_cursor.isNull(_cursorIndexOfContextTranslation)) {
              _tmpContextTranslation = null;
            } else {
              _tmpContextTranslation = _cursor.getString(_cursorIndexOfContextTranslation);
            }
            final String _tmpHighlightText;
            if (_cursor.isNull(_cursorIndexOfHighlightText)) {
              _tmpHighlightText = null;
            } else {
              _tmpHighlightText = _cursor.getString(_cursorIndexOfHighlightText);
            }
            final List<String> _tmpOptions;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfOptions);
            _tmpOptions = __converters.jsonToStringList(_tmp_1);
            final int _tmpCorrectIndex;
            _tmpCorrectIndex = _cursor.getInt(_cursorIndexOfCorrectIndex);
            final Integer _tmpSelectedIndex;
            if (_cursor.isNull(_cursorIndexOfSelectedIndex)) {
              _tmpSelectedIndex = null;
            } else {
              _tmpSelectedIndex = _cursor.getInt(_cursorIndexOfSelectedIndex);
            }
            final Boolean _tmpIsCorrect;
            final Integer _tmp_2;
            if (_cursor.isNull(_cursorIndexOfIsCorrect)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getInt(_cursorIndexOfIsCorrect);
            }
            _tmpIsCorrect = _tmp_2 == null ? null : _tmp_2 != 0;
            final int _tmpRetryCount;
            _tmpRetryCount = _cursor.getInt(_cursorIndexOfRetryCount);
            final boolean _tmpIsReviewCopy;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsReviewCopy);
            _tmpIsReviewCopy = _tmp_3 != 0;
            final int _tmpGroupIndex;
            _tmpGroupIndex = _cursor.getInt(_cursorIndexOfGroupIndex);
            final int _tmpOrderInGroup;
            _tmpOrderInGroup = _cursor.getInt(_cursorIndexOfOrderInGroup);
            _item = new TestQuestionEntity(_tmpId,_tmpSessionId,_tmpVocabularyId,_tmpQuestionType,_tmpPrompt,_tmpContextTranslation,_tmpHighlightText,_tmpOptions,_tmpCorrectIndex,_tmpSelectedIndex,_tmpIsCorrect,_tmpRetryCount,_tmpIsReviewCopy,_tmpGroupIndex,_tmpOrderInGroup);
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
  public Object questionsInGroup(final String sessionId, final int groupIndex,
      final Continuation<? super List<TestQuestionEntity>> $completion) {
    final String _sql = "SELECT * FROM test_questions WHERE sessionId = ? AND groupIndex = ? ORDER BY orderInGroup, rowid";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, sessionId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, groupIndex);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<TestQuestionEntity>>() {
      @Override
      @NonNull
      public List<TestQuestionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "sessionId");
          final int _cursorIndexOfVocabularyId = CursorUtil.getColumnIndexOrThrow(_cursor, "vocabularyId");
          final int _cursorIndexOfQuestionType = CursorUtil.getColumnIndexOrThrow(_cursor, "questionType");
          final int _cursorIndexOfPrompt = CursorUtil.getColumnIndexOrThrow(_cursor, "prompt");
          final int _cursorIndexOfContextTranslation = CursorUtil.getColumnIndexOrThrow(_cursor, "contextTranslation");
          final int _cursorIndexOfHighlightText = CursorUtil.getColumnIndexOrThrow(_cursor, "highlightText");
          final int _cursorIndexOfOptions = CursorUtil.getColumnIndexOrThrow(_cursor, "options");
          final int _cursorIndexOfCorrectIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "correctIndex");
          final int _cursorIndexOfSelectedIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "selectedIndex");
          final int _cursorIndexOfIsCorrect = CursorUtil.getColumnIndexOrThrow(_cursor, "isCorrect");
          final int _cursorIndexOfRetryCount = CursorUtil.getColumnIndexOrThrow(_cursor, "retryCount");
          final int _cursorIndexOfIsReviewCopy = CursorUtil.getColumnIndexOrThrow(_cursor, "isReviewCopy");
          final int _cursorIndexOfGroupIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "groupIndex");
          final int _cursorIndexOfOrderInGroup = CursorUtil.getColumnIndexOrThrow(_cursor, "orderInGroup");
          final List<TestQuestionEntity> _result = new ArrayList<TestQuestionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TestQuestionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpSessionId;
            _tmpSessionId = _cursor.getString(_cursorIndexOfSessionId);
            final String _tmpVocabularyId;
            _tmpVocabularyId = _cursor.getString(_cursorIndexOfVocabularyId);
            final QuestionType _tmpQuestionType;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfQuestionType);
            _tmpQuestionType = __converters.stringToQuestionType(_tmp);
            final String _tmpPrompt;
            _tmpPrompt = _cursor.getString(_cursorIndexOfPrompt);
            final String _tmpContextTranslation;
            if (_cursor.isNull(_cursorIndexOfContextTranslation)) {
              _tmpContextTranslation = null;
            } else {
              _tmpContextTranslation = _cursor.getString(_cursorIndexOfContextTranslation);
            }
            final String _tmpHighlightText;
            if (_cursor.isNull(_cursorIndexOfHighlightText)) {
              _tmpHighlightText = null;
            } else {
              _tmpHighlightText = _cursor.getString(_cursorIndexOfHighlightText);
            }
            final List<String> _tmpOptions;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfOptions);
            _tmpOptions = __converters.jsonToStringList(_tmp_1);
            final int _tmpCorrectIndex;
            _tmpCorrectIndex = _cursor.getInt(_cursorIndexOfCorrectIndex);
            final Integer _tmpSelectedIndex;
            if (_cursor.isNull(_cursorIndexOfSelectedIndex)) {
              _tmpSelectedIndex = null;
            } else {
              _tmpSelectedIndex = _cursor.getInt(_cursorIndexOfSelectedIndex);
            }
            final Boolean _tmpIsCorrect;
            final Integer _tmp_2;
            if (_cursor.isNull(_cursorIndexOfIsCorrect)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getInt(_cursorIndexOfIsCorrect);
            }
            _tmpIsCorrect = _tmp_2 == null ? null : _tmp_2 != 0;
            final int _tmpRetryCount;
            _tmpRetryCount = _cursor.getInt(_cursorIndexOfRetryCount);
            final boolean _tmpIsReviewCopy;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsReviewCopy);
            _tmpIsReviewCopy = _tmp_3 != 0;
            final int _tmpGroupIndex;
            _tmpGroupIndex = _cursor.getInt(_cursorIndexOfGroupIndex);
            final int _tmpOrderInGroup;
            _tmpOrderInGroup = _cursor.getInt(_cursorIndexOfOrderInGroup);
            _item = new TestQuestionEntity(_tmpId,_tmpSessionId,_tmpVocabularyId,_tmpQuestionType,_tmpPrompt,_tmpContextTranslation,_tmpHighlightText,_tmpOptions,_tmpCorrectIndex,_tmpSelectedIndex,_tmpIsCorrect,_tmpRetryCount,_tmpIsReviewCopy,_tmpGroupIndex,_tmpOrderInGroup);
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
  public Object nextUnanswered(final String sessionId,
      final Continuation<? super TestQuestionEntity> $completion) {
    final String _sql = "SELECT * FROM test_questions WHERE sessionId = ? AND isCorrect IS NULL ORDER BY groupIndex, orderInGroup, rowid LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, sessionId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<TestQuestionEntity>() {
      @Override
      @Nullable
      public TestQuestionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "sessionId");
          final int _cursorIndexOfVocabularyId = CursorUtil.getColumnIndexOrThrow(_cursor, "vocabularyId");
          final int _cursorIndexOfQuestionType = CursorUtil.getColumnIndexOrThrow(_cursor, "questionType");
          final int _cursorIndexOfPrompt = CursorUtil.getColumnIndexOrThrow(_cursor, "prompt");
          final int _cursorIndexOfContextTranslation = CursorUtil.getColumnIndexOrThrow(_cursor, "contextTranslation");
          final int _cursorIndexOfHighlightText = CursorUtil.getColumnIndexOrThrow(_cursor, "highlightText");
          final int _cursorIndexOfOptions = CursorUtil.getColumnIndexOrThrow(_cursor, "options");
          final int _cursorIndexOfCorrectIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "correctIndex");
          final int _cursorIndexOfSelectedIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "selectedIndex");
          final int _cursorIndexOfIsCorrect = CursorUtil.getColumnIndexOrThrow(_cursor, "isCorrect");
          final int _cursorIndexOfRetryCount = CursorUtil.getColumnIndexOrThrow(_cursor, "retryCount");
          final int _cursorIndexOfIsReviewCopy = CursorUtil.getColumnIndexOrThrow(_cursor, "isReviewCopy");
          final int _cursorIndexOfGroupIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "groupIndex");
          final int _cursorIndexOfOrderInGroup = CursorUtil.getColumnIndexOrThrow(_cursor, "orderInGroup");
          final TestQuestionEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpSessionId;
            _tmpSessionId = _cursor.getString(_cursorIndexOfSessionId);
            final String _tmpVocabularyId;
            _tmpVocabularyId = _cursor.getString(_cursorIndexOfVocabularyId);
            final QuestionType _tmpQuestionType;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfQuestionType);
            _tmpQuestionType = __converters.stringToQuestionType(_tmp);
            final String _tmpPrompt;
            _tmpPrompt = _cursor.getString(_cursorIndexOfPrompt);
            final String _tmpContextTranslation;
            if (_cursor.isNull(_cursorIndexOfContextTranslation)) {
              _tmpContextTranslation = null;
            } else {
              _tmpContextTranslation = _cursor.getString(_cursorIndexOfContextTranslation);
            }
            final String _tmpHighlightText;
            if (_cursor.isNull(_cursorIndexOfHighlightText)) {
              _tmpHighlightText = null;
            } else {
              _tmpHighlightText = _cursor.getString(_cursorIndexOfHighlightText);
            }
            final List<String> _tmpOptions;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfOptions);
            _tmpOptions = __converters.jsonToStringList(_tmp_1);
            final int _tmpCorrectIndex;
            _tmpCorrectIndex = _cursor.getInt(_cursorIndexOfCorrectIndex);
            final Integer _tmpSelectedIndex;
            if (_cursor.isNull(_cursorIndexOfSelectedIndex)) {
              _tmpSelectedIndex = null;
            } else {
              _tmpSelectedIndex = _cursor.getInt(_cursorIndexOfSelectedIndex);
            }
            final Boolean _tmpIsCorrect;
            final Integer _tmp_2;
            if (_cursor.isNull(_cursorIndexOfIsCorrect)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getInt(_cursorIndexOfIsCorrect);
            }
            _tmpIsCorrect = _tmp_2 == null ? null : _tmp_2 != 0;
            final int _tmpRetryCount;
            _tmpRetryCount = _cursor.getInt(_cursorIndexOfRetryCount);
            final boolean _tmpIsReviewCopy;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsReviewCopy);
            _tmpIsReviewCopy = _tmp_3 != 0;
            final int _tmpGroupIndex;
            _tmpGroupIndex = _cursor.getInt(_cursorIndexOfGroupIndex);
            final int _tmpOrderInGroup;
            _tmpOrderInGroup = _cursor.getInt(_cursorIndexOfOrderInGroup);
            _result = new TestQuestionEntity(_tmpId,_tmpSessionId,_tmpVocabularyId,_tmpQuestionType,_tmpPrompt,_tmpContextTranslation,_tmpHighlightText,_tmpOptions,_tmpCorrectIndex,_tmpSelectedIndex,_tmpIsCorrect,_tmpRetryCount,_tmpIsReviewCopy,_tmpGroupIndex,_tmpOrderInGroup);
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
  public Object unansweredCount(final String sessionId,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM test_questions WHERE sessionId = ? AND isCorrect IS NULL";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, sessionId);
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
  public Object questionCount(final String sessionId,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM test_questions WHERE sessionId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, sessionId);
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
  public Object correctCount(final String sessionId,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM test_questions WHERE sessionId = ? AND isCorrect = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, sessionId);
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
  public Object incorrectCount(final String sessionId,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM test_questions WHERE sessionId = ? AND isCorrect = 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, sessionId);
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
  public Object firstUnansweredGroup(final String sessionId,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT MIN(groupIndex) FROM test_questions WHERE sessionId = ? AND isCorrect IS NULL";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, sessionId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @Nullable
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final Integer _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getInt(0);
            }
            _result = _tmp;
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
  public Object maxGroup(final String sessionId, final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT MAX(groupIndex) FROM test_questions WHERE sessionId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, sessionId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @Nullable
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final Integer _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getInt(0);
            }
            _result = _tmp;
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
  public Flow<List<TestSessionEntity>> observeInProgress(final String language,
      final VocabBook book) {
    final String _sql = "SELECT * FROM test_sessions WHERE languageCode = ? AND book = ? AND status = 'IN_PROGRESS' ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, language);
    _argIndex = 2;
    final String _tmp = __converters.vocabBookToString(book);
    _statement.bindString(_argIndex, _tmp);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"test_sessions"}, new Callable<List<TestSessionEntity>>() {
      @Override
      @NonNull
      public List<TestSessionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLanguageCode = CursorUtil.getColumnIndexOrThrow(_cursor, "languageCode");
          final int _cursorIndexOfBook = CursorUtil.getColumnIndexOrThrow(_cursor, "book");
          final int _cursorIndexOfTestType = CursorUtil.getColumnIndexOrThrow(_cursor, "testType");
          final int _cursorIndexOfSessionDate = CursorUtil.getColumnIndexOrThrow(_cursor, "sessionDate");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfTotalVocabularyItems = CursorUtil.getColumnIndexOrThrow(_cursor, "totalVocabularyItems");
          final int _cursorIndexOfCurrentGroup = CursorUtil.getColumnIndexOrThrow(_cursor, "currentGroup");
          final int _cursorIndexOfCurrentQuestion = CursorUtil.getColumnIndexOrThrow(_cursor, "currentQuestion");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfScorePercent = CursorUtil.getColumnIndexOrThrow(_cursor, "scorePercent");
          final int _cursorIndexOfPassed = CursorUtil.getColumnIndexOrThrow(_cursor, "passed");
          final int _cursorIndexOfDailyTaskId = CursorUtil.getColumnIndexOrThrow(_cursor, "dailyTaskId");
          final int _cursorIndexOfLearningSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "learningSessionId");
          final int _cursorIndexOfVocabularyIds = CursorUtil.getColumnIndexOrThrow(_cursor, "vocabularyIds");
          final int _cursorIndexOfFirstAttemptCorrect = CursorUtil.getColumnIndexOrThrow(_cursor, "firstAttemptCorrect");
          final int _cursorIndexOfFirstAttemptTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "firstAttemptTotal");
          final int _cursorIndexOfPassingThreshold = CursorUtil.getColumnIndexOrThrow(_cursor, "passingThreshold");
          final List<TestSessionEntity> _result = new ArrayList<TestSessionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TestSessionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpLanguageCode;
            _tmpLanguageCode = _cursor.getString(_cursorIndexOfLanguageCode);
            final VocabBook _tmpBook;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfBook);
            _tmpBook = __converters.stringToVocabBook(_tmp_1);
            final TestType _tmpTestType;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfTestType);
            _tmpTestType = __converters.stringToTestType(_tmp_2);
            final String _tmpSessionDate;
            _tmpSessionDate = _cursor.getString(_cursorIndexOfSessionDate);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final int _tmpTotalVocabularyItems;
            _tmpTotalVocabularyItems = _cursor.getInt(_cursorIndexOfTotalVocabularyItems);
            final int _tmpCurrentGroup;
            _tmpCurrentGroup = _cursor.getInt(_cursorIndexOfCurrentGroup);
            final int _tmpCurrentQuestion;
            _tmpCurrentQuestion = _cursor.getInt(_cursorIndexOfCurrentQuestion);
            final TestStatus _tmpStatus;
            final String _tmp_3;
            _tmp_3 = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.stringToTestStatus(_tmp_3);
            final Double _tmpScorePercent;
            if (_cursor.isNull(_cursorIndexOfScorePercent)) {
              _tmpScorePercent = null;
            } else {
              _tmpScorePercent = _cursor.getDouble(_cursorIndexOfScorePercent);
            }
            final Boolean _tmpPassed;
            final Integer _tmp_4;
            if (_cursor.isNull(_cursorIndexOfPassed)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getInt(_cursorIndexOfPassed);
            }
            _tmpPassed = _tmp_4 == null ? null : _tmp_4 != 0;
            final String _tmpDailyTaskId;
            if (_cursor.isNull(_cursorIndexOfDailyTaskId)) {
              _tmpDailyTaskId = null;
            } else {
              _tmpDailyTaskId = _cursor.getString(_cursorIndexOfDailyTaskId);
            }
            final String _tmpLearningSessionId;
            if (_cursor.isNull(_cursorIndexOfLearningSessionId)) {
              _tmpLearningSessionId = null;
            } else {
              _tmpLearningSessionId = _cursor.getString(_cursorIndexOfLearningSessionId);
            }
            final List<String> _tmpVocabularyIds;
            final String _tmp_5;
            _tmp_5 = _cursor.getString(_cursorIndexOfVocabularyIds);
            _tmpVocabularyIds = __converters.jsonToStringList(_tmp_5);
            final int _tmpFirstAttemptCorrect;
            _tmpFirstAttemptCorrect = _cursor.getInt(_cursorIndexOfFirstAttemptCorrect);
            final int _tmpFirstAttemptTotal;
            _tmpFirstAttemptTotal = _cursor.getInt(_cursorIndexOfFirstAttemptTotal);
            final Integer _tmpPassingThreshold;
            if (_cursor.isNull(_cursorIndexOfPassingThreshold)) {
              _tmpPassingThreshold = null;
            } else {
              _tmpPassingThreshold = _cursor.getInt(_cursorIndexOfPassingThreshold);
            }
            _item = new TestSessionEntity(_tmpId,_tmpLanguageCode,_tmpBook,_tmpTestType,_tmpSessionDate,_tmpCreatedAt,_tmpCompletedAt,_tmpTotalVocabularyItems,_tmpCurrentGroup,_tmpCurrentQuestion,_tmpStatus,_tmpScorePercent,_tmpPassed,_tmpDailyTaskId,_tmpLearningSessionId,_tmpVocabularyIds,_tmpFirstAttemptCorrect,_tmpFirstAttemptTotal,_tmpPassingThreshold);
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
  public Object inProgress(final String language, final VocabBook book,
      final Continuation<? super List<TestSessionEntity>> $completion) {
    final String _sql = "SELECT * FROM test_sessions WHERE languageCode = ? AND book = ? AND status = 'IN_PROGRESS' ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, language);
    _argIndex = 2;
    final String _tmp = __converters.vocabBookToString(book);
    _statement.bindString(_argIndex, _tmp);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<TestSessionEntity>>() {
      @Override
      @NonNull
      public List<TestSessionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLanguageCode = CursorUtil.getColumnIndexOrThrow(_cursor, "languageCode");
          final int _cursorIndexOfBook = CursorUtil.getColumnIndexOrThrow(_cursor, "book");
          final int _cursorIndexOfTestType = CursorUtil.getColumnIndexOrThrow(_cursor, "testType");
          final int _cursorIndexOfSessionDate = CursorUtil.getColumnIndexOrThrow(_cursor, "sessionDate");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfTotalVocabularyItems = CursorUtil.getColumnIndexOrThrow(_cursor, "totalVocabularyItems");
          final int _cursorIndexOfCurrentGroup = CursorUtil.getColumnIndexOrThrow(_cursor, "currentGroup");
          final int _cursorIndexOfCurrentQuestion = CursorUtil.getColumnIndexOrThrow(_cursor, "currentQuestion");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfScorePercent = CursorUtil.getColumnIndexOrThrow(_cursor, "scorePercent");
          final int _cursorIndexOfPassed = CursorUtil.getColumnIndexOrThrow(_cursor, "passed");
          final int _cursorIndexOfDailyTaskId = CursorUtil.getColumnIndexOrThrow(_cursor, "dailyTaskId");
          final int _cursorIndexOfLearningSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "learningSessionId");
          final int _cursorIndexOfVocabularyIds = CursorUtil.getColumnIndexOrThrow(_cursor, "vocabularyIds");
          final int _cursorIndexOfFirstAttemptCorrect = CursorUtil.getColumnIndexOrThrow(_cursor, "firstAttemptCorrect");
          final int _cursorIndexOfFirstAttemptTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "firstAttemptTotal");
          final int _cursorIndexOfPassingThreshold = CursorUtil.getColumnIndexOrThrow(_cursor, "passingThreshold");
          final List<TestSessionEntity> _result = new ArrayList<TestSessionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TestSessionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpLanguageCode;
            _tmpLanguageCode = _cursor.getString(_cursorIndexOfLanguageCode);
            final VocabBook _tmpBook;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfBook);
            _tmpBook = __converters.stringToVocabBook(_tmp_1);
            final TestType _tmpTestType;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfTestType);
            _tmpTestType = __converters.stringToTestType(_tmp_2);
            final String _tmpSessionDate;
            _tmpSessionDate = _cursor.getString(_cursorIndexOfSessionDate);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final int _tmpTotalVocabularyItems;
            _tmpTotalVocabularyItems = _cursor.getInt(_cursorIndexOfTotalVocabularyItems);
            final int _tmpCurrentGroup;
            _tmpCurrentGroup = _cursor.getInt(_cursorIndexOfCurrentGroup);
            final int _tmpCurrentQuestion;
            _tmpCurrentQuestion = _cursor.getInt(_cursorIndexOfCurrentQuestion);
            final TestStatus _tmpStatus;
            final String _tmp_3;
            _tmp_3 = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.stringToTestStatus(_tmp_3);
            final Double _tmpScorePercent;
            if (_cursor.isNull(_cursorIndexOfScorePercent)) {
              _tmpScorePercent = null;
            } else {
              _tmpScorePercent = _cursor.getDouble(_cursorIndexOfScorePercent);
            }
            final Boolean _tmpPassed;
            final Integer _tmp_4;
            if (_cursor.isNull(_cursorIndexOfPassed)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getInt(_cursorIndexOfPassed);
            }
            _tmpPassed = _tmp_4 == null ? null : _tmp_4 != 0;
            final String _tmpDailyTaskId;
            if (_cursor.isNull(_cursorIndexOfDailyTaskId)) {
              _tmpDailyTaskId = null;
            } else {
              _tmpDailyTaskId = _cursor.getString(_cursorIndexOfDailyTaskId);
            }
            final String _tmpLearningSessionId;
            if (_cursor.isNull(_cursorIndexOfLearningSessionId)) {
              _tmpLearningSessionId = null;
            } else {
              _tmpLearningSessionId = _cursor.getString(_cursorIndexOfLearningSessionId);
            }
            final List<String> _tmpVocabularyIds;
            final String _tmp_5;
            _tmp_5 = _cursor.getString(_cursorIndexOfVocabularyIds);
            _tmpVocabularyIds = __converters.jsonToStringList(_tmp_5);
            final int _tmpFirstAttemptCorrect;
            _tmpFirstAttemptCorrect = _cursor.getInt(_cursorIndexOfFirstAttemptCorrect);
            final int _tmpFirstAttemptTotal;
            _tmpFirstAttemptTotal = _cursor.getInt(_cursorIndexOfFirstAttemptTotal);
            final Integer _tmpPassingThreshold;
            if (_cursor.isNull(_cursorIndexOfPassingThreshold)) {
              _tmpPassingThreshold = null;
            } else {
              _tmpPassingThreshold = _cursor.getInt(_cursorIndexOfPassingThreshold);
            }
            _item = new TestSessionEntity(_tmpId,_tmpLanguageCode,_tmpBook,_tmpTestType,_tmpSessionDate,_tmpCreatedAt,_tmpCompletedAt,_tmpTotalVocabularyItems,_tmpCurrentGroup,_tmpCurrentQuestion,_tmpStatus,_tmpScorePercent,_tmpPassed,_tmpDailyTaskId,_tmpLearningSessionId,_tmpVocabularyIds,_tmpFirstAttemptCorrect,_tmpFirstAttemptTotal,_tmpPassingThreshold);
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
  public Object inProgressForDate(final String date,
      final Continuation<? super List<TestSessionEntity>> $completion) {
    final String _sql = "SELECT * FROM test_sessions WHERE sessionDate = ? AND status = 'IN_PROGRESS'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, date);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<TestSessionEntity>>() {
      @Override
      @NonNull
      public List<TestSessionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLanguageCode = CursorUtil.getColumnIndexOrThrow(_cursor, "languageCode");
          final int _cursorIndexOfBook = CursorUtil.getColumnIndexOrThrow(_cursor, "book");
          final int _cursorIndexOfTestType = CursorUtil.getColumnIndexOrThrow(_cursor, "testType");
          final int _cursorIndexOfSessionDate = CursorUtil.getColumnIndexOrThrow(_cursor, "sessionDate");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfTotalVocabularyItems = CursorUtil.getColumnIndexOrThrow(_cursor, "totalVocabularyItems");
          final int _cursorIndexOfCurrentGroup = CursorUtil.getColumnIndexOrThrow(_cursor, "currentGroup");
          final int _cursorIndexOfCurrentQuestion = CursorUtil.getColumnIndexOrThrow(_cursor, "currentQuestion");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfScorePercent = CursorUtil.getColumnIndexOrThrow(_cursor, "scorePercent");
          final int _cursorIndexOfPassed = CursorUtil.getColumnIndexOrThrow(_cursor, "passed");
          final int _cursorIndexOfDailyTaskId = CursorUtil.getColumnIndexOrThrow(_cursor, "dailyTaskId");
          final int _cursorIndexOfLearningSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "learningSessionId");
          final int _cursorIndexOfVocabularyIds = CursorUtil.getColumnIndexOrThrow(_cursor, "vocabularyIds");
          final int _cursorIndexOfFirstAttemptCorrect = CursorUtil.getColumnIndexOrThrow(_cursor, "firstAttemptCorrect");
          final int _cursorIndexOfFirstAttemptTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "firstAttemptTotal");
          final int _cursorIndexOfPassingThreshold = CursorUtil.getColumnIndexOrThrow(_cursor, "passingThreshold");
          final List<TestSessionEntity> _result = new ArrayList<TestSessionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TestSessionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpLanguageCode;
            _tmpLanguageCode = _cursor.getString(_cursorIndexOfLanguageCode);
            final VocabBook _tmpBook;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfBook);
            _tmpBook = __converters.stringToVocabBook(_tmp);
            final TestType _tmpTestType;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfTestType);
            _tmpTestType = __converters.stringToTestType(_tmp_1);
            final String _tmpSessionDate;
            _tmpSessionDate = _cursor.getString(_cursorIndexOfSessionDate);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final int _tmpTotalVocabularyItems;
            _tmpTotalVocabularyItems = _cursor.getInt(_cursorIndexOfTotalVocabularyItems);
            final int _tmpCurrentGroup;
            _tmpCurrentGroup = _cursor.getInt(_cursorIndexOfCurrentGroup);
            final int _tmpCurrentQuestion;
            _tmpCurrentQuestion = _cursor.getInt(_cursorIndexOfCurrentQuestion);
            final TestStatus _tmpStatus;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.stringToTestStatus(_tmp_2);
            final Double _tmpScorePercent;
            if (_cursor.isNull(_cursorIndexOfScorePercent)) {
              _tmpScorePercent = null;
            } else {
              _tmpScorePercent = _cursor.getDouble(_cursorIndexOfScorePercent);
            }
            final Boolean _tmpPassed;
            final Integer _tmp_3;
            if (_cursor.isNull(_cursorIndexOfPassed)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getInt(_cursorIndexOfPassed);
            }
            _tmpPassed = _tmp_3 == null ? null : _tmp_3 != 0;
            final String _tmpDailyTaskId;
            if (_cursor.isNull(_cursorIndexOfDailyTaskId)) {
              _tmpDailyTaskId = null;
            } else {
              _tmpDailyTaskId = _cursor.getString(_cursorIndexOfDailyTaskId);
            }
            final String _tmpLearningSessionId;
            if (_cursor.isNull(_cursorIndexOfLearningSessionId)) {
              _tmpLearningSessionId = null;
            } else {
              _tmpLearningSessionId = _cursor.getString(_cursorIndexOfLearningSessionId);
            }
            final List<String> _tmpVocabularyIds;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfVocabularyIds);
            _tmpVocabularyIds = __converters.jsonToStringList(_tmp_4);
            final int _tmpFirstAttemptCorrect;
            _tmpFirstAttemptCorrect = _cursor.getInt(_cursorIndexOfFirstAttemptCorrect);
            final int _tmpFirstAttemptTotal;
            _tmpFirstAttemptTotal = _cursor.getInt(_cursorIndexOfFirstAttemptTotal);
            final Integer _tmpPassingThreshold;
            if (_cursor.isNull(_cursorIndexOfPassingThreshold)) {
              _tmpPassingThreshold = null;
            } else {
              _tmpPassingThreshold = _cursor.getInt(_cursorIndexOfPassingThreshold);
            }
            _item = new TestSessionEntity(_tmpId,_tmpLanguageCode,_tmpBook,_tmpTestType,_tmpSessionDate,_tmpCreatedAt,_tmpCompletedAt,_tmpTotalVocabularyItems,_tmpCurrentGroup,_tmpCurrentQuestion,_tmpStatus,_tmpScorePercent,_tmpPassed,_tmpDailyTaskId,_tmpLearningSessionId,_tmpVocabularyIds,_tmpFirstAttemptCorrect,_tmpFirstAttemptTotal,_tmpPassingThreshold);
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
  public Flow<List<TestSessionEntity>> observeHistory(final String language, final VocabBook book) {
    final String _sql = "\n"
            + "        SELECT * FROM test_sessions\n"
            + "        WHERE languageCode = ? AND book = ? AND testType != 'GROUP_QUIZ'\n"
            + "        ORDER BY createdAt DESC\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, language);
    _argIndex = 2;
    final String _tmp = __converters.vocabBookToString(book);
    _statement.bindString(_argIndex, _tmp);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"test_sessions"}, new Callable<List<TestSessionEntity>>() {
      @Override
      @NonNull
      public List<TestSessionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLanguageCode = CursorUtil.getColumnIndexOrThrow(_cursor, "languageCode");
          final int _cursorIndexOfBook = CursorUtil.getColumnIndexOrThrow(_cursor, "book");
          final int _cursorIndexOfTestType = CursorUtil.getColumnIndexOrThrow(_cursor, "testType");
          final int _cursorIndexOfSessionDate = CursorUtil.getColumnIndexOrThrow(_cursor, "sessionDate");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfTotalVocabularyItems = CursorUtil.getColumnIndexOrThrow(_cursor, "totalVocabularyItems");
          final int _cursorIndexOfCurrentGroup = CursorUtil.getColumnIndexOrThrow(_cursor, "currentGroup");
          final int _cursorIndexOfCurrentQuestion = CursorUtil.getColumnIndexOrThrow(_cursor, "currentQuestion");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfScorePercent = CursorUtil.getColumnIndexOrThrow(_cursor, "scorePercent");
          final int _cursorIndexOfPassed = CursorUtil.getColumnIndexOrThrow(_cursor, "passed");
          final int _cursorIndexOfDailyTaskId = CursorUtil.getColumnIndexOrThrow(_cursor, "dailyTaskId");
          final int _cursorIndexOfLearningSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "learningSessionId");
          final int _cursorIndexOfVocabularyIds = CursorUtil.getColumnIndexOrThrow(_cursor, "vocabularyIds");
          final int _cursorIndexOfFirstAttemptCorrect = CursorUtil.getColumnIndexOrThrow(_cursor, "firstAttemptCorrect");
          final int _cursorIndexOfFirstAttemptTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "firstAttemptTotal");
          final int _cursorIndexOfPassingThreshold = CursorUtil.getColumnIndexOrThrow(_cursor, "passingThreshold");
          final List<TestSessionEntity> _result = new ArrayList<TestSessionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TestSessionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpLanguageCode;
            _tmpLanguageCode = _cursor.getString(_cursorIndexOfLanguageCode);
            final VocabBook _tmpBook;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfBook);
            _tmpBook = __converters.stringToVocabBook(_tmp_1);
            final TestType _tmpTestType;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfTestType);
            _tmpTestType = __converters.stringToTestType(_tmp_2);
            final String _tmpSessionDate;
            _tmpSessionDate = _cursor.getString(_cursorIndexOfSessionDate);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final int _tmpTotalVocabularyItems;
            _tmpTotalVocabularyItems = _cursor.getInt(_cursorIndexOfTotalVocabularyItems);
            final int _tmpCurrentGroup;
            _tmpCurrentGroup = _cursor.getInt(_cursorIndexOfCurrentGroup);
            final int _tmpCurrentQuestion;
            _tmpCurrentQuestion = _cursor.getInt(_cursorIndexOfCurrentQuestion);
            final TestStatus _tmpStatus;
            final String _tmp_3;
            _tmp_3 = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.stringToTestStatus(_tmp_3);
            final Double _tmpScorePercent;
            if (_cursor.isNull(_cursorIndexOfScorePercent)) {
              _tmpScorePercent = null;
            } else {
              _tmpScorePercent = _cursor.getDouble(_cursorIndexOfScorePercent);
            }
            final Boolean _tmpPassed;
            final Integer _tmp_4;
            if (_cursor.isNull(_cursorIndexOfPassed)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getInt(_cursorIndexOfPassed);
            }
            _tmpPassed = _tmp_4 == null ? null : _tmp_4 != 0;
            final String _tmpDailyTaskId;
            if (_cursor.isNull(_cursorIndexOfDailyTaskId)) {
              _tmpDailyTaskId = null;
            } else {
              _tmpDailyTaskId = _cursor.getString(_cursorIndexOfDailyTaskId);
            }
            final String _tmpLearningSessionId;
            if (_cursor.isNull(_cursorIndexOfLearningSessionId)) {
              _tmpLearningSessionId = null;
            } else {
              _tmpLearningSessionId = _cursor.getString(_cursorIndexOfLearningSessionId);
            }
            final List<String> _tmpVocabularyIds;
            final String _tmp_5;
            _tmp_5 = _cursor.getString(_cursorIndexOfVocabularyIds);
            _tmpVocabularyIds = __converters.jsonToStringList(_tmp_5);
            final int _tmpFirstAttemptCorrect;
            _tmpFirstAttemptCorrect = _cursor.getInt(_cursorIndexOfFirstAttemptCorrect);
            final int _tmpFirstAttemptTotal;
            _tmpFirstAttemptTotal = _cursor.getInt(_cursorIndexOfFirstAttemptTotal);
            final Integer _tmpPassingThreshold;
            if (_cursor.isNull(_cursorIndexOfPassingThreshold)) {
              _tmpPassingThreshold = null;
            } else {
              _tmpPassingThreshold = _cursor.getInt(_cursorIndexOfPassingThreshold);
            }
            _item = new TestSessionEntity(_tmpId,_tmpLanguageCode,_tmpBook,_tmpTestType,_tmpSessionDate,_tmpCreatedAt,_tmpCompletedAt,_tmpTotalVocabularyItems,_tmpCurrentGroup,_tmpCurrentQuestion,_tmpStatus,_tmpScorePercent,_tmpPassed,_tmpDailyTaskId,_tmpLearningSessionId,_tmpVocabularyIds,_tmpFirstAttemptCorrect,_tmpFirstAttemptTotal,_tmpPassingThreshold);
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
  public Object latestSession(final String language, final VocabBook book, final String testType,
      final String date, final Continuation<? super TestSessionEntity> $completion) {
    final String _sql = "SELECT * FROM test_sessions WHERE languageCode = ? AND book = ? AND testType = ? AND sessionDate = ? ORDER BY createdAt DESC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 4);
    int _argIndex = 1;
    _statement.bindString(_argIndex, language);
    _argIndex = 2;
    final String _tmp = __converters.vocabBookToString(book);
    _statement.bindString(_argIndex, _tmp);
    _argIndex = 3;
    _statement.bindString(_argIndex, testType);
    _argIndex = 4;
    _statement.bindString(_argIndex, date);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<TestSessionEntity>() {
      @Override
      @Nullable
      public TestSessionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLanguageCode = CursorUtil.getColumnIndexOrThrow(_cursor, "languageCode");
          final int _cursorIndexOfBook = CursorUtil.getColumnIndexOrThrow(_cursor, "book");
          final int _cursorIndexOfTestType = CursorUtil.getColumnIndexOrThrow(_cursor, "testType");
          final int _cursorIndexOfSessionDate = CursorUtil.getColumnIndexOrThrow(_cursor, "sessionDate");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfTotalVocabularyItems = CursorUtil.getColumnIndexOrThrow(_cursor, "totalVocabularyItems");
          final int _cursorIndexOfCurrentGroup = CursorUtil.getColumnIndexOrThrow(_cursor, "currentGroup");
          final int _cursorIndexOfCurrentQuestion = CursorUtil.getColumnIndexOrThrow(_cursor, "currentQuestion");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfScorePercent = CursorUtil.getColumnIndexOrThrow(_cursor, "scorePercent");
          final int _cursorIndexOfPassed = CursorUtil.getColumnIndexOrThrow(_cursor, "passed");
          final int _cursorIndexOfDailyTaskId = CursorUtil.getColumnIndexOrThrow(_cursor, "dailyTaskId");
          final int _cursorIndexOfLearningSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "learningSessionId");
          final int _cursorIndexOfVocabularyIds = CursorUtil.getColumnIndexOrThrow(_cursor, "vocabularyIds");
          final int _cursorIndexOfFirstAttemptCorrect = CursorUtil.getColumnIndexOrThrow(_cursor, "firstAttemptCorrect");
          final int _cursorIndexOfFirstAttemptTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "firstAttemptTotal");
          final int _cursorIndexOfPassingThreshold = CursorUtil.getColumnIndexOrThrow(_cursor, "passingThreshold");
          final TestSessionEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpLanguageCode;
            _tmpLanguageCode = _cursor.getString(_cursorIndexOfLanguageCode);
            final VocabBook _tmpBook;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfBook);
            _tmpBook = __converters.stringToVocabBook(_tmp_1);
            final TestType _tmpTestType;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfTestType);
            _tmpTestType = __converters.stringToTestType(_tmp_2);
            final String _tmpSessionDate;
            _tmpSessionDate = _cursor.getString(_cursorIndexOfSessionDate);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final int _tmpTotalVocabularyItems;
            _tmpTotalVocabularyItems = _cursor.getInt(_cursorIndexOfTotalVocabularyItems);
            final int _tmpCurrentGroup;
            _tmpCurrentGroup = _cursor.getInt(_cursorIndexOfCurrentGroup);
            final int _tmpCurrentQuestion;
            _tmpCurrentQuestion = _cursor.getInt(_cursorIndexOfCurrentQuestion);
            final TestStatus _tmpStatus;
            final String _tmp_3;
            _tmp_3 = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.stringToTestStatus(_tmp_3);
            final Double _tmpScorePercent;
            if (_cursor.isNull(_cursorIndexOfScorePercent)) {
              _tmpScorePercent = null;
            } else {
              _tmpScorePercent = _cursor.getDouble(_cursorIndexOfScorePercent);
            }
            final Boolean _tmpPassed;
            final Integer _tmp_4;
            if (_cursor.isNull(_cursorIndexOfPassed)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getInt(_cursorIndexOfPassed);
            }
            _tmpPassed = _tmp_4 == null ? null : _tmp_4 != 0;
            final String _tmpDailyTaskId;
            if (_cursor.isNull(_cursorIndexOfDailyTaskId)) {
              _tmpDailyTaskId = null;
            } else {
              _tmpDailyTaskId = _cursor.getString(_cursorIndexOfDailyTaskId);
            }
            final String _tmpLearningSessionId;
            if (_cursor.isNull(_cursorIndexOfLearningSessionId)) {
              _tmpLearningSessionId = null;
            } else {
              _tmpLearningSessionId = _cursor.getString(_cursorIndexOfLearningSessionId);
            }
            final List<String> _tmpVocabularyIds;
            final String _tmp_5;
            _tmp_5 = _cursor.getString(_cursorIndexOfVocabularyIds);
            _tmpVocabularyIds = __converters.jsonToStringList(_tmp_5);
            final int _tmpFirstAttemptCorrect;
            _tmpFirstAttemptCorrect = _cursor.getInt(_cursorIndexOfFirstAttemptCorrect);
            final int _tmpFirstAttemptTotal;
            _tmpFirstAttemptTotal = _cursor.getInt(_cursorIndexOfFirstAttemptTotal);
            final Integer _tmpPassingThreshold;
            if (_cursor.isNull(_cursorIndexOfPassingThreshold)) {
              _tmpPassingThreshold = null;
            } else {
              _tmpPassingThreshold = _cursor.getInt(_cursorIndexOfPassingThreshold);
            }
            _result = new TestSessionEntity(_tmpId,_tmpLanguageCode,_tmpBook,_tmpTestType,_tmpSessionDate,_tmpCreatedAt,_tmpCompletedAt,_tmpTotalVocabularyItems,_tmpCurrentGroup,_tmpCurrentQuestion,_tmpStatus,_tmpScorePercent,_tmpPassed,_tmpDailyTaskId,_tmpLearningSessionId,_tmpVocabularyIds,_tmpFirstAttemptCorrect,_tmpFirstAttemptTotal,_tmpPassingThreshold);
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
  public Object latestForTask(final String dailyTaskId, final String testType,
      final Continuation<? super TestSessionEntity> $completion) {
    final String _sql = "SELECT * FROM test_sessions WHERE dailyTaskId = ? AND testType = ? ORDER BY createdAt DESC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, dailyTaskId);
    _argIndex = 2;
    _statement.bindString(_argIndex, testType);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<TestSessionEntity>() {
      @Override
      @Nullable
      public TestSessionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLanguageCode = CursorUtil.getColumnIndexOrThrow(_cursor, "languageCode");
          final int _cursorIndexOfBook = CursorUtil.getColumnIndexOrThrow(_cursor, "book");
          final int _cursorIndexOfTestType = CursorUtil.getColumnIndexOrThrow(_cursor, "testType");
          final int _cursorIndexOfSessionDate = CursorUtil.getColumnIndexOrThrow(_cursor, "sessionDate");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfTotalVocabularyItems = CursorUtil.getColumnIndexOrThrow(_cursor, "totalVocabularyItems");
          final int _cursorIndexOfCurrentGroup = CursorUtil.getColumnIndexOrThrow(_cursor, "currentGroup");
          final int _cursorIndexOfCurrentQuestion = CursorUtil.getColumnIndexOrThrow(_cursor, "currentQuestion");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfScorePercent = CursorUtil.getColumnIndexOrThrow(_cursor, "scorePercent");
          final int _cursorIndexOfPassed = CursorUtil.getColumnIndexOrThrow(_cursor, "passed");
          final int _cursorIndexOfDailyTaskId = CursorUtil.getColumnIndexOrThrow(_cursor, "dailyTaskId");
          final int _cursorIndexOfLearningSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "learningSessionId");
          final int _cursorIndexOfVocabularyIds = CursorUtil.getColumnIndexOrThrow(_cursor, "vocabularyIds");
          final int _cursorIndexOfFirstAttemptCorrect = CursorUtil.getColumnIndexOrThrow(_cursor, "firstAttemptCorrect");
          final int _cursorIndexOfFirstAttemptTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "firstAttemptTotal");
          final int _cursorIndexOfPassingThreshold = CursorUtil.getColumnIndexOrThrow(_cursor, "passingThreshold");
          final TestSessionEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpLanguageCode;
            _tmpLanguageCode = _cursor.getString(_cursorIndexOfLanguageCode);
            final VocabBook _tmpBook;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfBook);
            _tmpBook = __converters.stringToVocabBook(_tmp);
            final TestType _tmpTestType;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfTestType);
            _tmpTestType = __converters.stringToTestType(_tmp_1);
            final String _tmpSessionDate;
            _tmpSessionDate = _cursor.getString(_cursorIndexOfSessionDate);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final int _tmpTotalVocabularyItems;
            _tmpTotalVocabularyItems = _cursor.getInt(_cursorIndexOfTotalVocabularyItems);
            final int _tmpCurrentGroup;
            _tmpCurrentGroup = _cursor.getInt(_cursorIndexOfCurrentGroup);
            final int _tmpCurrentQuestion;
            _tmpCurrentQuestion = _cursor.getInt(_cursorIndexOfCurrentQuestion);
            final TestStatus _tmpStatus;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.stringToTestStatus(_tmp_2);
            final Double _tmpScorePercent;
            if (_cursor.isNull(_cursorIndexOfScorePercent)) {
              _tmpScorePercent = null;
            } else {
              _tmpScorePercent = _cursor.getDouble(_cursorIndexOfScorePercent);
            }
            final Boolean _tmpPassed;
            final Integer _tmp_3;
            if (_cursor.isNull(_cursorIndexOfPassed)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getInt(_cursorIndexOfPassed);
            }
            _tmpPassed = _tmp_3 == null ? null : _tmp_3 != 0;
            final String _tmpDailyTaskId;
            if (_cursor.isNull(_cursorIndexOfDailyTaskId)) {
              _tmpDailyTaskId = null;
            } else {
              _tmpDailyTaskId = _cursor.getString(_cursorIndexOfDailyTaskId);
            }
            final String _tmpLearningSessionId;
            if (_cursor.isNull(_cursorIndexOfLearningSessionId)) {
              _tmpLearningSessionId = null;
            } else {
              _tmpLearningSessionId = _cursor.getString(_cursorIndexOfLearningSessionId);
            }
            final List<String> _tmpVocabularyIds;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfVocabularyIds);
            _tmpVocabularyIds = __converters.jsonToStringList(_tmp_4);
            final int _tmpFirstAttemptCorrect;
            _tmpFirstAttemptCorrect = _cursor.getInt(_cursorIndexOfFirstAttemptCorrect);
            final int _tmpFirstAttemptTotal;
            _tmpFirstAttemptTotal = _cursor.getInt(_cursorIndexOfFirstAttemptTotal);
            final Integer _tmpPassingThreshold;
            if (_cursor.isNull(_cursorIndexOfPassingThreshold)) {
              _tmpPassingThreshold = null;
            } else {
              _tmpPassingThreshold = _cursor.getInt(_cursorIndexOfPassingThreshold);
            }
            _result = new TestSessionEntity(_tmpId,_tmpLanguageCode,_tmpBook,_tmpTestType,_tmpSessionDate,_tmpCreatedAt,_tmpCompletedAt,_tmpTotalVocabularyItems,_tmpCurrentGroup,_tmpCurrentQuestion,_tmpStatus,_tmpScorePercent,_tmpPassed,_tmpDailyTaskId,_tmpLearningSessionId,_tmpVocabularyIds,_tmpFirstAttemptCorrect,_tmpFirstAttemptTotal,_tmpPassingThreshold);
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
  public Object allSessions(final Continuation<? super List<TestSessionEntity>> $completion) {
    final String _sql = "SELECT * FROM test_sessions";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<TestSessionEntity>>() {
      @Override
      @NonNull
      public List<TestSessionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLanguageCode = CursorUtil.getColumnIndexOrThrow(_cursor, "languageCode");
          final int _cursorIndexOfBook = CursorUtil.getColumnIndexOrThrow(_cursor, "book");
          final int _cursorIndexOfTestType = CursorUtil.getColumnIndexOrThrow(_cursor, "testType");
          final int _cursorIndexOfSessionDate = CursorUtil.getColumnIndexOrThrow(_cursor, "sessionDate");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfTotalVocabularyItems = CursorUtil.getColumnIndexOrThrow(_cursor, "totalVocabularyItems");
          final int _cursorIndexOfCurrentGroup = CursorUtil.getColumnIndexOrThrow(_cursor, "currentGroup");
          final int _cursorIndexOfCurrentQuestion = CursorUtil.getColumnIndexOrThrow(_cursor, "currentQuestion");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfScorePercent = CursorUtil.getColumnIndexOrThrow(_cursor, "scorePercent");
          final int _cursorIndexOfPassed = CursorUtil.getColumnIndexOrThrow(_cursor, "passed");
          final int _cursorIndexOfDailyTaskId = CursorUtil.getColumnIndexOrThrow(_cursor, "dailyTaskId");
          final int _cursorIndexOfLearningSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "learningSessionId");
          final int _cursorIndexOfVocabularyIds = CursorUtil.getColumnIndexOrThrow(_cursor, "vocabularyIds");
          final int _cursorIndexOfFirstAttemptCorrect = CursorUtil.getColumnIndexOrThrow(_cursor, "firstAttemptCorrect");
          final int _cursorIndexOfFirstAttemptTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "firstAttemptTotal");
          final int _cursorIndexOfPassingThreshold = CursorUtil.getColumnIndexOrThrow(_cursor, "passingThreshold");
          final List<TestSessionEntity> _result = new ArrayList<TestSessionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TestSessionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpLanguageCode;
            _tmpLanguageCode = _cursor.getString(_cursorIndexOfLanguageCode);
            final VocabBook _tmpBook;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfBook);
            _tmpBook = __converters.stringToVocabBook(_tmp);
            final TestType _tmpTestType;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfTestType);
            _tmpTestType = __converters.stringToTestType(_tmp_1);
            final String _tmpSessionDate;
            _tmpSessionDate = _cursor.getString(_cursorIndexOfSessionDate);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final int _tmpTotalVocabularyItems;
            _tmpTotalVocabularyItems = _cursor.getInt(_cursorIndexOfTotalVocabularyItems);
            final int _tmpCurrentGroup;
            _tmpCurrentGroup = _cursor.getInt(_cursorIndexOfCurrentGroup);
            final int _tmpCurrentQuestion;
            _tmpCurrentQuestion = _cursor.getInt(_cursorIndexOfCurrentQuestion);
            final TestStatus _tmpStatus;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.stringToTestStatus(_tmp_2);
            final Double _tmpScorePercent;
            if (_cursor.isNull(_cursorIndexOfScorePercent)) {
              _tmpScorePercent = null;
            } else {
              _tmpScorePercent = _cursor.getDouble(_cursorIndexOfScorePercent);
            }
            final Boolean _tmpPassed;
            final Integer _tmp_3;
            if (_cursor.isNull(_cursorIndexOfPassed)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getInt(_cursorIndexOfPassed);
            }
            _tmpPassed = _tmp_3 == null ? null : _tmp_3 != 0;
            final String _tmpDailyTaskId;
            if (_cursor.isNull(_cursorIndexOfDailyTaskId)) {
              _tmpDailyTaskId = null;
            } else {
              _tmpDailyTaskId = _cursor.getString(_cursorIndexOfDailyTaskId);
            }
            final String _tmpLearningSessionId;
            if (_cursor.isNull(_cursorIndexOfLearningSessionId)) {
              _tmpLearningSessionId = null;
            } else {
              _tmpLearningSessionId = _cursor.getString(_cursorIndexOfLearningSessionId);
            }
            final List<String> _tmpVocabularyIds;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfVocabularyIds);
            _tmpVocabularyIds = __converters.jsonToStringList(_tmp_4);
            final int _tmpFirstAttemptCorrect;
            _tmpFirstAttemptCorrect = _cursor.getInt(_cursorIndexOfFirstAttemptCorrect);
            final int _tmpFirstAttemptTotal;
            _tmpFirstAttemptTotal = _cursor.getInt(_cursorIndexOfFirstAttemptTotal);
            final Integer _tmpPassingThreshold;
            if (_cursor.isNull(_cursorIndexOfPassingThreshold)) {
              _tmpPassingThreshold = null;
            } else {
              _tmpPassingThreshold = _cursor.getInt(_cursorIndexOfPassingThreshold);
            }
            _item = new TestSessionEntity(_tmpId,_tmpLanguageCode,_tmpBook,_tmpTestType,_tmpSessionDate,_tmpCreatedAt,_tmpCompletedAt,_tmpTotalVocabularyItems,_tmpCurrentGroup,_tmpCurrentQuestion,_tmpStatus,_tmpScorePercent,_tmpPassed,_tmpDailyTaskId,_tmpLearningSessionId,_tmpVocabularyIds,_tmpFirstAttemptCorrect,_tmpFirstAttemptTotal,_tmpPassingThreshold);
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
  public Object allQuestions(final Continuation<? super List<TestQuestionEntity>> $completion) {
    final String _sql = "SELECT * FROM test_questions";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<TestQuestionEntity>>() {
      @Override
      @NonNull
      public List<TestQuestionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "sessionId");
          final int _cursorIndexOfVocabularyId = CursorUtil.getColumnIndexOrThrow(_cursor, "vocabularyId");
          final int _cursorIndexOfQuestionType = CursorUtil.getColumnIndexOrThrow(_cursor, "questionType");
          final int _cursorIndexOfPrompt = CursorUtil.getColumnIndexOrThrow(_cursor, "prompt");
          final int _cursorIndexOfContextTranslation = CursorUtil.getColumnIndexOrThrow(_cursor, "contextTranslation");
          final int _cursorIndexOfHighlightText = CursorUtil.getColumnIndexOrThrow(_cursor, "highlightText");
          final int _cursorIndexOfOptions = CursorUtil.getColumnIndexOrThrow(_cursor, "options");
          final int _cursorIndexOfCorrectIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "correctIndex");
          final int _cursorIndexOfSelectedIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "selectedIndex");
          final int _cursorIndexOfIsCorrect = CursorUtil.getColumnIndexOrThrow(_cursor, "isCorrect");
          final int _cursorIndexOfRetryCount = CursorUtil.getColumnIndexOrThrow(_cursor, "retryCount");
          final int _cursorIndexOfIsReviewCopy = CursorUtil.getColumnIndexOrThrow(_cursor, "isReviewCopy");
          final int _cursorIndexOfGroupIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "groupIndex");
          final int _cursorIndexOfOrderInGroup = CursorUtil.getColumnIndexOrThrow(_cursor, "orderInGroup");
          final List<TestQuestionEntity> _result = new ArrayList<TestQuestionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TestQuestionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpSessionId;
            _tmpSessionId = _cursor.getString(_cursorIndexOfSessionId);
            final String _tmpVocabularyId;
            _tmpVocabularyId = _cursor.getString(_cursorIndexOfVocabularyId);
            final QuestionType _tmpQuestionType;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfQuestionType);
            _tmpQuestionType = __converters.stringToQuestionType(_tmp);
            final String _tmpPrompt;
            _tmpPrompt = _cursor.getString(_cursorIndexOfPrompt);
            final String _tmpContextTranslation;
            if (_cursor.isNull(_cursorIndexOfContextTranslation)) {
              _tmpContextTranslation = null;
            } else {
              _tmpContextTranslation = _cursor.getString(_cursorIndexOfContextTranslation);
            }
            final String _tmpHighlightText;
            if (_cursor.isNull(_cursorIndexOfHighlightText)) {
              _tmpHighlightText = null;
            } else {
              _tmpHighlightText = _cursor.getString(_cursorIndexOfHighlightText);
            }
            final List<String> _tmpOptions;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfOptions);
            _tmpOptions = __converters.jsonToStringList(_tmp_1);
            final int _tmpCorrectIndex;
            _tmpCorrectIndex = _cursor.getInt(_cursorIndexOfCorrectIndex);
            final Integer _tmpSelectedIndex;
            if (_cursor.isNull(_cursorIndexOfSelectedIndex)) {
              _tmpSelectedIndex = null;
            } else {
              _tmpSelectedIndex = _cursor.getInt(_cursorIndexOfSelectedIndex);
            }
            final Boolean _tmpIsCorrect;
            final Integer _tmp_2;
            if (_cursor.isNull(_cursorIndexOfIsCorrect)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getInt(_cursorIndexOfIsCorrect);
            }
            _tmpIsCorrect = _tmp_2 == null ? null : _tmp_2 != 0;
            final int _tmpRetryCount;
            _tmpRetryCount = _cursor.getInt(_cursorIndexOfRetryCount);
            final boolean _tmpIsReviewCopy;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsReviewCopy);
            _tmpIsReviewCopy = _tmp_3 != 0;
            final int _tmpGroupIndex;
            _tmpGroupIndex = _cursor.getInt(_cursorIndexOfGroupIndex);
            final int _tmpOrderInGroup;
            _tmpOrderInGroup = _cursor.getInt(_cursorIndexOfOrderInGroup);
            _item = new TestQuestionEntity(_tmpId,_tmpSessionId,_tmpVocabularyId,_tmpQuestionType,_tmpPrompt,_tmpContextTranslation,_tmpHighlightText,_tmpOptions,_tmpCorrectIndex,_tmpSelectedIndex,_tmpIsCorrect,_tmpRetryCount,_tmpIsReviewCopy,_tmpGroupIndex,_tmpOrderInGroup);
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
  public Flow<Integer> observeCompletedTestCount() {
    final String _sql = "SELECT COUNT(*) FROM test_sessions WHERE status = 'COMPLETED' AND testType != 'GROUP_QUIZ'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"test_sessions"}, new Callable<Integer>() {
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
