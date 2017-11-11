package com.example.bharadwaj.popularmovies.trailers;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.bharadwaj.popularmovies.movie_utilities.NetworkUtils;
import com.example.bharadwaj.popularmovies.movie_utilities.StringUtils;
import com.example.bharadwaj.popularmovies.json_parsers.TrailerJSONParser;
import com.example.bharadwaj.popularmovies.movies.MovieAsyncTaskLoader;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Bharadwaj on 10/16/17.
 */

public class TrailerAsyncTaskLoader extends AsyncTaskLoader<ArrayList<Trailer>> {

    private static final String LOG_TAG = TrailerAsyncTaskLoader.class.getSimpleName();
    Context mContext;
    ProgressBar mProgressBar;
    TrailerAdapter mTrailerAdapter;
    Bundle mBundle;

    public TrailerAsyncTaskLoader(Context context, ProgressBar mProgressBar, TrailerAdapter adapter, Bundle bundle) {
        super(context);
        mContext = context;
        this.mProgressBar = mProgressBar;
        mTrailerAdapter = adapter;
        mBundle = bundle;
    }

    @Override
    protected void onStartLoading() {
        Log.v(LOG_TAG, "Entering onStartLoading");
        super.onStartLoading();

        ArrayList<Trailer> trailers = mTrailerAdapter.getTrailers();
        if(null != trailers){
            deliverResult(trailers);
        }else {
            Log.v(LOG_TAG, "No trailers to show. Generating");
            mProgressBar.setVisibility(View.VISIBLE);
            forceLoad();
        }

        Log.v(LOG_TAG, "Leaving onStartLoading");
    }

    @Override
    public ArrayList<Trailer> loadInBackground() {


        Log.v(LOG_TAG, "Entering loadInBackground");

        String movieId = mBundle.getString(StringUtils.MOVIE_ID);
        URL builtUrl = NetworkUtils.buildTrailerURL(movieId);
        Log.v(LOG_TAG, "URL built : " + builtUrl);

        try {
            String movieJsonResponse = NetworkUtils.getResponseFromHttpUrl(builtUrl);
            //Log.v(LOG_TAG, "Movie JSON data sample : " + movieJsonData[0]);
            return TrailerJSONParser.getTrailers(movieJsonResponse);

        } catch (IOException e) {
            Log.v(LOG_TAG, "IO Exception occurred");
            e.printStackTrace();
        } catch (JSONException e) {
            Log.v(LOG_TAG, "JSON Exception occurred");
            e.printStackTrace();
        } finally {
            Log.v(LOG_TAG, "Leaving loadInBackground");
        }

        return null;
    }

    @Override
    public void deliverResult(ArrayList<Trailer> trailers) {
        Log.v(LOG_TAG, "Delivering Trailer result");
        super.deliverResult(trailers);
    }

}
