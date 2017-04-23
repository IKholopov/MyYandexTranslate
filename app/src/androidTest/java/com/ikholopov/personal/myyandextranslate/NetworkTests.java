package com.ikholopov.personal.myyandextranslate;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.AppCompatButton;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.ikholopov.personal.myyandextranslate.data.LanguagesInfo;
import com.ikholopov.personal.myyandextranslate.data.TranslateDBContainer;
import com.ikholopov.personal.myyandextranslate.data.TranslateDBProvider;
import com.ikholopov.personal.myyandextranslate.data.Translation;
import com.ikholopov.personal.myyandextranslate.net.TranslationManager;
import com.ikholopov.personal.myyandextranslate.ui.TranslatedViewHolder;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class NetworkTests {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.ikholopov.personal.myyandextranslate", appContext.getPackageName());
    }
    @Test
    public void testLoad() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        LanguagesInfo.INFO.fetchLanguagesInfo(appContext.getFilesDir(), true);
        assertEquals(LanguagesInfo.INFO.getLanguagesInfo().get("en").equals("English") ||
                LanguagesInfo.INFO.getLanguagesInfo().get("en").equals("Английский"), true);
        LanguagesInfo.INFO.deleteLanguagesInfo(appContext.getFilesDir());
        LanguagesInfo.INFO.fetchLanguagesInfo(appContext.getFilesDir(), true);
        assertEquals(LanguagesInfo.INFO.getLanguagesInfo().get("ru").equals("Russian") ||
                LanguagesInfo.INFO.getLanguagesInfo().get("ru").equals("Русский"), true);
    }
    @Test
    public void testTranslation() throws InterruptedException {
        Context appContext = InstrumentationRegistry.getTargetContext();
        RequestQueue queue = Volley.newRequestQueue(appContext);
        TranslatedViewHolder viewHolder = new TranslatedViewHolder(0, new TextView(appContext),
                new ProgressBar(appContext), new ScrollView(appContext), new AppCompatButton(appContext),
                new TextView(appContext), new AppCompatButton(appContext), new AppCompatButton(appContext),
                appContext);
        TranslateDBProvider provider = TranslateDBContainer.getProviderInstance(appContext);
        TranslationManager.translateText(queue, new TranslationManager.TranslateTaskParameters("Test", "en", "ru"),
                viewHolder,provider);
        Thread.sleep(2000);
        final String[] test = {""};
        assertEquals(viewHolder.mTranslatedText.getText().equals("Тест"), true);
        provider.getTranslation("en", "ru", "Test", new TranslateDBProvider.DBCallback<Translation>() {
            @Override
            public void onFinished(Translation translation) {
                test[0] = translation.getSourceText();
            }
        });
        Thread.sleep(2000);
        assertEquals(test[0].equals("Test"), true);
    }
}
