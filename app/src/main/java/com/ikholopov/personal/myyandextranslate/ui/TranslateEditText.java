package com.ikholopov.personal.myyandextranslate.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;

/**
 * EditText with multiline text and without newlines (we want to translate when enter is pressed)
 * Created by igor on 4/15/17.
 */

public class TranslateEditText extends android.support.v7.widget.AppCompatEditText {

    public TranslateEditText(Context context) {
        super(context);
    }

    public TranslateEditText(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public TranslateEditText(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs)
    {
        InputConnection conn = super.onCreateInputConnection(outAttrs);
        outAttrs.imeOptions &= ~EditorInfo.IME_FLAG_NO_ENTER_ACTION;
        return conn;
    }
}
