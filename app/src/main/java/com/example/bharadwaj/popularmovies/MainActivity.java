package com.example.bharadwaj.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.bharadwaj.popularmovies.databinding.ActivityMainBinding;
import com.example.bharadwaj.popularmovies.movie_utilities.MoviePreferences;


public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static MovieAdapter mMovieAdapter;
    private static ActivityMainBinding mainActivityBinding;

    protected static void showErrorMessage(String errorMessage) {
        Log.v(LOG_TAG, "Entering showErrorMessage method");
        mainActivityBinding.errorMessage.setText(errorMessage);
        mainActivityBinding.moviesRecyclerView.setVisibility(View.INVISIBLE);
        mainActivityBinding.errorMessage.setVisibility(View.VISIBLE);
        Log.v(LOG_TAG, "Error message shown : " + errorMessage);
        Log.v(LOG_TAG, "Leaving showErrorMessage method");
    }

    private static void showMovies() {
        Log.v(LOG_TAG, "Entering showMovies method");
        mainActivityBinding.errorMessage.setVisibility(View.INVISIBLE);
        mainActivityBinding.moviesRecyclerView.setVisibility(View.VISIBLE);
        Log.v(LOG_TAG, "Leaving showMovies method");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG, "Entering onCreate");
        mainActivityBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mMovieAdapter = new MovieAdapter(this);

        if (MainActivity.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.v(LOG_TAG, "Device orientation : PORTRAIT");
            mainActivityBinding.moviesRecyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 3));
        } else {
            Log.v(LOG_TAG, "Device orientation : + LANDSCAPE");
            mainActivityBinding.moviesRecyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 5));
        }

        mainActivityBinding.moviesRecyclerView.setAdapter(mMovieAdapter);
        mainActivityBinding.moviesRecyclerView.setHasFixedSize(true);

        if (isConnectedToInternet()) {
            loadMovies(MoviePreferences.DEFAULT_SORT_PREFERENCE);
        }
        Log.v(LOG_TAG, "Leaving onCreate");
    }

    private boolean isConnectedToInternet() {

        Log.v(LOG_TAG, "Entering isConnectedToInternet");
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isConnectedToInternet = (networkInfo != null) && networkInfo.isConnectedOrConnecting();
        Log.v(LOG_TAG, "Fetching active internet connection : " + isConnectedToInternet);
        if (!isConnectedToInternet) {
            Log.v(LOG_TAG, "Not connected to Internet");
            MainActivity.showErrorMessage(getString(R.string.no_active_network));
            Log.v(LOG_TAG, "Leaving isConnectedToInternet");
            return false;
        } else {
            Log.v(LOG_TAG, "Leaving isConnectedToInternet");
            return true;
        }
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
        new MovieAsyncTask(this, mainActivityBinding.movieProgressBar, mMovieAdapter).execute(sortPreference);
        Log.v(LOG_TAG, "Leaving loadMovies method");
    }

    @Override
    public void performOnClick(Movie currentMovie) {
        Log.v(LOG_TAG, "Entering performOnClick method");
        Intent intentToStartSpecificMovieDetail;
        Context context = this;
        Class destination = SpecificMovieDetail.class;

        intentToStartSpecificMovieDetail = new Intent(context, destination);
        intentToStartSpecificMovieDetail.putExtra(Intent.EXTRA_TEXT, currentMovie);

        Log.v(LOG_TAG, "Starting specific movie details intent.");
        startActivity(intentToStartSpecificMovieDetail);
        Log.v(LOG_TAG, "Leaving performOnClick method");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.v(LOG_TAG, "Entering onCreateOptionsMenu method");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sort_by, menu);
        Log.v(LOG_TAG, "Leaving onCreateOptionsMenu method");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.v(LOG_TAG, "Entering onOptionsItemSelected method");
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
        Log.v(LOG_TAG, "Leaving onOptionsItemSelected method");
        return super.onOptionsItemSelected(item);
    }
}
