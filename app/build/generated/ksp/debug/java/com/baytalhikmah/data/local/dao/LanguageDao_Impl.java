package com.baytalhikmah.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.baytalhikmah.data.local.Converters;
import com.baytalhikmah.data.local.LanguageEntity;
import com.baytalhikmah.data.model.MorphologyConfig;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
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
public final class LanguageDao_Impl implements LanguageDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<LanguageEntity> __insertionAdapterOfLanguageEntity;

  private final Converters __converters = new Converters();

  public LanguageDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfLanguageEntity = new EntityInsertionAdapter<LanguageEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `languages` (`code`,`name`,`nativeName`,`script`,`enabled`,`datasetVersion`,`coverageNote`,`licenseNote`,`sources`,`morphology`,`sortOrder`) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final LanguageEntity entity) {
        statement.bindString(1, entity.getCode());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getNativeName());
        statement.bindString(4, entity.getScript());
        final int _tmp = entity.getEnabled() ? 1 : 0;
        statement.bindLong(5, _tmp);
        statement.bindLong(6, entity.getDatasetVersion());
        statement.bindString(7, entity.getCoverageNote());
        statement.bindString(8, entity.getLicenseNote());
        final String _tmp_1 = __converters.stringListToJson(entity.getSources());
        statement.bindString(9, _tmp_1);
        final String _tmp_2 = __converters.morphologyToJson(entity.getMorphology());
        statement.bindString(10, _tmp_2);
        statement.bindLong(11, entity.getSortOrder());
      }
    };
  }

  @Override
  public Object upsert(final LanguageEntity language,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfLanguageEntity.insert(language);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<LanguageEntity>> observeAll() {
    final String _sql = "SELECT * FROM languages ORDER BY sortOrder, name";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"languages"}, new Callable<List<LanguageEntity>>() {
      @Override
      @NonNull
      public List<LanguageEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfCode = CursorUtil.getColumnIndexOrThrow(_cursor, "code");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfNativeName = CursorUtil.getColumnIndexOrThrow(_cursor, "nativeName");
          final int _cursorIndexOfScript = CursorUtil.getColumnIndexOrThrow(_cursor, "script");
          final int _cursorIndexOfEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "enabled");
          final int _cursorIndexOfDatasetVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "datasetVersion");
          final int _cursorIndexOfCoverageNote = CursorUtil.getColumnIndexOrThrow(_cursor, "coverageNote");
          final int _cursorIndexOfLicenseNote = CursorUtil.getColumnIndexOrThrow(_cursor, "licenseNote");
          final int _cursorIndexOfSources = CursorUtil.getColumnIndexOrThrow(_cursor, "sources");
          final int _cursorIndexOfMorphology = CursorUtil.getColumnIndexOrThrow(_cursor, "morphology");
          final int _cursorIndexOfSortOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "sortOrder");
          final List<LanguageEntity> _result = new ArrayList<LanguageEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final LanguageEntity _item;
            final String _tmpCode;
            _tmpCode = _cursor.getString(_cursorIndexOfCode);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpNativeName;
            _tmpNativeName = _cursor.getString(_cursorIndexOfNativeName);
            final String _tmpScript;
            _tmpScript = _cursor.getString(_cursorIndexOfScript);
            final boolean _tmpEnabled;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfEnabled);
            _tmpEnabled = _tmp != 0;
            final int _tmpDatasetVersion;
            _tmpDatasetVersion = _cursor.getInt(_cursorIndexOfDatasetVersion);
            final String _tmpCoverageNote;
            _tmpCoverageNote = _cursor.getString(_cursorIndexOfCoverageNote);
            final String _tmpLicenseNote;
            _tmpLicenseNote = _cursor.getString(_cursorIndexOfLicenseNote);
            final List<String> _tmpSources;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfSources);
            _tmpSources = __converters.jsonToStringList(_tmp_1);
            final MorphologyConfig _tmpMorphology;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfMorphology);
            _tmpMorphology = __converters.jsonToMorphology(_tmp_2);
            final int _tmpSortOrder;
            _tmpSortOrder = _cursor.getInt(_cursorIndexOfSortOrder);
            _item = new LanguageEntity(_tmpCode,_tmpName,_tmpNativeName,_tmpScript,_tmpEnabled,_tmpDatasetVersion,_tmpCoverageNote,_tmpLicenseNote,_tmpSources,_tmpMorphology,_tmpSortOrder);
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
  public Object getEnabled(final Continuation<? super List<LanguageEntity>> $completion) {
    final String _sql = "SELECT * FROM languages WHERE enabled = 1 ORDER BY sortOrder, name";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<LanguageEntity>>() {
      @Override
      @NonNull
      public List<LanguageEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfCode = CursorUtil.getColumnIndexOrThrow(_cursor, "code");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfNativeName = CursorUtil.getColumnIndexOrThrow(_cursor, "nativeName");
          final int _cursorIndexOfScript = CursorUtil.getColumnIndexOrThrow(_cursor, "script");
          final int _cursorIndexOfEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "enabled");
          final int _cursorIndexOfDatasetVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "datasetVersion");
          final int _cursorIndexOfCoverageNote = CursorUtil.getColumnIndexOrThrow(_cursor, "coverageNote");
          final int _cursorIndexOfLicenseNote = CursorUtil.getColumnIndexOrThrow(_cursor, "licenseNote");
          final int _cursorIndexOfSources = CursorUtil.getColumnIndexOrThrow(_cursor, "sources");
          final int _cursorIndexOfMorphology = CursorUtil.getColumnIndexOrThrow(_cursor, "morphology");
          final int _cursorIndexOfSortOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "sortOrder");
          final List<LanguageEntity> _result = new ArrayList<LanguageEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final LanguageEntity _item;
            final String _tmpCode;
            _tmpCode = _cursor.getString(_cursorIndexOfCode);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpNativeName;
            _tmpNativeName = _cursor.getString(_cursorIndexOfNativeName);
            final String _tmpScript;
            _tmpScript = _cursor.getString(_cursorIndexOfScript);
            final boolean _tmpEnabled;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfEnabled);
            _tmpEnabled = _tmp != 0;
            final int _tmpDatasetVersion;
            _tmpDatasetVersion = _cursor.getInt(_cursorIndexOfDatasetVersion);
            final String _tmpCoverageNote;
            _tmpCoverageNote = _cursor.getString(_cursorIndexOfCoverageNote);
            final String _tmpLicenseNote;
            _tmpLicenseNote = _cursor.getString(_cursorIndexOfLicenseNote);
            final List<String> _tmpSources;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfSources);
            _tmpSources = __converters.jsonToStringList(_tmp_1);
            final MorphologyConfig _tmpMorphology;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfMorphology);
            _tmpMorphology = __converters.jsonToMorphology(_tmp_2);
            final int _tmpSortOrder;
            _tmpSortOrder = _cursor.getInt(_cursorIndexOfSortOrder);
            _item = new LanguageEntity(_tmpCode,_tmpName,_tmpNativeName,_tmpScript,_tmpEnabled,_tmpDatasetVersion,_tmpCoverageNote,_tmpLicenseNote,_tmpSources,_tmpMorphology,_tmpSortOrder);
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
  public Object get(final String code, final Continuation<? super LanguageEntity> $completion) {
    final String _sql = "SELECT * FROM languages WHERE code = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, code);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<LanguageEntity>() {
      @Override
      @Nullable
      public LanguageEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfCode = CursorUtil.getColumnIndexOrThrow(_cursor, "code");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfNativeName = CursorUtil.getColumnIndexOrThrow(_cursor, "nativeName");
          final int _cursorIndexOfScript = CursorUtil.getColumnIndexOrThrow(_cursor, "script");
          final int _cursorIndexOfEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "enabled");
          final int _cursorIndexOfDatasetVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "datasetVersion");
          final int _cursorIndexOfCoverageNote = CursorUtil.getColumnIndexOrThrow(_cursor, "coverageNote");
          final int _cursorIndexOfLicenseNote = CursorUtil.getColumnIndexOrThrow(_cursor, "licenseNote");
          final int _cursorIndexOfSources = CursorUtil.getColumnIndexOrThrow(_cursor, "sources");
          final int _cursorIndexOfMorphology = CursorUtil.getColumnIndexOrThrow(_cursor, "morphology");
          final int _cursorIndexOfSortOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "sortOrder");
          final LanguageEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpCode;
            _tmpCode = _cursor.getString(_cursorIndexOfCode);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpNativeName;
            _tmpNativeName = _cursor.getString(_cursorIndexOfNativeName);
            final String _tmpScript;
            _tmpScript = _cursor.getString(_cursorIndexOfScript);
            final boolean _tmpEnabled;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfEnabled);
            _tmpEnabled = _tmp != 0;
            final int _tmpDatasetVersion;
            _tmpDatasetVersion = _cursor.getInt(_cursorIndexOfDatasetVersion);
            final String _tmpCoverageNote;
            _tmpCoverageNote = _cursor.getString(_cursorIndexOfCoverageNote);
            final String _tmpLicenseNote;
            _tmpLicenseNote = _cursor.getString(_cursorIndexOfLicenseNote);
            final List<String> _tmpSources;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfSources);
            _tmpSources = __converters.jsonToStringList(_tmp_1);
            final MorphologyConfig _tmpMorphology;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfMorphology);
            _tmpMorphology = __converters.jsonToMorphology(_tmp_2);
            final int _tmpSortOrder;
            _tmpSortOrder = _cursor.getInt(_cursorIndexOfSortOrder);
            _result = new LanguageEntity(_tmpCode,_tmpName,_tmpNativeName,_tmpScript,_tmpEnabled,_tmpDatasetVersion,_tmpCoverageNote,_tmpLicenseNote,_tmpSources,_tmpMorphology,_tmpSortOrder);
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
  public Object count(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM languages";
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
  public Object nextSortOrder(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COALESCE(MAX(sortOrder), -1) + 1 FROM languages";
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
