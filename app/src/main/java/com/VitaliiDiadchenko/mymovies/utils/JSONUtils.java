package com.VitaliiDiadchenko.mymovies.utils;

import android.util.Log;

import com.VitaliiDiadchenko.mymovies.data.Review;
import com.VitaliiDiadchenko.mymovies.data.Trailer;
import com.VitaliiDiadchenko.mymovies.data.Movie;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class JSONUtils {

    private static final String KEY_RESULTS = "results";
    //Для отзывов
    private static final String KEY_AUTHOR = "author";
    private static final String KEY_CONTENT = "content";
    //Для видео
    private static final String KEY_KEY_OF_VIDEO = "key";
    private static final String KEY_NAME = "name";
    private static final String BASE_YOUTUBE_URL = "https://www.youtube.com/watch?v=";
    //вся информация о фильме
    private static final String KEY_VOTE_COUNT = "vote_count";
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_ORIGINAL_TITLE = "original_title";
    private static final String KEY_OVER_VIEW = "overview";
    private static final String KEY_POSTER_PATH = "poster_path";
    private static final String KEY_BACKGROUND_PATH = "backdrop_path";
    private static final String KEY_VOTE_AVERAGE = "vote_average";
    private static final String KEY_RELEASE_DATE = "release_date";

    public static final String BASE_POSTER_URL = "https://image.tmdb.org/t/p/";
    public static final String SMALL_POSTER_SIZE = "w185";
    public static final String BIG_POSTER_SIZE = "w780";

    public static ArrayList<Review> getReviewsFromJSON(JSONObject jsonObject) {
        ArrayList<Review> result = new ArrayList<>();
        if (jsonObject == null) {
            return result;
        }
        try {
            JSONArray jsonArray = jsonObject.getJSONArray(KEY_RESULTS);
            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject objectReview = jsonArray.getJSONObject(i);
                String author = objectReview.getString(KEY_AUTHOR);
                String content = objectReview.getString(KEY_CONTENT);
                Review review = new Review(author, content);
                result.add(review);
            }
        } catch (JSONException e) {
            Log.i("Error while getting Reviews", String.valueOf(e));
        }
        return result;
    }

    public static ArrayList<Trailer> getTrailersFromJSON(JSONObject jsonObject) {
        ArrayList<Trailer> result = new ArrayList<>();
        if (jsonObject == null) {
            return result;
        }
        try {
            JSONArray jsonArray = jsonObject.getJSONArray(KEY_RESULTS);
            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject objectTrailer = jsonArray.getJSONObject(i);
                String key = BASE_YOUTUBE_URL + objectTrailer.getString(KEY_KEY_OF_VIDEO);
                String name = objectTrailer.getString(KEY_NAME);
                Trailer trailer = new Trailer(key, name);
                result.add(trailer);
            }
        } catch (JSONException e) {
            Log.i("Error while getting Trailers", String.valueOf(e));
        }
        return result;
    }

    //создаем метод где будем получать значения с фильмами по ключам
    public static ArrayList<Movie> getMoviesFromJSON(JSONObject jsonObject) {
        ArrayList<Movie> result = new ArrayList<>();
        if (jsonObject == null) {
            return result;
        }
        try {
            JSONArray jsonArray = jsonObject.getJSONArray(KEY_RESULTS);
            //в цикле получаем фильмы
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject objectMovie = jsonArray.getJSONObject(i);
                int id = objectMovie.getInt(KEY_ID);
                int voteCount = objectMovie.getInt(KEY_VOTE_COUNT);
                String title = objectMovie.getString(KEY_TITLE);
                String originalTitle = objectMovie.getString(KEY_ORIGINAL_TITLE);
                String overView = objectMovie.getString(KEY_OVER_VIEW);
                String posterPath = BASE_POSTER_URL + SMALL_POSTER_SIZE + objectMovie.getString(KEY_POSTER_PATH);
                String bigPosterPath = BASE_POSTER_URL + BIG_POSTER_SIZE + objectMovie.getString(KEY_POSTER_PATH);
                String backdropPath = objectMovie.getString(KEY_BACKGROUND_PATH);
                double voteAverage = objectMovie.getDouble(KEY_VOTE_AVERAGE);
                String releaseDate = objectMovie.getString(KEY_RELEASE_DATE);
                Movie movie = new Movie(id, voteCount, title, originalTitle, overView, posterPath, bigPosterPath, backdropPath, voteAverage, releaseDate);
                result.add(movie);
            }
        } catch (JSONException e) {
            Log.i("Error while getting Movies", String.valueOf(e));
        }
        return result;
    }

}
