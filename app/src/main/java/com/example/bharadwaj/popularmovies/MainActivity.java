package com.example.bharadwaj.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.bharadwaj.popularmovies.databinding.ActivityMainBinding;
import com.example.bharadwaj.popularmovies.favorites.FavoriteAsyncTaskLoader;
import com.example.bharadwaj.popularmovies.favorites.FavoritesAdapter;
import com.example.bharadwaj.popularmovies.movie_utilities.MoviePreferences;
import com.example.bharadwaj.popularmovies.movie_utilities.NetworkUtils;
import com.example.bharadwaj.popularmovies.movie_utilities.StringUtils;
import com.example.bharadwaj.popularmovies.movies.Movie;
import com.example.bharadwaj.popularmovies.movies.MovieAdapter;
import com.example.bharadwaj.popularmovies.movies.MovieAsyncTaskLoader;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements
        MovieAdapter.MovieAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<Object>{

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int MOVIE_LOADER_ID = 50;
    private static final int FAVORITES_LOADER_ID = 51;
    private static MovieAdapter mMovieAdapter;
    private static FavoritesAdapter mFavoritesAdapter;
    private static ActivityMainBinding mainActivityBinding;
    Bundle bundle = new Bundle();

    protected static void showErrorMessage(String errorMessage) {
        Log.v(LOG_TAG, "Entering showErrorMessage method");
        mainActivityBinding.errorMessage.setText(errorMessage);
        mainActivityBinding.moviesRecyclerView.setVisibility(View.INVISIBLE);
        mainActivityBinding.errorMessage.setVisibility(View.VISIBLE);
        Log.v(LOG_TAG, "Error message shown : " + errorMessage);
        Log.v(LOG_TAG, "Leaving showErrorMessage method");
    }

    private void showMovies(String sortPreference) {
        //Log.v(LOG_TAG, "Entering showMovies method");
        mainActivityBinding.errorMessage.setVisibility(View.INVISIBLE);
        mainActivityBinding.moviesRecyclerView.setVisibility(View.VISIBLE);

        if (sortPreference.equals(MoviePreferences.SORT_BY_POPULAR)) {
            setTitle(R.string.most_popular);
        }
        if (sortPreference.equals(MoviePreferences.SORT_BY_TOP_RATED)) {
            setTitle(R.string.top_rated);
        }
        if (sortPreference.equalsIgnoreCase("favorites")){
            setTitle(R.string.favorites);
        }
        //Log.v(LOG_TAG, "Leaving showMovies method");
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

        if (NetworkUtils.isConnectedToInternet(this)) {
            loadMovies(MoviePreferences.DEFAULT_SORT_PREFERENCE);
        }else {
            MainActivity.showErrorMessage(getString(R.string.no_active_network));
        }
        Log.v(LOG_TAG, "Leaving onCreate");
    }


    /*private void loadMoviesOnStart(String sortPreference) {
        //Log.v(LOG_TAG, "Entering loadMoviesOnStart method");

        showMovies(sortPreference);
        bundle.putString(StringUtils.SORT_PREFERENCE, sortPreference);
        Log.v(LOG_TAG, "Initializing Loader");
        getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, bundle, MainActivity.this);

        //Log.v(LOG_TAG, "Leaving loadMoviesOnStart method");
    }*/

    private void loadMovies(String sortPreference) {
        //Log.v(LOG_TAG, "Entering loadMovies method");

        showMovies(sortPreference);
        bundle.putString(StringUtils.SORT_PREFERENCE, sortPreference);
        Log.v(LOG_TAG, "Restarting Loader");
        getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, bundle, MainActivity.this);

        //Log.v(LOG_TAG, "Leaving loadMovies method");
    }

    private void loadFavoriteMovies() {
        Log.v(LOG_TAG, "Entering loadFavoriteMovies method");

        mFavoritesAdapter = new FavoritesAdapter(this);
        mainActivityBinding.moviesRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        mainActivityBinding.moviesRecyclerView.setAdapter(mFavoritesAdapter);
        mainActivityBinding.moviesRecyclerView.setHasFixedSize(true);

        showMovies("favorites");
        Log.v(LOG_TAG, "Restarting Loader");
        getSupportLoaderManager().restartLoader(FAVORITES_LOADER_ID, bundle, MainActivity.this);

        Log.v(LOG_TAG, "Leaving loadFavoriteMovies method");
    }

    @Override
    public void performOnClick(Movie currentMovie) {
        //Log.v(LOG_TAG, "Entering performOnClick method");
        Intent intentToStartSpecificMovieDetail;
        Context context = this;
        Class destination = SpecificMovieDetail.class;

        intentToStartSpecificMovieDetail = new Intent(context, destination);
        intentToStartSpecificMovieDetail.putExtra(Intent.EXTRA_TEXT, currentMovie);

        Log.v(LOG_TAG, "Starting specific movie details intent.");
        startActivity(intentToStartSpecificMovieDetail);
        //Log.v(LOG_TAG, "Leaving performOnClick method");
    }

    void clearMovies() {
        Log.v(LOG_TAG, "Clearing movies from adapter");
        mMovieAdapter.setMovieData(null);
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
        Log.v(LOG_TAG, "Entering onOptionsItemSelected");
        int itemId = item.getItemId();

        clearMovies();

        switch (itemId) {

            case R.id.refresh:
                String sortPreference = bundle.getString(StringUtils.SORT_PREFERENCE);
                loadMovies(sortPreference);
                break;

            case R.id.top_rated:
                loadMovies(MoviePreferences.SORT_BY_TOP_RATED);
                break;

            case R.id.most_popular:
                loadMovies(MoviePreferences.SORT_BY_POPULAR);
                break;

            case R.id.favorites:
                loadFavoriteMovies();
                break;

            default:
        }
        Log.v(LOG_TAG, "Leaving onOptionsItemSelected");
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader onCreateLoader(int loaderID, Bundle bundle) {
        Log.v(LOG_TAG, "Entering onCreateLoader");

        Log.v(LOG_TAG, "LOADER ID : " + loaderID);
        Loader loader = null;
        if(loaderID == MOVIE_LOADER_ID){
            loader = new MovieAsyncTaskLoader(this, mainActivityBinding.movieProgressBar, mMovieAdapter, bundle);
        }else if(loaderID == FAVORITES_LOADER_ID){
            loader = new FavoriteAsyncTaskLoader(this, mainActivityBinding.movieProgressBar,mFavoritesAdapter , bundle);
        }
        Log.v(LOG_TAG, "Leaving onCreateLoader");
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Object> loader, Object objects) {

        Log.v(LOG_TAG, "Entering onLoadFinished");
        Log.v(LOG_TAG, "LOADER ID : " + loader.getId());

        mainActivityBinding.movieProgressBar.setVisibility(View.INVISIBLE);
        if (objects == null) {
            Log.v(LOG_TAG, "No Movies to show");
            MainActivity.showErrorMessage(getString(R.string.error_occurred));
            return;
        }
        if (objects.getClass().getName().equals(ArrayList.class.getName())) {
            //Log.v(LOG_TAG, "Movies sample: " + movies.get(0));
            ArrayList<Movie> movies;
            movies = (ArrayList<Movie>) objects;
            mMovieAdapter.setMovieData(movies);
        }
        if (objects.getClass().getName().equals(Cursor.class.getName())) {
            //Log.v(LOG_TAG, "Movies sample: " + movies.get(0));
            Cursor cursor;
            cursor = (Cursor) objects;
            mFavoritesAdapter.setCursor(cursor);
        }
        Log.v(LOG_TAG, "Leaving onLoadFinished");
    }


    @Override
    public void onLoaderReset(Loader loader) {
        Log.v(LOG_TAG, "Entering onLoaderReset");
        Log.v(LOG_TAG, "Leaving onLoaderReset");

    }
}

