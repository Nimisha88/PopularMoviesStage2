package com.example.android.moviemania.utilities;

import android.content.ContentValues;
import com.example.android.moviemania.data.MovieContract.VideoEntry;
import com.example.android.moviemania.data.MovieContract.MovieEntry;
import com.example.android.moviemania.data.MovieContract.ReviewEntry;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.util.StringTokenizer;

public class OpenMovieJSONUtils {

    //JSON Name Identifiers
    private static final String MOVIE_RESULTS = "results";
    private static final String MOVIE_VOTE_COUNT = "vote_count";
    private static final String MOVIE_ID = "id";
    private static final String MOVIE_VIDEO_KEY = "key";
    private static final String MOVIE_REVIEW_KEY = "url";
    private static final String MOVIE_VOTE_AVERAGE = "vote_average";
    private static final String MOVIE_TITLE = "title";
    private static final String MOVIE_POPULARITY = "popularity";
    private static final String MOVIE_POSTER_PATH = "poster_path";
    private static final String MOVIE_ORIGINAL_LANGUAGE = "original_language";
    private static final String MOVIE_ORIGINAL_TITLE = "original_title";
    private static final String MOVIE_GENRE_IDS = "genre_ids";
    private static final String MOVIE_BACKDROP_PATH = "backdrop_path";
    private static final String MOVIE_ADULT = "adult";
    private static final String MOVIE_OVERVIEW = "overview";
    private static final String MOVIE_RELEASE_DATE = "release_date";
    private static final String MESSAGE_CODE = "cod";

    private static int[] parseReleaseDate(String releaseDate) {

        int parsedReleaseDate[] = new int[3];
        StringTokenizer date = new StringTokenizer(releaseDate, "-");
        parsedReleaseDate[2] = Integer.parseInt(date.nextToken());
        parsedReleaseDate[1] = Integer.parseInt(date.nextToken());
        parsedReleaseDate[0] = Integer.parseInt(date.nextToken());
        return parsedReleaseDate;

    }

    public static ContentValues[] getMovieDataFromAPIJSONResult(String movieJSONData) throws JSONException {

        //Movie Data
        ContentValues[] parsedMovieData;
        //Convert API Result in JSON Object
        JSONObject movieJSON = new JSONObject(movieJSONData);

        /* Is there an error? */
        if (movieJSON.has(MESSAGE_CODE)) {
            int errorCode = movieJSON.getInt(MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* Location invalid */
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }
        }

        JSONArray movieResultJSONArray = movieJSON.getJSONArray(MOVIE_RESULTS);
        parsedMovieData = new ContentValues[movieResultJSONArray.length()];
        int parsedReleaseDate[];

        //Loop through JSON Array
        for (int pos = 0; pos < movieResultJSONArray.length(); pos++) {
            JSONObject movieResultObject = movieResultJSONArray.getJSONObject(pos);
            parsedReleaseDate = parseReleaseDate(movieResultObject.getString(MOVIE_RELEASE_DATE));

            ContentValues movieData = new ContentValues();
            movieData.put(MovieEntry.COLUMN_VOTE_COUNT, movieResultObject.getInt(MOVIE_VOTE_COUNT));
            movieData.put(MovieEntry.COLUMN_ID, movieResultObject.getInt(MOVIE_ID));
            movieData.put(MovieEntry.COLUMN_VOTE_AVERAGE, movieResultObject.getDouble(MOVIE_VOTE_AVERAGE));
            movieData.put(MovieEntry.COLUMN_TITLE, movieResultObject.getString(MOVIE_TITLE));
            movieData.put(MovieEntry.COLUMN_POPULARITY, movieResultObject.getDouble(MOVIE_POPULARITY));
            movieData.put(MovieEntry.COLUMN_POSTER_PATH, movieResultObject.getString(MOVIE_POSTER_PATH));
            movieData.put(MovieEntry.COLUMN_ORIGINAL_LANGUAGE, movieResultObject.getString(MOVIE_ORIGINAL_LANGUAGE));
            movieData.put(MovieEntry.COLUMN_ORIGINAL_TITLE, movieResultObject.getString(MOVIE_ORIGINAL_TITLE));
            movieData.put(MovieEntry.COLUMN_BACKDROP_PATH, movieResultObject.getString(MOVIE_BACKDROP_PATH));
            movieData.put(MovieEntry.COLUMN_ADULT, movieResultObject.getBoolean(MOVIE_ADULT));
            movieData.put(MovieEntry.COLUMN_OVERVIEW, movieResultObject.getString(MOVIE_OVERVIEW));
            movieData.put(MovieEntry.COLUMN_RELEASE_DAY, parsedReleaseDate[0]);
            movieData.put(MovieEntry.COLUMN_RELEASE_MONTH, parsedReleaseDate[1]);
            movieData.put(MovieEntry.COLUMN_RELEASE_YEAR, parsedReleaseDate[2]);

            parsedMovieData[pos] = movieData;
        }
        return parsedMovieData;
    }

    public static ContentValues[] getVideoDataFromAPIJSONResult(String jsonMovieVideoResponse, long movieID) throws JSONException {

        ContentValues[] parsedVideoData;

        JSONObject movieJSON = new JSONObject(jsonMovieVideoResponse);

        /* Is there an error? */
        if (movieJSON.has(MESSAGE_CODE)) {
            int errorCode = movieJSON.getInt(MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* Location invalid */
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }
        }

        JSONArray videoResultJSONArray = movieJSON.getJSONArray(MOVIE_RESULTS);
        parsedVideoData = new ContentValues[videoResultJSONArray.length()];

        //Loop through JSON Array
        for (int pos = 0; pos < videoResultJSONArray.length(); pos++) {
            JSONObject videoResultObject = videoResultJSONArray.getJSONObject(pos);

            ContentValues videoData = new ContentValues();

            //Add MovieID
            videoData.put(VideoEntry.COLUMN_MOVIE_ID, Long.toString(movieID));
            //Add VideoKey
            videoData.put(VideoEntry.COLUMN_VIDEO_KEY, videoResultObject.getString(MOVIE_VIDEO_KEY));

            parsedVideoData[pos] = videoData;
        }
        return parsedVideoData;
    }

    public static ContentValues[] getReviewDataFromAPIJSONResult(String jsonMovieReviewResponse, long movieID) throws JSONException {

        ContentValues[] parsedReviewData;

        JSONObject reviewJSON = new JSONObject(jsonMovieReviewResponse);

        /* Is there an error? */
        if (reviewJSON.has(MESSAGE_CODE)) {
            int errorCode = reviewJSON.getInt(MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* Location invalid */
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }
        }

        JSONArray reviewResultJSONArray = reviewJSON.getJSONArray(MOVIE_RESULTS);
        parsedReviewData = new ContentValues[reviewResultJSONArray.length()];

        //Loop through JSON Array
        for (int pos = 0; pos < reviewResultJSONArray.length(); pos++) {
            JSONObject reviewResultObject = reviewResultJSONArray.getJSONObject(pos);

            ContentValues reviewData = new ContentValues();

            //Add MovieID
            reviewData.put(ReviewEntry.COLUMN_MOVIE_ID, Long.toString(movieID));
            //Add VideoKey
            reviewData.put(ReviewEntry.COLUMN_REVIEW_URL, reviewResultObject.getString(MOVIE_REVIEW_KEY));

            parsedReviewData[pos] = reviewData;
        }
        return parsedReviewData;
    }
}
