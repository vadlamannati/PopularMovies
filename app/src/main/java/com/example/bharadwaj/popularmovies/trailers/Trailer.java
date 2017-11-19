package com.example.bharadwaj.popularmovies.trailers;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Bharadwaj on 10/15/17.
 */

public class Trailer implements Parcelable {

    public static final String TRAILER = "Trailer-";
    private String mKey;

    public Trailer(String key) {
        this.mKey = key;
    }

    protected Trailer(Parcel in) {
        mKey = in.readString();
    }

    public static final Creator<Trailer> CREATOR = new Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };

    public String getmKey() {
        return mKey;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mKey);
    }
}
