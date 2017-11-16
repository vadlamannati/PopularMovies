package com.example.bharadwaj.popularmovies.favorites;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bharadwaj.popularmovies.R;
import com.example.bharadwaj.popularmovies.json_parsers.MovieJSONParser;
import com.example.bharadwaj.popularmovies.movies.Movie;
import com.example.bharadwaj.popularmovies.favorites.FavoriteContract.Favorites;

/**
 * Created by Bharadwaj on 11/11/17.
 */

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoriteViewHolder>{

    private static final String LOG_TAG = FavoritesAdapter.class.getSimpleName();
    private final FavoriteAdapterOnClickHandler mOnClickHandler;

    private Cursor mCursor;

    public void setCursor(Cursor cursor) {
        this.mCursor = cursor;
        notifyDataSetChanged();
    }

    public Cursor getCursor() {
        return mCursor;
    }

    public FavoritesAdapter(FavoriteAdapterOnClickHandler onClickHandler) {
        mOnClickHandler = onClickHandler;
    }

    @Override
    public FavoriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.favorite_list_item, parent, false);

        //Log.v(LOG_TAG, "Returning new ViewHolder object");
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FavoriteViewHolder movieHolder, int position) {

        mCursor.moveToPosition(position);

        Movie movie;

        Log.v(LOG_TAG, "Position is : " + position);
        //Log.v(LOG_TAG, "Current Movie is : " + mCursor.get);

        String movieId = mCursor.getString(mCursor.getColumnIndex(Favorites.COLUMN_MOVIE_ID));
        String movieName = mCursor.getString(mCursor.getColumnIndex(Favorites.COLUMN_MOVIE_NAME));
        String posterPath = mCursor.getString(mCursor.getColumnIndex(Favorites.COLUMN_MOVIE_POSTER_PATH));
        String overview = mCursor.getString(mCursor.getColumnIndex(Favorites.COLUMN_MOVIE_OVERVIEW));
        String userRating = mCursor.getString(mCursor.getColumnIndex(Favorites.COLUMN_MOVIE_USER_RATING));
        String releaseDate = mCursor.getString(mCursor.getColumnIndex(Favorites.COLUMN_MOVIE_RELEASE_DATE));

        movie = new Movie(movieId, movieName, posterPath, overview, userRating, releaseDate);

        Log.v(LOG_TAG, "Binding Movie to the view : " + movie.toString());

        movieHolder.mFavoriteMovieName.setText(movieName);
        movieHolder.mFavoriteMovieRating.setText(userRating + MovieJSONParser.USER_RATING_REFERENCE_TOTAL);
        movieHolder.mFavoriteMovieReleaseDate.setText(releaseDate);
        MovieJSONParser.buildPosterFromPath(movie.getPosterPath(), movieHolder.mFavoriteMovieThumbnail);

        movieHolder.itemView.setTag(mCursor.getInt(mCursor.getColumnIndex(Favorites._ID)));
    }

    @Override
    public int getItemCount() {
        if(null == mCursor){
            return 0;
        }
        return mCursor.getCount();
    }

    public interface FavoriteAdapterOnClickHandler {
        void performOnClick(Movie currentMovie);
    }

    class FavoriteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView mFavoriteMovieName;
        private ImageView mFavoriteMovieThumbnail;
        private TextView mFavoriteMovieRating;
        private TextView mFavoriteMovieReleaseDate;


        public FavoriteViewHolder(View itemView){
            super(itemView);
            mFavoriteMovieThumbnail = itemView.findViewById(R.id.favorite_movie_thumbnail);
            mFavoriteMovieName = itemView.findViewById(R.id.favorite_movie_name);
            mFavoriteMovieRating = itemView.findViewById(R.id.favorite_movie_rating);
            mFavoriteMovieReleaseDate = itemView.findViewById(R.id.favorite_movie_release_date);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            //Log.v(LOG_TAG, "Entering onClick method in ViewHolder");
            int currentAdapterPosition = getAdapterPosition();

            mCursor.moveToPosition(currentAdapterPosition);
            Movie currentMovie;

            Log.v(LOG_TAG, "Position is : " + currentAdapterPosition);
            //Log.v(LOG_TAG, "Current Movie is : " + mCursor.get);

            String movieId = mCursor.getString(mCursor.getColumnIndex(Favorites.COLUMN_MOVIE_ID));
            String movieName = mCursor.getString(mCursor.getColumnIndex(Favorites.COLUMN_MOVIE_NAME));
            String posterPath = mCursor.getString(mCursor.getColumnIndex(Favorites.COLUMN_MOVIE_POSTER_PATH));
            String overview = mCursor.getString(mCursor.getColumnIndex(Favorites.COLUMN_MOVIE_OVERVIEW));
            String userRating = mCursor.getString(mCursor.getColumnIndex(Favorites.COLUMN_MOVIE_USER_RATING));
            String releaseDate = mCursor.getString(mCursor.getColumnIndex(Favorites.COLUMN_MOVIE_RELEASE_DATE));

            currentMovie = new Movie(movieId, movieName, posterPath, overview, userRating, releaseDate);

            mOnClickHandler.performOnClick(currentMovie);
        }
    }
}
