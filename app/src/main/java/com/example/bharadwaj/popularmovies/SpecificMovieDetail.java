package com.example.bharadwaj.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import com.example.bharadwaj.popularmovies.databinding.ActivitySpecificMovieDetailBinding;
import com.example.bharadwaj.popularmovies.json_parsers.MovieJSONParser;
import com.example.bharadwaj.popularmovies.movie_utilities.NetworkUtils;
import com.example.bharadwaj.popularmovies.movies.Movie;
import com.example.bharadwaj.popularmovies.reviews.Review;
import com.example.bharadwaj.popularmovies.reviews.ReviewAdapter;
import com.example.bharadwaj.popularmovies.reviews.ReviewAsyncTaskLoader;
import com.example.bharadwaj.popularmovies.trailers.Trailer;
import com.example.bharadwaj.popularmovies.trailers.TrailerAdapter;
import com.example.bharadwaj.popularmovies.trailers.TrailerAsyncTaskLoader;

import java.util.ArrayList;

public class SpecificMovieDetail extends AppCompatActivity implements
        TrailerAdapter.TrailerAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<ArrayList<? extends Object>>,
        ReviewAdapter.ReviewAdapterOnClickHandler{

    private static final String LOG_TAG = SpecificMovieDetail.class.getSimpleName();
    private static final String VIDEOS = "videos";
    private static final String ID = "id";
    private static final int TRAILER_LOADER_ID = 51;
    private static final int REVIEW_LOADER_ID = 52;

    //Using Binding to avoid findViewById multiple times. Binding is more faster as well.
    static ActivitySpecificMovieDetailBinding specificMovieDetailBinding;
    private TrailerAdapter mTrailerAdapter;
    private ReviewAdapter mReviewAdapter;

    private Bundle mBundle = new Bundle();

    protected static void showErrorMessageForTrailers(String errorMessage) {
        Log.v(LOG_TAG, "Entering showErrorMessageForTrailers method");
        specificMovieDetailBinding.trailersErrorView.setText(errorMessage);
        specificMovieDetailBinding.movieTrailersView.setVisibility(View.INVISIBLE);
        specificMovieDetailBinding.trailersErrorView.setVisibility(View.VISIBLE);
        Log.v(LOG_TAG, "Error message shown : " + errorMessage);
        Log.v(LOG_TAG, "Leaving showErrorMessageForTrailers method");
    }

    protected static void showErrorMessageforReviews(String errorMessage) {
        Log.v(LOG_TAG, "Entering showErrorMessageforReviews method");
        specificMovieDetailBinding.reviewsErrorView.setText(errorMessage);
        specificMovieDetailBinding.movieReviewsView.setVisibility(View.INVISIBLE);
        specificMovieDetailBinding.reviewsErrorView.setVisibility(View.VISIBLE);
        Log.v(LOG_TAG, "Error message shown : " + errorMessage);
        Log.v(LOG_TAG, "Leaving showErrorMessageforReviews method");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        specificMovieDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_specific_movie_detail);

        Log.v(LOG_TAG, "Action bar : " + getActionBar());

        //Enabling UP naviation on the activity to go back to parent activity. Parent defined in manifest file
        if (getSupportActionBar() != null) {
            Log.v(LOG_TAG, "Support action bar : " + getSupportActionBar());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            Log.v(LOG_TAG, "Support Action Bar is null");
        }

        Intent intentSourceActivity = getIntent();
        Log.v(LOG_TAG, "Intent received : " + intentSourceActivity.toString());
        Movie specificMovieDetails = null;

        if (intentSourceActivity.getExtras() != null) {
            if (intentSourceActivity.hasExtra(Intent.EXTRA_TEXT)) {
                specificMovieDetails = intentSourceActivity.getParcelableExtra(Intent.EXTRA_TEXT);
                setMovieDetailsToActivity(specificMovieDetails);
                Log.v(LOG_TAG, "Setting specific movie details to activity.");
            } else {
                Log.v(LOG_TAG, "No movie details fetched");
            }
        }

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
        mReviewAdapter = new ReviewAdapter(this);
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

    @Override
    public void performOnClick(Review currentReview) {
        //Log.v(LOG_TAG, "Entering performOnClick method");
        Log.v(LOG_TAG, "Review item clicked : " + currentReview.getContent());
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
    public void onLoadFinished(Loader<ArrayList<? extends Object>> loader, ArrayList<? extends Object> objects) {
        Log.v(LOG_TAG, "Entering onLoadFinished");

        ArrayList<Trailer> trailers;
        ArrayList<Review> reviews;

        if (objects == null) {
            if(loader.getId() == TRAILER_LOADER_ID){
                Log.v(LOG_TAG, "No Trailers to show");
                SpecificMovieDetail.showErrorMessageForTrailers(getString(R.string.error_occurred));
            }
            if(loader.getId() == REVIEW_LOADER_ID){
                Log.v(LOG_TAG, "No Reviews to show");
                SpecificMovieDetail.showErrorMessageforReviews(getString(R.string.error_occurred));
            }
        } else if(objects.get(0) instanceof Trailer){
            specificMovieDetailBinding.trailerProgressBar.setVisibility(View.INVISIBLE);

            trailers = new ArrayList<Trailer>();
            for (Object object: objects) {
                if(object instanceof Trailer){
                    trailers.add((Trailer) object);
                }
            }
            mTrailerAdapter.setTrailerData(trailers);
        }else if(objects.get(0) instanceof Review){
            specificMovieDetailBinding.reviewProgressBar.setVisibility(View.INVISIBLE);

            reviews = new ArrayList<Review>();
            for (Object object: objects) {
                if(object instanceof Review){
                    reviews.add((Review) object);
                }
            }
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