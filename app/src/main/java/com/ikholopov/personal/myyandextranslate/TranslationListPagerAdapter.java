package com.ikholopov.personal.myyandextranslate;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Adapter to switch between history and favorites
 * Created by igor on 4/10/17.
 */

public class TranslationListPagerAdapter extends FragmentPagerAdapter{

    public TranslationListPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Fragment getItem(int position) {
        TranslationsListPageFragment fragment = new TranslationsListPageFragment();
        Bundle args = new Bundle();
        args.putString(TranslationsListPageFragment.PAGE_MODE_ARGUMENT,
                position == 0 ?
                        TranslationsListPageFragment.PageMode.HISTORY.toString() :
                        TranslationsListPageFragment.PageMode.FAVORITES.toString());
        fragment.setArguments(args);
        return fragment;
    }
}
