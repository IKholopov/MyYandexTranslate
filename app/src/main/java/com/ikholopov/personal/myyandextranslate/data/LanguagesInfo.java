package com.ikholopov.personal.myyandextranslate.data;

import android.net.Uri;
import android.util.Log;

import com.ikholopov.personal.myyandextranslate.BuildConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

/**
 * LanguagesInfo - singleton, containing information on languages that are supported
 *
 * Created by igor on 4/12/17.
 */

public final class LanguagesInfo {

    final String LOG_TAG = LanguagesInfo.class.getSimpleName();

    HashMap<String, String> mLanguages = new HashMap<String, String>();
    String mUiCode = "";

    //for stored json config file
    final String UI_LANG = "ui_lang";
    final String LANG_INFO = "lang_info";
    final String NAME_TAG = "name_tag";
    final String NAME_RES = "name_res";

    private LanguagesInfo() {
    }


    public static final String LANGUAGES_INFO_FILE = "language_info.json";

    public static final LanguagesInfo INFO = new LanguagesInfo();       //No lazy initialization, but since it is first thing to be loaded anyway, that's ok

    //Retuns null if not initialized
    public final HashMap<String, String> getLanguagesInfo() {
        if(mLanguages.size() == 0) {
            return null;
        }
        return mLanguages;
    }

    //Should be ran in async mode. Force argument makes it be downloaded from the web regardless of local file.
    public synchronized  void fetchLanguagesInfo(File filesDir, boolean force) {
        String locale = Locale.getDefault().getLanguage();
        if(force || mLanguages.size() == 0 || !mUiCode.equals(locale)) {
            loadData(filesDir, locale, force);
        }
    }

    public synchronized  void deleteLanguagesInfo(File filesDir) {
        mLanguages.clear();
        File infoFile = new File(filesDir, LANGUAGES_INFO_FILE);
        if(infoFile.exists()) {
                infoFile.delete();
        }
    }

    private void loadData(File filesDir, String locale, boolean force) {
        try {
            File infoFile = new File(filesDir, LANGUAGES_INFO_FILE);
            if(!force && infoFile.exists() && parseSavedFile(infoFile, locale)) {
                return;
            }
            String jsonString = downloadJsonString();
            parseJsonString(jsonString, infoFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean parseSavedFile(File infoFile, String locale) throws IOException {
        InputStream readFile = null;
        try {
            readFile = new FileInputStream(infoFile);
            int size = readFile.available();
            byte[] buffer = new byte[size];
            readFile.read(buffer);
            String jsonString = new String(buffer, "UTF-8");
            JSONObject root = new JSONObject(jsonString);
            mUiCode = root.getString(UI_LANG);
            if(!mUiCode.equals(locale)) {
                return false;
            }
            JSONArray langs = root.getJSONArray(LANG_INFO);
            for (int i = 0; i < langs.length(); ++i) {
                JSONObject lang = langs.getJSONObject(i);
                String nameTag = lang.getString(NAME_TAG);
                String nameResource = lang.getString(NAME_RES);
                mLanguages.put(nameTag, nameResource);
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        } finally {
            if(readFile != null) {
                readFile.close();
            }
        }
        return true;
    }


    //The answer is not cached, but that's ok, since we will save the needed information in separate config file
    final String downloadJsonString() {;
        final String API_KEY = "key";
        final String UI_PARAMETER = "ui";
        Uri builtUri = Uri.parse(BuildConfig.YANDEX_TRANSLATE_GET_LANGS_URL).buildUpon()
                .appendQueryParameter(UI_PARAMETER, Locale.getDefault().getLanguage())
                .appendQueryParameter(API_KEY, BuildConfig.YANDEX_TRANSLATE_API_KEY)
                .build();
        try {
            URL url = new URL(builtUri.toString());
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }
            String jsonString = buffer.toString();
            return jsonString;
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
        return null;
    }

    void parseJsonString(String jsonString, File file) throws IOException {
        if(jsonString == null) {
            return;
        }
        final String LANGS_TAG = "langs";
        mUiCode = Locale.getDefault().getLanguage();
        OutputStream configFile = null;
        try {
            configFile = new FileOutputStream(file);
            JSONObject root = new JSONObject(jsonString);
            JSONObject langs = root.getJSONObject(LANGS_TAG);
            Iterator<?> keys = langs.keys();
            while( keys.hasNext() ) {
                String key = (String)keys.next();
                String nameResource = langs.getString(key);
                mLanguages.put(key, nameResource);
            }
            JSONObject rootLocal = new JSONObject();
            rootLocal.put(UI_LANG, mUiCode);
            JSONArray langsInfo = new JSONArray();
            for (int i = 0; i <  mLanguages.values().size(); ++i) {
                String nameTag = (String)mLanguages.keySet().toArray()[i];
                String nameResource = (String)mLanguages.values().toArray()[i];
                JSONObject lang = new JSONObject();
                lang.put(NAME_TAG, nameTag);
                lang.put(NAME_RES, nameResource);
                langsInfo.put(i, lang);
            }
            rootLocal.put(LANG_INFO, langsInfo);
            configFile.write(rootLocal.toString().getBytes());
        } catch (FileNotFoundException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        } finally {
            if(configFile != null) {
                configFile.close();
            }
        }
    }
}
