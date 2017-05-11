package com.ikholopov.personal.myyandextranslate.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.ikholopov.personal.myyandextranslate.data.TranslateContract.*;

/**
 * Helper to create/update database of translations
 * Created by igor on 4/15/17.
 */

public class TranslateDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "translations.db";
    public static final int DATABASE_VERSION = 1;


    public TranslateDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_TRANSLATIONS_TABLE = "CREATE TABLE " + TranslationEntry.TABLE_NAME +
                " (" +
                        TranslationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        TranslationEntry.COLUMN_SOURCE_LANGUAGE + " TEXT NOT NULL, " +
                        TranslationEntry.COLUMN_TARGET_LANGUAGE + " TEXT NOT NULL, " +
                        TranslationEntry.COLUMN_SOURCE_TEXT + " TEXT NOT NULL, " +
                        TranslationEntry.COLUMN_TRANSLATED_TEXT+ " TEXT NOT NULL, " +
                        TranslationEntry.COLUMN_DATE + " INTEGER UNIQUE NOT NULL, " +
                        TranslationEntry.COLUMN_FAVORITE + " INTEGER NOT NULL " +
                ");";
        db.execSQL(SQL_CREATE_TRANSLATIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TranslationEntry.TABLE_NAME);
        onCreate(db);
    }
}
