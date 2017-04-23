package com.ikholopov.personal.myyandextranslate;

import com.ikholopov.personal.myyandextranslate.data.LanguagesInfo;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class LanguageInfoUnitTest {
    @Test
    public void parseConfigFile() throws Exception {
        LanguagesInfo.INFO.fetchLanguagesInfo(new File("/home/igor/Projects/MyYandexTranslate/app/src/test/res/files"), true);
        assertEquals(LanguagesInfo.INFO.getLanguagesInfo().get("ru").equals("ru"), true);
    }
}