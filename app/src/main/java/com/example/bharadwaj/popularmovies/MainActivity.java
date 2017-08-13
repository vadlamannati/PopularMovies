package com.example.bharadwaj.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.movies_recycler_view);
        mMovieAdapter = new MovieAdapter(this);

        GridLayoutManager layoutManager = new GridLayoutManager(MainActivity.this, 3);

        mRecyclerView.setAdapter(mMovieAdapter);
        mRecyclerView.setLayoutManager(layoutManager);

        loadMovies(MoviePreferences.DEFAULT_SORT_PREFERENCE);
    }

    public void loadMovies(String sortPreference) {

        Log.v(LOG_TAG, "Entering loadMovies method");
        if (sortPreference.equals(MoviePreferences.SORT_BY_POPULAR)){
            setTitle(R.string.most_popular);
        }
        if (sortPreference.equals(MoviePreferences.SORT_BY_TOP_RATED)) {
            setTitle(R.string.top_rated);
        }

        new MovieAsyncTask().execute(sortPreference);
    }

    @Override
    public void performOnClick(HashMap<String, String> currentMovie) {
        Log.v(LOG_TAG, currentMovie.get(MovieJSONParser.TITLE) + " movie clicked.");
        Intent intentToStartSpecificMovieDetail;
        Context context = this;
        Class destination = SpecificMovieDetail.class;

        intentToStartSpecificMovieDetail = new Intent(context, destination);
        intentToStartSpecificMovieDetail.putExtra(Intent.EXTRA_TEXT, currentMovie);

        startActivity(intentToStartSpecificMovieDetail);
        Log.v(LOG_TAG, "Starting specific movie details intent.");

    }

    public class MovieAsyncTask extends AsyncTask<String, Void, ArrayList<HashMap<String, String>>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Log.v(LOG_TAG, "Entering onPreExecute method");

        }

        @Override
        protected ArrayList<HashMap<String, String>> doInBackground(String... params) {
            Log.v(LOG_TAG, "Entering doInBackground method");

            String sortPreference = params[0];
            URL builtUrl = NetworkUtils.buildURL(sortPreference);
            Log.v(LOG_TAG, "URL built : " + builtUrl);

            try {
                String movieJsonResponse = NetworkUtils.getResponseFromHttpUrl(builtUrl);
                ArrayList<HashMap<String, String>> movieJsonData = MovieJSONParser.getMovieJsonData(MainActivity.this, movieJsonResponse);
                //Log.v(LOG_TAG, "Movie JSON data sample : " + movieJsonData[0]);
                return movieJsonData;

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
        protected void onPostExecute(ArrayList<HashMap<String, String>> movieData) {
            super.onPostExecute(movieData);
            Log.v(LOG_TAG, "Entering onPostExecute method");

            Log.v(LOG_TAG, "Movie Data length : " + movieData.size());
            mMovieAdapter.setMovieData(movieData);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sort_by, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.top_rated:
                mMovieAdapter.setMovieData(null);
                loadMovies(MoviePreferences.SORT_BY_TOP_RATED);

            case R.id.most_popular:
                mMovieAdapter.setMovieData(null);
                loadMovies(MoviePreferences.SORT_BY_POPULAR);

            default:
        }

        return super.onOptionsItemSelected(item);
    }
}
