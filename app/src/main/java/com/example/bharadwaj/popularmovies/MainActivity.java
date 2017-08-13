package com.example.bharadwaj.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.bharadwaj.popularmovies.movie_utilities.MovieJSONParser;
import com.example.bharadwaj.popularmovies.movie_utilities.MoviePreferences;
import com.example.bharadwaj.popularmovies.movie_utilities.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    private ProgressBar mProgressBar;
    private TextView mErrorMessageDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.movies_recycler_view);
        mMovieAdapter = new MovieAdapter(this);

        if(MainActivity.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            mRecyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 3));
        }
        else{
            mRecyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 5));
        }

        mRecyclerView.setAdapter(mMovieAdapter);
        mRecyclerView.setHasFixedSize(true);

        mProgressBar = (ProgressBar) findViewById(R.id.movie_progress_bar);
        mErrorMessageDisplay = (TextView) findViewById(R.id.error_message);

        loadMovies(MoviePreferences.DEFAULT_SORT_PREFERENCE);
    }

    private void loadMovies(String sortPreference) {
        Log.v(LOG_TAG, "Entering loadMovies method");
        showMovies();
        if (sortPreference.equals(MoviePreferences.SORT_BY_POPULAR)) {
            setTitle(R.string.most_popular);
        }
        if (sortPreference.equals(MoviePreferences.SORT_BY_TOP_RATED)) {
            setTitle(R.string.top_rated);
        }

        new MovieAsyncTask().execute(sortPreference);
    }

    @Override
    public void performOnClick(Movie currentMovie) {
        //Log.v(LOG_TAG, currentMovie.get(MovieJSONParser.TITLE) + " movie clicked.");
        Intent intentToStartSpecificMovieDetail;
        Context context = this;
        Class destination = SpecificMovieDetail.class;

        intentToStartSpecificMovieDetail = new Intent(context, destination);
        intentToStartSpecificMovieDetail.putExtra(Intent.EXTRA_TEXT, currentMovie);

        startActivity(intentToStartSpecificMovieDetail);
        Log.v(LOG_TAG, "Starting specific movie details intent.");

    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    private void showMovies() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private class MovieAsyncTask extends AsyncTask<String, Void, ArrayList<Movie>> {

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
            Log.v(LOG_TAG, "Movie Data length : " + movies.size());

            mProgressBar.setVisibility(View.INVISIBLE);
            if (movies.isEmpty()) {
                Log.v(LOG_TAG, "No Movies to show");
                showErrorMessage();
            }else{
                mMovieAdapter.setMovieData(movies);
            }
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

        mMovieAdapter.setMovieData(null);

        switch (itemId) {

            case R.id.refresh:
                loadMovies(MoviePreferences.DEFAULT_SORT_PREFERENCE);
                break;

            case R.id.top_rated:
                loadMovies(MoviePreferences.SORT_BY_TOP_RATED);
                break;

            case R.id.most_popular:
                loadMovies(MoviePreferences.SORT_BY_POPULAR);
                break;

            default:
        }

        return super.onOptionsItemSelected(item);
    }
}
