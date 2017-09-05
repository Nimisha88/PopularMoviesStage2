package com.example.android.moviemania.sync;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import com.example.android.moviemania.data.MovieContract.MovieEntry;
import com.example.android.moviemania.data.MovieContract.VideoEntry;
import com.example.android.moviemania.data.MovieContract.ReviewEntry;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

public class MovieSyncUtils {

    private static final String TAG = MovieSyncUtils.class.getSimpleName();
    private static final int NOTIFICATION_INTERVAL_SECONDS = 86400;
    //private static final int NOTIFICATION_INTERVAL_SECONDS = 10;
    private static final int SYNC_FLEXTIME_SECONDS = NOTIFICATION_INTERVAL_SECONDS/24;

    private static final String MOVIE_SYNC_TAG = "movie-sync";

    private static boolean sInitialized;

    private static void scheduleMovieSync(@NonNull final Context context) {

        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        Job syncMovieJob = dispatcher.newJobBuilder()
                .setService(MovieFirebaseJobService.class)
                .setTag(MOVIE_SYNC_TAG)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(NOTIFICATION_INTERVAL_SECONDS, NOTIFICATION_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                .setReplaceCurrent(true)
                .build();
        dispatcher.schedule(syncMovieJob);

        sInitialized = true;
    }

    synchronized public static void initialize(@NonNull final Context context) {

        //Check if Job Scheduler already initialized. If yes, exit else run Job Scheduler
        if (!sInitialized) {
            Log.i(TAG, "Started Syncronizing Movie Data scheduled Job");
            scheduleMovieSync(context);
        }


        //Check if Content Provider has Data on a Separate Thread
        Thread checkForEmptyContent = new Thread(new Runnable() {
            @Override
            public void run() {

                Uri movieContentUri = MovieEntry.CONTENT_URI;
                String[] projectionColumns = {MovieEntry._ID};

                Cursor cursor = context.getContentResolver().query(
                        movieContentUri,
                        projectionColumns,
                        null,
                        null,
                        null
                );

                if (cursor == null || cursor.getCount() == 0) {
                    Log.i(TAG, "Since no data in Content Provider synchronizing data immediately!");
                    startImmediateSync(context);
                }

                if (cursor != null)
                    cursor.close();
            }
        });

        checkForEmptyContent.start();
    }

    private static void startImmediateSync(@NonNull final Context context) {
        Intent intentToSyncImmediately = new Intent(context, MovieIntentService.class);
        Log.i(TAG, "Starting synchronizing Movie Data");
        context.startService(intentToSyncImmediately);
    }

    public static void deleteTables(Context context) {

        try {

        //Delete All Data in DB, uncomment only when needed
        context.getContentResolver().delete(MovieEntry.CONTENT_URI, null, null);
        context.getContentResolver().delete(VideoEntry.CONTENT_URI, null, null);
        context.getContentResolver().delete(ReviewEntry.CONTENT_URI, null, null);
        //context.getContentResolver().delete(FavoriteEntry.CONTENT_URI, null, null);
        } catch (Exception e) {
            Log.i(TAG, "deleteTables: Caught Exception, seems one of the DB not present");
        }
    }
}
