package com.ikholopov.personal.myyandextranslate;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

/**
 * Adapter to switch between translation fragment, history/favorites and settings
 * Created by igor on 4/9/17.
 */

public class TranslatePagerAdapter extends
        FragmentStatePagerAdapter {

    //3 fragments to switch between
    public static int NUM_ITEMS = 3;

    public TranslatePagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    private TranslateFragment mTranslateFragment;

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new TranslateFragment();
            case 1:
                return new HistoryAndFavoritesFragment();
            case 2:
                return new SettingsFragment();
            default:
                return null;
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {                              //Needed to get TranslationFragment from MainActivity
        Fragment createdFragment = (Fragment) super.instantiateItem(container, position);
        switch (position) {
            case 0:
                mTranslateFragment = (TranslateFragment) createdFragment;
                break;
        }
        return createdFragment;
    }

    public TranslateFragment getTranslateFragment() {
        return mTranslateFragment;
    }
}
