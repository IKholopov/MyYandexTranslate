package com.ikholopov.personal.myyandextranslate;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * Contains history and favorites tabs
 * Created by igor on 4/15/17.
 */

public class HistoryAndFavoritesFragment extends Fragment{

    //Tag for logs
    private static final String LOG_TAG = "FAVORITES_TAG";

    private FragmentPagerAdapter mAdapterViewPager;


    public HistoryAndFavoritesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favorites, container, false);
        final String[] mModeNames = {getContext().getResources().getString(R.string.history_button),
                getContext().getResources().getString(R.string.favourites_button)};
        ViewPager favoritesPager = (ViewPager)rootView.findViewById(R.id.favorites_pager);
        ViewCompat.setNestedScrollingEnabled(rootView.findViewById(R.id.fav_card_vies), true);
        ViewCompat.setNestedScrollingEnabled(favoritesPager, true);
        mAdapterViewPager = new TranslationListPagerAdapter(getActivity().getSupportFragmentManager());
        favoritesPager.setAdapter(mAdapterViewPager);

        TabLayout tabLayout = (TabLayout)rootView.findViewById(R.id.favorites_tabs);
        tabLayout.setupWithViewPager(favoritesPager);
        for (int i = 0; i < tabLayout.getTabCount(); ++i) {
            tabLayout.getTabAt(i).setText(mModeNames[i]);
        }
        return rootView;
    }
}
