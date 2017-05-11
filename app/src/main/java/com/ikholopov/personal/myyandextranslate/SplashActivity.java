package com.ikholopov.personal.myyandextranslate;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.ikholopov.personal.myyandextranslate.net.GetLanguagesInfoTask;
import com.ikholopov.personal.myyandextranslate.net.IAsyncTaskListener;

/**
 * Splash screen. Also handles loading of languages config
 * Created by igor on 4/23/17.
 */

public class SplashActivity extends AppCompatActivity {

    class TaskListener implements IAsyncTaskListener<Boolean> {
        SplashActivity mActivity;

        TaskListener(SplashActivity activity) {
            mActivity = activity;
        }

        @Override
        public void onSuccess(Boolean success) {
            if(success) {
                Intent intent = new Intent(mActivity, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mActivity.redirect();
                    }
                });
                builder.setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
    }

    GetLanguagesInfoTask mTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //redirect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        redirect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mTask.setListener(null);
    }

    void redirect() {
        mTask = new GetLanguagesInfoTask(getApplicationContext().getFilesDir());
        mTask.executeWithListener(new TaskListener(this), false);
    }
}

