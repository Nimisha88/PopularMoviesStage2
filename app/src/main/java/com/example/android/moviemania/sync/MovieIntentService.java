package com.example.android.moviemania.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

public class MovieIntentService extends IntentService {

    private static final String TAG = MovieIntentService.class.getSimpleName();

    public MovieIntentService() {
        super("MovieIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.i(TAG, "Movie Intent Service is syncronizing Movie Data");

        String action = intent.getAction();

        if (action != null) {
            MovieSyncTask.dismissNotification(this);
            return;
        }

        MovieSyncTask.syncMovie(this);
    }
}
