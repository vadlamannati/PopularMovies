package com.example.bharadwaj.popularmovies.favorites;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Bharadwaj on 11/18/17.
 */

public class FavoritesCursorBundle implements Parcelable {

    private Cursor mFavoriteCursor;

    public FavoritesCursorBundle(Parcel in) {
    }

    public static final Creator<FavoritesCursorBundle> CREATOR = new Creator<FavoritesCursorBundle>() {
        @Override
        public FavoritesCursorBundle createFromParcel(Parcel in) {
            return new FavoritesCursorBundle(in);
        }

        @Override
        public FavoritesCursorBundle[] newArray(int size) {
            return new FavoritesCursorBundle[size];
        }
    };

    public Cursor getFavoriteCursor() {
        return mFavoriteCursor;
    }

    public void setFavoriteCursor(Cursor favoriteCursor) {
        this.mFavoriteCursor = favoriteCursor;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
    }
}
