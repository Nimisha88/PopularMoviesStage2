package com.example.android.moviemania.utilities;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Action;
import android.support.v4.content.ContextCompat;

import com.example.android.moviemania.MovieActivity;
import com.example.android.moviemania.R;
import com.example.android.moviemania.sync.MovieIntentService;
import com.example.android.moviemania.sync.MovieSyncTask;

public class NotificationUtils {

    private static final String TAG = NotificationUtils.class.getSimpleName();
    private static final int MOVIE_NOTIFICATION_ID = 123;
    private static final int MOVIE_PENDING_INTENT_ID = 1000;
    private static final int ACTION_IGNORE_PENDING_INTENT_ID = 1001;

    public static void clearAllNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public static void notifyUser(Context context) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.mipmap.movies)
                .setLargeIcon(getLargeIcon(context))
                .setContentTitle(context.getString(R.string.noti_content_title))
                .setContentText(context.getString(R.string.noti_content))
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentIntent(contentIntent(context))
                .addAction(ignoreReminderAction(context))
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(MOVIE_NOTIFICATION_ID, notificationBuilder.build());
    }

    private static PendingIntent contentIntent(Context context) {
        Intent startActivityIntent = new Intent(context, MovieActivity.class);
        return PendingIntent.getActivity(
                context,
                MOVIE_PENDING_INTENT_ID,
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static Action ignoreReminderAction(Context context) {
        Intent ignoreReminderIntent = new Intent(context, MovieIntentService.class);
        ignoreReminderIntent.setAction(MovieSyncTask.ACTION_DISMISS_NOTIFICATION);
        PendingIntent ignoreReminderPendingIntent = PendingIntent.getService(
                context,
                ACTION_IGNORE_PENDING_INTENT_ID,
                ignoreReminderIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action ignoreReminderAction = new NotificationCompat.Action(R.drawable.ic_like,
                "Okay!",
                ignoreReminderPendingIntent);
        return ignoreReminderAction;
    }

    public static Bitmap getLargeIcon(Context context) {
        Resources res = context.getResources();
        Bitmap largeIcon = BitmapFactory.decodeResource(res, R.mipmap.movies);
        return largeIcon;
    }
}
