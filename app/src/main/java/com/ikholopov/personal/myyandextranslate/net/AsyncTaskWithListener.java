package com.ikholopov.personal.myyandextranslate.net;

import android.os.AsyncTask;

/**
 * Created by igor on 4/15/17.
 */

public abstract class AsyncTaskWithListener<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
    public abstract void setListener(IAsyncTaskListener listener);

    public AsyncTaskWithListener executeWithListener(IAsyncTaskListener listener, Params... params) {
        this.setListener(listener);
        this.execute(params);
        return this;
    }
}
