package com.example.bharadwaj.popularmovies.reviews;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Bharadwaj on 10/15/17.
 */

public class Review implements Parcelable{

    public static final String SAYS = " says,";

    public static String mTotalResults;
    private String mAuthor;
    private String mContent;

    public Review(String author, String content) {
        this.mAuthor = author;
        this.mContent = content;
    }

    protected Review(Parcel in) {
        mAuthor = in.readString();
        mContent = in.readString();
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    public String getAuthor() {
        return mAuthor;
    }

    public String getContent() {
        return mContent;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mAuthor);
        parcel.writeString(mContent);
    }
}
