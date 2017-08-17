package com.example.bharadwaj.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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

public class MovieAsyncTask extends AsyncTask<String, Void, ArrayList<Movie>> {

    private static final String LOG_TAG = MovieAsyncTask.class.getSimpleName();
    Context mContext;
    ProgressBar mProgressBar;
    MovieAdapter mMovieAdapter;

    public MovieAsyncTask(Context context, ProgressBar mProgressBar, MovieAdapter adapter) {
        mContext = context;
        this.mProgressBar = mProgressBar;
        mMovieAdapter = adapter;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.v(LOG_TAG, "Entering onPreExecute method");

        mProgressBar.setVisibility(View.VISIBLE);

    }

    @Override
    protected ArrayList<Movie> doInBackground(String... params) {
        Log.v(LOG_TAG, "Entering doInBackground method");

        String sortPreference = params[0];
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
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<Movie> movies) {
        Log.v(LOG_TAG, "Entering onPostExecute method");

        mProgressBar.setVisibility(View.INVISIBLE);
        if (movies == null) {
            Log.v(LOG_TAG, "No Movies to show");
            MainActivity.showErrorMessage(mContext.getString(R.string.error_occurred));
        } else {
            mMovieAdapter.setMovieData(movies);
        }
    }
}