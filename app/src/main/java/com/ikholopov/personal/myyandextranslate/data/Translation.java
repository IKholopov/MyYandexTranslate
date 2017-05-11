package com.ikholopov.personal.myyandextranslate.data;

/**
 * Created by igor on 4/16/17.
 */

public class Translation {
    private long id;
    private String sourceText;
    private String translationText;
    private String sourceLang;
    private String targetLang;
    private boolean favorite;

    public long getId() {
        return id;
    }

    public String getTranslationText() {
        return translationText;
    }

    public String getSourceLang() {
        return sourceLang;
    }

    public String getTargetLang() {
        return targetLang;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public Translation(long id, String sourceText, String translationText,
                       String sourceLang, String targetLang, boolean favorite) {
        this.id = id;
        this.sourceText = sourceText;
        this.translationText = translationText;
        this.sourceLang = sourceLang;
        this.targetLang = targetLang;
        this.favorite = favorite;
    }

    public String getSourceText() {
        return sourceText;
    }
}
