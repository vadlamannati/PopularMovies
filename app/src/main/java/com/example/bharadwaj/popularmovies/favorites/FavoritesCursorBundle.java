package com.example.bharadwaj.popularmovies.favorites;

import android.database.Cursor;

import java.io.Serializable;

/**
 * Created by Bharadwaj on 11/18/17.
 */

public class FavoritesCursorBundle implements Serializable {

    private Cursor mFavoriteCursor;

    public Cursor getFavoriteCursor() {
        return mFavoriteCursor;
    }

    public void setFavoriteCursor(Cursor favoriteCursor) {
        this.mFavoriteCursor = favoriteCursor;
    }
}
