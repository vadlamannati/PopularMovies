package com.example.bharadwaj.popularmovies.json_parsers;

import android.util.Log;

import com.example.bharadwaj.popularmovies.reviews.Review;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

/**
 * Created by Bharadwaj on 11/7/17.
 */

public class ReviewJSONParser {

    private static final String LOG_TAG = ReviewJSONParser.class.getSimpleName();
    private final static String AUTHOR = "author";
    private final static String CONTENT = "content";

    private final static String RESULTS = "results";
    private final static String ERROR_CODE = "cod";

    public static ArrayList<Review> getReviews(String reviewJsonResponse) throws JSONException {
        Log.v(LOG_TAG, "Entering getReviews method");


        Review review;
        ArrayList<Review> reviewJsonData;
        JSONObject reviewJsonResponseObject;
        JSONArray reviewResultsArray;

        reviewJsonResponseObject = new JSONObject(reviewJsonResponse);

        if (reviewJsonResponseObject.has(ERROR_CODE)) {
            int errorCode = reviewJsonResponseObject.getInt(ERROR_CODE);
            Log.v(LOG_TAG, "Error code : " + errorCode);
            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    return null;
                default:
                    return null;
            }

        }

        reviewResultsArray = reviewJsonResponseObject.getJSONArray(RESULTS);
        reviewJsonData = new ArrayList<>();

        Log.v(LOG_TAG, "Review Array length : " + reviewResultsArray.length());
        for (int i = 0; i < reviewResultsArray.length(); i++) {

            JSONObject reviewResults = reviewResultsArray.getJSONObject(i);
            String author = reviewResults.getString(AUTHOR);
            String content = reviewResults.getString(CONTENT);
            review = new Review(author, content);
            reviewJsonData.add(review);
        }
        Log.v(LOG_TAG, "Leaving getReviews method");
        return reviewJsonData;
    }

}
