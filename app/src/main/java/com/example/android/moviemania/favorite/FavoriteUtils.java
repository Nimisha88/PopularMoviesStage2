package com.example.android.moviemania.favorite;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import com.example.android.moviemania.R;
import com.example.android.moviemania.data.MovieContract.FavoriteEntry;

public class FavoriteUtils {

    private static final String TAG = FavoriteUtils.class.getSimpleName();

    public static void addToFavorite(Context context, long movieID) {

        Uri favoriteMovieURItoBeAdded = FavoriteEntry.CONTENT_URI.buildUpon()
                .appendPath(Long.toString(movieID)).build();

        Intent addFavIntent = new Intent(context, FavoriteIntentService.class);
        addFavIntent.setAction(context.getString(R.string.act_add_fav));
        addFavIntent.putExtra(context.getString(R.string.favorite_action_extra_key), favoriteMovieURItoBeAdded.toString());
        context.startService(addFavIntent);


    }

    public static void deleteFromFavorite(Context context, long movieID) {

        Uri favoriteMovieURItoBeDeleted = FavoriteEntry.CONTENT_URI.buildUpon()
                .appendPath(Long.toString(movieID)).build();

        Intent delFavIntent = new Intent(context, FavoriteIntentService.class);
        delFavIntent.setAction(context.getString(R.string.act_del_fav));
        delFavIntent.putExtra(context.getString(R.string.favorite_action_extra_key), favoriteMovieURItoBeDeleted.toString());
        context.startService(delFavIntent);


    }

    public static boolean isFavorite(Context context, long movieID) {

        boolean isFav = true;

        Uri movieContentUri = FavoriteEntry.CONTENT_URI.buildUpon().appendPath(Long.toString(movieID)).build();
        String[] projectionColumns = FavoriteEntry.getDBTableProjection();

        Cursor cursor = context.getContentResolver().query(
                movieContentUri,
                projectionColumns,
                null,
                null,
                null
        );

        if (cursor == null || cursor.getCount() == 0) {
            Log.i(TAG, "Since no data in Content Provider synchronizing data immediately!");
            isFav = false;
        }

        if (cursor != null)
            cursor.close();

        return isFav;
    }

}
