package com.ikholopov.personal.myyandextranslate.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ikholopov.personal.myyandextranslate.data.TranslateContract.TranslationEntry;

/**Backend for working with database
 * Created by igor on 4/15/17.
 */

public class TranslateDBBackend {
    private final TranslateDBHelper mHelper;

    //Constructor expects Application context
    public TranslateDBBackend(Context context) {
        mHelper = new TranslateDBHelper(context);
    }

    public Cursor getHistory() {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        String tables = TranslationEntry.TABLE_NAME;
        String[] columns = new String[] {TranslationEntry._ID,
                TranslationEntry.COLUMN_SOURCE_LANGUAGE, TranslationEntry.COLUMN_TARGET_LANGUAGE,
                TranslationEntry.COLUMN_SOURCE_TEXT, TranslationEntry.COLUMN_TRANSLATED_TEXT,
                TranslationEntry.COLUMN_DATE,
                TranslationEntry.COLUMN_FAVORITE};
        String orderBy = TranslationEntry.COLUMN_DATE + " DESC";
        Cursor c = db.query(tables, columns, null, null, null, null, orderBy);
        if(c != null) {
            c.moveToFirst();
        }
        return c;
    }

    public Cursor getFavorites() {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        String tables = TranslationEntry.TABLE_NAME;
        String[] columns = new String[] {TranslationEntry._ID,
                TranslationEntry.COLUMN_SOURCE_LANGUAGE, TranslationEntry.COLUMN_TARGET_LANGUAGE,
                TranslationEntry.COLUMN_SOURCE_TEXT, TranslationEntry.COLUMN_TRANSLATED_TEXT,
                TranslationEntry.COLUMN_DATE,
                TranslationEntry.COLUMN_FAVORITE};
        String orderBy = TranslationEntry.COLUMN_DATE + " DESC";
        Cursor c = db.query(tables, columns, TranslationEntry.COLUMN_FAVORITE + " = " + 1, null, null, null, orderBy);
        if(c != null) {
            c.moveToFirst();
        }
        return c;
    }

    //returns Id
    public long insertItem(String sourceLang, String targetLang, String sourceText, String translatedText,
                           boolean favorite) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        long id = -1;
        db.beginTransaction();
        try {
            final long timeMs = System.currentTimeMillis();
            ContentValues values = new ContentValues();
            values.put(TranslationEntry.COLUMN_DATE, timeMs);
            values.put(TranslationEntry.COLUMN_FAVORITE, favorite);
            values.put(TranslationEntry.COLUMN_SOURCE_LANGUAGE, sourceLang);
            values.put(TranslationEntry.COLUMN_TARGET_LANGUAGE, targetLang);
            values.put(TranslationEntry.COLUMN_SOURCE_TEXT, sourceText);
            values.put(TranslationEntry.COLUMN_TRANSLATED_TEXT, translatedText);
            id = db.insertOrThrow(TranslationEntry.TABLE_NAME, null, values);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return id;
    }

    public Cursor getItem(long id) {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        String tables = TranslationEntry.TABLE_NAME;
        String[] columns = new String[] {TranslationEntry._ID,
                TranslationEntry.COLUMN_SOURCE_LANGUAGE, TranslationEntry.COLUMN_TARGET_LANGUAGE,
                TranslationEntry.COLUMN_SOURCE_TEXT, TranslationEntry.COLUMN_TRANSLATED_TEXT,
                TranslationEntry.COLUMN_DATE,
                TranslationEntry.COLUMN_FAVORITE
        };
        String[] selectors = new String[] {
                String.valueOf(id)
        };
        Cursor c = db.query(tables, columns, TranslationEntry._ID + "=?", selectors, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    public Cursor getItem(String sourceLang, String targetLang, String sourceText) {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        String tables = TranslationEntry.TABLE_NAME;
        String[] columns = new String[] {TranslationEntry._ID,
                TranslationEntry.COLUMN_SOURCE_LANGUAGE, TranslationEntry.COLUMN_TARGET_LANGUAGE,
                TranslationEntry.COLUMN_SOURCE_TEXT, TranslationEntry.COLUMN_TRANSLATED_TEXT,
                TranslationEntry.COLUMN_DATE,
                TranslationEntry.COLUMN_FAVORITE
        };
        String[] selectors = new String[] {
                sourceLang, targetLang, sourceText
        };
        Cursor c = db.query(tables, columns, TranslationEntry.COLUMN_SOURCE_LANGUAGE + "=? AND " +
                        TranslationEntry.COLUMN_TARGET_LANGUAGE + "=? AND " +
                TranslationEntry.COLUMN_SOURCE_TEXT + "=?", selectors, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    public boolean updateDate(long id) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.beginTransaction();
        boolean success = false;
        try {
            final long timeMs = System.currentTimeMillis();
            ContentValues values = new ContentValues();
            values.put(TranslationEntry.COLUMN_DATE, timeMs);
            db.update(TranslationEntry.TABLE_NAME, values, TranslationEntry._ID + "=" + id, null);
            db.setTransactionSuccessful();
            success = true;
        } finally {
            db.endTransaction();
        }
        return success;
    }

    public boolean changeFavorite(long id, boolean favorite) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.beginTransaction();
        boolean success = false;
        try {
            ContentValues values = new ContentValues();
            values.put(TranslationEntry.COLUMN_FAVORITE, favorite);
            db.update(TranslationEntry.TABLE_NAME, values, TranslationEntry._ID + "=" + id, null);
            db.setTransactionSuccessful();
            success = true;
        } finally {
            db.endTransaction();
        }
        return success;
    }

    public boolean clearFavorites() {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.beginTransaction();
        boolean success = false;
        try {
            ContentValues values = new ContentValues();
            values.put(TranslationEntry.COLUMN_FAVORITE, 0);
            db.update(TranslationEntry.TABLE_NAME, values, null, null);
            db.setTransactionSuccessful();
            success = true;
        } finally {
            db.endTransaction();
        }
        return success;
    }

    public boolean clearHistory() {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.beginTransaction();
        boolean success = false;
        try {
            db.delete(TranslationEntry.TABLE_NAME, null, null);
            db.setTransactionSuccessful();
            success = true;
        } finally {
            db.endTransaction();
        }
        return success;
    }
}
