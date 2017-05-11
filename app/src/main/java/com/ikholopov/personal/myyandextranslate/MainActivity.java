package com.ikholopov.personal.myyandextranslate;

import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.TabLayout;

import com.ikholopov.personal.myyandextranslate.data.TranslateDBContainer;
import com.ikholopov.personal.myyandextranslate.data.TranslateDBProvider;
import com.ikholopov.personal.myyandextranslate.data.Translation;

public class MainActivity extends AppCompatActivity implements TranslationsListPageFragment.SetTranslationListener{

    private TranslatePagerAdapter mAdapterViewPager;
    private TabLayout mTabLayout;
    private Translation mTranslation;                                                               //Selected translation to pass to TranslateFragment
    private int[] mTabIcons = {R.drawable.ic_translate,
                               R.drawable.ic_favorites,
                               R.drawable.ic_settings};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewPager mainPager = (ViewPager)findViewById(R.id.main_pager);

        mAdapterViewPager = new TranslatePagerAdapter(getSupportFragmentManager());
        mainPager.setAdapter(mAdapterViewPager);

        mTabLayout = (TabLayout)findViewById(R.id.sliding_tabs);
        mTabLayout.setupWithViewPager(mainPager);
        for (int i = 0; i < mTabLayout.getTabCount(); ++i) {
            mTabLayout.getTabAt(i).setIcon(mTabIcons[i]);
        }
        mTabLayout.addOnTabSelectedListener(
                new TabLayout.ViewPagerOnTabSelectedListener(mainPager) {

                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        super.onTabSelected(tab);
                        int tabIconColor = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);
                        tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {
                        super.onTabUnselected(tab);
                        int tabIconColor = ContextCompat.getColor(getApplicationContext(), R.color.colorLight);
                        tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {
                        super.onTabReselected(tab);
                        int tabIconColor = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);
                        tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                    }
                }
        );
        mTabLayout.getTabAt(0).select();
    }

    //Pass chosen translation to TranslateFragment
    @Override
    public void setTranslation(long id) {
        TranslateDBContainer.getProviderInstance(getApplicationContext()).getTranslation(id, new TranslateDBProvider.DBCallback<Translation>() {
            @Override
            public void onFinished(Translation translation) {
                mTranslation = translation;
                mAdapterViewPager.getTranslateFragment().UpdateSelectedTranslation(mTranslation);
                mTabLayout.getTabAt(0).select();
            }
        });
    }
}
