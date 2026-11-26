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
import com.baytalhikmah.data.local.ReadingLexemeEntity;
import com.baytalhikmah.data.local.ReadingProgressEntity;
import com.baytalhikmah.data.local.ReadingSectionEntity;
import com.baytalhikmah.data.local.ReadingSectionProgressEntity;
import com.baytalhikmah.data.local.ReadingWorkEntity;
import com.baytalhikmah.data.model.ReadingStatus;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
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
public final class ReadingDao_Impl implements ReadingDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ReadingSectionProgressEntity> __insertionAdapterOfReadingSectionProgressEntity;

  private final Converters __converters = new Converters();

  private final EntityInsertionAdapter<ReadingProgressEntity> __insertionAdapterOfReadingProgressEntity;

  private final EntityInsertionAdapter<ReadingWorkEntity> __insertionAdapterOfReadingWorkEntity;

  private final EntityInsertionAdapter<ReadingLexemeEntity> __insertionAdapterOfReadingLexemeEntity;

  private final EntityInsertionAdapter<ReadingSectionEntity> __insertionAdapterOfReadingSectionEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteSections;

  private final SharedSQLiteStatement __preparedStmtOfDeleteLexemes;

  public ReadingDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfReadingSectionProgressEntity = new EntityInsertionAdapter<ReadingSectionProgressEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `reading_section_progress` (`sectionId`,`workId`,`status`,`startedAt`,`finishedAt`,`lastTestedAt`,`attempts`,`lastCorrect`,`lastTotal`,`bestCorrect`) VALUES (?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ReadingSectionProgressEntity entity) {
        statement.bindString(1, entity.getSectionId());
        statement.bindString(2, entity.getWorkId());
        final String _tmp = __converters.readingStatusToString(entity.getStatus());
        statement.bindString(3, _tmp);
        if (entity.getStartedAt() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getStartedAt());
        }
        if (entity.getFinishedAt() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getFinishedAt());
        }
        if (entity.getLastTestedAt() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getLastTestedAt());
        }
        statement.bindLong(7, entity.getAttempts());
        if (entity.getLastCorrect() == null) {
          statement.bindNull(8);
        } else {
          statement.bindLong(8, entity.getLastCorrect());
        }
        if (entity.getLastTotal() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getLastTotal());
        }
        statement.bindLong(10, entity.getBestCorrect());
      }
    };
    this.__insertionAdapterOfReadingProgressEntity = new EntityInsertionAdapter<ReadingProgressEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `reading_progress` (`workId`,`status`,`startedAt`,`finishedAt`,`lastTestedAt`,`attempts`,`lastCorrect`,`lastTotal`,`bestCorrect`) VALUES (?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ReadingProgressEntity entity) {
        statement.bindString(1, entity.getWorkId());
        final String _tmp = __converters.readingStatusToString(entity.getStatus());
        statement.bindString(2, _tmp);
        if (entity.getStartedAt() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getStartedAt());
        }
        if (entity.getFinishedAt() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getFinishedAt());
        }
        if (entity.getLastTestedAt() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getLastTestedAt());
        }
        statement.bindLong(6, entity.getAttempts());
        if (entity.getLastCorrect() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getLastCorrect());
        }
        if (entity.getLastTotal() == null) {
          statement.bindNull(8);
        } else {
          statement.bindLong(8, entity.getLastTotal());
        }
        statement.bindLong(9, entity.getBestCorrect());
      }
    };
    this.__insertionAdapterOfReadingWorkEntity = new EntityInsertionAdapter<ReadingWorkEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `reading_works` (`id`,`languageCode`,`author`,`title`,`summary`,`textSource`,`translationSource`,`bookCount`,`sortOrder`) VALUES (?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ReadingWorkEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getLanguageCode());
        statement.bindString(3, entity.getAuthor());
        statement.bindString(4, entity.getTitle());
        statement.bindString(5, entity.getSummary());
        statement.bindString(6, entity.getTextSource());
        statement.bindString(7, entity.getTranslationSource());
        statement.bindLong(8, entity.getBookCount());
        statement.bindLong(9, entity.getSortOrder());
      }
    };
    this.__insertionAdapterOfReadingLexemeEntity = new EntityInsertionAdapter<ReadingLexemeEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `reading_lexemes` (`id`,`workId`,`surface`,`lemma`,`pos`,`gloss`) VALUES (?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ReadingLexemeEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getWorkId());
        statement.bindString(3, entity.getSurface());
        statement.bindString(4, entity.getLemma());
        statement.bindString(5, entity.getPos());
        statement.bindString(6, entity.getGloss());
      }
    };
    this.__insertionAdapterOfReadingSectionEntity = new EntityInsertionAdapter<ReadingSectionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `reading_sections` (`id`,`workId`,`bookNumber`,`bookLabel`,`sectionLabel`,`heading`,`originalText`,`translationText`,`newWordIds`,`sortOrder`) VALUES (?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ReadingSectionEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getWorkId());
        statement.bindLong(3, entity.getBookNumber());
        statement.bindString(4, entity.getBookLabel());
        statement.bindString(5, entity.getSectionLabel());
        statement.bindString(6, entity.getHeading());
        statement.bindString(7, entity.getOriginalText());
        statement.bindString(8, entity.getTranslationText());
        final String _tmp = __converters.stringListToJson(entity.getNewWordIds());
        statement.bindString(9, _tmp);
        statement.bindLong(10, entity.getSortOrder());
      }
    };
    this.__preparedStmtOfDeleteSections = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM reading_sections WHERE workId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteLexemes = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM reading_lexemes WHERE workId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object upsertSectionProgress(final ReadingSectionProgressEntity item,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfReadingSectionProgressEntity.insert(item);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object upsertProgress(final ReadingProgressEntity item,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfReadingProgressEntity.insert(item);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object upsertWorks(final List<ReadingWorkEntity> items,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfReadingWorkEntity.insert(items);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object upsertLexemes(final List<ReadingLexemeEntity> items,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfReadingLexemeEntity.insert(items);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object upsertSections(final List<ReadingSectionEntity> items,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfReadingSectionEntity.insert(items);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteSections(final String workId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteSections.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, workId);
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
          __preparedStmtOfDeleteSections.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteLexemes(final String workId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteLexemes.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, workId);
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
          __preparedStmtOfDeleteLexemes.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ReadingWorkEntity>> observeWorks(final String language) {
    final String _sql = "SELECT * FROM reading_works WHERE languageCode = ? ORDER BY sortOrder";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, language);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"reading_works"}, new Callable<List<ReadingWorkEntity>>() {
      @Override
      @NonNull
      public List<ReadingWorkEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLanguageCode = CursorUtil.getColumnIndexOrThrow(_cursor, "languageCode");
          final int _cursorIndexOfAuthor = CursorUtil.getColumnIndexOrThrow(_cursor, "author");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfSummary = CursorUtil.getColumnIndexOrThrow(_cursor, "summary");
          final int _cursorIndexOfTextSource = CursorUtil.getColumnIndexOrThrow(_cursor, "textSource");
          final int _cursorIndexOfTranslationSource = CursorUtil.getColumnIndexOrThrow(_cursor, "translationSource");
          final int _cursorIndexOfBookCount = CursorUtil.getColumnIndexOrThrow(_cursor, "bookCount");
          final int _cursorIndexOfSortOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "sortOrder");
          final List<ReadingWorkEntity> _result = new ArrayList<ReadingWorkEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ReadingWorkEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpLanguageCode;
            _tmpLanguageCode = _cursor.getString(_cursorIndexOfLanguageCode);
            final String _tmpAuthor;
            _tmpAuthor = _cursor.getString(_cursorIndexOfAuthor);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpSummary;
            _tmpSummary = _cursor.getString(_cursorIndexOfSummary);
            final String _tmpTextSource;
            _tmpTextSource = _cursor.getString(_cursorIndexOfTextSource);
            final String _tmpTranslationSource;
            _tmpTranslationSource = _cursor.getString(_cursorIndexOfTranslationSource);
            final int _tmpBookCount;
            _tmpBookCount = _cursor.getInt(_cursorIndexOfBookCount);
            final int _tmpSortOrder;
            _tmpSortOrder = _cursor.getInt(_cursorIndexOfSortOrder);
            _item = new ReadingWorkEntity(_tmpId,_tmpLanguageCode,_tmpAuthor,_tmpTitle,_tmpSummary,_tmpTextSource,_tmpTranslationSource,_tmpBookCount,_tmpSortOrder);
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
  public Object getAllWorks(final Continuation<? super List<ReadingWorkEntity>> $completion) {
    final String _sql = "SELECT * FROM reading_works ORDER BY languageCode, sortOrder";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ReadingWorkEntity>>() {
      @Override
      @NonNull
      public List<ReadingWorkEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLanguageCode = CursorUtil.getColumnIndexOrThrow(_cursor, "languageCode");
          final int _cursorIndexOfAuthor = CursorUtil.getColumnIndexOrThrow(_cursor, "author");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfSummary = CursorUtil.getColumnIndexOrThrow(_cursor, "summary");
          final int _cursorIndexOfTextSource = CursorUtil.getColumnIndexOrThrow(_cursor, "textSource");
          final int _cursorIndexOfTranslationSource = CursorUtil.getColumnIndexOrThrow(_cursor, "translationSource");
          final int _cursorIndexOfBookCount = CursorUtil.getColumnIndexOrThrow(_cursor, "bookCount");
          final int _cursorIndexOfSortOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "sortOrder");
          final List<ReadingWorkEntity> _result = new ArrayList<ReadingWorkEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ReadingWorkEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpLanguageCode;
            _tmpLanguageCode = _cursor.getString(_cursorIndexOfLanguageCode);
            final String _tmpAuthor;
            _tmpAuthor = _cursor.getString(_cursorIndexOfAuthor);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpSummary;
            _tmpSummary = _cursor.getString(_cursorIndexOfSummary);
            final String _tmpTextSource;
            _tmpTextSource = _cursor.getString(_cursorIndexOfTextSource);
            final String _tmpTranslationSource;
            _tmpTranslationSource = _cursor.getString(_cursorIndexOfTranslationSource);
            final int _tmpBookCount;
            _tmpBookCount = _cursor.getInt(_cursorIndexOfBookCount);
            final int _tmpSortOrder;
            _tmpSortOrder = _cursor.getInt(_cursorIndexOfSortOrder);
            _item = new ReadingWorkEntity(_tmpId,_tmpLanguageCode,_tmpAuthor,_tmpTitle,_tmpSummary,_tmpTextSource,_tmpTranslationSource,_tmpBookCount,_tmpSortOrder);
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
  public Object getWork(final String id,
      final Continuation<? super ReadingWorkEntity> $completion) {
    final String _sql = "SELECT * FROM reading_works WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ReadingWorkEntity>() {
      @Override
      @Nullable
      public ReadingWorkEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLanguageCode = CursorUtil.getColumnIndexOrThrow(_cursor, "languageCode");
          final int _cursorIndexOfAuthor = CursorUtil.getColumnIndexOrThrow(_cursor, "author");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfSummary = CursorUtil.getColumnIndexOrThrow(_cursor, "summary");
          final int _cursorIndexOfTextSource = CursorUtil.getColumnIndexOrThrow(_cursor, "textSource");
          final int _cursorIndexOfTranslationSource = CursorUtil.getColumnIndexOrThrow(_cursor, "translationSource");
          final int _cursorIndexOfBookCount = CursorUtil.getColumnIndexOrThrow(_cursor, "bookCount");
          final int _cursorIndexOfSortOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "sortOrder");
          final ReadingWorkEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpLanguageCode;
            _tmpLanguageCode = _cursor.getString(_cursorIndexOfLanguageCode);
            final String _tmpAuthor;
            _tmpAuthor = _cursor.getString(_cursorIndexOfAuthor);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpSummary;
            _tmpSummary = _cursor.getString(_cursorIndexOfSummary);
            final String _tmpTextSource;
            _tmpTextSource = _cursor.getString(_cursorIndexOfTextSource);
            final String _tmpTranslationSource;
            _tmpTranslationSource = _cursor.getString(_cursorIndexOfTranslationSource);
            final int _tmpBookCount;
            _tmpBookCount = _cursor.getInt(_cursorIndexOfBookCount);
            final int _tmpSortOrder;
            _tmpSortOrder = _cursor.getInt(_cursorIndexOfSortOrder);
            _result = new ReadingWorkEntity(_tmpId,_tmpLanguageCode,_tmpAuthor,_tmpTitle,_tmpSummary,_tmpTextSource,_tmpTranslationSource,_tmpBookCount,_tmpSortOrder);
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
  public Object books(final String workId,
      final Continuation<? super List<ReadingBookRow>> $completion) {
    final String _sql = "\n"
            + "        SELECT bookNumber AS bookNumber, bookLabel AS bookLabel, COUNT(*) AS sectionCount\n"
            + "        FROM reading_sections WHERE workId = ?\n"
            + "        GROUP BY bookNumber ORDER BY bookNumber\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, workId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ReadingBookRow>>() {
      @Override
      @NonNull
      public List<ReadingBookRow> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfBookNumber = 0;
          final int _cursorIndexOfBookLabel = 1;
          final int _cursorIndexOfSectionCount = 2;
          final List<ReadingBookRow> _result = new ArrayList<ReadingBookRow>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ReadingBookRow _item;
            final int _tmpBookNumber;
            _tmpBookNumber = _cursor.getInt(_cursorIndexOfBookNumber);
            final String _tmpBookLabel;
            _tmpBookLabel = _cursor.getString(_cursorIndexOfBookLabel);
            final int _tmpSectionCount;
            _tmpSectionCount = _cursor.getInt(_cursorIndexOfSectionCount);
            _item = new ReadingBookRow(_tmpBookNumber,_tmpBookLabel,_tmpSectionCount);
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
  public Object sections(final String workId,
      final Continuation<? super List<ReadingSectionEntity>> $completion) {
    final String _sql = "SELECT * FROM reading_sections WHERE workId = ? ORDER BY sortOrder";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, workId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ReadingSectionEntity>>() {
      @Override
      @NonNull
      public List<ReadingSectionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfWorkId = CursorUtil.getColumnIndexOrThrow(_cursor, "workId");
          final int _cursorIndexOfBookNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "bookNumber");
          final int _cursorIndexOfBookLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "bookLabel");
          final int _cursorIndexOfSectionLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "sectionLabel");
          final int _cursorIndexOfHeading = CursorUtil.getColumnIndexOrThrow(_cursor, "heading");
          final int _cursorIndexOfOriginalText = CursorUtil.getColumnIndexOrThrow(_cursor, "originalText");
          final int _cursorIndexOfTranslationText = CursorUtil.getColumnIndexOrThrow(_cursor, "translationText");
          final int _cursorIndexOfNewWordIds = CursorUtil.getColumnIndexOrThrow(_cursor, "newWordIds");
          final int _cursorIndexOfSortOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "sortOrder");
          final List<ReadingSectionEntity> _result = new ArrayList<ReadingSectionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ReadingSectionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpWorkId;
            _tmpWorkId = _cursor.getString(_cursorIndexOfWorkId);
            final int _tmpBookNumber;
            _tmpBookNumber = _cursor.getInt(_cursorIndexOfBookNumber);
            final String _tmpBookLabel;
            _tmpBookLabel = _cursor.getString(_cursorIndexOfBookLabel);
            final String _tmpSectionLabel;
            _tmpSectionLabel = _cursor.getString(_cursorIndexOfSectionLabel);
            final String _tmpHeading;
            _tmpHeading = _cursor.getString(_cursorIndexOfHeading);
            final String _tmpOriginalText;
            _tmpOriginalText = _cursor.getString(_cursorIndexOfOriginalText);
            final String _tmpTranslationText;
            _tmpTranslationText = _cursor.getString(_cursorIndexOfTranslationText);
            final List<String> _tmpNewWordIds;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfNewWordIds);
            _tmpNewWordIds = __converters.jsonToStringList(_tmp);
            final int _tmpSortOrder;
            _tmpSortOrder = _cursor.getInt(_cursorIndexOfSortOrder);
            _item = new ReadingSectionEntity(_tmpId,_tmpWorkId,_tmpBookNumber,_tmpBookLabel,_tmpSectionLabel,_tmpHeading,_tmpOriginalText,_tmpTranslationText,_tmpNewWordIds,_tmpSortOrder);
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
  public Object sectionInWork(final String workId, final String sectionId,
      final Continuation<? super ReadingSectionEntity> $completion) {
    final String _sql = "SELECT * FROM reading_sections WHERE workId = ? AND id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, workId);
    _argIndex = 2;
    _statement.bindString(_argIndex, sectionId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ReadingSectionEntity>() {
      @Override
      @Nullable
      public ReadingSectionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfWorkId = CursorUtil.getColumnIndexOrThrow(_cursor, "workId");
          final int _cursorIndexOfBookNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "bookNumber");
          final int _cursorIndexOfBookLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "bookLabel");
          final int _cursorIndexOfSectionLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "sectionLabel");
          final int _cursorIndexOfHeading = CursorUtil.getColumnIndexOrThrow(_cursor, "heading");
          final int _cursorIndexOfOriginalText = CursorUtil.getColumnIndexOrThrow(_cursor, "originalText");
          final int _cursorIndexOfTranslationText = CursorUtil.getColumnIndexOrThrow(_cursor, "translationText");
          final int _cursorIndexOfNewWordIds = CursorUtil.getColumnIndexOrThrow(_cursor, "newWordIds");
          final int _cursorIndexOfSortOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "sortOrder");
          final ReadingSectionEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpWorkId;
            _tmpWorkId = _cursor.getString(_cursorIndexOfWorkId);
            final int _tmpBookNumber;
            _tmpBookNumber = _cursor.getInt(_cursorIndexOfBookNumber);
            final String _tmpBookLabel;
            _tmpBookLabel = _cursor.getString(_cursorIndexOfBookLabel);
            final String _tmpSectionLabel;
            _tmpSectionLabel = _cursor.getString(_cursorIndexOfSectionLabel);
            final String _tmpHeading;
            _tmpHeading = _cursor.getString(_cursorIndexOfHeading);
            final String _tmpOriginalText;
            _tmpOriginalText = _cursor.getString(_cursorIndexOfOriginalText);
            final String _tmpTranslationText;
            _tmpTranslationText = _cursor.getString(_cursorIndexOfTranslationText);
            final List<String> _tmpNewWordIds;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfNewWordIds);
            _tmpNewWordIds = __converters.jsonToStringList(_tmp);
            final int _tmpSortOrder;
            _tmpSortOrder = _cursor.getInt(_cursorIndexOfSortOrder);
            _result = new ReadingSectionEntity(_tmpId,_tmpWorkId,_tmpBookNumber,_tmpBookLabel,_tmpSectionLabel,_tmpHeading,_tmpOriginalText,_tmpTranslationText,_tmpNewWordIds,_tmpSortOrder);
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
  public Object getSection(final String id,
      final Continuation<? super ReadingSectionEntity> $completion) {
    final String _sql = "SELECT * FROM reading_sections WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ReadingSectionEntity>() {
      @Override
      @Nullable
      public ReadingSectionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfWorkId = CursorUtil.getColumnIndexOrThrow(_cursor, "workId");
          final int _cursorIndexOfBookNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "bookNumber");
          final int _cursorIndexOfBookLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "bookLabel");
          final int _cursorIndexOfSectionLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "sectionLabel");
          final int _cursorIndexOfHeading = CursorUtil.getColumnIndexOrThrow(_cursor, "heading");
          final int _cursorIndexOfOriginalText = CursorUtil.getColumnIndexOrThrow(_cursor, "originalText");
          final int _cursorIndexOfTranslationText = CursorUtil.getColumnIndexOrThrow(_cursor, "translationText");
          final int _cursorIndexOfNewWordIds = CursorUtil.getColumnIndexOrThrow(_cursor, "newWordIds");
          final int _cursorIndexOfSortOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "sortOrder");
          final ReadingSectionEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpWorkId;
            _tmpWorkId = _cursor.getString(_cursorIndexOfWorkId);
            final int _tmpBookNumber;
            _tmpBookNumber = _cursor.getInt(_cursorIndexOfBookNumber);
            final String _tmpBookLabel;
            _tmpBookLabel = _cursor.getString(_cursorIndexOfBookLabel);
            final String _tmpSectionLabel;
            _tmpSectionLabel = _cursor.getString(_cursorIndexOfSectionLabel);
            final String _tmpHeading;
            _tmpHeading = _cursor.getString(_cursorIndexOfHeading);
            final String _tmpOriginalText;
            _tmpOriginalText = _cursor.getString(_cursorIndexOfOriginalText);
            final String _tmpTranslationText;
            _tmpTranslationText = _cursor.getString(_cursorIndexOfTranslationText);
            final List<String> _tmpNewWordIds;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfNewWordIds);
            _tmpNewWordIds = __converters.jsonToStringList(_tmp);
            final int _tmpSortOrder;
            _tmpSortOrder = _cursor.getInt(_cursorIndexOfSortOrder);
            _result = new ReadingSectionEntity(_tmpId,_tmpWorkId,_tmpBookNumber,_tmpBookLabel,_tmpSectionLabel,_tmpHeading,_tmpOriginalText,_tmpTranslationText,_tmpNewWordIds,_tmpSortOrder);
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
  public Object sectionsInBook(final String workId, final int bookNumber,
      final Continuation<? super List<ReadingSectionEntity>> $completion) {
    final String _sql = "SELECT * FROM reading_sections WHERE workId = ? AND bookNumber = ? ORDER BY sortOrder";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, workId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, bookNumber);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ReadingSectionEntity>>() {
      @Override
      @NonNull
      public List<ReadingSectionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfWorkId = CursorUtil.getColumnIndexOrThrow(_cursor, "workId");
          final int _cursorIndexOfBookNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "bookNumber");
          final int _cursorIndexOfBookLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "bookLabel");
          final int _cursorIndexOfSectionLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "sectionLabel");
          final int _cursorIndexOfHeading = CursorUtil.getColumnIndexOrThrow(_cursor, "heading");
          final int _cursorIndexOfOriginalText = CursorUtil.getColumnIndexOrThrow(_cursor, "originalText");
          final int _cursorIndexOfTranslationText = CursorUtil.getColumnIndexOrThrow(_cursor, "translationText");
          final int _cursorIndexOfNewWordIds = CursorUtil.getColumnIndexOrThrow(_cursor, "newWordIds");
          final int _cursorIndexOfSortOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "sortOrder");
          final List<ReadingSectionEntity> _result = new ArrayList<ReadingSectionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ReadingSectionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpWorkId;
            _tmpWorkId = _cursor.getString(_cursorIndexOfWorkId);
            final int _tmpBookNumber;
            _tmpBookNumber = _cursor.getInt(_cursorIndexOfBookNumber);
            final String _tmpBookLabel;
            _tmpBookLabel = _cursor.getString(_cursorIndexOfBookLabel);
            final String _tmpSectionLabel;
            _tmpSectionLabel = _cursor.getString(_cursorIndexOfSectionLabel);
            final String _tmpHeading;
            _tmpHeading = _cursor.getString(_cursorIndexOfHeading);
            final String _tmpOriginalText;
            _tmpOriginalText = _cursor.getString(_cursorIndexOfOriginalText);
            final String _tmpTranslationText;
            _tmpTranslationText = _cursor.getString(_cursorIndexOfTranslationText);
            final List<String> _tmpNewWordIds;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfNewWordIds);
            _tmpNewWordIds = __converters.jsonToStringList(_tmp);
            final int _tmpSortOrder;
            _tmpSortOrder = _cursor.getInt(_cursorIndexOfSortOrder);
            _item = new ReadingSectionEntity(_tmpId,_tmpWorkId,_tmpBookNumber,_tmpBookLabel,_tmpSectionLabel,_tmpHeading,_tmpOriginalText,_tmpTranslationText,_tmpNewWordIds,_tmpSortOrder);
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
  public Object lexemesByIds(final List<String> ids,
      final Continuation<? super List<ReadingLexemeEntity>> $completion) {
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT * FROM reading_lexemes WHERE id IN (");
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
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ReadingLexemeEntity>>() {
      @Override
      @NonNull
      public List<ReadingLexemeEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfWorkId = CursorUtil.getColumnIndexOrThrow(_cursor, "workId");
          final int _cursorIndexOfSurface = CursorUtil.getColumnIndexOrThrow(_cursor, "surface");
          final int _cursorIndexOfLemma = CursorUtil.getColumnIndexOrThrow(_cursor, "lemma");
          final int _cursorIndexOfPos = CursorUtil.getColumnIndexOrThrow(_cursor, "pos");
          final int _cursorIndexOfGloss = CursorUtil.getColumnIndexOrThrow(_cursor, "gloss");
          final List<ReadingLexemeEntity> _result = new ArrayList<ReadingLexemeEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ReadingLexemeEntity _item_1;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpWorkId;
            _tmpWorkId = _cursor.getString(_cursorIndexOfWorkId);
            final String _tmpSurface;
            _tmpSurface = _cursor.getString(_cursorIndexOfSurface);
            final String _tmpLemma;
            _tmpLemma = _cursor.getString(_cursorIndexOfLemma);
            final String _tmpPos;
            _tmpPos = _cursor.getString(_cursorIndexOfPos);
            final String _tmpGloss;
            _tmpGloss = _cursor.getString(_cursorIndexOfGloss);
            _item_1 = new ReadingLexemeEntity(_tmpId,_tmpWorkId,_tmpSurface,_tmpLemma,_tmpPos,_tmpGloss);
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
  public Object lexemesForWork(final String workId,
      final Continuation<? super List<ReadingLexemeEntity>> $completion) {
    final String _sql = "SELECT * FROM reading_lexemes WHERE workId = ? ORDER BY surface";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, workId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ReadingLexemeEntity>>() {
      @Override
      @NonNull
      public List<ReadingLexemeEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfWorkId = CursorUtil.getColumnIndexOrThrow(_cursor, "workId");
          final int _cursorIndexOfSurface = CursorUtil.getColumnIndexOrThrow(_cursor, "surface");
          final int _cursorIndexOfLemma = CursorUtil.getColumnIndexOrThrow(_cursor, "lemma");
          final int _cursorIndexOfPos = CursorUtil.getColumnIndexOrThrow(_cursor, "pos");
          final int _cursorIndexOfGloss = CursorUtil.getColumnIndexOrThrow(_cursor, "gloss");
          final List<ReadingLexemeEntity> _result = new ArrayList<ReadingLexemeEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ReadingLexemeEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpWorkId;
            _tmpWorkId = _cursor.getString(_cursorIndexOfWorkId);
            final String _tmpSurface;
            _tmpSurface = _cursor.getString(_cursorIndexOfSurface);
            final String _tmpLemma;
            _tmpLemma = _cursor.getString(_cursorIndexOfLemma);
            final String _tmpPos;
            _tmpPos = _cursor.getString(_cursorIndexOfPos);
            final String _tmpGloss;
            _tmpGloss = _cursor.getString(_cursorIndexOfGloss);
            _item = new ReadingLexemeEntity(_tmpId,_tmpWorkId,_tmpSurface,_tmpLemma,_tmpPos,_tmpGloss);
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
  public Flow<List<ReadingProgressEntity>> observeProgress() {
    final String _sql = "SELECT * FROM reading_progress";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"reading_progress"}, new Callable<List<ReadingProgressEntity>>() {
      @Override
      @NonNull
      public List<ReadingProgressEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfWorkId = CursorUtil.getColumnIndexOrThrow(_cursor, "workId");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "startedAt");
          final int _cursorIndexOfFinishedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "finishedAt");
          final int _cursorIndexOfLastTestedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastTestedAt");
          final int _cursorIndexOfAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "attempts");
          final int _cursorIndexOfLastCorrect = CursorUtil.getColumnIndexOrThrow(_cursor, "lastCorrect");
          final int _cursorIndexOfLastTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "lastTotal");
          final int _cursorIndexOfBestCorrect = CursorUtil.getColumnIndexOrThrow(_cursor, "bestCorrect");
          final List<ReadingProgressEntity> _result = new ArrayList<ReadingProgressEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ReadingProgressEntity _item;
            final String _tmpWorkId;
            _tmpWorkId = _cursor.getString(_cursorIndexOfWorkId);
            final ReadingStatus _tmpStatus;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.stringToReadingStatus(_tmp);
            final Long _tmpStartedAt;
            if (_cursor.isNull(_cursorIndexOfStartedAt)) {
              _tmpStartedAt = null;
            } else {
              _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            }
            final Long _tmpFinishedAt;
            if (_cursor.isNull(_cursorIndexOfFinishedAt)) {
              _tmpFinishedAt = null;
            } else {
              _tmpFinishedAt = _cursor.getLong(_cursorIndexOfFinishedAt);
            }
            final Long _tmpLastTestedAt;
            if (_cursor.isNull(_cursorIndexOfLastTestedAt)) {
              _tmpLastTestedAt = null;
            } else {
              _tmpLastTestedAt = _cursor.getLong(_cursorIndexOfLastTestedAt);
            }
            final int _tmpAttempts;
            _tmpAttempts = _cursor.getInt(_cursorIndexOfAttempts);
            final Integer _tmpLastCorrect;
            if (_cursor.isNull(_cursorIndexOfLastCorrect)) {
              _tmpLastCorrect = null;
            } else {
              _tmpLastCorrect = _cursor.getInt(_cursorIndexOfLastCorrect);
            }
            final Integer _tmpLastTotal;
            if (_cursor.isNull(_cursorIndexOfLastTotal)) {
              _tmpLastTotal = null;
            } else {
              _tmpLastTotal = _cursor.getInt(_cursorIndexOfLastTotal);
            }
            final int _tmpBestCorrect;
            _tmpBestCorrect = _cursor.getInt(_cursorIndexOfBestCorrect);
            _item = new ReadingProgressEntity(_tmpWorkId,_tmpStatus,_tmpStartedAt,_tmpFinishedAt,_tmpLastTestedAt,_tmpAttempts,_tmpLastCorrect,_tmpLastTotal,_tmpBestCorrect);
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
  public Object progress(final String workId,
      final Continuation<? super ReadingProgressEntity> $completion) {
    final String _sql = "SELECT * FROM reading_progress WHERE workId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, workId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ReadingProgressEntity>() {
      @Override
      @Nullable
      public ReadingProgressEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfWorkId = CursorUtil.getColumnIndexOrThrow(_cursor, "workId");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "startedAt");
          final int _cursorIndexOfFinishedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "finishedAt");
          final int _cursorIndexOfLastTestedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastTestedAt");
          final int _cursorIndexOfAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "attempts");
          final int _cursorIndexOfLastCorrect = CursorUtil.getColumnIndexOrThrow(_cursor, "lastCorrect");
          final int _cursorIndexOfLastTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "lastTotal");
          final int _cursorIndexOfBestCorrect = CursorUtil.getColumnIndexOrThrow(_cursor, "bestCorrect");
          final ReadingProgressEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpWorkId;
            _tmpWorkId = _cursor.getString(_cursorIndexOfWorkId);
            final ReadingStatus _tmpStatus;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.stringToReadingStatus(_tmp);
            final Long _tmpStartedAt;
            if (_cursor.isNull(_cursorIndexOfStartedAt)) {
              _tmpStartedAt = null;
            } else {
              _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            }
            final Long _tmpFinishedAt;
            if (_cursor.isNull(_cursorIndexOfFinishedAt)) {
              _tmpFinishedAt = null;
            } else {
              _tmpFinishedAt = _cursor.getLong(_cursorIndexOfFinishedAt);
            }
            final Long _tmpLastTestedAt;
            if (_cursor.isNull(_cursorIndexOfLastTestedAt)) {
              _tmpLastTestedAt = null;
            } else {
              _tmpLastTestedAt = _cursor.getLong(_cursorIndexOfLastTestedAt);
            }
            final int _tmpAttempts;
            _tmpAttempts = _cursor.getInt(_cursorIndexOfAttempts);
            final Integer _tmpLastCorrect;
            if (_cursor.isNull(_cursorIndexOfLastCorrect)) {
              _tmpLastCorrect = null;
            } else {
              _tmpLastCorrect = _cursor.getInt(_cursorIndexOfLastCorrect);
            }
            final Integer _tmpLastTotal;
            if (_cursor.isNull(_cursorIndexOfLastTotal)) {
              _tmpLastTotal = null;
            } else {
              _tmpLastTotal = _cursor.getInt(_cursorIndexOfLastTotal);
            }
            final int _tmpBestCorrect;
            _tmpBestCorrect = _cursor.getInt(_cursorIndexOfBestCorrect);
            _result = new ReadingProgressEntity(_tmpWorkId,_tmpStatus,_tmpStartedAt,_tmpFinishedAt,_tmpLastTestedAt,_tmpAttempts,_tmpLastCorrect,_tmpLastTotal,_tmpBestCorrect);
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
  public Flow<List<ReadingSectionProgressEntity>> observeSectionProgress() {
    final String _sql = "SELECT * FROM reading_section_progress";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"reading_section_progress"}, new Callable<List<ReadingSectionProgressEntity>>() {
      @Override
      @NonNull
      public List<ReadingSectionProgressEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfSectionId = CursorUtil.getColumnIndexOrThrow(_cursor, "sectionId");
          final int _cursorIndexOfWorkId = CursorUtil.getColumnIndexOrThrow(_cursor, "workId");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "startedAt");
          final int _cursorIndexOfFinishedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "finishedAt");
          final int _cursorIndexOfLastTestedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastTestedAt");
          final int _cursorIndexOfAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "attempts");
          final int _cursorIndexOfLastCorrect = CursorUtil.getColumnIndexOrThrow(_cursor, "lastCorrect");
          final int _cursorIndexOfLastTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "lastTotal");
          final int _cursorIndexOfBestCorrect = CursorUtil.getColumnIndexOrThrow(_cursor, "bestCorrect");
          final List<ReadingSectionProgressEntity> _result = new ArrayList<ReadingSectionProgressEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ReadingSectionProgressEntity _item;
            final String _tmpSectionId;
            _tmpSectionId = _cursor.getString(_cursorIndexOfSectionId);
            final String _tmpWorkId;
            _tmpWorkId = _cursor.getString(_cursorIndexOfWorkId);
            final ReadingStatus _tmpStatus;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.stringToReadingStatus(_tmp);
            final Long _tmpStartedAt;
            if (_cursor.isNull(_cursorIndexOfStartedAt)) {
              _tmpStartedAt = null;
            } else {
              _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            }
            final Long _tmpFinishedAt;
            if (_cursor.isNull(_cursorIndexOfFinishedAt)) {
              _tmpFinishedAt = null;
            } else {
              _tmpFinishedAt = _cursor.getLong(_cursorIndexOfFinishedAt);
            }
            final Long _tmpLastTestedAt;
            if (_cursor.isNull(_cursorIndexOfLastTestedAt)) {
              _tmpLastTestedAt = null;
            } else {
              _tmpLastTestedAt = _cursor.getLong(_cursorIndexOfLastTestedAt);
            }
            final int _tmpAttempts;
            _tmpAttempts = _cursor.getInt(_cursorIndexOfAttempts);
            final Integer _tmpLastCorrect;
            if (_cursor.isNull(_cursorIndexOfLastCorrect)) {
              _tmpLastCorrect = null;
            } else {
              _tmpLastCorrect = _cursor.getInt(_cursorIndexOfLastCorrect);
            }
            final Integer _tmpLastTotal;
            if (_cursor.isNull(_cursorIndexOfLastTotal)) {
              _tmpLastTotal = null;
            } else {
              _tmpLastTotal = _cursor.getInt(_cursorIndexOfLastTotal);
            }
            final int _tmpBestCorrect;
            _tmpBestCorrect = _cursor.getInt(_cursorIndexOfBestCorrect);
            _item = new ReadingSectionProgressEntity(_tmpSectionId,_tmpWorkId,_tmpStatus,_tmpStartedAt,_tmpFinishedAt,_tmpLastTestedAt,_tmpAttempts,_tmpLastCorrect,_tmpLastTotal,_tmpBestCorrect);
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
  public Object sectionProgress(final String sectionId,
      final Continuation<? super ReadingSectionProgressEntity> $completion) {
    final String _sql = "SELECT * FROM reading_section_progress WHERE sectionId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, sectionId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ReadingSectionProgressEntity>() {
      @Override
      @Nullable
      public ReadingSectionProgressEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfSectionId = CursorUtil.getColumnIndexOrThrow(_cursor, "sectionId");
          final int _cursorIndexOfWorkId = CursorUtil.getColumnIndexOrThrow(_cursor, "workId");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "startedAt");
          final int _cursorIndexOfFinishedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "finishedAt");
          final int _cursorIndexOfLastTestedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastTestedAt");
          final int _cursorIndexOfAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "attempts");
          final int _cursorIndexOfLastCorrect = CursorUtil.getColumnIndexOrThrow(_cursor, "lastCorrect");
          final int _cursorIndexOfLastTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "lastTotal");
          final int _cursorIndexOfBestCorrect = CursorUtil.getColumnIndexOrThrow(_cursor, "bestCorrect");
          final ReadingSectionProgressEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpSectionId;
            _tmpSectionId = _cursor.getString(_cursorIndexOfSectionId);
            final String _tmpWorkId;
            _tmpWorkId = _cursor.getString(_cursorIndexOfWorkId);
            final ReadingStatus _tmpStatus;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.stringToReadingStatus(_tmp);
            final Long _tmpStartedAt;
            if (_cursor.isNull(_cursorIndexOfStartedAt)) {
              _tmpStartedAt = null;
            } else {
              _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            }
            final Long _tmpFinishedAt;
            if (_cursor.isNull(_cursorIndexOfFinishedAt)) {
              _tmpFinishedAt = null;
            } else {
              _tmpFinishedAt = _cursor.getLong(_cursorIndexOfFinishedAt);
            }
            final Long _tmpLastTestedAt;
            if (_cursor.isNull(_cursorIndexOfLastTestedAt)) {
              _tmpLastTestedAt = null;
            } else {
              _tmpLastTestedAt = _cursor.getLong(_cursorIndexOfLastTestedAt);
            }
            final int _tmpAttempts;
            _tmpAttempts = _cursor.getInt(_cursorIndexOfAttempts);
            final Integer _tmpLastCorrect;
            if (_cursor.isNull(_cursorIndexOfLastCorrect)) {
              _tmpLastCorrect = null;
            } else {
              _tmpLastCorrect = _cursor.getInt(_cursorIndexOfLastCorrect);
            }
            final Integer _tmpLastTotal;
            if (_cursor.isNull(_cursorIndexOfLastTotal)) {
              _tmpLastTotal = null;
            } else {
              _tmpLastTotal = _cursor.getInt(_cursorIndexOfLastTotal);
            }
            final int _tmpBestCorrect;
            _tmpBestCorrect = _cursor.getInt(_cursorIndexOfBestCorrect);
            _result = new ReadingSectionProgressEntity(_tmpSectionId,_tmpWorkId,_tmpStatus,_tmpStartedAt,_tmpFinishedAt,_tmpLastTestedAt,_tmpAttempts,_tmpLastCorrect,_tmpLastTotal,_tmpBestCorrect);
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
  public Flow<List<ReadingWorkSummaryRow>> observeWorkSectionSummary() {
    final String _sql = "\n"
            + "        SELECT s.workId AS workId, COUNT(*) AS sectionCount,\n"
            + "               SUM(CASE WHEN p.status IS NOT NULL THEN 1 ELSE 0 END) AS startedSections,\n"
            + "               SUM(CASE WHEN p.status = 'FINISHED' THEN 1 ELSE 0 END) AS finishedSections\n"
            + "        FROM reading_sections s\n"
            + "        LEFT JOIN reading_section_progress p ON p.sectionId = s.id\n"
            + "        GROUP BY s.workId\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"reading_sections",
        "reading_section_progress"}, new Callable<List<ReadingWorkSummaryRow>>() {
      @Override
      @NonNull
      public List<ReadingWorkSummaryRow> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfWorkId = 0;
          final int _cursorIndexOfSectionCount = 1;
          final int _cursorIndexOfStartedSections = 2;
          final int _cursorIndexOfFinishedSections = 3;
          final List<ReadingWorkSummaryRow> _result = new ArrayList<ReadingWorkSummaryRow>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ReadingWorkSummaryRow _item;
            final String _tmpWorkId;
            _tmpWorkId = _cursor.getString(_cursorIndexOfWorkId);
            final int _tmpSectionCount;
            _tmpSectionCount = _cursor.getInt(_cursorIndexOfSectionCount);
            final int _tmpStartedSections;
            _tmpStartedSections = _cursor.getInt(_cursorIndexOfStartedSections);
            final int _tmpFinishedSections;
            _tmpFinishedSections = _cursor.getInt(_cursorIndexOfFinishedSections);
            _item = new ReadingWorkSummaryRow(_tmpWorkId,_tmpSectionCount,_tmpStartedSections,_tmpFinishedSections);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
