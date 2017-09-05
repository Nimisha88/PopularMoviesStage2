package com.example.android.moviemania;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import com.example.android.moviemania.data.MovieContract;
import com.example.android.moviemania.data.MovieContract.MovieEntry;
import com.example.android.moviemania.data.MovieContract.VideoEntry;
import com.example.android.moviemania.data.MovieContract.ReviewEntry;
import com.example.android.moviemania.data.MovieContract.FavoriteEntry;
import com.example.android.moviemania.favorite.FavoriteUtils;
import com.example.android.moviemania.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;
import android.support.design.widget.FloatingActionButton;
import android.widget.Toast;

import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MovieDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        TrailerAdapter.TrailerOnClickHandler, ReviewAdapter.ReviewOnClickHandler{

    private static final String TAG = MovieDetailActivity.class.getSimpleName();

    //Bind Views
    @BindView(R.id.img_thumbnail) ImageView mThumbnailImageView;
    @BindView(R.id.tv_movie_title) TextView mMovieTitleTextView;
    @BindView(R.id.tv_movie_overview) TextView mMovieOverviewTextView;
    @BindView(R.id.tv_movie_vote_average) TextView mMovieVoteAverageTextView;
    @BindView(R.id.rb_movie_vote_average) RatingBar mMovieVoteAverageRatingBar;
    @BindView(R.id.rv_trailers) RecyclerView mTrailerRecyclerView;
    @BindView(R.id.rv_reviews) RecyclerView mReviewRecyclerView;
    @BindView(R.id.fab) FloatingActionButton mFloatingActionButton;
    @BindView(R.id.tv_review_unavailable) TextView mReviewUnavailableTextView;
    @BindView(R.id.tv_trailer_unavailable) TextView mTrailerUnavailableTextView;


    //TrailerRecyclerView
    private RecyclerView.LayoutManager mTrailerLayoutManager;
    private TrailerAdapter mTrailerAdapter;
    private int mTrailerPosition = RecyclerView.NO_POSITION;

    //ReviewRecyclerView
    private RecyclerView.LayoutManager mReviewLayoutManager;
    private ReviewAdapter mReviewAdapter;
    private int mReviewPosition = RecyclerView.NO_POSITION;

    //ButterKnife Unbinder
    private Unbinder unbinder;

    //Projection
    private static final String[] MAIN_MOVIES_DISPLAY_PROJECTION = MovieEntry.getDBTableProjection();
    private static final String[] TRAILERS_DISPLAY_PROJECTION = VideoEntry.getDBTableProjection();
    private static final String[] REVIEW_DISPLAY_PROJECTION = MovieContract.ReviewEntry.getDBTableProjection();

    private static final int MOVIE_DETAIL_LOADER = 131;
    private static final int TRAILER_LOADER = 132;
    private static final int REVIEW_LOADER = 133;


    private Uri mMovieDisplayUri;
    private Uri mTrailerUri;
    private Uri mReviewUri;
    private long mMovieID = 0;
    private String mContentURIIdentifier;
    private Toast mToast;
    private boolean whetherSetFabOnClick = true;
    private URL mFirstTrailerURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_movie_detail);
        setContentView(R.layout.activity_detail);

        //Get the Intent that started this Activity
        Intent intentThatStartedThisActivity = getIntent();

        Log.i(TAG, "In Movie Activity Detail, fetched Intent");

        unbinder = ButterKnife.bind(this);

        if (intentThatStartedThisActivity != null) {

            String intentMovieUniqueID = getString(R.string.detail_act_movie_auto_id_key);
            String intentMovieIDKey = getString(R.string.detail_act_movie_id_key);
            String intentContentURIKey = getString(R.string.detail_act_uri_key);

            if (intentThatStartedThisActivity.hasExtra(intentContentURIKey)) {
                mContentURIIdentifier = intentThatStartedThisActivity.getStringExtra(intentContentURIKey);
            }

            if (intentThatStartedThisActivity.hasExtra(intentMovieUniqueID)) {

                //Extract Movie Details that was clicked
                long movieUniqueID = intentThatStartedThisActivity.getLongExtra(intentMovieUniqueID, 0);

                Log.i(TAG, "In Movie Activity Detail, fetched Movie Unique ID");

                //Fetch Movie Clicked Data
                if (mContentURIIdentifier.equals(getString(R.string.content_uri_movie)))
                    mMovieDisplayUri = MovieEntry.CONTENT_URI.buildUpon()
                        .appendPath(Long.toString(movieUniqueID))
                        .build();
            }

            if (intentThatStartedThisActivity.hasExtra(intentMovieIDKey)) {

                //Extract Movie Details that was clicked
                mMovieID = intentThatStartedThisActivity.getLongExtra(intentMovieIDKey, 0);

                Log.i(TAG, "In Movie Activity Detail, fetched Movie ID");

                //Fetch Trailer Data
                mTrailerUri = VideoEntry.CONTENT_URI.buildUpon()
                        .appendPath(Long.toString(mMovieID)).build();

                mReviewUri = ReviewEntry.CONTENT_URI.buildUpon()
                        .appendPath(Long.toString(mMovieID)).build();

                if (mContentURIIdentifier.equals(getString(R.string.content_uri_fav)))
                    mMovieDisplayUri = FavoriteEntry.CONTENT_URI.buildUpon()
                        .appendPath(Long.toString(mMovieID))
                        .build();

            }

            Log.i(TAG, "In Movie Activity Detail, fetched Uri " + mTrailerUri);
            Log.i(TAG, "In Movie Activity Detail, fetched Uri " + mReviewUri);
            Log.i(TAG, "In Movie Activity Detail, fetched Uri " + mMovieDisplayUri);
        }

        if (mMovieDisplayUri == null || mTrailerUri == null || mReviewUri == null)
            throw new NullPointerException("Movie/Trailer/Review Content URI for Detail Activity cannot be NULL!");

        Log.i(TAG, "onCreate: Initializing Member Variables in Movie Detail Activity");

        //Trailer
        mTrailerLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mTrailerRecyclerView.setLayoutManager(mTrailerLayoutManager);
        mTrailerRecyclerView.setHasFixedSize(true);
        mTrailerAdapter = new TrailerAdapter(this, this);
        mTrailerRecyclerView.setAdapter(mTrailerAdapter);
        mTrailerRecyclerView.setVisibility(View.INVISIBLE);

        //Review
        mReviewLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mReviewRecyclerView.setLayoutManager(mReviewLayoutManager);
        mReviewRecyclerView.setHasFixedSize(true);
        mReviewAdapter = new ReviewAdapter(this, this);
        mReviewRecyclerView.setAdapter(mReviewAdapter);
        mReviewRecyclerView.setVisibility(View.INVISIBLE);

        Log.i(TAG, "onCreate: Movie Detail Activity start Loaders");

        //Loaders

        Loader<Cursor> movieLoader = getSupportLoaderManager().getLoader(MOVIE_DETAIL_LOADER);
        Loader<Cursor> trailerLoader = getSupportLoaderManager().getLoader(TRAILER_LOADER);
        Loader<Cursor> reviewLoader = getSupportLoaderManager().getLoader(REVIEW_LOADER);

        if (movieLoader == null)
            getSupportLoaderManager().initLoader(MOVIE_DETAIL_LOADER, null, this);
        else
            getSupportLoaderManager().restartLoader(MOVIE_DETAIL_LOADER, null, this);

        if (trailerLoader == null)
            getSupportLoaderManager().initLoader(TRAILER_LOADER, null, this);
        else
            getSupportLoaderManager().restartLoader(TRAILER_LOADER, null, this);

        if (reviewLoader == null)
            getSupportLoaderManager().initLoader(REVIEW_LOADER, null, this);
        else
            getSupportLoaderManager().restartLoader(REVIEW_LOADER, null, this);


        if (mContentURIIdentifier.equals(getString(R.string.content_uri_fav))) {
            whetherSetFabOnClick = false;
            mFloatingActionButton.setVisibility(View.GONE);
            mToast = Toast.makeText(this, "Some data may not be available as this is a local Favorite copy.", Toast.LENGTH_LONG);
            mToast.show();
        } else
            mFloatingActionButton.setVisibility(View.VISIBLE);

        if (FavoriteUtils.isFavorite(this, mMovieID) && !(mContentURIIdentifier.equals(getString(R.string.content_uri_fav)))) {
            whetherSetFabOnClick = false;
            mFloatingActionButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.contrastLike)));
        }

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(whetherSetFabOnClick) {
                    // Create a new intent to start an AddTaskActivity
                    FavoriteUtils.addToFavorite(getApplicationContext(), mMovieID);

                    //Make Toast
                    if (mToast != null)
                        mToast.cancel();
                    Toast.makeText(MovieDetailActivity.this, "Movie Added to Favorites!", Toast.LENGTH_SHORT).show();

                    whetherSetFabOnClick = false;
                    mFloatingActionButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.contrastLike)));
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: In Movie Activity Detail Unbinder");
        unbinder.unbind();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {

        Log.i(TAG, "In Movie Activity Detail, in Loader");

        switch (loaderId) {
            case MOVIE_DETAIL_LOADER:
                Log.i(TAG, "In Movie Activity Detail, in Movie Loader");
                return new CursorLoader(this,
                        mMovieDisplayUri,
                        MAIN_MOVIES_DISPLAY_PROJECTION,
                        null,
                        null,
                        null);
            case TRAILER_LOADER:
                Log.i(TAG, "In Movie Activity Detail, in Trailer Loader");
                return new CursorLoader(this,
                        mTrailerUri,
                        TRAILERS_DISPLAY_PROJECTION,
                        null,
                        null,
                        null);

            case REVIEW_LOADER:
                Log.i(TAG, "onCreateLoader: in Review Loader");
                return new CursorLoader(this,
                        mReviewUri,
                        REVIEW_DISPLAY_PROJECTION,
                        null,
                        null,
                        null);

            default:
                throw new RuntimeException("Detail Activity Loader not implemented " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursorData) {

        switch (loader.getId()) {
            case MOVIE_DETAIL_LOADER: {
                Log.i(TAG, "In Movie Activity Detail, in Movie Loader on Finished");
                boolean cursorHasValidData = false;
                if (cursorData != null && cursorData.moveToFirst()) {
                /* We have valid data, continue on to bind the data to the UI */
                    Log.i(TAG, "In Movie Activity Detail, Cursor moved to First");
                    cursorHasValidData = true;
                }

                if (!cursorHasValidData) {
                /* No data to display, simply return and do nothing */
                    return;
                }

                Log.i(TAG, "In Movie Activity Detail, Movie Cursor has Data, loading");

                //Display Movie Details on Screen
                Picasso.with(this).load(NetworkUtils.buildPosterURL(cursorData.getString(getResources().getInteger(R.integer.index_movie_poster_path))).toString())
                        .placeholder(R.mipmap.movies).error(R.mipmap.image_unavailable).into(mThumbnailImageView);
                //Picasso.with(this).load(R.mipmap.image_unavailable).into(mMovieTrailerImageView);
                //Picasso.with(this).load(R.mipmap.image_unavailable).into(mMovieReviewImageView);

                String movieTitleTextWithReleaseYear = cursorData.getString(getResources().getInteger(R.integer.index_movie_title))
                        + " (" + cursorData.getString(getResources().getInteger(R.integer.index_movie_release_year)) + ")";

                mMovieTitleTextView.setText(movieTitleTextWithReleaseYear);
                mMovieOverviewTextView.setText(cursorData.getString(getResources().getInteger(R.integer.index_movie_overview)));
                mMovieVoteAverageTextView.setText(cursorData.getString(getResources().getInteger(R.integer.index_movie_vote_average)) + "/" + getString(R.string.rating_on));
                mMovieVoteAverageRatingBar.setRating(Float.parseFloat(cursorData.getString(getResources().getInteger(R.integer.index_movie_vote_average))));
                //mMovieReleaseDateTextView.setText(cursorData.getString(getResources().getInteger(R.integer.index_movie_release_year)));
            }
            break;

            case TRAILER_LOADER: {

                Log.i(TAG, "In Movie Activity Detail, in Trailer Loader on Finished");
                Log.i(TAG, "In Movie Activity Detail, Trailer Cursor has Data, loading");

                mTrailerAdapter.swapCursor(cursorData);

                if (mTrailerPosition == RecyclerView.NO_POSITION)
                    mTrailerPosition = 0;

                mTrailerRecyclerView.smoothScrollToPosition(mTrailerPosition);

                if (cursorData.getCount() != 0) {
                    mTrailerRecyclerView.setVisibility(View.VISIBLE);
                    setFirstTrailerURLForShare(cursorData);
                } else
                    mTrailerUnavailableTextView.setVisibility(View.VISIBLE);

            }
            break;

            case REVIEW_LOADER: {
                Log.i(TAG, "onLoadFinished: In Movie Activity Detail, in Review Loader on Finished");
                Log.i(TAG, "onLoadFinished: In Movie Activity Detail, Review Cursor has Data, loading");

                mReviewAdapter.swapCursor(cursorData);

                if (mReviewPosition == RecyclerView.NO_POSITION)
                    mReviewPosition = 0;

                mReviewRecyclerView.smoothScrollToPosition(mReviewPosition);

                if(cursorData.getCount() !=0 )
                    mReviewRecyclerView.setVisibility(View.VISIBLE);
                else
                    mReviewUnavailableTextView.setVisibility(View.VISIBLE);
            }
            break;
        }
    }

    private void setFirstTrailerURLForShare(Cursor cursor) {
        cursor.moveToFirst();
        String videoKey = cursor.getString(getResources().getInteger(R.integer.index_video_video_key));

        try {

            URL firstTrailerURL = NetworkUtils.buildWatchVideoURL(this, videoKey);
            mFirstTrailerURL = firstTrailerURL;
            Log.i(TAG, "First Trailer URL is - " + firstTrailerURL.toString());

        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "First Trailer URL is Null");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        switch (loader.getId()) {
            case TRAILER_LOADER:
                mTrailerAdapter.swapCursor(null);
                break;

            case REVIEW_LOADER:
                mReviewAdapter.swapCursor(null);
                break;
        }

    }

    @Override
    public void onTrailerClick(String watchVideoURL) {

        Log.i(TAG, "onClick: Inside on Trailer Click, starting and intent for URL - " + watchVideoURL);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(watchVideoURL));

        if(intent.resolveActivity(getPackageManager()) != null)
            startActivity(intent);

    }

    @Override
    public void onReviewClick(String reviewURL) {

        Log.i(TAG, "onClick: Inside on Review Click, starting and intent for URL - " + reviewURL);

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(reviewURL));
        if(intent.resolveActivity(getPackageManager()) != null)
            startActivity(intent);

    }

    //Handling Menu and Menu Click
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.movie_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_share:

                String mimeType = "text/plain";
                String title = "Movie Trailer URL";
                String textToShare = mFirstTrailerURL.toString();

                ShareCompat.IntentBuilder
                        .from(this)
                        .setType(mimeType)
                        .setChooserTitle(title)
                        .setText(textToShare)
                        .startChooser();
        }
        return super.onOptionsItemSelected(item);
    }
}