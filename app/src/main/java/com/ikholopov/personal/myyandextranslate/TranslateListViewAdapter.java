package com.ikholopov.personal.myyandextranslate;

import android.content.Context;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ikholopov.personal.myyandextranslate.data.TranslateDBContainer;
import com.ikholopov.personal.myyandextranslate.data.TranslateDBProvider;

/**
 * Adapter for history/favorites lists
 * Created by igor on 4/16/17.
 */

public class TranslateListViewAdapter extends CursorAdapter {

    public static class ViewHolder {
        public final AppCompatButton favoriteButton;
        public final TextView originalTextView;
        public final TextView translatedTextView;
        public final TextView directionTextView;
        public boolean favorite = false;
        public long id = -1;

        public ViewHolder(View view, final Context context) {
            favoriteButton = (AppCompatButton) view.findViewById(R.id.favorites_button);
            originalTextView = (TextView) view.findViewById(R.id.original_text_view);
            translatedTextView = (TextView) view.findViewById(R.id.translated_text_view);
            directionTextView = (TextView) view.findViewById(R.id.direction_text_view);
            favoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(id != -1) {
                        if (!favorite) {
                            TranslateDBContainer.getProviderInstance(context).addToFavorites(id,
                                    new TranslateDBProvider.DBCallback<Boolean>() {
                                        @Override
                                        public void onFinished(Boolean success) {
                                            if(success) {
                                                favorite = true;
                                            }
                                        }
                                    });
                        } else {
                            TranslateDBContainer.getProviderInstance(context).removeFromFavorites(id,
                                    new TranslateDBProvider.DBCallback<Boolean>() {
                                        @Override
                                        public void onFinished(Boolean success) {
                                            if(success) {
                                                favorite = false;
                                            }
                                        }
                                    });
                        }
                    }
                }
            });
        }
    }

    public TranslateListViewAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.history_list_item, parent, false);
        view.setTag(new ViewHolder(view, context.getApplicationContext()));
        return view;
    }

    @Override
    public void bindView(View view, Context context, final Cursor cursor)
    {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.directionTextView.setText(cursor.getString(
                TranslateDBProvider.COLUMN_SOURCE_LANGUAGE_ROW_ID) + "-" +
                cursor.getString(TranslateDBProvider.COLUMN_TARGET_LANGUAGE_ROW_ID));
        viewHolder.translatedTextView.setText(cursor.getString(
                TranslateDBProvider.COLUMN_TRANSLATED_TEXT_ROW_ID));
        viewHolder.originalTextView.setText(cursor.getString(
                TranslateDBProvider.COLUMN_SOURCE_TEXT_ROW_ID));
        Drawable icon = ContextCompat.getDrawable(context,
                cursor.getInt(TranslateDBProvider.COLUMN_FAVORITE_ROW_ID) == 1 ?
                        R.mipmap.ic_turned_on: R.mipmap.ic_turned_off);
        icon.setColorFilter(context.getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        viewHolder.favoriteButton.setBackground(icon);
        viewHolder.id = cursor.getInt(TranslateDBProvider.COLUMN_ID_ROW_ID);
        viewHolder.favorite = cursor.getInt(TranslateDBProvider.COLUMN_FAVORITE_ROW_ID) > 0;
    }
}
