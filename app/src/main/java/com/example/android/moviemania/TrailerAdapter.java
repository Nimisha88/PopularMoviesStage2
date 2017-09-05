package com.example.android.moviemania;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.example.android.moviemania.utilities.NetworkUtils;

import java.net.URL;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    private static final String TAG = MovieAdapter.class.getSimpleName();
    private Context mContext;
    private final TrailerOnClickHandler mClickHandler;
    private Cursor mTrailerDataCursor;

    //Constructors must have List<> in order to populate List/Grid View
    public TrailerAdapter(Context context, TrailerOnClickHandler handler) {
        mContext = context;
        mClickHandler = handler;
    }

    //Interface Listener Handler
    public interface TrailerOnClickHandler {
        void onTrailerClick(String watchVideoURL);
    }

    //Swap Cursor when data changes
    void swapCursor(Cursor newCursor) {
        mTrailerDataCursor = newCursor;
        notifyDataSetChanged();
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        int LayoutIDForView = R.layout.trailer_view;
        LayoutInflater movieViewLayoutInflater = LayoutInflater.from(mContext);
        View trailerView = movieViewLayoutInflater.inflate(LayoutIDForView, viewGroup, false);
        return new TrailerAdapter.TrailerViewHolder(trailerView);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        mTrailerDataCursor.moveToPosition(position);
        String videoKey = mTrailerDataCursor.getString(mContext.getResources().getInteger(R.integer.index_video_video_key));

        try {

            URL watchVideoURL = NetworkUtils.buildWatchVideoURL(mContext, videoKey);
            Log.i(TAG, "URL associating to the view is - " + watchVideoURL.toString());
            holder.mTrailerImageView.setTag(watchVideoURL.toString());

        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "Watch Video URL is Null");
        }

    }

    @Override
    public int getItemCount() {
        if (mTrailerDataCursor == null)
            return 0;

        return mTrailerDataCursor.getCount();
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder {
        private ImageView mTrailerImageView;
        //private WebView mTrailerWebView;

        //Constructor
        public TrailerViewHolder(View view) {
            super(view);
            mTrailerImageView = (ImageView) view.findViewById(R.id.img_trailer);
            //mTrailerWebView = (WebView) view.findViewById(R.id.wv_trailer);
            showTrailerView();

            mTrailerImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String watchVideoURL = view.getTag().toString();
                    Log.i(TAG, "onClick:" + watchVideoURL);
                    mClickHandler.onTrailerClick(watchVideoURL);
                }
            });
        }

        public void showTrailerView() {
            mTrailerImageView.setVisibility(View.VISIBLE);
        }
    }
}
