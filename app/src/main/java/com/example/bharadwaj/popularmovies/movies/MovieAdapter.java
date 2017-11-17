package com.example.bharadwaj.popularmovies.movies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.bharadwaj.popularmovies.R;
import com.example.bharadwaj.popularmovies.json_parsers.MovieJSONParser;

import java.util.ArrayList;

/**
 * @author Bharadwaj on 8/6/17.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder>{
    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();
    private final MovieAdapterOnClickHandler mOnClickHandler;
    private ArrayList<Movie> mMovieData;

    public MovieAdapter(MovieAdapterOnClickHandler onClickHandler) {
        mOnClickHandler = onClickHandler;
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
        if(movieData != null){
            Log.v(LOG_TAG, "Setting Movie data to Adapter. Movie Data length : " + movieData.size());
        }
        //Log.v(LOG_TAG, "Entering setMovieData method");

        mMovieData = movieData;
        notifyDataSetChanged();

        //Log.v(LOG_TAG, "Leaving setMovieData method");
    }

    public ArrayList<Movie> getMovies(){
        return mMovieData;
    }

    public interface MovieAdapterOnClickHandler {
        void performOnClick(Movie currentMovie);
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView mMovieThumbnail;


        public MovieViewHolder(View itemView) {
            super(itemView);
            mMovieThumbnail = itemView.findViewById(R.id.movie_thumbnail);
            //Log.v(LOG_TAG, "Attaching movie overview view with ViewHolder");
            mMovieThumbnail.setOnClickListener(this);
            //itemView.setOnClickListener(this); is working as well. But this isn't working for Trailers
        }

        @Override
        public void onClick(View view) {
            //Log.v(LOG_TAG, "Entering onClick method in ViewHolder");
            int currentAdapterPosition = getAdapterPosition();
            Movie currentMovie = mMovieData.get(currentAdapterPosition);
            mOnClickHandler.performOnClick(currentMovie);
        }
    }

}
