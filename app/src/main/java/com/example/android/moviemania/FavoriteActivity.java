package com.example.android.moviemania;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import com.example.android.moviemania.data.MovieContract.FavoriteEntry;
import com.example.android.moviemania.favorite.FavoriteUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class FavoriteActivity extends AppCompatActivity implements MovieAdapter.ListItemOnClickHandler, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = MovieActivity.class.getSimpleName();

    //Loader Constant
    private static final int FAV_LOADER = 115;
    private final static int GRID_COLUMN_COUNT = 3;

    //Member Variables
    private RecyclerView.LayoutManager layoutManager;
    private MovieAdapter mMovieAdapter;
    private int mPosition = RecyclerView.NO_POSITION;
    private static final String SAVE_SCROLL_KEY = "SaveScroll";
    private Parcelable mScrollState;

    //ButterKnife Unbinder
    private Unbinder unbinder;

    //Bind Views
    @BindView(R.id.rv_fav_display)
    RecyclerView mFavDisplayRecyclerView;

    //Projection for Movies Table
    private static final String[] MAIN_FAV_DISPLAY_PROJECTION = FavoriteEntry.getDBTableProjection();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SAVE_SCROLL_KEY)) {
                mScrollState = savedInstanceState.getParcelable(SAVE_SCROLL_KEY);
            }
        }

        //Delete All Data in DB, uncomment only when needed
        //getContentResolver().delete(MovieEntry.CONTENT_URI, null, null);

        //View Binder
        unbinder = ButterKnife.bind(this);

        layoutManager = new GridLayoutManager(this, GRID_COLUMN_COUNT);
        mFavDisplayRecyclerView.setLayoutManager(layoutManager);
        mFavDisplayRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this, this);
        mFavDisplayRecyclerView.setAdapter(mMovieAdapter);

        Log.i(TAG, "Favorite Activity Variables Initialized");

        //Set Visibility of Elements
        //showLoadProgressBar();
        mFavDisplayRecyclerView.setVisibility(View.INVISIBLE);

        Log.i(TAG, "Favorite Activity starting Loader");
        Loader<Cursor> loader = getSupportLoaderManager().getLoader(FAV_LOADER);

        if (loader == null)
            getSupportLoaderManager().initLoader(FAV_LOADER, null, this);
        else
            getSupportLoaderManager().restartLoader(FAV_LOADER, null, this);

        Log.i(TAG, "Favorite Activity Load Complete");

        //Swipe to Delete
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // Here is where you'll implement swipe to delete

                // COMPLETED (1) Construct the URI for the item to delete
                //[Hint] Use getTag (from the adapter code) to get the id of the swiped item
                // Retrieve the id of the task to delete
                long movieId = (long) viewHolder.itemView.getTag();

                // Build appropriate uri with String row id appended
                //Uri deleteFavMovieURI = FavoriteEntry.CONTENT_URI.buildUpon().appendPath(Long.toString(movieId)).build();

                // COMPLETED (2) Delete a single row of data using a ContentResolver
                //getContentResolver().delete(deleteFavMovieURI, null, null);

                FavoriteUtils.deleteFromFavorite(FavoriteActivity.this, movieId);

                // COMPLETED (3) Restart the loader to re-query for all tasks after a deletion
                getSupportLoaderManager().restartLoader(FAV_LOADER, null, FavoriteActivity.this);

            }
        }).attachToRecyclerView(mFavDisplayRecyclerView);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SAVE_SCROLL_KEY, mFavDisplayRecyclerView.getLayoutManager().onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mScrollState = savedInstanceState.getParcelable(SAVE_SCROLL_KEY);
    }

    /*//Show Error Message View
    public void showErrorMessageView() {
        mMovieDisplayRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageTextView.setVisibility(View.VISIBLE);
        mLoadProgressBar.setVisibility(View.INVISIBLE);
    }*/

    //Show Movie Data View
    private void showMovieDataView() {
        mFavDisplayRecyclerView.setVisibility(View.VISIBLE);
        //mErrorMessageTextView.setVisibility(View.INVISIBLE);
        //mLoadProgressBar.setVisibility(View.INVISIBLE);
    }

    /*//Show Load Progress Bar
    public void showLoadProgressBar() {
        mMovieDisplayRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
        mLoadProgressBar.setVisibility(View.VISIBLE);
    } */

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        switch (loaderId) {
            case FAV_LOADER:
                Uri movieDataQueryURI = FavoriteEntry.CONTENT_URI;

                Log.i(TAG, "Fetched URI - " + movieDataQueryURI);

                return new CursorLoader(this,
                        movieDataQueryURI,
                        MAIN_FAV_DISPLAY_PROJECTION,
                        null,
                        null,
                        null);

            default:
                throw new RuntimeException("Loader ID " + loaderId + " not implemented!!");

        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor dataCursor) {

        mMovieAdapter.swapCursor(dataCursor);

        if (dataCursor.getCount() != 0)
            showMovieDataView();

        if (mScrollState != null) {

            Log.i(TAG, "onLoadFinished: mScroll State not NULL!");
            mFavDisplayRecyclerView.getLayoutManager().onRestoreInstanceState(mScrollState);
            mScrollState = null;

        } else {

            Log.i(TAG, "onLoadFinished: mScroll State is NULL!");

            if (mPosition == RecyclerView.NO_POSITION)
                mPosition = 0;

            mFavDisplayRecyclerView.smoothScrollToPosition(mPosition);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieAdapter.swapCursor(null);
    }

    @Override
    public void onClick(long movieUniqueID, long movieID) {
        Class detailActivity = MovieDetailActivity.class;
        Intent movieDetailIntent = new Intent(this, detailActivity);
        movieDetailIntent.putExtra(getString(R.string.detail_act_movie_auto_id_key), movieUniqueID);
        movieDetailIntent.putExtra(getString(R.string.detail_act_movie_id_key), movieID);
        movieDetailIntent.putExtra(getString(R.string.detail_act_uri_key), getString(R.string.content_uri_fav));
        startActivity(movieDetailIntent);
    }
}
