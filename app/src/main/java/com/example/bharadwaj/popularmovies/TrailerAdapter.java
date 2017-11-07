package com.example.bharadwaj.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.ArrayList;

/**
 * Created by Bharadwaj on 10/15/17.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder > {
    private static final String LOG_TAG = TrailerAdapter.class.getSimpleName();
    private final TrailerAdapterOnClickHandler mOnClickHandler;
    private ArrayList<Trailer> mTrailerData;

    public TrailerAdapter(TrailerAdapterOnClickHandler onClickHandler) {
        mOnClickHandler = onClickHandler;
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Log.v(LOG_TAG, "Entering onCreateViewHolder method");

        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.trailer_list_item, parent, false);

        //Log.v(LOG_TAG, "Returning new ViewHolder object");
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder  trailerHolder, int position) {
        //Log.v(LOG_TAG, "Entering onBindViewHolder method ");

        Trailer trailer = mTrailerData.get(position);

        Log.v(LOG_TAG, "Position is : " + position);
        Log.v(LOG_TAG, "Current Trailer is : " + trailer);

        trailerHolder.mTrailerView.setText(Trailer.TRAILER + String.valueOf(position));
        //MovieJSONParser.buildPosterFromPath(movie.getPosterPath(), movieHolder.mMovieThumbnail);

    }

    @Override
    public int getItemCount() {
        //Log.v(LOG_TAG, "Entering getItemCount method");

        if (null == mTrailerData) return 0;
        return mTrailerData.size();
    }

    public void setTrailerData(ArrayList<Trailer> trailerData) {
        //Log.v(LOG_TAG, "Setting Movie data to Adapter. Movie Data length : " + movieData.size());
        //Log.v(LOG_TAG, "Entering setMovieData method");

        mTrailerData = trailerData;
        notifyDataSetChanged();

        //Log.v(LOG_TAG, "Leaving setMovieData method");
    }

    public ArrayList<Trailer> getTrailers(){
        return mTrailerData;
    }

    public interface TrailerAdapterOnClickHandler {
        void performOnClick(Trailer currentTrailer);
    }

    public class TrailerViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Button mTrailerView;


        public TrailerViewHolder (View itemView) {
            super(itemView);
            mTrailerView = itemView.findViewById(R.id.trailer_item_view);
            //mTrailerView.;
            //Log.v(LOG_TAG, "Attaching movie overview view with ViewHolder");
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            //Log.v(LOG_TAG, "Entering onClick method in ViewHolder");
            int currentAdapterPosition = getAdapterPosition();
            Trailer currentTrailer = mTrailerData.get(currentAdapterPosition);
            mOnClickHandler.performOnClick(currentTrailer);
        }
    }

}