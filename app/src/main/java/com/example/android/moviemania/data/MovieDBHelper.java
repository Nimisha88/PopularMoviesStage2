package com.example.android.moviemania.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.example.android.moviemania.data.MovieContract.VideoEntry;
import com.example.android.moviemania.data.MovieContract.FavoriteEntry;
import com.example.android.moviemania.data.MovieContract.MovieEntry;
import com.example.android.moviemania.data.MovieContract.ReviewEntry;

public class MovieDBHelper extends SQLiteOpenHelper {

    private static final String TAG = MovieDBHelper.class.getSimpleName();

    //DB Name
    private static final String DATABASE_NAME = "movie.db";

    // If you change the database schema, you must increment the database version
    private static final int DATABASE_VERSION = 1;

    // Constructor
    public MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create a table to hold Movie data
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE IF NOT EXISTS " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieEntry.COLUMN_VOTE_COUNT + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_ID + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_VOTE_AVERAGE + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_POPULARITY + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_ORIGINAL_LANGUAGE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_BACKDROP_PATH + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_ADULT + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_RELEASE_DAY + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_RELEASE_MONTH + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_RELEASE_YEAR + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_CREATE_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                "); ";

        // Create a table to hold Fav data
        final String SQL_CREATE_FAVORITE_TABLE = "CREATE TABLE IF NOT EXISTS " + FavoriteEntry.TABLE_NAME + " (" +
                FavoriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FavoriteEntry.COLUMN_VOTE_COUNT + " INTEGER NOT NULL, " +
                FavoriteEntry.COLUMN_MOVIE_ID + " INTEGER UNIQUE NOT NULL, " +
                FavoriteEntry.COLUMN_VOTE_AVERAGE + " INTEGER NOT NULL, " +
                FavoriteEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_POPULARITY + " INTEGER NOT NULL, " +
                FavoriteEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_ORIGINAL_LANGUAGE + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_BACKDROP_PATH + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_ADULT + " INTEGER NOT NULL, " +
                FavoriteEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_RELEASE_DAY + " INTEGER NOT NULL, " +
                FavoriteEntry.COLUMN_RELEASE_MONTH + " INTEGER NOT NULL, " +
                FavoriteEntry.COLUMN_RELEASE_YEAR + " INTEGER NOT NULL, " +
                FavoriteEntry.COLUMN_CREATE_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                "); ";

        // Create a table to hold Video Trailer
        final String SQL_CREATE_VIDEO_TABLE = "CREATE TABLE IF NOT EXISTS " +
                VideoEntry.TABLE_NAME + " (" +
                VideoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                VideoEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                VideoEntry.COLUMN_VIDEO_KEY + " TEXT NOT NULL, " +
                VideoEntry.COLUMN_CREATE_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                "); ";

        // Create a table to hold Video Trailer
        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE IF NOT EXISTS " +
                ReviewEntry.TABLE_NAME + " (" +
                ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ReviewEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                ReviewEntry.COLUMN_REVIEW_URL + " TEXT NOT NULL, " +
                ReviewEntry.COLUMN_CREATE_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                "); ";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);
        Log.i(TAG, MovieEntry.TABLE_NAME + " Table created successfully!");

        db.execSQL(SQL_CREATE_FAVORITE_TABLE);
        Log.i(TAG, FavoriteEntry.TABLE_NAME + " Table created successfully!");

        db.execSQL(SQL_CREATE_VIDEO_TABLE);
        Log.i(TAG, VideoEntry.TABLE_NAME + " Table created successfully!");

        db.execSQL(SQL_CREATE_REVIEW_TABLE);
        Log.i(TAG, ReviewEntry.TABLE_NAME + " Table created successfully!");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // If DATABASE_VERSION changes, the table will be dropped and recreated
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);

        // If DATABASE_VERSION changes, the table will be dropped and recreated
        db.execSQL("DROP TABLE IF EXISTS " + FavoriteEntry.TABLE_NAME);

        // If DATABASE_VERSION changes, the table will be dropped and recreated
        db.execSQL("DROP TABLE IF EXISTS " + VideoEntry.TABLE_NAME);
        onCreate(db);

    }
}
