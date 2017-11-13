package com.example.bharadwaj.popularmovies.favorites;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.bharadwaj.popularmovies.favorites.FavoriteContract.Favorites;

/**
 * Created by Bharadwaj on 11/11/17.
 */

public class FavoriteDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "favoritesDB.db";

    private static final int VERSION = 1;
    private static final String LOGTAG = FavoriteDBHelper.class.getSimpleName();

    public FavoriteDBHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        Log.v(LOGTAG, "FavoriteDBHelper constructor");
    }

    @Override
    public void onCreate(SQLiteDatabase favoritesDB) {
        Log.v(LOGTAG, "Entering onCreate");
        final String TABLE_NAME = "CREATE TABLE "  + Favorites.TABLE_NAME + " (" +
                Favorites._ID                + " INTEGER PRIMARY KEY, " +
                Favorites.COLUMN_MOVIE_ID    + " TEXT NOT NULL, " +
                Favorites.COLUMN_MOVIE_NAME  + " TEXT NOT NULL);";

        Log.v(LOGTAG, "Creating Table: " + TABLE_NAME);
        favoritesDB.execSQL(TABLE_NAME);
        Log.v(LOGTAG, "Leaving onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase favoritesDB, int i, int i1) {
        favoritesDB.execSQL("DROP TABLE IF EXISTS " + Favorites.TABLE_NAME);
        onCreate(favoritesDB);
    }
}
