package com.example.android.moviemania.favorite;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.example.android.moviemania.R;

public class FavoriteIntentService extends IntentService {

    private static final String TAG = FavoriteIntentService.class.getSimpleName();

    public FavoriteIntentService() {
        super("FavoriteIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String action = intent.getAction();
        String actionUri = intent.getStringExtra(getString(R.string.favorite_action_extra_key));
        FavoriteTask.executeAction(this, action, Uri.parse(actionUri));
    }
}
