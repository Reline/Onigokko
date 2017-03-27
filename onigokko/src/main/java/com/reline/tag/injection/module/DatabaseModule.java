package com.reline.tag.injection.module;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteOpenHelper;

import com.reline.tag.database.DatabaseAccessObject;
import com.reline.tag.database.DatabaseHelper;
import com.reline.tag.database.SqlBriteDao;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import dagger.Module;
import dagger.Provides;
import rx.schedulers.Schedulers;

@Module
public class DatabaseModule {

    @Provides
    SQLiteOpenHelper provideDatabaseHelper(Context context) {
        return new DatabaseHelper(context);
    }

    @Provides
    SqlBrite provideSqlBrite() {
        return new SqlBrite.Builder().build();
    }

    @Provides
    BriteDatabase provideDatabase(SqlBrite sqlBrite, SQLiteOpenHelper databaseHelper) {
        final BriteDatabase db = sqlBrite.wrapDatabaseHelper(databaseHelper, Schedulers.io());
        db.setLoggingEnabled(true);
        return db;
    }

    @Provides
    DatabaseAccessObject provideDao(BriteDatabase db, SharedPreferences preferences) {
        return new SqlBriteDao(db, preferences);
    }
}
