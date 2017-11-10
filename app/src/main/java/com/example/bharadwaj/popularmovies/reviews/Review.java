package com.example.bharadwaj.popularmovies.reviews;

/**
 * Created by Bharadwaj on 10/15/17.
 */

public class Review {

    public static final String SAYS = " says,";

    private String mAuthor;
    private String mContent;

    public Review(String author, String content) {
        this.mAuthor = author;
        this.mContent = content;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getContent() {
        return mContent;
    }

}
