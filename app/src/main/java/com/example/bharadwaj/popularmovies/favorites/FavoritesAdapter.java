package com.example.bharadwaj.popularmovies.favorites;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.bharadwaj.popularmovies.R;
import com.example.bharadwaj.popularmovies.movies.Movie;
import com.example.bharadwaj.popularmovies.trailers.Trailer;
import com.example.bharadwaj.popularmovies.favorites.FavoriteContract.Favorites;
import java.util.ArrayList;

/**
 * Created by Bharadwaj on 11/11/17.
 */

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoriteViewHolder>{

    private static final String LOG_TAG = FavoritesAdapter.class.getSimpleName();

    private Context mContext;
    private Cursor mCursor;

    public void setCursor(Cursor cursor) {
        this.mCursor = cursor;
    }

    public Cursor getCursor() {
        return mCursor;
    }

    public FavoritesAdapter(Context context) {
        mContext = context;
    }

    @Override
    public FavoriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.favorite_list_item, parent, false);

        //Log.v(LOG_TAG, "Returning new ViewHolder object");
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FavoriteViewHolder holder, int position) {


        int IDIndex = mCursor.getColumnIndex(Favorites._ID);
        int movieIDindex = mCursor.getColumnIndex(Favorites.COLUMN_MOVIE_ID);
        int movieNameIndex = mCursor.getColumnIndex(Favorites.COLUMN_MOVIE_NAME);

        Log.v(LOG_TAG, "Position is : " + position);
        //Log.v(LOG_TAG, "Current Movie is : " + mCursor.get);

        int ID = mCursor.getInt(IDIndex);
        String movieID = mCursor.getString(movieIDindex);
        String movieName = mCursor.getString(movieNameIndex);

        holder.mMovieNameView.setText(movieName);
        //MovieJSONParser.buildPosterFromPath(movie.getPosterPath(), movieHolder.mMovieThumbnail);

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class FavoriteViewHolder extends RecyclerView.ViewHolder{

        private final LinearLayout mMovieLayout;
        private TextView mMovieNameView;

        public FavoriteViewHolder(View itemView){
            super(itemView);
            mMovieLayout = itemView.findViewById(R.layout.favorite_list_item);
            mMovieNameView = itemView.findViewById(R.id.favorite_movie_name);
        }
    }
}
