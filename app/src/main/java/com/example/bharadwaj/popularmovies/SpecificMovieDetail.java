package com.example.bharadwaj.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

public class SpecificMovieDetail extends AppCompatActivity {

    private static final String LOG_TAG = SpecificMovieDetail.class.getSimpleName();
    private TextView mReleaseDate;
    private TextView mUserRating;
    private TextView mOverview;
    private ImageView mPoster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_movie_detail);

        Intent intentSourceActivity = getIntent();
        Log.v(LOG_TAG, "Intent received from : " + intentSourceActivity.toString());

        if(null != intentSourceActivity){
            if(intentSourceActivity.hasExtra(Intent.EXTRA_TEXT)){
                HashMap<String, String> specificMovieDetails = (HashMap) intentSourceActivity.getSerializableExtra(Intent.EXTRA_TEXT);
                Log.v(LOG_TAG, "Hashmap values : " + specificMovieDetails);
                setMovieDetailsToActivity(specificMovieDetails);
                Log.v(LOG_TAG, "Setting specific movie details to activity.");
            }
        }
    }

    void setMovieDetailsToActivity(HashMap<String, String> movieDetails){

        mPoster = (ImageView) findViewById(R.id.movie_poster);
        mReleaseDate = (TextView) findViewById(R.id.movie_release_date_value);
        mUserRating = (TextView) findViewById(R.id.movie_user_rating_value);
        mOverview = (TextView) findViewById(R.id.movie_overview);

        String posterPath = movieDetails.get(MovieJSONParser.POSTER_PATH);
        MovieJSONParser.buildPosterFromPath(posterPath, mPoster);

        setTitle(movieDetails.get(MovieJSONParser.TITLE));
        mReleaseDate.setText(movieDetails.get(MovieJSONParser.RELEASE_DATE));
        mUserRating.setText(movieDetails.get(MovieJSONParser.USER_RATING));
        mOverview.setText(movieDetails.get(MovieJSONParser.OVERVIEW));
    }

}
