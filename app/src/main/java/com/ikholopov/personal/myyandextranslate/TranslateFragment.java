package com.ikholopov.personal.myyandextranslate;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.ikholopov.personal.myyandextranslate.data.LanguagesInfo;
import com.ikholopov.personal.myyandextranslate.data.NotificationManager;
import com.ikholopov.personal.myyandextranslate.data.TranslateDBContainer;
import com.ikholopov.personal.myyandextranslate.data.TranslateDBProvider;
import com.ikholopov.personal.myyandextranslate.data.Translation;
import com.ikholopov.personal.myyandextranslate.net.GetLanguagesInfoTask;
import com.ikholopov.personal.myyandextranslate.net.IAsyncTaskListener;
import com.ikholopov.personal.myyandextranslate.net.TranslationManager;
import com.ikholopov.personal.myyandextranslate.ui.TranslateEditText;
import com.ikholopov.personal.myyandextranslate.ui.TranslatedViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.ikholopov.personal.myyandextranslate.net.TranslationManager.DETECT_LANG;


public class TranslateFragment extends Fragment{

    //Tag for logs
    private static final String LOG_TAG = "TRANSLATE_TAG";

    //Keys for saving instance
    private static final String STATE_KEY_TRANSLATED = "STATE_KEY_TRANSLATED";
    private static final String STATE_KEY_TRANSLATED_ID = "STATE_KEY_TRANSLATED_ID";

    private HashMap<String, String> mLanguages = null;
    private ArrayList<String> mLeftLanguageResources = new ArrayList<String>();
    private ArrayList<String> mLeftLanguageKeys = new ArrayList<String>();
    private ArrayList<String> mRightLanguageResources = new ArrayList<String>();
    private ArrayList<String> mRightLanguageKeys = new ArrayList<String>();

    private Spinner mLeftSpinner;
    private LanguageSpinnerAdapter mLeftSpinnerAdapter;

    private Spinner mRightSpinner;
    private LanguageSpinnerAdapter mRightSpinnerAdapter;

    private GetLanguagesInfoTask getLanguagesInfoTask = null;
    private RequestQueue mQueue = null;

    private TranslateEditText mTextToTranslate;

    private TranslatedViewHolder mTranslatedViewHolder;
    private TranslationManager mTranslationManager;

    private TranslateDBProvider mDbProvider;
    private NotificationManager mNotificationManager;
    private NotificationManager.Listener mNotificationListener = new NotificationManager.Listener() {   //Listening for changes of model
        @Override                                                                                       //(ex. adding to favorites)
        public void onContentChanged() {
            if(mTranslatedViewHolder != null) {
                mDbProvider.getTranslation(mTranslatedViewHolder.getId(), new TranslateDBProvider.DBCallback<Translation>() {
                    @Override
                    public void onFinished(Translation translation) {
                        if(translation != null && mTranslatedViewHolder != null) {
                            mTranslatedViewHolder.setTranslatedTextMode(translation, true);
                        }
                    }
                });
            }
        }
    };


    public TranslateFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Providers
        mDbProvider = TranslateDBContainer.getProviderInstance(getActivity().getApplicationContext());
        mNotificationManager = TranslateDBContainer.getNotificationManagerInstance();
        View rootView = inflater.inflate(R.layout.fragment_translate, container, false);

        mQueue = Volley.newRequestQueue(getActivity());

        //Spinners
        mLeftSpinner = (Spinner)rootView.findViewById(R.id.left_spinner);
        mLeftSpinnerAdapter = new LanguageSpinnerAdapter(getContext(), mLeftLanguageResources);
        mLeftSpinner.setAdapter(mLeftSpinnerAdapter);
        mRightSpinner = (Spinner)rootView.findViewById(R.id.right_spinner);
        mRightSpinnerAdapter = new LanguageSpinnerAdapter(getContext(), mRightLanguageResources);
        mRightSpinner.setAdapter(mRightSpinnerAdapter);
        mRightSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mRightSpinnerAdapter.mSelectedPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        initializeSpinners();

        //Swap Button
        final AppCompatButton swapButton = (AppCompatButton)rootView.findViewById(R.id.swap_button);
        swapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation rotate = AnimationUtils.loadAnimation(getContext(), R.anim.barrel_roll);
                swapButton.startAnimation(rotate);
                if(mLeftSpinner.getSelectedItemPosition() == 0) {
                    return;
                }
                int swapPosition = mLeftSpinner.getSelectedItemPosition() - 1;
                mLeftSpinner.setSelection(mRightSpinner.getSelectedItemPosition() + 1, true);
                mRightSpinner.setSelection(swapPosition);
            }
        });
        if(mLeftSpinner.getSelectedItemPosition() == 0) {
            swapButton.setEnabled(false);
        }
        mLeftSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mLeftSpinnerAdapter.mSelectedPosition = position;
                if(position == 0) {                                                                 //Disable swapping when "Detect" option is selected for language
                    swapButton.setEnabled(false);
                    return;
                }
                swapButton.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //Translated Field
        long id = (savedInstanceState != null) && (savedInstanceState.getBoolean(STATE_KEY_TRANSLATED)) ?
                savedInstanceState.getLong(STATE_KEY_TRANSLATED_ID) : 0;
        mTranslatedViewHolder = new TranslatedViewHolder(id,
                (TextView) rootView.findViewById(R.id.translated_text),
                (ProgressBar) rootView.findViewById(R.id.progress_bar_translation),
                (ScrollView) rootView.findViewById(R.id.translated_text_scroll_view),
                (AppCompatButton) rootView.findViewById(R.id.translate_button),
                (TextView) rootView.findViewById(R.id.error_text),
                (AppCompatButton) rootView.findViewById(R.id.retry_button),
                (AppCompatButton) rootView.findViewById(R.id.translated_text_fav),
                getContext());
        mTranslatedViewHolder.mFailedToConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mTextToTranslate.getText().toString().equals("")) {
                    translateText(mTextToTranslate.getText().toString());
                }
            }
        });
        mTranslatedViewHolder.mTranslateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mTextToTranslate.getText().toString().equals("")) {
                    translateText(mTextToTranslate.getText().toString());
                }
                InputMethodManager inputManager = (InputMethodManager)                              //Hide keyboard
                        getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });
        mTranslatedViewHolder.mFavIconButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mTranslatedViewHolder.mFavorite) {
                    mDbProvider.removeFromFavorites(mTranslatedViewHolder.getId(), new TranslateDBProvider.DBCallback<Boolean>() {
                        @Override
                        public void onFinished(Boolean aBoolean) {
                            if(mTranslatedViewHolder != null) {
                                mTranslatedViewHolder.mFavorite = false;
                            }
                        }
                    });
                } else {
                    mDbProvider.addToFavorites(mTranslatedViewHolder.getId(), new TranslateDBProvider.DBCallback<Boolean>() {
                        @Override
                        public void onFinished(Boolean aBoolean) {
                            if(mTranslatedViewHolder != null) {
                                mTranslatedViewHolder.mFavorite = true;
                            }
                        }
                    });
                }
            }
        });

        //Translate Field
        mTextToTranslate = (TranslateEditText)rootView.findViewById(R.id.text_to_translate);
        mTextToTranslate.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE ) {
                    translateText(mTextToTranslate.getText().toString());
                    return false;
                }
                return false;
            }
        });
        AppCompatButton clearButton = (AppCompatButton) rootView.findViewById(R.id.clear_text_to_translate);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTextToTranslate.setText("");
                mTranslatedViewHolder.setReadyToTranslateMode(true);
            }
        });

        //Restoring state
        if(savedInstanceState != null && savedInstanceState.getBoolean(STATE_KEY_TRANSLATED)) {
            mDbProvider.getTranslation(savedInstanceState.getLong(STATE_KEY_TRANSLATED_ID), new TranslateDBProvider.DBCallback<Translation>() {
                @Override
                public void onFinished(Translation translation) {
                    if(mTranslatedViewHolder != null) {
                        mTranslatedViewHolder.setTranslatedTextMode(translation, false);
                    }
                }
            });
        } else {
            mTranslatedViewHolder.setReadyToTranslateMode(true);
        }

        return rootView;
    }

    @Override
    public void onStop() {
        if(getLanguagesInfoTask != null) {
            getLanguagesInfoTask.setListener(null);
        }
        if(mQueue != null) {
            mQueue.cancelAll(TranslationManager.REQUEST_TAG);
        }
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);          //Saving last chosen languages
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(R.string.left_last_lang), mLeftSpinner.getSelectedItemPosition());
        editor.putInt(getString(R.string.right_last_lang), mRightSpinner.getSelectedItemPosition());
        editor.commit();
        mNotificationManager.removeListener(mNotificationListener);

        super.onStop();
    }

    @Override
    public void onStart() {
        mNotificationManager.addListener(mNotificationListener);
        super.onStart();
    }

    @Override
    public void onSaveInstanceState(Bundle savedState) {
        savedState.putBoolean(STATE_KEY_TRANSLATED,
                mTranslatedViewHolder.mScrollView.getVisibility() != View.GONE );
        savedState.putLong(STATE_KEY_TRANSLATED_ID,
                mTranslatedViewHolder.getId());
        super.onSaveInstanceState(savedState);
    }

    //Passing translation from history or favorites
    public void UpdateSelectedTranslation(Translation selectedTranslation) {
        if( selectedTranslation != null) {
            mTextToTranslate.setText(selectedTranslation.getSourceText());
            mTranslatedViewHolder.setTranslatedTextMode(selectedTranslation, true);
            mLeftSpinner.setSelection(mTranslationManager.getKeyForLeftSpinner(
                    selectedTranslation.getSourceLang()), true);
            mRightSpinner.setSelection(mTranslationManager.getKeyForRightSpinner(
                    selectedTranslation.getTargetLang()), true);
        }
    }

    private IAsyncTaskListener<Boolean> onLanguageInfoUpdated() {
        return new IAsyncTaskListener<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                if(result) {
                    mLanguages = LanguagesInfo.INFO.getLanguagesInfo();
                    languageInfoToAdapters(true);
                }
                else {
                    Toast.makeText(getActivity(), "No connection", Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    //Insert languages into spinners
    private void languageInfoToAdapters(boolean finished) {
        if(mLanguages == null || mLanguages.size() == 0) {
            return;
        }
        mLeftLanguageKeys.clear();
        mLeftLanguageResources.clear();
        mRightLanguageKeys.clear();
        mRightLanguageResources.clear();
        List<Map.Entry<String, String>> list =
                new LinkedList<Map.Entry<String, String>>(mLanguages.entrySet() );
        Collections.sort( list, new Comparator<Map.Entry<String, String>>()
        {
            public int compare( Map.Entry<String, String> o1, Map.Entry<String, String> o2 )
            {
                return (o1.getValue()).compareTo( o2.getValue() );
            }
        } );
        mLeftLanguageKeys.add(DETECT_LANG);
        mLeftLanguageResources.add(getString(R.string.detect_lang_name));
        for(Map.Entry<String, String> val: list) {
            mLeftLanguageKeys.add(val.getKey());
            mLeftLanguageResources.add(val.getValue());
            mRightLanguageKeys.add(val.getKey());
            mRightLanguageResources.add(val.getValue());
        }
        mLeftSpinnerAdapter.notifyDataSetChanged();
        mRightSpinnerAdapter.notifyDataSetChanged();
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        int defaultLeftValue = getResources().getInteger(R.integer.left_language_default);
        int defaultRightValue = getResources().getInteger(R.integer.right_language_default);
        mLeftSpinner.setSelection(sharedPref.getInt(getString(R.string.left_last_lang),
                                                    defaultLeftValue), true);
        mRightSpinner.setSelection(sharedPref.getInt(getString(R.string.right_last_lang),
                                                               defaultRightValue), true);
        mTranslationManager = new TranslationManager(list);
    }

    private void initializeSpinners() {
        mLanguages = LanguagesInfo.INFO.getLanguagesInfo();
        if(mLanguages == null) {
            if(getLanguagesInfoTask != null) {
                getLanguagesInfoTask.setListener(null);
            }
            getLanguagesInfoTask = new GetLanguagesInfoTask(getContext().getFilesDir());
            getLanguagesInfoTask.executeWithListener(onLanguageInfoUpdated(), false);
            languageInfoToAdapters(false);
        }
        else {
            languageInfoToAdapters(true);
        }
    }

    private void translateText(String text) {
        if(mLanguages == null || mLanguages.size() == 0) {
            return;
        }
        String sourceLang = mLeftLanguageKeys.get(mLeftSpinner.getSelectedItemPosition());
        String targetLang = mRightLanguageKeys.get(mRightSpinner.getSelectedItemPosition());
        mTranslatedViewHolder.setLoadingMode(true);
        TranslationManager.translateText(mQueue,
                new TranslationManager.TranslateTaskParameters(text, sourceLang, targetLang),
                mTranslatedViewHolder, mDbProvider);
    }
}
