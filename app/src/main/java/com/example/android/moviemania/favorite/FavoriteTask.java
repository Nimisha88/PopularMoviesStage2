package com.example.android.moviemania.favorite;

import android.content.Context;
import android.net.Uri;

public class FavoriteTask {

    private static final String TAG = FavoriteTask.class.getSimpleName();

    private static final String ACTION_ADD_FAV = "add-favorite";
    private static final String ACTION_DELETE_FAV = "delete-favorite";

    synchronized public static void executeAction(Context context, String action, Uri actionURI) {
        switch(action) {
            case ACTION_ADD_FAV:
                addFavorite(context, actionURI);
                break;
            case ACTION_DELETE_FAV:
                deleteFavorite(context, actionURI);
                break;
            default:
                throw new UnsupportedOperationException("Favorite Action is NOT enlisted!");
        }
    }

    private static void addFavorite(Context context, Uri actionURI) {

        context.getContentResolver().insert(actionURI, null);

    }

    private static void deleteFavorite (Context context, Uri actionURI) {

        context.getContentResolver().delete(actionURI, null, null);

    }
}
