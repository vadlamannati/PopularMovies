package com.example.bharadwaj.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.bharadwaj.popularmovies.databinding.ActivityMainBinding;
import com.example.bharadwaj.popularmovies.favorites.FavoriteAsyncTaskLoader;
import com.example.bharadwaj.popularmovies.favorites.FavoriteContract;
import com.example.bharadwaj.popularmovies.favorites.FavoritesAdapter;
import com.example.bharadwaj.popularmovies.favorites.FavoritesCursorBundle;
import com.example.bharadwaj.popularmovies.movie_utilities.MoviePreferences;
import com.example.bharadwaj.popularmovies.movie_utilities.NetworkUtils;
import com.example.bharadwaj.popularmovies.movie_utilities.StringUtils;
import com.example.bharadwaj.popularmovies.movies.Movie;
import com.example.bharadwaj.popularmovies.movies.MovieAdapter;
import com.example.bharadwaj.popularmovies.movies.MovieAsyncTaskLoader;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements
        MovieAdapter.MovieAdapterOnClickHandler,
        FavoritesAdapter.FavoriteAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<Object> {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int MOVIE_LOADER_ID = 50;
    private static final int FAVORITES_LOADER_ID = 51;

    private static MovieAdapter mMovieAdapter;
    private static FavoritesAdapter mFavoritesAdapter;
    private static ActivityMainBinding mainActivityBinding;
    private ArrayList<Movie> mSavedMovies;
    private Cursor mCursor;
    private FavoritesCursorBundle mSerializableFavorites;
    private GridLayoutManager mGridLayoutManager;
    private LinearLayoutManager mLinearLayoutManager;
    private Parcelable mLayoutManagerState;

    Bundle bundle = new Bundle();
    String currentSelection = MoviePreferences.DEFAULT_SORT_PREFERENCE;

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
        if (sortPreference.equalsIgnoreCase(MoviePreferences.FAVORITE_MOVIES)) {
            setTitle(R.string.favorites);
        }
        //Log.v(LOG_TAG, "Leaving showMovies method");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG, "Entering onCreate");
        mainActivityBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        Log.v(LOG_TAG, "Current view selection is : " + currentSelection);
        if (NetworkUtils.isConnectedToInternet(this)) {

            if (null != savedInstanceState) {
                mLayoutManagerState = savedInstanceState.getParcelable(StringUtils.SAVED_STATE);
                currentSelection = savedInstanceState.getString(StringUtils.SAVING_INSTANCE);
                Log.v(LOG_TAG, "Retrieving saved instance : " + currentSelection);

                if (currentSelection.equals(MoviePreferences.DEFAULT_SORT_PREFERENCE) || currentSelection.equals(MoviePreferences.SORT_BY_TOP_RATED)) {
                    mSavedMovies = savedInstanceState.getParcelableArrayList(StringUtils.SAVED_MOVIES);
                    resetAdapterForMovieData();
                    loadMovies(currentSelection);
                } else if (currentSelection.equals(MoviePreferences.FAVORITE_MOVIES)) {
                    mSerializableFavorites = (FavoritesCursorBundle) savedInstanceState.getParcelable(StringUtils.SAVED_CURSOR);
                    mCursor = mSerializableFavorites.getFavoriteCursor();

                    resetAdapterForFavoriteMovieData();
                    loadFavoriteMovies();
                    mLinearLayoutManager.onRestoreInstanceState(mLayoutManagerState);
                }
                mSavedMovies = null;
            } else {
                resetAdapterForMovieData();
                loadMovies(MoviePreferences.DEFAULT_SORT_PREFERENCE);
            }

        } else {
            MainActivity.showErrorMessage(getString(R.string.no_active_network));
        }

        Log.v(LOG_TAG, "Leaving onCreate");
    }

    private void loadMovies(String sortPreference) {
        //Log.v(LOG_TAG, "Entering loadMovies method");

        showMovies(sortPreference);
        bundle.putString(StringUtils.SORT_PREFERENCE, sortPreference);
        Log.v(LOG_TAG, "Restarting Loader");
        getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, bundle, MainActivity.this);
    }

    private void loadFavoriteMovies() {
        Log.v(LOG_TAG, "Entering loadFavoriteMovies method");

        showMovies(MoviePreferences.FAVORITE_MOVIES);
        //mFavoritesAdapter.clearFavoriteMovies();
        //mFavoritesAdapter.setCursor(null);
        Log.v(LOG_TAG, "Restarting Loader");
        getSupportLoaderManager().restartLoader(FAVORITES_LOADER_ID, bundle, MainActivity.this);

        Log.v(LOG_TAG, "Leaving loadFavoriteMovies method");
    }

    private void resetAdapterForFavoriteMovieData() {
        mFavoritesAdapter = new FavoritesAdapter(this);
        if (mCursor != null) {
            mFavoritesAdapter.setCursor(mCursor);
        }
        mLinearLayoutManager = new LinearLayoutManager(MainActivity.this);
        mainActivityBinding.moviesRecyclerView.setLayoutManager(mLinearLayoutManager);
        mainActivityBinding.moviesRecyclerView.setAdapter(mFavoritesAdapter);
        mainActivityBinding.moviesRecyclerView.setHasFixedSize(true);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int position = viewHolder.getAdapterPosition();
                int id = (int) viewHolder.itemView.getTag();
                String stringId = Integer.toString(id);
                Uri uri = FavoriteContract.Favorites.CONTENT_URI;
                uri = uri.buildUpon().appendPath(stringId).build();
                getContentResolver().delete(uri, null, null);
                mFavoritesAdapter.setCursor(null);
                Log.v(LOG_TAG, "Restarting Loader");
                getSupportLoaderManager().restartLoader(FAVORITES_LOADER_ID, null, MainActivity.this);
            }
        }).attachToRecyclerView(mainActivityBinding.moviesRecyclerView);

        mainActivityBinding.favoritesExplanationText.setVisibility(View.VISIBLE);
        mainActivityBinding.favoritesExplanationTextSaperator.setVisibility(View.VISIBLE);
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

    void resetAdapterForMovieData() {
        mMovieAdapter = new MovieAdapter(this);

        if (MainActivity.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.v(LOG_TAG, "Device orientation : PORTRAIT");
            mGridLayoutManager = new GridLayoutManager(MainActivity.this, 3);
            mainActivityBinding.moviesRecyclerView.setLayoutManager(mGridLayoutManager);
        } else {
            Log.v(LOG_TAG, "Device orientation : + LANDSCAPE");
            mGridLayoutManager = new GridLayoutManager(MainActivity.this, 5);
            mainActivityBinding.moviesRecyclerView.setLayoutManager(mGridLayoutManager);
        }
        if (mSavedMovies != null) {
            mMovieAdapter.setMovieData(mSavedMovies);
            mGridLayoutManager.onRestoreInstanceState(mLayoutManagerState);
        }

        mainActivityBinding.moviesRecyclerView.setAdapter(mMovieAdapter);
        mainActivityBinding.moviesRecyclerView.setHasFixedSize(true);

        mainActivityBinding.favoritesExplanationText.setVisibility(View.GONE);
        mainActivityBinding.favoritesExplanationTextSaperator.setVisibility(View.GONE);
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
                String sortPreference = currentSelection;
                if (currentSelection.equals(MoviePreferences.SORT_BY_POPULAR) || currentSelection.equals(MoviePreferences.SORT_BY_TOP_RATED)) {
                    resetAdapterForMovieData();
                    loadMovies(sortPreference);
                } else if (currentSelection.equals(MoviePreferences.FAVORITE_MOVIES)) {
                    resetAdapterForFavoriteMovieData();
                    loadFavoriteMovies();
                }
                break;

            case R.id.top_rated:
                resetAdapterForMovieData();
                loadMovies(MoviePreferences.SORT_BY_TOP_RATED);
                currentSelection = MoviePreferences.SORT_BY_TOP_RATED;
                break;

            case R.id.most_popular:
                resetAdapterForMovieData();
                loadMovies(MoviePreferences.SORT_BY_POPULAR);
                currentSelection = MoviePreferences.SORT_BY_POPULAR;
                break;

            case R.id.favorites:
                resetAdapterForFavoriteMovieData();
                loadFavoriteMovies();
                currentSelection = MoviePreferences.FAVORITE_MOVIES;
                break;

            default:
        }
        Log.v(LOG_TAG, "Leaving onOptionsItemSelected");
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(StringUtils.SAVING_INSTANCE, currentSelection);
        if (currentSelection.equals(MoviePreferences.FAVORITE_MOVIES)) {
            mSerializableFavorites = new FavoritesCursorBundle(null);
            mSerializableFavorites.setFavoriteCursor(mCursor);
            outState.putParcelable(StringUtils.SAVED_CURSOR, mSerializableFavorites);
            outState.putParcelable(StringUtils.SAVED_STATE, mLinearLayoutManager.onSaveInstanceState());
        } else {
            outState.putParcelableArrayList(StringUtils.SAVED_MOVIES, mMovieAdapter.getMovies());
            outState.putParcelable(StringUtils.SAVED_STATE, mGridLayoutManager.onSaveInstanceState());
        }
        Log.v(LOG_TAG, "Saving instance to : " + currentSelection);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.v(LOG_TAG, "Current selection on orientation change : " + currentSelection);
    }

    void showNoFavoritesAndHideOthers() {
        mainActivityBinding.moviesRecyclerView.setVisibility(View.GONE);
        mainActivityBinding.favoritesExplanationText.setText(StringUtils.NO_FAVORITE_MOVIES_TEXT);
    }

    @Override
    public Loader<Object> onCreateLoader(int loaderID, Bundle bundle) {
        Log.v(LOG_TAG, "Entering onCreateLoader");

        Log.v(LOG_TAG, "LOADER ID : " + loaderID);
        Loader loader = null;
        switch (loaderID) {
            case MOVIE_LOADER_ID:
                loader = new MovieAsyncTaskLoader(this, mainActivityBinding.movieProgressBar, mMovieAdapter, bundle);
                break;
            case FAVORITES_LOADER_ID:
                loader = new FavoriteAsyncTaskLoader(this, mainActivityBinding.movieProgressBar, mFavoritesAdapter, bundle);
                break;
            default:
        }
        Log.v(LOG_TAG, "Leaving onCreateLoader");
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Object> loader, Object objects) {

        Log.v(LOG_TAG, "Entering onLoadFinished");
        Log.v(LOG_TAG, "LOADER ID : " + loader.getId());

        mainActivityBinding.movieProgressBar.setVisibility(View.GONE);
        if (objects == null) {
            Log.v(LOG_TAG, "No Movies to show");
            MainActivity.showErrorMessage(getString(R.string.error_occurred));
            return;
        }

        switch (loader.getId()) {
            case MOVIE_LOADER_ID:
                ArrayList<Movie> movies;
                movies = (ArrayList<Movie>) objects;
                mMovieAdapter.setMovieData(movies);
                break;
            case FAVORITES_LOADER_ID:
                mCursor = (Cursor) objects;
                Log.v(LOG_TAG, "Cursor length : " + mCursor.getCount());
                mFavoritesAdapter.setCursor(mCursor);
                if (mCursor.getCount() == 0) {
                    showNoFavoritesAndHideOthers();
                } else {
                    mainActivityBinding.favoritesExplanationText.setText(StringUtils.SWIPE_TO_DELETE_TEXT);
                }
                break;
            default:
        }
        Log.v(LOG_TAG, "Leaving onLoadFinished");
    }

    @Override
    public void onLoaderReset(Loader loader) {
        Log.v(LOG_TAG, "Entering onLoaderReset");
        Log.v(LOG_TAG, "Leaving onLoaderReset");
    }
}

