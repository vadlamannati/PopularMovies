package com.example.bharadwaj.popularmovies;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.bharadwaj.popularmovies.movie_utilities.MovieJSONParser;
import com.example.bharadwaj.popularmovies.movie_utilities.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Bharadwaj on 8/14/17.
 */

public class MovieAsyncTaskLoader extends AsyncTaskLoader<ArrayList<Movie>> {

    private static final String LOG_TAG = MovieAsyncTaskLoader.class.getSimpleName();
    Context mContext;
    ProgressBar mProgressBar;
    MovieAdapter mMovieAdapter;
    Bundle mBundle;
    private static final String SORT_PREFERENCE = "SORT_PREFERENCE";

    public MovieAsyncTaskLoader(Context context, ProgressBar mProgressBar, MovieAdapter adapter, Bundle bundle) {
        super(context);
        mContext = context;
        this.mProgressBar = mProgressBar;
        mMovieAdapter = adapter;
        mBundle = bundle;
    }

    @Override
    protected void onStartLoading() {
        Log.v(LOG_TAG, "Entering onStartLoading");
        super.onStartLoading();

        ArrayList<Movie> movies = mMovieAdapter.getMovies();
        if(null != movies){
            deliverResult(movies);
        }else {
            Log.v(LOG_TAG, "No movies to show. Generating");
            mProgressBar.setVisibility(View.VISIBLE);
            forceLoad();
        }

        Log.v(LOG_TAG, "Leaving onStartLoading");
    }

    @Override
    public ArrayList<Movie> loadInBackground() {


        Log.v(LOG_TAG, "Entering loadInBackground");

        String sortPreference = mBundle.getString(SORT_PREFERENCE);
        URL builtUrl = NetworkUtils.buildURL(sortPreference);
        Log.v(LOG_TAG, "URL built : " + builtUrl);

        try {
            String movieJsonResponse = NetworkUtils.getResponseFromHttpUrl(builtUrl);
            //Log.v(LOG_TAG, "Movie JSON data sample : " + movieJsonData[0]);
            return MovieJSONParser.getMovies(movieJsonResponse);

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
    public void deliverResult(ArrayList<Movie> movies) {
        Log.v(LOG_TAG, "Delivering result");
        super.deliverResult(movies);
    }
}