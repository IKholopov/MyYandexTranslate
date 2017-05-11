package com.ikholopov.personal.myyandextranslate.net;

import android.content.Context;
import android.os.AsyncTask;

import com.ikholopov.personal.myyandextranslate.data.LanguagesInfo;

import java.io.File;
import java.util.HashMap;


import static com.ikholopov.personal.myyandextranslate.data.LanguagesInfo.INFO;

/**
 * Task to fetch languages from config file - either from config file or from the internet
 * Created by igor on 4/12/17.
 */

public class GetLanguagesInfoTask extends AsyncTaskWithListener<Boolean, Void, Boolean>{
    private IAsyncTaskListener<Boolean> mListener = null;
    private File mDirs;

    public GetLanguagesInfoTask(File dirs) {
        mDirs = dirs;
    }

    @Override
    protected Boolean doInBackground(Boolean... params) {
        LanguagesInfo.INFO.fetchLanguagesInfo(mDirs, params[0]);
        HashMap<String, String> map = LanguagesInfo.INFO.getLanguagesInfo();
        if(map != null && map.size() > 0) {
            return true;
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
            if (mListener != null) {                                                                //As far as I know, onPause of activity and onPostExecute are running in one thread, so no race condition
                mListener.onSuccess(result);
            }
    }

    @Override
    public void setListener(IAsyncTaskListener listener) {
        mListener = listener;
    }
}
