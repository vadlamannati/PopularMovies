package com.example.bharadwaj.popularmovies.reviews;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.bharadwaj.popularmovies.json_parsers.ReviewJSONParser;
import com.example.bharadwaj.popularmovies.movie_utilities.NetworkUtils;
import com.example.bharadwaj.popularmovies.movie_utilities.StringUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Bharadwaj on 10/16/17.
 */

public class ReviewAsyncTaskLoader extends AsyncTaskLoader<ArrayList<Review>> {

    private static final String LOG_TAG = ReviewAsyncTaskLoader.class.getSimpleName();
    Context mContext;
    ProgressBar mProgressBar;
    ReviewAdapter mReviewAdapter;
    Bundle mBundle;

    public ReviewAsyncTaskLoader(Context context, ProgressBar mProgressBar, ReviewAdapter adapter, Bundle bundle) {
        super(context);
        mContext = context;
        this.mProgressBar = mProgressBar;
        mReviewAdapter = adapter;
        mBundle = bundle;
    }

    @Override
    protected void onStartLoading() {
        Log.v(LOG_TAG, "Entering onStartLoading");
        super.onStartLoading();

        ArrayList<Review> reviews = mReviewAdapter.getReviews();
        if(null != reviews){
            deliverResult(reviews);
        }else {
            Log.v(LOG_TAG, "No reviews to show. Generating");
            mProgressBar.setVisibility(View.VISIBLE);
            forceLoad();
        }

        Log.v(LOG_TAG, "Leaving onStartLoading");
    }

    @Override
    public ArrayList<Review> loadInBackground() {


        Log.v(LOG_TAG, "Entering loadInBackground");

        String movieId = mBundle.getString(StringUtils.MOVIE_ID);
        URL builtUrl = NetworkUtils.buildReviewURL(movieId);
        Log.v(LOG_TAG, "URL built : " + builtUrl);

        ArrayList<Review> reviews;
        try {
            String movieJsonResponse = NetworkUtils.getResponseFromHttpUrl(builtUrl);
            reviews = ReviewJSONParser.getReviews(movieJsonResponse);
            Log.v(LOG_TAG, "Movie JSON data sample : " + reviews.get(0));
            return reviews;
        } catch (IOException e) {
            Log.v(LOG_TAG, "IO Exception occurred");
            e.printStackTrace();
        } catch (JSONException e) {
            Log.v(LOG_TAG, "JSON Exception occurred");
            e.printStackTrace();
        } finally {
            Log.v(LOG_TAG, "Leaving loadInBackground");
        }

        return null;
    }

    @Override
    public void deliverResult(ArrayList<Review> reviews) {
        Log.v(LOG_TAG, "Delivering result");
        super.deliverResult(reviews);
    }

}
