package com.ikholopov.personal.myyandextranslate.data;

import android.provider.BaseColumns;

/**
 * Model description
 * Created by igor on 4/11/17.
 */

public class TranslateContract {
    public static final class TranslationEntry implements BaseColumns {
        public static final String TABLE_NAME = "translations";
        public static final String COLUMN_SOURCE_LANGUAGE = "source_lang";
        public static final String COLUMN_TARGET_LANGUAGE = "target_lang";
        public static final String COLUMN_SOURCE_TEXT = "source_text";
        public static final String COLUMN_TRANSLATED_TEXT = "target_text";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_FAVORITE = "favorite";
    }
}
