package com.example.bharadwaj.popularmovies;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.bharadwaj.popularmovies.databinding.ActivitySpecificMovieDetailBinding;
import com.example.bharadwaj.popularmovies.movie_utilities.MovieJSONParser;

public class SpecificMovieDetail extends AppCompatActivity {

    private static final String LOG_TAG = SpecificMovieDetail.class.getSimpleName();

    //Using Binding to avoid findViewById multiple times. Binding is more faster as well.
    ActivitySpecificMovieDetailBinding specificMovieDetailBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        specificMovieDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_specific_movie_detail);

        Log.v(LOG_TAG, "Action bar : " + getActionBar());

        //Enabling UP naviation on the activity to go back to parent activity. Parent defined in manifest file
        if(getSupportActionBar() != null){
            Log.v(LOG_TAG, "Support action bar : " + getSupportActionBar());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }else{
            Log.v(LOG_TAG, "Support Action Bar is null");
        }

        Intent intentSourceActivity = getIntent();
        Log.v(LOG_TAG, "Intent received : " + intentSourceActivity.toString());

        if (intentSourceActivity.getExtras() != null) {
            if (intentSourceActivity.hasExtra(Intent.EXTRA_TEXT)) {
                Movie specificMovieDetails = intentSourceActivity.getParcelableExtra(Intent.EXTRA_TEXT);
                setMovieDetailsToActivity(specificMovieDetails);
                Log.v(LOG_TAG, "Setting specific movie details to activity.");
            }
        }
    }

    private void setMovieDetailsToActivity(Movie movie) {

        //Setting title of page to selected movie name
        //setTitle(movie.getTitle());

        MovieJSONParser.buildPosterFromPath(movie.getPosterPath(), specificMovieDetailBinding.moviePoster);
        specificMovieDetailBinding.movieName.setText(movie.getTitle());
        specificMovieDetailBinding.movieReleaseYear.setText(movie.getReleaseDate());
        specificMovieDetailBinding.movieUserRating.setText(movie.getUserRating() + MovieJSONParser.RATING_TOTAL_REFERENCE);
        specificMovieDetailBinding.moviePlot.setText(movie.getOverview());


    }

}
