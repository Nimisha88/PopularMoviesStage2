package com.example.android.moviemania.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import com.example.android.moviemania.R;
import com.example.android.moviemania.data.MovieContract.VideoEntry;
import com.example.android.moviemania.data.MovieContract.MovieEntry;
import com.example.android.moviemania.data.MovieContract.ReviewEntry;
import com.example.android.moviemania.utilities.NetworkUtils;
import com.example.android.moviemania.utilities.NotificationUtils;
import com.example.android.moviemania.utilities.OpenMovieJSONUtils;

import java.net.URL;

public class MovieSyncTask {

    private static final String TAG = MovieSyncTask.class.getSimpleName();
    public static final String ACTION_DISMISS_NOTIFICATION = "act-dismiss-notify";

    synchronized public static void syncMovie(Context context) {

        try {

            ContentResolver movieContentResolver = context.getContentResolver();

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            String userSortChoice = sharedPreferences.getString(context.getString(R.string.movie_sort_key), context.getString(R.string.movie_sort_default));

            URL movieURL;

            //Get URL
            movieURL = NetworkUtils.buildMovieURL(userSortChoice, context);

            //Fetch JSON
            String jsonMovieResponse = NetworkUtils.getResponseFromHttpURL(movieURL);

            //Parse API JSON Result as Values to be inserted in DB
            ContentValues[] movieDataValues = OpenMovieJSONUtils.getMovieDataFromAPIJSONResult(jsonMovieResponse);

            Log.i(TAG, "Data fetched from API, storing in SQLite");

            if (movieDataValues != null || movieDataValues.length != 0) {

                //Delete Old Movie Data
                movieContentResolver.delete(MovieEntry.CONTENT_URI, null, null);

                //Insert New Data
                movieContentResolver.bulkInsert(MovieEntry.CONTENT_URI, movieDataValues);
            }

            Log.i(TAG, "Data stored in Movie DB successfully! Sending User Notification");

            for(ContentValues value: movieDataValues) {

                long movieID = value.getAsLong(MovieEntry.COLUMN_ID);

                URL movieVideosURL = NetworkUtils.buildVideosURL(context, movieID);
                URL movieReviewsURL = NetworkUtils.buildReviewsURL(context, movieID);

                String jsonMovieVideoResponse = NetworkUtils.getResponseFromHttpURL(movieVideosURL);
                String jsonMovieReviewResponse = NetworkUtils.getResponseFromHttpURL(movieReviewsURL);

                ContentValues[] movieVideos = OpenMovieJSONUtils.getVideoDataFromAPIJSONResult(jsonMovieVideoResponse, movieID);
                ContentValues[] movieReviews = OpenMovieJSONUtils.getReviewDataFromAPIJSONResult(jsonMovieReviewResponse, movieID);

                if (movieVideos != null || movieVideos.length != 0) {

                    //Delete Old Videos Data
                    movieContentResolver.delete(VideoEntry.CONTENT_URI,
                            VideoEntry.COLUMN_MOVIE_ID + "=?",
                            new String[]{Long.toString(movieID)});

                    //Insert New Data
                    movieContentResolver.bulkInsert(VideoEntry.CONTENT_URI, movieVideos);

                    Log.i(TAG, "Data stored in Videos DB successfully! Sending User Notification");

                }

                if (movieReviews != null || movieReviews.length != 0) {

                    //Delete Old Videos Data
                    movieContentResolver.delete(ReviewEntry.CONTENT_URI,
                            ReviewEntry.COLUMN_MOVIE_ID + "=?",
                            new String[]{Long.toString(movieID)});

                    //Insert New Data
                    movieContentResolver.bulkInsert(ReviewEntry.CONTENT_URI, movieReviews);

                    Log.i(TAG, "Data stored in Review DB successfully! Sending User Notification");

                }
            }

            Log.i(TAG, "Data stored in DB successfully! Sending User Notification");

            sendNotification(context);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error in Syncing Movie - Something not working in Movie Sync Task");
        }

    }

    public static void  sendNotification(Context context) {

        //Cancel All existing Notifications
        NotificationUtils.clearAllNotifications(context);

        //Trigger a new Notification
        NotificationUtils.notifyUser(context);

        Log.i(TAG, "User Notification sent successfully!");

    }

    public static void dismissNotification(Context context) {
        NotificationUtils.clearAllNotifications(context);
    }

}