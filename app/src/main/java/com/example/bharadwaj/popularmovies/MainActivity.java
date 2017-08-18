package com.example.bharadwaj.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import com.example.bharadwaj.popularmovies.databinding.ActivityMainBinding;
import com.example.bharadwaj.popularmovies.movie_utilities.MovieJSONParser;
import com.example.bharadwaj.popularmovies.movie_utilities.MoviePreferences;
import com.example.bharadwaj.popularmovies.movie_utilities.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static MovieAdapter mMovieAdapter;
    private static ActivityMainBinding mainActivityBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivityBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mMovieAdapter = new MovieAdapter(this);

        if(MainActivity.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            mainActivityBinding.moviesRecyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 3));
        }
        else{
            mainActivityBinding.moviesRecyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 5));
            setTitle("HI");
        }

        mainActivityBinding.moviesRecyclerView.setAdapter(mMovieAdapter);
        mainActivityBinding.moviesRecyclerView.setHasFixedSize(true);

        if(isConnectedToInternet()){
            loadMovies(MoviePreferences.DEFAULT_SORT_PREFERENCE);
        }
    }

    private boolean isConnectedToInternet(){

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isConnectedToInternet = (networkInfo!=null) && networkInfo.isConnectedOrConnecting();
        Log.v(LOG_TAG, "Fetching active internet connection : " + isConnectedToInternet);
        if(!isConnectedToInternet){
            Log.v(LOG_TAG, "Not connected to Internet");
            MainActivity.showErrorMessage(getString(R.string.no_active_network));
            return false;
        }
        else{
            return true;
        }
    }

    /**
     * This method loads a list of movies into MainActivity.
     *
     * @param sortPreference Sorting order of movies selected by the user.
     */
    private void loadMovies(String sortPreference) {
        Log.v(LOG_TAG, "Entering loadMovies method");
        showMovies();
        if (sortPreference.equals(MoviePreferences.SORT_BY_POPULAR)) {
            setTitle(R.string.most_popular);
        }
        if (sortPreference.equals(MoviePreferences.SORT_BY_TOP_RATED)) {
            setTitle(R.string.top_rated);
        }

        new MovieAsyncTask(this, mainActivityBinding.movieProgressBar, mMovieAdapter).execute(sortPreference);
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

    protected static void showErrorMessage(String errorMessage) {
        mainActivityBinding.errorMessage.setText(errorMessage);

        mainActivityBinding.moviesRecyclerView.setVisibility(View.INVISIBLE);
        mainActivityBinding.errorMessage.setVisibility(View.VISIBLE);
    }

    private static void showMovies() {
        mainActivityBinding.errorMessage.setVisibility(View.INVISIBLE);
        mainActivityBinding.moviesRecyclerView.setVisibility(View.VISIBLE);
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
