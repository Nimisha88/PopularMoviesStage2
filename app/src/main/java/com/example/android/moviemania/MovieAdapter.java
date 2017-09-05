package com.example.android.moviemania;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.example.android.moviemania.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;
import java.net.URL;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    //Member Variables
    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();
    private Context mContext;
    private final ListItemOnClickHandler mClickHandler;
    private Cursor mMovieDataCursor;

    //Constructors must have List<> in order to populate List/Grid View
    public MovieAdapter(Context context, ListItemOnClickHandler handler) {
        mContext = context;
        mClickHandler = handler;
    }

    //Interface Listener Handler
    public interface ListItemOnClickHandler {
        void onClick(long movieUniqueID, long movieID);
    }

    //Swap Cursor when data changes
    void swapCursor(Cursor newCursor) {
        mMovieDataCursor = newCursor;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {

        if (mMovieDataCursor == null)
            return 0;

        return mMovieDataCursor.getCount();
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        int LayoutIDForView = R.layout.movie_view;
        LayoutInflater movieViewLayoutInflater = LayoutInflater.from(mContext);
        View movieView = movieViewLayoutInflater.inflate(LayoutIDForView, viewGroup, false);
        return new MovieViewHolder(movieView);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder movieViewHolder, int position) {
        mMovieDataCursor.moveToPosition(position);
        movieViewHolder.itemView.setTag(mMovieDataCursor.getLong(mContext.getResources().getInteger(R.integer.index_movie_id)));
        String posterPathString = mMovieDataCursor.getString(mContext.getResources().getInteger(R.integer.index_movie_poster_path));
        URL posterURL = NetworkUtils.buildPosterURL(posterPathString);
        Picasso.with(mContext).load(posterURL.toString())
                .placeholder(R.mipmap.movies)
                .error(R.mipmap.image_unavailable)
                .into(movieViewHolder.mMoviePosterImage);
    }

    //Inner Class ViewHolder
    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mMoviePosterImage;

        //Constructor
        public MovieViewHolder(View view) {
            super(view);
            mMoviePosterImage = (ImageView) view.findViewById(R.id.img_movie_poster);
            showMoviePoster();
            view.setOnClickListener(this);
        }

        public void showMoviePoster() {
            mMoviePosterImage.setVisibility(View.VISIBLE);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mMovieDataCursor.moveToPosition(adapterPosition);
            long movieUniqueID = mMovieDataCursor.getLong(mContext.getResources().getInteger(R.integer.index_movie_auto_id));
            long movieID = mMovieDataCursor.getLong(mContext.getResources().getInteger(R.integer.index_movie_id));
            mClickHandler.onClick(movieUniqueID, movieID);
        }
    }
}