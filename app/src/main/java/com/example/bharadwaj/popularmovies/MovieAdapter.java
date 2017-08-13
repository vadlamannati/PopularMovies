package com.example.bharadwaj.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.bharadwaj.popularmovies.movie_utilities.MovieJSONParser;

import java.util.ArrayList;

/**
 * @author Bharadwaj on 8/6/17.
 *
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    private ArrayList<Movie> mMovieData;
    private final MovieAdapterOnClickHandler mOnClickHandler;

    public interface MovieAdapterOnClickHandler {
        void performOnClick(Movie currentMovie);
    }

    public MovieAdapter(MovieAdapterOnClickHandler onClickHandler) {
        mOnClickHandler = onClickHandler;
    }

 
    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView mMovieThumbnail;


        public MovieViewHolder(View itemView) {
            super(itemView);
            mMovieThumbnail = itemView.findViewById(R.id.movie_thumbnail);
            //Log.v(LOG_TAG, "Attaching movie overview view with ViewHolder");
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.v(LOG_TAG, "Entering onClick method in ViewHolder");
            int currentAdapterPosition = getAdapterPosition();
            Movie currentMovie = mMovieData.get(currentAdapterPosition);
            mOnClickHandler.performOnClick(currentMovie);
        }
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Log.v(LOG_TAG, "Entering onCreateViewHolder method");

        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.movie_list_item, parent, false);

        //Log.v(LOG_TAG, "Returning new ViewHolder object");
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder movieHolder, int position) {
        //Log.v(LOG_TAG, "Entering onBindViewHolder method ");

        Movie movie = mMovieData.get(position);

        //Log.v(LOG_TAG, "Position is : " + position);
        //Log.v(LOG_TAG, "Current Movie is : " + movie);

        MovieJSONParser.buildPosterFromPath(movie.getPosterPath(), movieHolder.mMovieThumbnail);

    }

    @Override
    public int getItemCount() {
        //Log.v(LOG_TAG, "Entering getItemCount method");

        if (null == mMovieData) return 0;
        return mMovieData.size();
    }


    public void setMovieData(ArrayList<Movie> movieData) {
        //Log.v(LOG_TAG, "Setting Movie data to Adapter. Movie Data length : " + movieData.size());
        mMovieData = movieData;

        notifyDataSetChanged();
    }

}
