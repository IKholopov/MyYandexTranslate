package com.ikholopov.personal.myyandextranslate.data;

import android.os.Handler;
import android.os.Looper;

import java.util.HashSet;

/**
 * Notification manager, which run notifies view when model is changed
 * Created by igor on 4/15/17.
 */

public class NotificationManager {
    private HashSet<Listener> mListeners = new HashSet<>();
    private Handler mHandler = new Handler(Looper.getMainLooper()); //Handler on UI
    private Runnable mNotifyUI = new Runnable() {
        @Override
        public void run() {
            notifyUiThread();
        }
    };

    public interface Listener {
        void onContentChanged();
    }

    public void addListener(Listener listener) {
        mListeners.add(listener);
    }

    public void removeListener(Listener listener) {
        mListeners.remove(listener);
    }

    void notifyListeners() {
        mHandler.removeCallbacks(mNotifyUI);
        mHandler.post(mNotifyUI);
    }

    private void notifyUiThread() {
        for(Listener listener: mListeners) {
            listener.onContentChanged();
        }
    }
}
