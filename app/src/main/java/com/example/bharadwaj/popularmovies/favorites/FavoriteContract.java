package com.example.bharadwaj.popularmovies.favorites;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Bharadwaj on 11/11/17.
 */

public class FavoriteContract {

    public static final String AUTHORITY = "com.example.bharadwaj.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    //Possible paths to access data:
    public static final String FAVORITES = "favorites";
    public static final String FAVORITE = "favorite";

    public static final class Favorites implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(FAVORITES).build();

        public static final String TABLE_NAME = "favorites";

        // "_ID" column will be automatically generated due to the BaseColumns
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_MOVIE_NAME = "movie_name";

    }

}
