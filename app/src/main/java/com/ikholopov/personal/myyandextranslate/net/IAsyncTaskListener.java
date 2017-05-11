package com.ikholopov.personal.myyandextranslate.net;

/**
 * Created by igor on 4/15/17.
 */

public interface IAsyncTaskListener<Result> {
    void onSuccess(Result result);
}
