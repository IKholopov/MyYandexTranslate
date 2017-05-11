package com.ikholopov.personal.myyandextranslate;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ikholopov.personal.myyandextranslate.data.TranslateDBContainer;
import com.ikholopov.personal.myyandextranslate.data.TranslateDBProvider;
import com.ikholopov.personal.myyandextranslate.net.GetLanguagesInfoTask;


public class SettingsFragment extends Fragment{

    //Tag for logs
    private static final String LOG_TAG = "SETTINGS_TAG";

    private GetLanguagesInfoTask getLanguagesInfoTask = null;

    private TranslateDBProvider mProvider;

    public SettingsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mProvider = TranslateDBContainer.getProviderInstance(getActivity().getApplicationContext());

        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        AppCompatButton refreshLangs = (AppCompatButton) rootView.findViewById(R.id.reset_languages);   //Refreshes languages by forcing loading of languages config
        refreshLangs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLanguagesInfoTask = new GetLanguagesInfoTask(getContext().getFilesDir());
                getLanguagesInfoTask.execute(true);
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(getContext(), getString(R.string.refreshing_languages_database), duration);
                toast.show();
            }
        });
        AppCompatButton clearHistory = (AppCompatButton) rootView.findViewById(R.id.clear_history);
        clearHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProvider.clearHistory(new TranslateDBProvider.DBCallback<Boolean>() {
                    @Override
                    public void onFinished(Boolean aBoolean) {
                        int duration = Toast.LENGTH_SHORT;
                        if(!isAdded()) {
                            return;
                        }
                        Toast toast = Toast.makeText(getContext(), getString(R.string.deleted_history), duration);
                        toast.show();
                    }
                });
            }
        });
        AppCompatButton clearFavorites = (AppCompatButton) rootView.findViewById(R.id.clear_favorites);
        clearFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProvider.clearFavorites(new TranslateDBProvider.DBCallback<Boolean>() {
                    @Override
                    public void onFinished(Boolean aBoolean) {
                        int duration = Toast.LENGTH_SHORT;
                        if(!isAdded()) {
                            return;
                        }
                        Toast toast = Toast.makeText(getContext(), getString(R.string.deleted_favorites), duration);
                        toast.show();
                    }
                });
            }
        });
        return rootView;
    }
}
