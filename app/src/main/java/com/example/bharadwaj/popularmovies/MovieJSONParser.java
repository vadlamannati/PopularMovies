package com.example.bharadwaj.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Bharadwaj on 8/11/17.
 */

public class MovieJSONParser {


    private static final String LOG_TAG = MovieJSONParser.class.getSimpleName();

    final static String TITLE = "title";
    final static String POSTER_PATH = "poster_path";
    final static String OVERVIEW = "overview";
    final static String USER_RATING = "vote_average";
    final static String RELEASE_DATE = "release_date";
    final static String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w342/";

    public static ArrayList<HashMap<String, String>> getMovieJsonData(Context context, String movieJsonResponse) throws JSONException {

        final String RESULTS = "results";
        final String ERROR_CODE = "cod";

        HashMap<String,String> movieJsonDataMap = null;
        ArrayList<HashMap<String,String>> movieJsonData;
        JSONObject movieJsonResponseObject = null;
        JSONArray movieResultsArray = null;

        movieJsonResponseObject = new JSONObject(movieJsonResponse);

        if (movieJsonResponseObject.has(ERROR_CODE)) {
            int errorCode = movieJsonResponseObject.getInt(ERROR_CODE);
            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    return null;
                default:
                    return null;
            }

        }

        movieResultsArray = movieJsonResponseObject.getJSONArray(RESULTS);

        movieJsonData = new ArrayList<HashMap<String, String>>();


        Log.v(LOG_TAG, "Movie Array length : " + movieResultsArray.length());
        for (int i = 0; i < movieResultsArray.length(); i++) {

            JSONObject movieResults = movieResultsArray.getJSONObject(i);

            String title = movieResults.getString(TITLE);
            String userRating = movieResults.getString(USER_RATING);
            String posterPath = movieResults.getString(POSTER_PATH);
            String releaseDate = movieResults.getString(RELEASE_DATE);
            String overview = movieResults.getString(OVERVIEW);

            //Log.v(LOG_TAG, "Title : " + title);
            movieJsonDataMap = new HashMap<String, String>();
            movieJsonDataMap.put(POSTER_PATH, posterPath);
            movieJsonDataMap.put(TITLE, title);
            movieJsonDataMap.put(RELEASE_DATE, releaseDate);
            movieJsonDataMap.put(USER_RATING, userRating);
            movieJsonDataMap.put(OVERVIEW, overview);

            //Log.v(LOG_TAG, "movie map : " + movieJsonDataMap.toString());
            movieJsonData.add(movieJsonDataMap);

        }
        //Log.v(LOG_TAG, "Built JSON data sample: " + movieJsonData[0]);
        //Log.v(LOG_TAG, "\n Movie Json data : \n" + movieJsonData);
        return movieJsonData;
    }

    public static void buildPosterFromPath(String posterPath, ImageView movieThumbnail){
        Uri movieUri = movieUri = Uri.parse(IMAGE_BASE_URL + posterPath).buildUpon().build();
        //Log.v(LOG_TAG, "Movie Uri built : "+movieUri);

        Picasso.with(movieThumbnail.getContext()).load(movieUri).into(movieThumbnail);
    }

}
