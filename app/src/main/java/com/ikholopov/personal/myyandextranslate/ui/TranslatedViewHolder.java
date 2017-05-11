package com.ikholopov.personal.myyandextranslate.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ikholopov.personal.myyandextranslate.R;
import com.ikholopov.personal.myyandextranslate.data.Translation;

/**
 * ViewHolder to manage loading of translation and displaying of results
 * Created by igor on 4/20/17.
 */

public class TranslatedViewHolder {
    private long mTranslationId;
    public TextView mTranslatedText;
    public ProgressBar mProgress;
    public ScrollView mScrollView;
    public AppCompatButton mTranslateButton;
    public TextView mFailedToConnectText;
    public AppCompatButton mFailedToConnectButton;
    public AppCompatButton mFavIconButton;
    private Context mContext;
    public boolean mFavorite;

    public TranslatedViewHolder(long translationId, TextView view, ProgressBar progress, ScrollView scrollView,
                                AppCompatButton translateButton,
                                TextView failedToConnectConnectView,
                                AppCompatButton failedToConnectButton,
                                AppCompatButton favIconButton, Context context) {
        mTranslationId = translationId;
        mTranslatedText = view;
        mProgress = progress;
        mScrollView = scrollView;

        mTranslateButton = translateButton;
        mFailedToConnectText = failedToConnectConnectView;
        mFailedToConnectButton = failedToConnectButton;
        mFavIconButton = favIconButton;
        mContext = context;
    }

    //Display translation
    public void setTranslatedTextMode(Translation translation, boolean animate) {
        if(translation == null) {
            this.setReadyToTranslateMode(animate);
            return;
        }
        mTranslationId = translation.getId();
        mFavorite = translation.isFavorite();
        Drawable favIcon = mFavorite ?  ContextCompat.getDrawable(mContext, R.mipmap.ic_turned_on) :
                ContextCompat.getDrawable(mContext, R.mipmap.ic_turned_off);
        favIcon.mutate().setColorFilter(ContextCompat.getColor(mContext, R.color.colorAccent), PorterDuff.Mode.SRC_IN);
        mFavIconButton.setBackgroundDrawable(favIcon);
        mTranslatedText.setText(translation.getTranslationText());
        showTranslated(animate);
        hideError(animate);
        hideLoading(animate);
        hideReadyToTranslate(animate);
    }

    public void setReadyToTranslateMode(boolean animate) {
        showReadyToTranslate(animate);
        hideTranslated(animate);
        hideError(animate);
        hideLoading(animate);
    }

    public void setErrorMode(boolean animate) {
        showError(animate);
        hideReadyToTranslate(animate);
        hideTranslated(animate);
        hideLoading(animate);
    }

    public void setLoadingMode(boolean animate) {
        showLoading(animate);
        hideError(animate);
        hideReadyToTranslate(animate);
        hideTranslated(animate);
    }

    //Show and hide Elements

    private void showTranslated(boolean animate) {
        if(mScrollView.getVisibility() == View.VISIBLE) {
            return;
        }
        if(animate) {
            Animation slide_up = AnimationUtils.loadAnimation(mContext.getApplicationContext(),
                    R.anim.slide_up);
            mScrollView.startAnimation(slide_up);
        }
        mScrollView.setVisibility(View.VISIBLE);
    }

    private void hideTranslated(boolean animate) {
        if(mScrollView.getVisibility() == View.GONE) {
            return;
        }
        if(animate) {
            Animation slide_down = AnimationUtils.loadAnimation(mContext.getApplicationContext(),
                    R.anim.slide_down);
            mScrollView.startAnimation(slide_down);
        }
        mScrollView.setVisibility(View.GONE);
    }

    private void showLoading(boolean animate) {
        mProgress.setVisibility(View.VISIBLE);
    }

    private void hideLoading(boolean animate) {
        mProgress.setVisibility(View.GONE);
    }

    private void showError(boolean animate) {
        mFailedToConnectText.setVisibility(View.VISIBLE);
        mFailedToConnectButton.setVisibility(View.VISIBLE);
    }

    private void hideError(boolean animate) {
        mFailedToConnectText.setVisibility(View.GONE);
        mFailedToConnectButton.setVisibility(View.GONE);
    }

    private void showReadyToTranslate(boolean animate) {
        if(mTranslateButton.getVisibility() == View.VISIBLE) {
            return;
        }
        if(animate) {
            Animation slide_up = AnimationUtils.loadAnimation(mContext.getApplicationContext(),
                    R.anim.slide_up);
            mTranslateButton.startAnimation(slide_up);
        }
        mTranslateButton.setVisibility(View.VISIBLE);
    }

    private void hideReadyToTranslate(boolean animate) {
        if(mTranslateButton.getVisibility() == View.GONE) {
            return;
        }
        if(animate) {
            Animation slide_down = AnimationUtils.loadAnimation(mContext.getApplicationContext(),
                    R.anim.slide_down);
            mTranslateButton.startAnimation(slide_down);
        }
        mTranslateButton.setVisibility(View.GONE);
    }

    public long getId() {
        return mTranslationId;
    }
}
