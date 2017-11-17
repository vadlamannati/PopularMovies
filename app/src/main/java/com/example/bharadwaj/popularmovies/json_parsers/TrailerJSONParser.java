package com.example.bharadwaj.popularmovies.json_parsers;

import android.util.Log;

import com.example.bharadwaj.popularmovies.trailers.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

/**
 * Created by Bharadwaj on 10/16/17.
 */

public class TrailerJSONParser {

    private static final String LOG_TAG = TrailerJSONParser.class.getSimpleName();
    private final static String KEY = "key";
    private final static String TYPE = "type";
    private final static String TRAILER = "Trailer";
    private final static String TEASER = "Teaser";
    private final static String FEATURETTE = "Featurette";

    private final static String RESULTS = "results";
    private final static String ERROR_CODE = "cod";

    public static ArrayList<Trailer> getTrailers(String trailerJsonResponse) throws JSONException {
        Log.v(LOG_TAG, "Entering getTrailers method");


        Trailer trailer;
        ArrayList<Trailer> trailerJsonData;
        JSONObject trailerJsonResponseObject;
        JSONArray trailerResultsArray;

        trailerJsonResponseObject = new JSONObject(trailerJsonResponse);

        if (trailerJsonResponseObject.has(ERROR_CODE)) {
            int errorCode = trailerJsonResponseObject.getInt(ERROR_CODE);
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

        trailerResultsArray = trailerJsonResponseObject.getJSONArray(RESULTS);
        trailerJsonData = new ArrayList<>();

        Log.v(LOG_TAG, "Trailer Array length : " + trailerResultsArray.length());
        for (int i = 0; i < trailerResultsArray.length(); i++) {

            JSONObject trailerResults = trailerResultsArray.getJSONObject(i);
            String key = trailerResults.getString(KEY);
            String type = trailerResults.getString(TYPE);
            if(type.equals(TRAILER) || type.equals(TEASER) || type.equals(FEATURETTE)){
                trailer = new Trailer(key);
                trailerJsonData.add(trailer);
            }
        }
        Log.v(LOG_TAG, "Leaving getTrailers method");
        return trailerJsonData;
    }
}
