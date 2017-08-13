package com.example.bharadwaj.popularmovies;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Bharadwaj on 8/6/17.
 */

public class NetworkUtils {

    private final static String LOG_TAG = NetworkUtils.class.getSimpleName();
    private final static String BASE_MOVIES_URL = "http://api.themoviedb.org/3/movie/";
    private final static String POPULAR_MOVIES_URL = BASE_MOVIES_URL + "popular";


    //private final static String p = "";


    //Query parameters
    private final static String API_KEY_PARAM = "api_key";
    private final static String QUERY_PARAM = "q";


    //Query Param values
    private final static String API_KEY_VALUE = "cfdb086905a2ae796fb3453e69966fe6";


    public static URL buildURL(String sortPreference){

        Log.v(LOG_TAG, "Sorting by " + sortPreference);

        URL url = null;

        Uri builtURI = Uri.parse(BASE_MOVIES_URL + sortPreference).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, API_KEY_VALUE)
                .build();

        try {
            url = new URL(builtURI.toString());
        } catch (MalformedURLException e) {
            Log.v(LOG_TAG, "Malformed URL exception occurred");
            e.printStackTrace();
        }

        Log.v(LOG_TAG, "URL Built : " + url);

        return url;

    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        Log.v(LOG_TAG, "Getting JSON response from URL : " + url);
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

}
