package com.example.bharadwaj.popularmovies.movie_utilities;

import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ImageView;

import com.example.bharadwaj.popularmovies.Movie;
import com.example.bharadwaj.popularmovies.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

/**
 * @author Bharadwaj on 8/11/17.
 */

public class MovieJSONParser {


    private static final String LOG_TAG = MovieJSONParser.class.getSimpleName();

    private final static String ID = "id";
    private final static String TITLE = "title";
    private final static String POSTER_PATH = "poster_path";
    private final static String OVERVIEW = "overview";
    private final static String USER_RATING = "vote_average";
    private final static String RELEASE_DATE = "release_date";
    private final static String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185/";

    private final static int TARGET_WIDTH = 345;
    private final static int TARGET_HEIGHT = 380;

    private final static String RESULTS = "results";
    private final static String ERROR_CODE = "cod";

    public final static String RATING_TOTAL_REFERENCE = "/10";

    public static ArrayList<Movie> getMovies(String movieJsonResponse) throws JSONException {
        Log.v(LOG_TAG, "Entering getMovies method");

        Movie movie;
        ArrayList<Movie> movieJsonData;
        JSONObject movieJsonResponseObject;
        JSONArray movieResultsArray;

        movieJsonResponseObject = new JSONObject(movieJsonResponse);

        if (movieJsonResponseObject.has(ERROR_CODE)) {
            int errorCode = movieJsonResponseObject.getInt(ERROR_CODE);
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

        movieResultsArray = movieJsonResponseObject.getJSONArray(RESULTS);
        movieJsonData = new ArrayList<>();

        Log.v(LOG_TAG, "Movie Array length : " + movieResultsArray.length());
        for (int i = 0; i < movieResultsArray.length(); i++) {

            JSONObject movieResults = movieResultsArray.getJSONObject(i);

            String id = movieResults.getString(ID);
            String title = movieResults.getString(TITLE);
            String userRating = movieResults.getString(USER_RATING);
            String posterPath = movieResults.getString(POSTER_PATH);
            String releaseDate = movieResults.getString(RELEASE_DATE);
            String overview = movieResults.getString(OVERVIEW);

            movie = new Movie(id, title, posterPath, overview, userRating, releaseDate);

            movieJsonData.add(movie);

        }
        Log.v(LOG_TAG, "Leaving getMovies method");
        return movieJsonData;
    }

    public static void buildPosterFromPath(String posterPath, ImageView movieThumbnail) {
        Uri movieUri = Uri.parse(IMAGE_BASE_URL + posterPath).buildUpon().build();
        //Log.v(LOG_TAG, "Movie Uri built : "+movieUri);

        Picasso.with(movieThumbnail.getContext())
                .load(movieUri)
                .placeholder(ContextCompat.getDrawable(movieThumbnail.getContext(), R.drawable.place_holder))
                .error(ContextCompat.getDrawable(movieThumbnail.getContext(), R.drawable.error))
                .resize(TARGET_WIDTH, TARGET_HEIGHT)
                .into(movieThumbnail);
    }

}
