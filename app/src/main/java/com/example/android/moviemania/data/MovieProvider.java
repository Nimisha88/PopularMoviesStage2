package com.example.android.moviemania.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.example.android.moviemania.data.MovieContract.VideoEntry;
import com.example.android.moviemania.data.MovieContract.MovieEntry;
import com.example.android.moviemania.data.MovieContract.FavoriteEntry;
import com.example.android.moviemania.data.MovieContract.ReviewEntry;
import com.example.android.moviemania.utilities.ProviderUtils;

public class MovieProvider extends ContentProvider {

    private static final String TAG = MovieProvider.class.getSimpleName();

    private Context mContext;

    //Member Movie DB Helper, Favorite DB Helper
    private MovieDBHelper mMovieDBHelper;

    //Final Integer Constants for the directory of tasks and a single item
    private static final int MOVIES = 100;
    private static final int MOVIES_WITH_ID = 101;
    private static final int FAVORITES = 200;
    private static final int FAVORITES_WITH_ID = 201;
    private static final int VIDEOS = 300;
    private static final int VIDEOS_WITH_ID = 301;
    private static final int REVIEWS = 400;
    private static final int REVIEWS_WITH_ID = 401;

    //URI Matcher
    private static final UriMatcher sUriMatcher = buildUriMatcher();


    private static UriMatcher buildUriMatcher() {

        // Initialize a UriMatcher with no matches by passing in NO_MATCH to the constructor
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        /*
          All paths added to the UriMatcher have a corresponding int.
          For each kind of uri you may want to access, add the corresponding match with addURI.
          The two calls below add matches for the task directory and a single item by ID.
         */
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_MOVIES, MOVIES);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_MOVIES + "/#", MOVIES_WITH_ID);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_FAVORITES, FAVORITES);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_FAVORITES + "/#", FAVORITES_WITH_ID);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_VIDEOS, VIDEOS);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_VIDEOS + "/#", VIDEOS_WITH_ID);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_REVIEWS, REVIEWS);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_REVIEWS + "/#", REVIEWS_WITH_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mContext = getContext();
        mMovieDBHelper = new MovieDBHelper(mContext);
        return true;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {

        final SQLiteDatabase db = mMovieDBHelper.getWritableDatabase();
        int rowsInserted;

        switch (sUriMatcher.match(uri)) {
            case MOVIES:
                rowsInserted = 0;
                db.beginTransaction();
                try {
                    for (ContentValues value: values) {
                        long insertId = db.insert(MovieEntry.TABLE_NAME, null, value);
                        if (insertId != 0)
                            rowsInserted++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;

            case VIDEOS:
                rowsInserted = 0;
                db.beginTransaction();
                try {
                    for (ContentValues value: values) {
                        long insertId = db.insert(VideoEntry.TABLE_NAME, null, value);
                        if (insertId != 0)
                            rowsInserted++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;

            case REVIEWS:
                rowsInserted = 0;
                db.beginTransaction();
                try {
                    for (ContentValues value: values) {
                        long insertId = db.insert(ReviewEntry.TABLE_NAME, null, value);
                        if (insertId != 0)
                            rowsInserted++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;

            default:
                return super.bulkInsert(uri, values);
        }

        if (rowsInserted > 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return rowsInserted;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        //Access Read-Only DataBase to retrieve Data
        final SQLiteDatabase db = mMovieDBHelper.getReadableDatabase();
        Cursor resultCursor;

        switch (sUriMatcher.match(uri)) {
            case MOVIES_WITH_ID:
                String movieUniqueID = uri.getLastPathSegment();
                selection = MovieEntry._ID + "=?";
                selectionArgs = new String[]{movieUniqueID};
            case MOVIES:
                resultCursor = db.query(MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case FAVORITES_WITH_ID:
                String favoriteMovieID = uri.getLastPathSegment();
                selection = FavoriteEntry.COLUMN_MOVIE_ID + "=?";
                selectionArgs = new String[]{favoriteMovieID};
            case FAVORITES:
                resultCursor = db.query(FavoriteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case VIDEOS_WITH_ID:
                String movieID = uri.getLastPathSegment();
                selection = VideoEntry.COLUMN_MOVIE_ID + "=?";
                selectionArgs = new String[]{movieID};
            case VIDEOS:
                resultCursor = db.query(VideoEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case REVIEWS_WITH_ID:
                String movieIDForReview = uri.getLastPathSegment();
                selection = ReviewEntry.COLUMN_MOVIE_ID + "=?";
                selectionArgs = new String[]{movieIDForReview};
            case REVIEWS:
                resultCursor = db.query(ReviewEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown URI - " + uri + "!!");
        }

        resultCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return resultCursor;

    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        SQLiteDatabase db = mMovieDBHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {
            case FAVORITES_WITH_ID:
                String movieID = uri.getLastPathSegment();
                long favoriteID = 0;
                Cursor cursor = query(MovieEntry.CONTENT_URI,
                        MovieEntry.getDBTableProjection(),
                        MovieEntry.COLUMN_ID + "=?",
                        new String[]{movieID},
                        null);
                ContentValues cv = ProviderUtils.getFavoriteContentValuesFromMovieCursor(mContext, cursor);
                if (cv == null)
                    return null;
                try {
                    favoriteID = db.insert(FavoriteEntry.TABLE_NAME, null, cv);
                } catch (Exception e) {
                    Log.e(TAG, "Probably the Movie already exists in Favorites or some DB Insert Error.");
                }

                if (favoriteID > 0) {
                    Log.i(TAG, "Data inserted in Favorite Table");
                    getContext().getContentResolver().notifyChange(uri, null);
                    return FavoriteEntry.CONTENT_URI.buildUpon().appendPath(Long.toString(favoriteID)).build();
                } else
                    return null;

            default:
                throw new UnsupportedOperationException("Unknown URI - " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        SQLiteDatabase db = mMovieDBHelper.getWritableDatabase();
        int rowsDeleted;

        switch (sUriMatcher.match(uri)) {
            case MOVIES_WITH_ID:
                String movieAutoID = uri.getLastPathSegment();
                selection = MovieEntry._ID + "=?";
                selectionArgs = new String[]{movieAutoID};
            case MOVIES:
                rowsDeleted = db.delete(MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case FAVORITES_WITH_ID:
                String favMovieID = uri.getLastPathSegment();
                selection = FavoriteEntry.COLUMN_MOVIE_ID + "=?";
                selectionArgs = new String[]{favMovieID};
            case FAVORITES:
                rowsDeleted = db.delete(FavoriteEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case VIDEOS_WITH_ID:
                String movieIDForVideos = uri.getLastPathSegment();
                selection = VideoEntry.COLUMN_MOVIE_ID + "=?";
                selectionArgs = new String[]{movieIDForVideos};
            case VIDEOS:
                rowsDeleted = db.delete(VideoEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case REVIEWS_WITH_ID:
                String movieIDForReviews = uri.getLastPathSegment();
                selection = ReviewEntry.COLUMN_MOVIE_ID + "=?";
                selectionArgs = new String[]{movieIDForReviews};
            case REVIEWS:
                rowsDeleted = db.delete(ReviewEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown URI " + uri);
        }

        if (rowsDeleted != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
