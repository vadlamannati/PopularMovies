package com.example.bharadwaj.popularmovies.movie_utilities;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * @author Bharadwaj on 8/6/17.
 */

public class NetworkUtils {

    private final static String LOG_TAG = NetworkUtils.class.getSimpleName();
    private final static String BASE_MOVIES_URL = "http://api.themoviedb.org/3/movie/";

    //Query parameters
    private final static String API_KEY_PARAM = "api_key";

    //Query Param values
    private final static String API_KEY_VALUE = "cfdb086905a2ae796fb3453e69966fe6";


    public static URL buildURL(String sortPreference) {
        Log.v(LOG_TAG, "Entering buildURL method");
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
        Log.v(LOG_TAG, "Leaving buildURL method");

        return url;

    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        Log.v(LOG_TAG, "Entering getResponseFromHttpUrl method");

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setConnectTimeout(5000);
        urlConnection.setReadTimeout(5000);

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
            Log.v(LOG_TAG, "Leaving getResponseFromHttpUrl method");
        }
    }
}
