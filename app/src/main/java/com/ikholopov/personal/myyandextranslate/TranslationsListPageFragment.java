package com.ikholopov.personal.myyandextranslate;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ikholopov.personal.myyandextranslate.data.NotificationManager;
import com.ikholopov.personal.myyandextranslate.data.TranslateDBContainer;
import com.ikholopov.personal.myyandextranslate.data.TranslateDBProvider;
import com.ikholopov.personal.myyandextranslate.data.Translation;


/**
 * History or favorites tab
 * Created by igor on 4/15/17.
 */
public class TranslationsListPageFragment extends Fragment {

    //Tag for logs
    private static final String LOG_TAG = "FAVORITE_PAGE_TAG";
    public static final String PAGE_MODE_ARGUMENT = "page_mode";

    enum PageMode  {
        HISTORY,
        FAVORITES
    }

    //Interface to pass selected translation to activity and then to Translation view
    public interface SetTranslationListener {
        void setTranslation(long id);
    }

    private PageMode mPageMode;

    private ListView mTranslationsList;
    private TranslateListViewAdapter mAdapter;
    Cursor mListCursor = null;

    private TranslateDBProvider mDbProvider;
    private NotificationManager mNotificationManager;
    private NotificationManager.Listener mNotificationListener = new NotificationManager.Listener() {
        @Override
        public void onContentChanged() {
            getCursor();
        }
    };
    private SetTranslationListener mTranslationListener;

    public TranslationsListPageFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle preSets = getArguments();
        mPageMode = PageMode.valueOf(preSets.getString(PAGE_MODE_ARGUMENT));
        mDbProvider = TranslateDBContainer.getProviderInstance(getActivity().getApplicationContext());
        mNotificationManager = TranslateDBContainer.getNotificationManagerInstance();
        View rootView = inflater.inflate(R.layout.fragment_favorite_page, container, false);
        mTranslationsList = (ListView) rootView.findViewById(R.id.history_list);
        mAdapter = new TranslateListViewAdapter(getContext(), null, false);
        mTranslationsList.setAdapter(mAdapter);
        getCursor();
        mTranslationsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                mTranslationListener.setTranslation(cursor.getInt(TranslateDBProvider.COLUMN_ID_ROW_ID));
            }
        });
        return rootView;
    }

    @Override
    public void onStop() {
        mNotificationManager.removeListener(mNotificationListener);
        super.onStop();
    }

    @Override
    public void onStart() {
        mNotificationManager.addListener(mNotificationListener);
        super.onStart();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mTranslationListener = (SetTranslationListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement SetTranslationListener");
        }
    }

    private void swapCursor(Cursor cursor) {
        Cursor oldCursor = mListCursor;
        mListCursor = cursor;
        mAdapter.swapCursor(mListCursor);
        if(oldCursor != null) {
            oldCursor.close();
        }
    }

    public void getCursor() {
        if(mPageMode == PageMode.HISTORY) {
            mDbProvider.getHistory(new TranslateDBProvider.DBCallback<Cursor>() {
                @Override
                public void onFinished(Cursor cursor) {
                    swapCursor(cursor);
                }
            });
        } else {
            mDbProvider.getFavorites(new TranslateDBProvider.DBCallback<Cursor>() {
                @Override
                public void onFinished(Cursor cursor) {
                    swapCursor(cursor);
                }
            });
        }
    }
}
