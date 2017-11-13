package com.example.bharadwaj.popularmovies.favorites;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.bharadwaj.popularmovies.favorites.FavoriteContract.Favorites;

/**
 * Created by Bharadwaj on 11/11/17.
 */

public class FavoriteAsyncTaskLoader extends AsyncTaskLoader<Cursor> {

    private static final String LOG_TAG = FavoriteAsyncTaskLoader.class.getSimpleName();
    Context mContext;
    ProgressBar mProgressBar;
    FavoritesAdapter mFavoriteAdapter;
    Bundle mBundle;

    public FavoriteAsyncTaskLoader(Context context, ProgressBar mProgressBar, FavoritesAdapter adapter, Bundle bundle) {
        super(context);
        mContext = context;
        this.mProgressBar = mProgressBar;
        mFavoriteAdapter = adapter;
        mBundle = bundle;
    }

    @Override
    protected void onStartLoading() {
        Log.v(LOG_TAG, "Entering onStartLoading");
        super.onStartLoading();

        Cursor cursor = mFavoriteAdapter.getCursor();
        if(null != cursor){
            deliverResult(cursor);
        }else {
            Log.v(LOG_TAG, "No movies to show. Generating");
            mProgressBar.setVisibility(View.VISIBLE);
            forceLoad();
        }

        Log.v(LOG_TAG, "Leaving onStartLoading");
    }

    @Override
    public Cursor loadInBackground() {
        Log.v(LOG_TAG, "Entering loadInBackground");

        Cursor cursor;
        cursor = getContext().getContentResolver().query(Favorites.CONTENT_URI,
                null,
                null,
                null,
                null);
        Log.v(LOG_TAG, "Leaving loadInBackground");
        return cursor;
    }

    @Override
    public void deliverResult(Cursor cursor) {
        Log.v(LOG_TAG, "Delivering result");
        super.deliverResult(cursor);
    }
}
