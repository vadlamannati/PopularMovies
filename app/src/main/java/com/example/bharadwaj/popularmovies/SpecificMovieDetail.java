package com.example.bharadwaj.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.bharadwaj.popularmovies.databinding.ActivitySpecificMovieDetailBinding;
import com.example.bharadwaj.popularmovies.favorites.FavoriteContract.Favorites;
import com.example.bharadwaj.popularmovies.json_parsers.MovieJSONParser;
import com.example.bharadwaj.popularmovies.movie_utilities.NetworkUtils;
import com.example.bharadwaj.popularmovies.movies.Movie;
import com.example.bharadwaj.popularmovies.reviews.Review;
import com.example.bharadwaj.popularmovies.reviews.ReviewAdapter;
import com.example.bharadwaj.popularmovies.reviews.ReviewAsyncTaskLoader;
import com.example.bharadwaj.popularmovies.trailers.Trailer;
import com.example.bharadwaj.popularmovies.trailers.TrailerAdapter;
import com.example.bharadwaj.popularmovies.trailers.TrailerAsyncTaskLoader;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;

import java.util.ArrayList;

public class SpecificMovieDetail extends AppCompatActivity implements
        TrailerAdapter.TrailerAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<Object>{

    private static final String LOG_TAG = SpecificMovieDetail.class.getSimpleName();
    private static final String VIDEOS = "videos";
    private static final String ID = "id";
    private static final int TRAILER_LOADER_ID = 51;
    private static final int REVIEW_LOADER_ID = 52;

    //Using Binding to avoid findViewById multiple times. Binding is more faster as well.
    static ActivitySpecificMovieDetailBinding specificMovieDetailBinding;
    private TrailerAdapter mTrailerAdapter;
    private ReviewAdapter mReviewAdapter;
    private Movie specificMovieDetails = null;
    private Bundle mBundle = new Bundle();
    MaterialFavoriteButton favoriteButton;

    protected static void showErrorMessageForTrailers(String errorMessage) {
        Log.v(LOG_TAG, "Entering showErrorMessageForTrailers method");
        specificMovieDetailBinding.trailersErrorView.setText(errorMessage);
        specificMovieDetailBinding.movieTrailersView.setVisibility(View.INVISIBLE);
        specificMovieDetailBinding.trailersErrorView.setVisibility(View.VISIBLE);
        specificMovieDetailBinding.reviewProgressBar.setVisibility(View.INVISIBLE);
        Log.v(LOG_TAG, "Error message shown : " + errorMessage);
        Log.v(LOG_TAG, "Leaving showErrorMessageForTrailers method");
    }

    protected static void showErrorMessageforReviews(String errorMessage) {
        Log.v(LOG_TAG, "Entering showErrorMessageforReviews method");
        specificMovieDetailBinding.reviewsErrorView.setText(errorMessage);
        specificMovieDetailBinding.movieReviewsView.setVisibility(View.INVISIBLE);
        specificMovieDetailBinding.reviewsErrorView.setVisibility(View.VISIBLE);
        specificMovieDetailBinding.reviewProgressBar.setVisibility(View.INVISIBLE);
        Log.v(LOG_TAG, "Error message shown : " + errorMessage);
        Log.v(LOG_TAG, "Leaving showErrorMessageforReviews method");
    }

    protected static void emptyReviews(String message) {
        Log.v(LOG_TAG, "Entering emptyReviews method");
        specificMovieDetailBinding.reviewsErrorView.setText(message);
        specificMovieDetailBinding.movieReviewsView.setVisibility(View.INVISIBLE);
        specificMovieDetailBinding.reviewsErrorView.setVisibility(View.VISIBLE);
        specificMovieDetailBinding.reviewProgressBar.setVisibility(View.INVISIBLE);
        Log.v(LOG_TAG, "Message shown : " + message);
        Log.v(LOG_TAG, "Leaving emptyReviews method");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        specificMovieDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_specific_movie_detail);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        Log.v(LOG_TAG, "ToolBar : " + toolbar);
        setSupportActionBar(toolbar);

        Log.v(LOG_TAG, "Action bar : " + getActionBar());

        //Enabling UP naviation on the activity to go back to parent activity. Parent defined in manifest file
        if (getSupportActionBar() != null) {
            Log.v(LOG_TAG, "Support action bar : " + getSupportActionBar());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            Log.v(LOG_TAG, "Support Action Bar is : " + this.getSupportActionBar());
        }

        Intent intentSourceActivity = getIntent();
        Log.v(LOG_TAG, "Intent received : " + intentSourceActivity.toString());

        if (intentSourceActivity.getExtras() != null) {
            if (intentSourceActivity.hasExtra(Intent.EXTRA_TEXT)) {
                specificMovieDetails = intentSourceActivity.getParcelableExtra(Intent.EXTRA_TEXT);
                setMovieDetailsToActivity(specificMovieDetails);
                Log.v(LOG_TAG, "Setting specific movie details to activity.");
            } else {
                Log.v(LOG_TAG, "No movie details fetched");
            }
        }

        //Fetching Favorite information from DB
        favoriteButton = new MaterialFavoriteButton.Builder(this)
                .favorite(checkIfMovieIsFavorite())
                .type(MaterialFavoriteButton.STYLE_HEART)
                .rotationDuration(400)
                .create();

        toolbar.addView(favoriteButton);
        favoriteButton.setOnFavoriteChangeListener(
                new MaterialFavoriteButton.OnFavoriteChangeListener() {
                    @Override
                    public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favoriteStatusChanged) {
                        Log.v(LOG_TAG, "Favorite button clicked. Favorite status changed to: " + favoriteStatusChanged);

                        if(favoriteStatusChanged){
                            insertMovieIntoDB();
                        }else {
                            deleteMovieFromDB();
                        }

                    }
                });

        //Fetching Trailers
        mTrailerAdapter = new TrailerAdapter(this);


        if (SpecificMovieDetail.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.v(LOG_TAG, "Device orientation : PORTRAIT");
            specificMovieDetailBinding.movieTrailersView.setLayoutManager(new GridLayoutManager(SpecificMovieDetail.this, 3));
        } else {
            Log.v(LOG_TAG, "Device orientation : + LANDSCAPE");
            specificMovieDetailBinding.movieTrailersView.setLayoutManager(new GridLayoutManager(SpecificMovieDetail.this, 5));
        }


        specificMovieDetailBinding.movieTrailersView.setAdapter(mTrailerAdapter);
        specificMovieDetailBinding.movieTrailersView.setHasFixedSize(true);

        if (NetworkUtils.isConnectedToInternet(this)) {
            if (null != specificMovieDetails) {
                loadTrailersOnStart(specificMovieDetails.getID());
            }
        } else {
            SpecificMovieDetail.showErrorMessageForTrailers(getString(R.string.no_active_network));
        }


        //Fetching Reviews
        mReviewAdapter = new ReviewAdapter();
        specificMovieDetailBinding.movieReviewsView.setLayoutManager(new LinearLayoutManager(SpecificMovieDetail.this));
        specificMovieDetailBinding.movieReviewsView.setAdapter(mReviewAdapter);
        specificMovieDetailBinding.movieReviewsView.setHasFixedSize(true);

        if (NetworkUtils.isConnectedToInternet(this)) {
            if (null != specificMovieDetails) {
                loadReviewsOnStart(specificMovieDetails.getID());
            }
        } else {
            SpecificMovieDetail.showErrorMessageforReviews(getString(R.string.no_active_network));
        }

        Log.v(LOG_TAG, "Leaving onCreate");
    }

    boolean checkIfMovieIsFavorite(){

        String selection = Favorites.COLUMN_MOVIE_ID + "= ?";
        String[] selectionArgs = {specificMovieDetails.getID()};

        Cursor cursor = getContentResolver().query(Favorites.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                null);
        if(cursor.getCount()==0){
            //implement
            Log.v(LOG_TAG, "This movie isn't a favorite " + specificMovieDetails.getTitle());
            return false;
        }else {
            Log.v(LOG_TAG, "This movie is a favorite one " + specificMovieDetails.getTitle());
            return true;
        }
    }

    void insertMovieIntoDB(){

        Log.v(LOG_TAG, "Inserting Movie details : " + specificMovieDetails.toString());

        ContentValues contentValues = new ContentValues();
        contentValues.put(Favorites.COLUMN_MOVIE_ID, specificMovieDetails.getID());
        contentValues.put(Favorites.COLUMN_MOVIE_NAME, specificMovieDetails.getTitle());
        contentValues.put(Favorites.COLUMN_MOVIE_POSTER_PATH, specificMovieDetails.getPosterPath());
        contentValues.put(Favorites.COLUMN_MOVIE_OVERVIEW, specificMovieDetails.getOverview());
        contentValues.put(Favorites.COLUMN_MOVIE_RELEASE_DATE, specificMovieDetails.getReleaseDate());
        contentValues.put(Favorites.COLUMN_MOVIE_USER_RATING, specificMovieDetails.getUserRating());

        Uri insertedRowUri = getContentResolver().insert(Favorites.CONTENT_URI, contentValues);
        Log.v(LOG_TAG, "Inserted Row Uri is : " + insertedRowUri.getPath());
    }

    void deleteMovieFromDB(){
        String selection = Favorites.COLUMN_MOVIE_ID + "= ?";
        String[] selectionArgs = {specificMovieDetails.getID()};

        Cursor cursor = getContentResolver().query(Favorites.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                null);
        Log.v(LOG_TAG, "Cursor count : " + cursor.getCount());

        int rowsDeleted = getContentResolver().delete(Favorites.CONTENT_URI,
                selection,
                selectionArgs);
        if(rowsDeleted != 0 ){
            Log.v(LOG_TAG, "Rows deleted : " + rowsDeleted);
            Log.v(LOG_TAG, "This movie removed as a favorite " + specificMovieDetails.getTitle());
        }else {
            Log.v(LOG_TAG, "This movie is not in th favorites DB " + specificMovieDetails.getTitle());
        }
    }


private void setMovieDetailsToActivity(Movie movie) {
        MovieJSONParser.buildPosterFromPath(movie.getPosterPath(), specificMovieDetailBinding.moviePoster);
        specificMovieDetailBinding.movieName.setText(movie.getTitle());
        specificMovieDetailBinding.movieReleaseYear.setText(movie.getReleaseDate());
        specificMovieDetailBinding.movieUserRating.setText(movie.getUserRating() + MovieJSONParser.RATING_TOTAL_REFERENCE);
        specificMovieDetailBinding.moviePlot.setText(movie.getOverview());
    }

    private void loadTrailersOnStart(String movieID) {
        //showMovies(sortPreference);
        mBundle.putString(ID, movieID);
        Log.v(LOG_TAG, "Initializing Loader");
        getSupportLoaderManager().initLoader(TRAILER_LOADER_ID, mBundle, SpecificMovieDetail.this);
    }

    private void loadReviewsOnStart(String movieID) {
        //showMovies(sortPreference);
        mBundle.putString(ID, movieID);
        Log.v(LOG_TAG, "Initializing Loader");
        getSupportLoaderManager().initLoader(REVIEW_LOADER_ID, mBundle, SpecificMovieDetail.this);
    }

    @Override
    public void performOnClick(Trailer currentTrailer) {
        //Log.v(LOG_TAG, "Entering performOnClick method");
        Log.v(LOG_TAG, "Trailer item clicked : " + currentTrailer.getmKey());
        startIntentForTrailer(this, currentTrailer.getmKey());
        //Log.v(LOG_TAG, "Leaving performOnClick method");
    }

    void startIntentForTrailer(Context context, String key){
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + key));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + key));
        try {
            context.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            context.startActivity(webIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.v(LOG_TAG, "Entering onCreateOptionsMenu method");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.favorites, menu);
        Log.v(LOG_TAG, "Leaving onCreateOptionsMenu method");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.v(LOG_TAG, "Entering onOptionsItemSelected");
        Log.v(LOG_TAG, "Leaving onOptionsItemSelected");
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader onCreateLoader(int loaderID, Bundle bundle) {
        Log.v(LOG_TAG, "Entering onCreateLoader");
        Loader loader;
        if(loaderID == TRAILER_LOADER_ID) {
            loader = new TrailerAsyncTaskLoader(this, specificMovieDetailBinding.trailerProgressBar, mTrailerAdapter, bundle);
        }else if(loaderID == REVIEW_LOADER_ID){
            loader = new ReviewAsyncTaskLoader(this, specificMovieDetailBinding.reviewProgressBar, mReviewAdapter, bundle);
        }else {
            loader = null;
        }
        Log.v(LOG_TAG, "Leaving onCreateLoader");
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Object> loader, Object objects) {
        Log.v(LOG_TAG, "Entering onLoadFinished");
        Log.v(LOG_TAG, "Loader ID : " + loader.getId());

        if (objects == null && Integer.parseInt(Review.mTotalResults) != 0) {
            if(loader.getId() == TRAILER_LOADER_ID){
                Log.v(LOG_TAG, "Couldn't fetch Trailers to show");
                SpecificMovieDetail.showErrorMessageForTrailers(getString(R.string.error_occurred));
            }
            if(loader.getId() == REVIEW_LOADER_ID){
                Log.v(LOG_TAG, "Couldn't fetch Reviews to show");
                SpecificMovieDetail.showErrorMessageforReviews(getString(R.string.error_occurred));
            }
            Log.v(LOG_TAG, "Leaving onLoadFinished");
            return;
        }

        if(loader.getId() == TRAILER_LOADER_ID){
            specificMovieDetailBinding.trailerProgressBar.setVisibility(View.INVISIBLE);

            ArrayList<Trailer> trailers = new ArrayList<>();
            trailers = (ArrayList<Trailer>)objects;

            Log.v(LOG_TAG, "Attaching Trailers to adapter");
            mTrailerAdapter.setTrailerData(trailers);
        }

        if(loader.getId() == REVIEW_LOADER_ID){
            specificMovieDetailBinding.reviewProgressBar.setVisibility(View.INVISIBLE);
            if (Integer.parseInt(Review.mTotalResults) == 0) {
                Log.v(LOG_TAG, "This movie doesn't have any Reviews to show");
                SpecificMovieDetail.emptyReviews(getString(R.string.empty_reviews));
                Log.v(LOG_TAG, "Leaving onLoadFinished");
                return;
            }
            ArrayList<Review> reviews = new ArrayList<>();
            reviews = (ArrayList<Review>)objects;

            Log.v(LOG_TAG, "Attaching reviews to adapter");
            mReviewAdapter.setReviewData(reviews);
        }
        Log.v(LOG_TAG, "Leaving onLoadFinished");

    }

    @Override
    public void onLoaderReset(Loader loader) {
        Log.v(LOG_TAG, "Entering onLoaderReset");
        Log.v(LOG_TAG, "Leaving onLoaderReset");
    }
}