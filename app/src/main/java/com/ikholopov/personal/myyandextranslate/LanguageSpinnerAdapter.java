package com.ikholopov.personal.myyandextranslate;

import android.app.Activity;
import android.content.Context;
import android.provider.BaseColumns;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Adapter for lists of languages
 * Created by igor on 4/12/17.
 */

public class LanguageSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {

    private ArrayList<String> mLanguageList;
    private Context mContext;
    int mSelectedPosition;

    public LanguageSpinnerAdapter(Context context, ArrayList<String> languageList) {
        mLanguageList = languageList;
        mContext = context;
    }

    @Override
    public int getCount() {
        if(mLanguageList == null){
            return 0;
        }
        return mLanguageList.size();
    }

    @Override
    public Object getItem(int position) {
        return mLanguageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView = (TextView) View.inflate(mContext, R.layout.spinner_selected_item, null);
        textView.setText(mLanguageList.get(position));
        return textView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView textView = (TextView) View.inflate(mContext, R.layout.spinner_dropdown_item, null);
        if(mSelectedPosition == position) {
            textView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
            textView.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimaryDark));
        }
        textView.setText(mLanguageList.get(position));
        return textView;
    }


}
