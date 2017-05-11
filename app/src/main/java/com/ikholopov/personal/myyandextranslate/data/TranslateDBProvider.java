package com.ikholopov.personal.myyandextranslate.data;

import android.content.Context;
import android.database.Cursor;
import android.os.Looper;
import android.os.Handler;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Provider to work with the model
 * Created by igor on 4/15/17.
 */

public class TranslateDBProvider {
    private final TranslateDBBackend mBackend;
    private final NotificationManager mNotifications;
    private final DBProviderExecutor mExecutor;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    public static final int COLUMN_ID_ROW_ID = 0;
    public static final int COLUMN_SOURCE_LANGUAGE_ROW_ID = 1;
    public static final int COLUMN_TARGET_LANGUAGE_ROW_ID = 2;
    public static final int COLUMN_SOURCE_TEXT_ROW_ID = 3;
    public static final int COLUMN_TRANSLATED_TEXT_ROW_ID = 4;
    public static final int COLUMN_DATE_ROW_ID = 5;
    public static final int COLUMN_FAVORITE_ROW_ID = 6;

    public interface DBCallback<Result> {
        void onFinished(Result result);
    }

    TranslateDBProvider(Context context) {              //Pass application context?
        mBackend = new TranslateDBBackend(context);
        mNotifications = TranslateDBContainer.getNotificationManagerInstance();
        mExecutor = new DBProviderExecutor();
    }

    public void getHistory(final DBCallback<Cursor> callback) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final Cursor c = mBackend.getHistory();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFinished(c);
                    }
                });
            }
        });
    }

    public void getFavorites(final DBCallback<Cursor> callback) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final Cursor c = mBackend.getFavorites();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFinished(c);
                    }
                });
            }
        });
    }

    public void insertTranslation(final Translation translation,
                                  final DBCallback<Long> callback) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final long translationId;
                Cursor c = mBackend.getItem(translation.getSourceLang(), translation.getTargetLang(),
                        translation.getSourceText());
                if(c != null && c.getCount() > 0) {
                    translationId = c.getInt(COLUMN_ID_ROW_ID);
                    mBackend.updateDate(translationId);
                } else {
                     translationId =
                            mBackend.insertItem(translation.getSourceLang(), translation.getTargetLang(),
                                    translation.getSourceText(), translation.getTranslationText(), false);
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFinished(translationId);
                    }
                });
            }
        });
        mNotifications.notifyListeners();
    }

    public void getTranslation(final long id, final DBCallback<Translation> callback) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Cursor c = mBackend.getItem(id);
                final Translation translation;
                if(c != null && c.getCount() > 0) {
                    translation = new Translation(c.getInt(COLUMN_ID_ROW_ID),
                            c.getString(COLUMN_SOURCE_TEXT_ROW_ID), c.getString(COLUMN_TRANSLATED_TEXT_ROW_ID),
                            c.getString(COLUMN_SOURCE_LANGUAGE_ROW_ID), c.getString(COLUMN_TARGET_LANGUAGE_ROW_ID),
                            c.getInt(COLUMN_FAVORITE_ROW_ID) > 0);

                } else {
                    translation = null;
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFinished(translation);
                    }
                });
            }
        });
    }

    public void getTranslation(final String sourceLang, final String targetLang, final String textToTranslate,      //We want to avoid duplications of entries
                               final DBCallback<Translation> callback) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Cursor c = mBackend.getItem(sourceLang, targetLang,
                        textToTranslate);
                final Translation translation;
                if(c != null && c.getCount() > 0) {
                    translation = new Translation(c.getInt(COLUMN_ID_ROW_ID),
                            c.getString(COLUMN_SOURCE_TEXT_ROW_ID), c.getString(COLUMN_TRANSLATED_TEXT_ROW_ID),
                            c.getString(COLUMN_SOURCE_LANGUAGE_ROW_ID), c.getString(COLUMN_TARGET_LANGUAGE_ROW_ID),
                            c.getInt(COLUMN_FAVORITE_ROW_ID) > 0);

                } else {
                    translation = null;
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFinished(translation);
                    }
                });
            }
        });
    }

    public void addToFavorites(final long translationId, final DBCallback<Boolean> callback) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final boolean success = mBackend.changeFavorite(translationId, true);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFinished(success);
                    }
                });
            }
        });
        mNotifications.notifyListeners();
    }

    public void removeFromFavorites(final long translationId, final DBCallback<Boolean> callback) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final boolean success = mBackend.changeFavorite(translationId, false);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFinished(success);
                    }
                });
            }
        });
        mNotifications.notifyListeners();
    }

    public void clearFavorites(final DBCallback<Boolean> callback) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final boolean success = mBackend.clearFavorites();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFinished(success);
                    }
                });
            }
        });
        mNotifications.notifyListeners();
    }

    public void clearHistory(final DBCallback<Boolean> callback) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final boolean success = mBackend.clearHistory();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFinished(success);
                    }
                });
            }
        });
        mNotifications.notifyListeners();
    }

    class DBProviderExecutor extends ThreadPoolExecutor {
        DBProviderExecutor () {
            super(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        }
    }
}
