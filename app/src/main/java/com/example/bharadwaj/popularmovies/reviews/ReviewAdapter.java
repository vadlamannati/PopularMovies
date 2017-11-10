package com.example.bharadwaj.popularmovies.reviews;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bharadwaj.popularmovies.R;

import java.util.ArrayList;

/**
 * Created by Bharadwaj on 10/15/17.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder > {
    private static final String LOG_TAG = ReviewAdapter.class.getSimpleName();
    private final ReviewAdapterOnClickHandler mOnClickHandler;
    private ArrayList<Review> mReviewData;

    public ReviewAdapter(ReviewAdapterOnClickHandler onClickHandler) {
        mOnClickHandler = onClickHandler;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Log.v(LOG_TAG, "Entering onCreateViewHolder method");

        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.review_list_item, parent, false);

        //Log.v(LOG_TAG, "Returning new ViewHolder object");
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder  reviewHolder, int position) {
        //Log.v(LOG_TAG, "Entering onBindViewHolder method ");

        Review review = mReviewData.get(position);

        Log.v(LOG_TAG, "Position is : " + position);
        Log.v(LOG_TAG, "Current Review is : " + review);

        reviewHolder.mReviewAuthorView.setText(String.valueOf(review.getAuthor()));
        reviewHolder.mReviewContentView.setText(String.valueOf(review.getContent()));
        //MovieJSONParser.buildPosterFromPath(movie.getPosterPath(), movieHolder.mMovieThumbnail);

    }

    @Override
    public int getItemCount() {
        //Log.v(LOG_TAG, "Entering getItemCount method");

        if (null == mReviewData) return 0;
        return mReviewData.size();
    }

    public void setReviewData(ArrayList<Review> reviewData) {
        //Log.v(LOG_TAG, "Setting Movie data to Adapter. Movie Data length : " + movieData.size());
        //Log.v(LOG_TAG, "Entering setMovieData method");

        mReviewData = reviewData;
        notifyDataSetChanged();

        //Log.v(LOG_TAG, "Leaving setMovieData method");
    }

    public ArrayList<Review> getReviews(){
        return mReviewData;
    }

    public interface ReviewAdapterOnClickHandler {
        void performOnClick(Review currentReview);
    }

    public class ReviewViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mReviewAuthorView;
        private TextView mReviewContentView;


        public ReviewViewHolder (View itemView) {
            super(itemView);
            mReviewAuthorView = itemView.findViewById(R.id.review_author);
            mReviewContentView = itemView.findViewById(R.id.review_content);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            //Log.v(LOG_TAG, "Entering onClick method in ViewHolder");
            int currentAdapterPosition = getAdapterPosition();
            Review currentReview = mReviewData.get(currentAdapterPosition);
            mOnClickHandler.performOnClick(currentReview);
        }
    }

}