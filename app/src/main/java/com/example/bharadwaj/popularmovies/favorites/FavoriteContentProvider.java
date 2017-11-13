package com.example.bharadwaj.popularmovies.favorites;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import com.example.bharadwaj.popularmovies.favorites.FavoriteContract.Favorites;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import static com.example.bharadwaj.popularmovies.favorites.FavoriteContract.Favorites.TABLE_NAME;

/**
 * Created by Bharadwaj on 11/11/17.
 */

public class FavoriteContentProvider extends ContentProvider {

    public static final int FAVORITES = 100;
    public static final int FAVORITES_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final String LOG_TAG = FavoriteContentProvider.class.getSimpleName();

    public static UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(FavoriteContract.AUTHORITY, FavoriteContract.FAVORITES, FAVORITES);
        uriMatcher.addURI(FavoriteContract.AUTHORITY, FavoriteContract.FAVORITES + "/#", FAVORITES_WITH_ID);

        return uriMatcher;
    }

    private FavoriteDBHelper mFavoriteDBHelper;

    @Override
    public boolean onCreate() {
        Log.v(LOG_TAG, "Entering onCreate");
        Context context = getContext();
        mFavoriteDBHelper = new FavoriteDBHelper(context);
        Log.v(LOG_TAG, "Leaving onCreate");
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri,
                        @Nullable String[] projection,
                        @Nullable String selection,
                        @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {
        final SQLiteDatabase favoriteDB = mFavoriteDBHelper.getReadableDatabase();
        Log.v(LOG_TAG, "Getting Readable DB instance");
        Log.v(LOG_TAG, "DB open status : " + favoriteDB.isOpen());
        Log.v(LOG_TAG, "-------------------------------------------------------");
        Log.v(LOG_TAG, "Projection : " + projection);
        Log.v(LOG_TAG, "Selection : " + selection);
        Log.v(LOG_TAG, "Selection arguments : " + selectionArgs);
        Log.v(LOG_TAG, "Sort Order : " + sortOrder);
        Log.v(LOG_TAG, "-------------------------------------------------------");

        int match = sUriMatcher.match(uri);
        Cursor cursor;

        switch (match) {
            case FAVORITES:
                cursor =  favoriteDB.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            // Default exception
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        SQLiteDatabase favoritesDB = mFavoriteDBHelper.getWritableDatabase();
        Uri returningUri;

        int match = sUriMatcher.match(uri);
        switch (match){
            case FAVORITES:
                long insertedRowID = favoritesDB.insert(TABLE_NAME, null, contentValues);
                if (insertedRowID > 0){
                    returningUri = ContentUris.withAppendedId(Favorites.CONTENT_URI, insertedRowID);
                }else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return returningUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }
}
