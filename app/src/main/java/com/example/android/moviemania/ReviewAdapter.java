package com.example.android.moviemania;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private static final String TAG = ReviewAdapter.class.getSimpleName();

    private Context mContext;
    private Cursor mReviewDataCursor;
    private ReviewOnClickHandler mClickHandler;

    public ReviewAdapter(Context context, ReviewOnClickHandler handler) {
        mContext = context;
        mClickHandler = handler;
    }

    //Interface Listener Handler
    public interface ReviewOnClickHandler {
        void onReviewClick(String watchVideoURL);
    }

    //Swap Cursor when data changes
    void swapCursor(Cursor newCursor) {
        mReviewDataCursor = newCursor;
        notifyDataSetChanged();
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        int LayoutIDForView = R.layout.review_view;
        LayoutInflater movieViewLayoutInflater = LayoutInflater.from(mContext);
        View reviewView = movieViewLayoutInflater.inflate(LayoutIDForView, viewGroup, false);
        return new ReviewAdapter.ReviewViewHolder(reviewView);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        mReviewDataCursor.moveToPosition(position);
        String reviewURL = mReviewDataCursor.getString(mContext.getResources().getInteger(R.integer.index_review_url));
        Log.i(TAG, "URL associating to the view is - " + reviewURL);
        holder.mReviewImageView.setTag(reviewURL);
    }

    @Override
    public int getItemCount() {
        if (mReviewDataCursor == null)
            return 0;
        return mReviewDataCursor.getCount();
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {

        private ImageView mReviewImageView;

        public ReviewViewHolder(View view) {
            super(view);

            mReviewImageView = (ImageView) view.findViewById(R.id.img_review);

            showReview();
            mReviewImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String reviewURL = view.getTag().toString();
                    Log.i(TAG, "onClick:" + reviewURL);
                    mClickHandler.onReviewClick(reviewURL);
                }
            });

        }

        public void showReview() {
            mReviewImageView.setVisibility(View.VISIBLE);
        }
    }
}
