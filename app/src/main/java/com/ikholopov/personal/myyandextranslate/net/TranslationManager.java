package com.ikholopov.personal.myyandextranslate.net;

import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ikholopov.personal.myyandextranslate.BuildConfig;
import com.ikholopov.personal.myyandextranslate.data.TranslateDBProvider;
import com.ikholopov.personal.myyandextranslate.data.Translation;
import com.ikholopov.personal.myyandextranslate.ui.TranslatedViewHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages the id of languages in spinners as well as running the translation process
 * Created by igor on 4/15/17.
 */

public class TranslationManager {

    private final static String LOG_TAG = TranslationManager.class.getSimpleName();

    public final static String REQUEST_TAG = TranslationManager.class.getSimpleName();
    private HashMap<String, Integer> mLanguageKeys;

    //Key for checking if the user want to detect the language
    public static final String DETECT_LANG = "detect-lang";

    public static class TranslateTaskParameters {
        String textToTranslate;
        String sourceLang;
        String targetLang;
        String directionOfTranslation;                                                              //from-to

        public TranslateTaskParameters(String textToTranslate, String sourceLanguage,
                                       String targetLanguage) {
            this.textToTranslate = textToTranslate;
            if(sourceLanguage == null) {
                this.directionOfTranslation = targetLanguage;
            }
            else {
                this.directionOfTranslation = sourceLanguage + "-" + targetLanguage;
            }
            this.sourceLang = sourceLanguage;
            this.targetLang = targetLanguage;
        }
    }

    public TranslationManager(List<Map.Entry<String, String>> languages) {
        mLanguageKeys = new HashMap<String, Integer>();
        for(int i = 0; i < languages.size(); ++i) {
            mLanguageKeys.put(languages.get(i).getKey(), i);
        }
    }

    public int getKeyForRightSpinner(String lang) {
        return mLanguageKeys.get(lang);
    }

    public int getKeyForLeftSpinner(String lang) {
        return mLanguageKeys.get(lang) + 1;                                                         //Since we have "Detect" option
    }

    private final static String getRequestUrl(TranslateTaskParameters args) {
        final String API_KEY = "key";
        final String TEXT_PARAMETER = "text";
        final String LANG_PARAMETER = "lang";
        if(args.sourceLang.equals(DETECT_LANG)) {
            args.directionOfTranslation = args.targetLang;
        }
        Uri builtUri = Uri.parse(BuildConfig.YANDEX_TRANSLATE_DO_TRANSLATION).buildUpon()
                .appendQueryParameter(API_KEY, BuildConfig.YANDEX_TRANSLATE_API_KEY)
                .appendQueryParameter(TEXT_PARAMETER, args.textToTranslate)
                .appendQueryParameter(LANG_PARAMETER, args.directionOfTranslation)
                .build();
        return builtUri.toString();
    }


    //Request using Volley RequestQueue
    public static void translateText(final RequestQueue queue, final TranslateTaskParameters args,
                                     final TranslatedViewHolder viewHolder, final TranslateDBProvider provider) {
        provider.getTranslation(args.sourceLang, args.targetLang, args.textToTranslate, new TranslateDBProvider.DBCallback<Translation>() {
            @Override
            public void onFinished(Translation translation) {
                if(translation != null) {                                                           //Translation found in history
                    InsertAndUpdateTranslatedInfo(provider, translation,
                                         viewHolder);
                    return;
                }
                String url = getRequestUrl(args);                                                   //Translation found in history
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String responce) {
                                final String TEXT_PARAMETER = "text";
                                final String LANG_PARAMETER = "lang";
                                try{
                                    JSONObject root = new JSONObject(responce);
                                    JSONArray texts = root.getJSONArray(TEXT_PARAMETER);
                                    final String translatedText = texts.getString(0);
                                    final String direction = root.getString(LANG_PARAMETER);
                                    args.sourceLang = direction.split("-")[0];
                                    args.targetLang = direction.split("-")[1];
                                    InsertAndUpdateTranslatedInfo(provider, new Translation(0,
                                                    args.textToTranslate, translatedText,
                                                    args.sourceLang, args.targetLang, false),
                                            viewHolder);
                                } catch (JSONException e) {
                                    Log.e(LOG_TAG, e.getMessage(), e);
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                viewHolder.setErrorMode(true);
                            }
                        }
                );
                stringRequest.setTag(REQUEST_TAG);
                queue.add(stringRequest);                                                           //REST API has no-store header, so no caching here, but we are saving the translation to history anyway,
            }
        });                                                                                         //So it's not really a huge issue
    }

    //Insert in model and update view
    private static void InsertAndUpdateTranslatedInfo(final TranslateDBProvider provider, final Translation translation,
                                                      final TranslatedViewHolder viewHolder) {
        provider.insertTranslation(new Translation(0, translation.getSourceText(),
                        translation.getTranslationText(),
                        translation.getSourceLang(), translation.getTargetLang(), false),
                new TranslateDBProvider.DBCallback<Long>() {
                    @Override
                    public void onFinished(Long result) {
                        provider.getTranslation(result, new TranslateDBProvider.DBCallback<Translation>() {
                            @Override
                            public void onFinished(Translation translation) {
                                if(translation != null) {
                                    viewHolder.setTranslatedTextMode(translation, true);
                                }
                            }
                        });
                    }
                });
    }
}
