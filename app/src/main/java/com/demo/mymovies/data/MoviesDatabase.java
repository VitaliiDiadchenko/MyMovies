package com.demo.mymovies.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
@Database(entities = {Movie.class, FavoriteMovie.class}, version = 3, exportSchema = false)
public abstract class MoviesDatabase extends RoomDatabase {

    private static MoviesDatabase moviesDatabase;
    private static final String DB_NAME = "movies.db";
    private static final Object LOCK = new Object();

    public static MoviesDatabase getInstance(Context context) {
        synchronized (LOCK) {
            if (moviesDatabase == null) {
                moviesDatabase = Room.databaseBuilder(context, MoviesDatabase.class, DB_NAME).fallbackToDestructiveMigration().build();
            }
        }
        return moviesDatabase;
    }

    public abstract MoviesDao moviesDao();
}
