package com.example.android.moviemania.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.example.android.moviemania.R;
import com.example.android.moviemania.data.MovieContract.FavoriteEntry;

public class ProviderUtils {

    public static ContentValues getFavoriteContentValuesFromMovieCursor(Context context, Cursor cursor) {

        if (cursor == null || cursor.getCount() == 0)
            return null;

        cursor.moveToFirst();

        ContentValues movieData = new ContentValues();
        movieData.put(FavoriteEntry.COLUMN_VOTE_COUNT, cursor.getInt(context.getResources().getInteger(R.integer.index_movie_vote_count)));
        movieData.put(FavoriteEntry.COLUMN_MOVIE_ID, cursor.getInt(context.getResources().getInteger(R.integer.index_movie_id)));
        movieData.put(FavoriteEntry.COLUMN_VOTE_AVERAGE, cursor.getInt(context.getResources().getInteger(R.integer.index_movie_vote_average)));
        movieData.put(FavoriteEntry.COLUMN_TITLE, cursor.getString(context.getResources().getInteger(R.integer.index_movie_title)));
        movieData.put(FavoriteEntry.COLUMN_POPULARITY, cursor.getInt(context.getResources().getInteger(R.integer.index_movie_popularity)));
        movieData.put(FavoriteEntry.COLUMN_POSTER_PATH, cursor.getString(context.getResources().getInteger(R.integer.index_movie_poster_path)));
        movieData.put(FavoriteEntry.COLUMN_ORIGINAL_LANGUAGE, cursor.getString(context.getResources().getInteger(R.integer.index_movie_original_language)));
        movieData.put(FavoriteEntry.COLUMN_ORIGINAL_TITLE, cursor.getString(context.getResources().getInteger(R.integer.index_movie_original_title)));
        movieData.put(FavoriteEntry.COLUMN_BACKDROP_PATH, cursor.getString(context.getResources().getInteger(R.integer.index_movie_backdrop_path)));
        movieData.put(FavoriteEntry.COLUMN_ADULT, cursor.getInt(context.getResources().getInteger(R.integer.index_movie_adult)));
        movieData.put(FavoriteEntry.COLUMN_OVERVIEW, cursor.getString(context.getResources().getInteger(R.integer.index_movie_overview)));
        movieData.put(FavoriteEntry.COLUMN_RELEASE_DAY, cursor.getInt(context.getResources().getInteger(R.integer.index_movie_release_day)));
        movieData.put(FavoriteEntry.COLUMN_RELEASE_MONTH, cursor.getInt(context.getResources().getInteger(R.integer.index_movie_release_month)));
        movieData.put(FavoriteEntry.COLUMN_RELEASE_YEAR, cursor.getInt(context.getResources().getInteger(R.integer.index_movie_release_year)));

        return movieData;
    }

}
