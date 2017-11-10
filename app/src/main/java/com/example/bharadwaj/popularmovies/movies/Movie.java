package com.example.bharadwaj.popularmovies.movies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Bharadwaj on 8/13/17.
 *         POJO class defined to deal with movie details in an easier way
 */

public class Movie implements Parcelable {

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel movieParcel) {
            return new Movie(movieParcel);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    private final String mID;
    private final String mTitle;
    private final String mPosterPath;
    private final String mOverview;
    private final String mUserRating;
    private final String mReleaseDate;

    public Movie(String id, String title, String posterPath, String overview, String userRating, String releaseDate) {
        this.mID = id;
        this.mTitle = title;
        this.mPosterPath = posterPath;
        this.mOverview = overview;
        this.mUserRating = userRating;
        this.mReleaseDate = releaseDate;
    }

    private Movie(Parcel movieParcel) {
        mID = movieParcel.readString();
        mTitle = movieParcel.readString();
        mPosterPath = movieParcel.readString();
        mOverview = movieParcel.readString();
        mUserRating = movieParcel.readString();
        mReleaseDate = movieParcel.readString();
    }

    public String getID() {
        return mID;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getPosterPath() {
        return mPosterPath;
    }

    public String getOverview() {
        return mOverview;
    }

    public String getUserRating() {
        return mUserRating;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mID);
        parcel.writeString(mTitle);
        parcel.writeString(mPosterPath);
        parcel.writeString(mOverview);
        parcel.writeString(mUserRating);
        parcel.writeString(mReleaseDate);
    }

    @Override
    public String toString() {

        return this.getID()
                + this.getTitle()
                + this.getPosterPath()
                + this.getOverview()
                + this.getUserRating()
                + this.getReleaseDate();
    }
}
