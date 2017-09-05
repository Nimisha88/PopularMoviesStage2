package com.example.android.moviemania.sync;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import android.content.Context;
import android.os.AsyncTask;

public class MovieFirebaseJobService extends JobService {

    private AsyncTask<Void, Void, Void> mFetchMovieTask;

    @Override
    public boolean onStartJob (final JobParameters jobParameters) {

        mFetchMovieTask = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                Context context = getApplicationContext();
                MovieSyncUtils.deleteTables(context);
                MovieSyncTask.syncMovie(context);
                MovieSyncTask.sendNotification(context);
                jobFinished(jobParameters, false);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                jobFinished(jobParameters, false);
            }
        };

        mFetchMovieTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        if(mFetchMovieTask != null) {
            mFetchMovieTask.cancel(true);
        }
        return true;
    }
}
