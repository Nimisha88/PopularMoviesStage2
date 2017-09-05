package com.example.android.moviemania.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract {

    // The authority to determine which Content Provider to access
    public static final String AUTHORITY = "com.example.android.moviemania";

    // The Base Content URI
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Paths for accessing data in this contract
    public static final String PATH_MOVIES = "movies";
    public static final String PATH_FAVORITES = "favorites";
    public static final String PATH_VIDEOS = "videos";
    public static final String PATH_REVIEWS = "reviews";


    //Inner Class for Content of Movies Table
    //Implements Base Column for _ID and _COUNT
    public static final class MovieEntry implements BaseColumns {

        //Content URI
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        //Table and Column Names
        public static final String TABLE_NAME = "movie";
        public static final String COLUMN_VOTE_COUNT = "vote_count";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_ORIGINAL_LANGUAGE = "original_language";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
        public static final String COLUMN_ADULT = "adult";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DAY = "release_day";
        public static final String COLUMN_RELEASE_MONTH = "release_month";
        public static final String COLUMN_RELEASE_YEAR = "release_year";
        public static final String COLUMN_CREATE_TIMESTAMP = "row_create_ts";

        public static String[] getDBTableProjection() {

            return new String[] {
                            MovieEntry._ID,
                            MovieEntry.COLUMN_VOTE_COUNT,
                            MovieEntry.COLUMN_ID,
                            MovieEntry.COLUMN_VOTE_AVERAGE,
                            MovieEntry.COLUMN_TITLE,
                            MovieEntry.COLUMN_POPULARITY,
                            MovieEntry.COLUMN_POSTER_PATH,
                            MovieEntry.COLUMN_ORIGINAL_LANGUAGE,
                            MovieEntry.COLUMN_ORIGINAL_TITLE,
                            MovieEntry.COLUMN_BACKDROP_PATH,
                            MovieEntry.COLUMN_ADULT,
                            MovieEntry.COLUMN_OVERVIEW,
                            MovieEntry.COLUMN_RELEASE_DAY,
                            MovieEntry.COLUMN_RELEASE_MONTH,
                            MovieEntry.COLUMN_RELEASE_YEAR,
                            MovieEntry.COLUMN_CREATE_TIMESTAMP
                    };
        }
    }

    public final static class FavoriteEntry implements BaseColumns {

        //Content URI
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();

        //Re-Implementation as Local copy Required
        public static final String TABLE_NAME = "favoritemovie";
        public static final String COLUMN_VOTE_COUNT = "vote_count";
        public static final String COLUMN_MOVIE_ID = "id";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_ORIGINAL_LANGUAGE = "original_language";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
        public static final String COLUMN_ADULT = "adult";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DAY = "release_day";
        public static final String COLUMN_RELEASE_MONTH = "release_month";
        public static final String COLUMN_RELEASE_YEAR = "release_year";
        public static final String COLUMN_CREATE_TIMESTAMP = "row_create_ts";

        public static String[] getDBTableProjection() {

            return new String[]{
                    FavoriteEntry._ID,
                    FavoriteEntry.COLUMN_VOTE_COUNT,
                    FavoriteEntry.COLUMN_MOVIE_ID,
                    FavoriteEntry.COLUMN_VOTE_AVERAGE,
                    FavoriteEntry.COLUMN_TITLE,
                    FavoriteEntry.COLUMN_POPULARITY,
                    FavoriteEntry.COLUMN_POSTER_PATH,
                    FavoriteEntry.COLUMN_ORIGINAL_LANGUAGE,
                    FavoriteEntry.COLUMN_ORIGINAL_TITLE,
                    FavoriteEntry.COLUMN_BACKDROP_PATH,
                    FavoriteEntry.COLUMN_ADULT,
                    FavoriteEntry.COLUMN_OVERVIEW,
                    FavoriteEntry.COLUMN_RELEASE_DAY,
                    FavoriteEntry.COLUMN_RELEASE_MONTH,
                    FavoriteEntry.COLUMN_RELEASE_YEAR,
                    FavoriteEntry.COLUMN_CREATE_TIMESTAMP
            };
        }
    }

    public static final class VideoEntry implements BaseColumns {

        //Content URI
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_VIDEOS).build();

        public static final String TABLE_NAME = "trailervideo";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_VIDEO_KEY = "video_key";
        public static final String COLUMN_CREATE_TIMESTAMP = "row_create_ts";

        public static String[] getDBTableProjection() {

            return new String[]{
                    VideoEntry._ID,
                    VideoEntry.COLUMN_MOVIE_ID,
                    VideoEntry.COLUMN_VIDEO_KEY,
                    VideoEntry.COLUMN_CREATE_TIMESTAMP
            };
        }
    }

    public static final class ReviewEntry implements BaseColumns {

        //Content URI
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEWS).build();

        public static final String TABLE_NAME = "moviereview";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_REVIEW_URL = "review_url";
        public static final String COLUMN_CREATE_TIMESTAMP = "row_create_ts";

        public static String[] getDBTableProjection() {

            return new String[]{
                    ReviewEntry._ID,
                    ReviewEntry.COLUMN_MOVIE_ID,
                    ReviewEntry.COLUMN_REVIEW_URL,
                    ReviewEntry.COLUMN_CREATE_TIMESTAMP
            };
        }
    }
}
