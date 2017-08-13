package com.example.bharadwaj.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bharadwaj.popularmovies.movie_utilities.MovieJSONParser;

public class SpecificMovieDetail extends AppCompatActivity {

    private static final String LOG_TAG = SpecificMovieDetail.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_movie_detail);

        Intent intentSourceActivity = getIntent();
        Log.v(LOG_TAG, "Intent received from : " + intentSourceActivity.toString());

        if(intentSourceActivity.getExtras()!= null){
            if(intentSourceActivity.hasExtra(Intent.EXTRA_TEXT)){
                Movie specificMovieDetails = intentSourceActivity.getParcelableExtra(Intent.EXTRA_TEXT);
                setMovieDetailsToActivity(specificMovieDetails);
                Log.v(LOG_TAG, "Setting specific movie details to activity.");
            }
        }
    }

    private void setMovieDetailsToActivity(Movie movie){

        ImageView mPoster = (ImageView) findViewById(R.id.movie_poster);
        TextView mReleaseDate = (TextView) findViewById(R.id.movie_release_date_value);
        TextView mUserRating = (TextView) findViewById(R.id.movie_user_rating_value);
        TextView mOverview = (TextView) findViewById(R.id.movie_plot);


        MovieJSONParser.buildPosterFromPath(movie.getPosterPath(), mPoster);
        setTitle(movie.getTitle());
        mReleaseDate.setText(movie.getReleaseDate());
        mUserRating.setText(movie.getUserRating());
        mOverview.setText(movie.getOverview());
    }

}
