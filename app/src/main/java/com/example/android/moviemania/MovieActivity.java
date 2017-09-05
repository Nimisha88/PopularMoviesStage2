package com.example.android.moviemania;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.android.moviemania.data.MovieContract.MovieEntry;
import com.example.android.moviemania.sync.MovieSyncUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MovieActivity extends AppCompatActivity implements MovieAdapter.ListItemOnClickHandler, LoaderManager.LoaderCallbacks<Cursor>, SharedPreferences.OnSharedPreferenceChangeListener {

    //Log Tag
    private static final String TAG = MovieActivity.class.getSimpleName();

    //Loader Constant
    private static final int MOVIE_MANIA_LOADER = 113;

    //Grid Count for Grid Layout
    private final static int GRID_COLUMN_COUNT = 3;

    private static final String SAVE_STATE_KEY = "SaveState";
    private static final String SAVE_SCROLL_KEY = "SaveScroll";
    private Parcelable mScrollState;
    private boolean mSaveState = false;
    private boolean mIsError = false;
    private boolean mIsSharedPrefChange = false;

    //Member Variables
    private String mUserSortChoice;
    private RecyclerView.LayoutManager layoutManager;
    private MovieAdapter mMovieAdapter;
    private int mPosition = RecyclerView.NO_POSITION;

    //ButterKnife Unbinder
    private Unbinder unbinder;

    //Bind Views
    @BindView(R.id.rv_movie_display)
    RecyclerView mMovieDisplayRecyclerView;
    @BindView(R.id.pb_loading_indicator)
    ProgressBar mLoadProgressBar;
    @BindView(R.id.tv_error_message_display)
    TextView mErrorMessageTextView;

    //Projection for Movies Table
    private static final String[] MAIN_MOVIES_DISPLAY_PROJECTION = MovieEntry.getDBTableProjection();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SAVE_STATE_KEY)) {
                mSaveState = savedInstanceState
                        .getBoolean(SAVE_STATE_KEY);
            }
            if (savedInstanceState.containsKey(SAVE_SCROLL_KEY)) {
                mScrollState = savedInstanceState.getParcelable(SAVE_SCROLL_KEY);
            }
        }

        setupSharedPreferences();

        //View Binder
        unbinder = ButterKnife.bind(this);

        layoutManager = new GridLayoutManager(this, GRID_COLUMN_COUNT);
        mMovieDisplayRecyclerView.setLayoutManager(layoutManager);
        mMovieDisplayRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this, this);
        mMovieDisplayRecyclerView.setAdapter(mMovieAdapter);

        Log.i(TAG, "All Movie Activity Variables Initialized");

        loadMovie();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(!mIsError) {
            outState.putBoolean(SAVE_STATE_KEY, true);
            outState.putParcelable(SAVE_SCROLL_KEY, mMovieDisplayRecyclerView.getLayoutManager().onSaveInstanceState());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mScrollState = savedInstanceState.getParcelable(SAVE_SCROLL_KEY);
    }

    private void loadMovie() {

        if (!mSaveState || mIsSharedPrefChange) {

            mSaveState = false;
            mIsSharedPrefChange = false;

            //Delete existing tables
            MovieSyncUtils.deleteTables(this);

            //Fetch Movie Data from API
            Log.i(TAG, "Syncing Movie Data from API");
            MovieSyncUtils.initialize(this);

        }
        //Set Visibility of Elements
        showLoadProgressBar();

        //Load Movies on UI
        Log.i(TAG, "Loading Movie Data on UI");
        Loader<Cursor> loader = getSupportLoaderManager().getLoader(MOVIE_MANIA_LOADER);

        if (loader == null)
            getSupportLoaderManager().initLoader(MOVIE_MANIA_LOADER, null, this);
        else
            getSupportLoaderManager().restartLoader(MOVIE_MANIA_LOADER, null, this);

    }

    private void setupSharedPreferences() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        mUserSortChoice = sharedPref.getString(getString(R.string.movie_sort_key), getString(R.string.movie_sort_default));
        sharedPref.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: In Movie Activity Unbinder");
        unbinder.unbind();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    //Show Error Message View
    private void showErrorMessageView() {
        mIsError = true;
        mMovieDisplayRecyclerView.setVisibility(View.GONE);
        mErrorMessageTextView.setVisibility(View.VISIBLE);
        mLoadProgressBar.setVisibility(View.GONE);
    }

    //Show Movie Data View
    private void showMovieDataView() {
        mIsError = false;
        mMovieDisplayRecyclerView.setVisibility(View.VISIBLE);
        mErrorMessageTextView.setVisibility(View.GONE);
        mLoadProgressBar.setVisibility(View.GONE);
    }

    //Show Load Progress Bar
    private void showLoadProgressBar() {
        mIsError = false;
        mMovieDisplayRecyclerView.setVisibility(View.GONE);
        mErrorMessageTextView.setVisibility(View.GONE);
        mLoadProgressBar.setVisibility(View.VISIBLE);
    }

    //Interface Implementation
    @Override
    public void onClick(long movieUniqueID, long movieID) {
        Class detailActivity = MovieDetailActivity.class;
        Intent movieDetailIntent = new Intent(this, detailActivity);
        movieDetailIntent.putExtra(getString(R.string.detail_act_movie_auto_id_key), movieUniqueID);
        movieDetailIntent.putExtra(getString(R.string.detail_act_movie_id_key), movieID);
        movieDetailIntent.putExtra(getString(R.string.detail_act_uri_key), getString(R.string.content_uri_movie));
        startActivity(movieDetailIntent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, final Bundle bundle) {

        switch (loaderId) {
            case MOVIE_MANIA_LOADER:
                Uri movieDataQueryURI = MovieEntry.CONTENT_URI;

                return new CursorLoader(this,
                        movieDataQueryURI,
                        MAIN_MOVIES_DISPLAY_PROJECTION,
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

        Log.i(TAG, "onLoadFinished: Recycler View Position" + mPosition);

        if (dataCursor.getCount() != 0)
            showMovieDataView();
        else
            showErrorMessageView();

        if (mScrollState != null) {

            Log.i(TAG, "onLoadFinished: mScroll State not NULL!");
            mMovieDisplayRecyclerView.getLayoutManager().onRestoreInstanceState(mScrollState);
            mScrollState = null;

        } else {

            Log.i(TAG, "onLoadFinished: mScroll State is NULL!");

            if (mPosition == RecyclerView.NO_POSITION)
                mPosition = 0;

            mMovieDisplayRecyclerView.smoothScrollToPosition(mPosition);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieAdapter.swapCursor(null);
    }

    //Handling Menu and Menu Click
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.movie_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_settings:
                Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
                startActivity(startSettingsActivity);
                return true;

            case R.id.menu_favorite:
                Intent favIntent = new Intent(this, FavoriteActivity.class);
                Log.i(TAG, "Starting Favorite Activity");
                startActivity(favIntent);
                return true;

            case R.id.menu_refresh:
                mSaveState = false;
                loadMovie();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(getString(R.string.movie_sort_key))) {
            mUserSortChoice = sharedPreferences.getString(key, getString(R.string.movie_sort_default));
            mIsSharedPrefChange = true;
            loadMovie();
        }
    }
}