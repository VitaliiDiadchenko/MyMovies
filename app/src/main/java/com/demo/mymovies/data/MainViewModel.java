package com.demo.mymovies.data;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainViewModel extends AndroidViewModel {

    private static MoviesDatabase moviesDatabase;
    private LiveData<List<Movie>> movies;
    private LiveData<List<FavoriteMovie>> favoriteMovies;

    public LiveData<List<Movie>> getMovies() {
        return movies;
    }

    public MainViewModel(@NonNull Application application) {
        super(application);
        moviesDatabase = MoviesDatabase.getInstance(getApplication());
        movies = moviesDatabase.moviesDao().getAllMovies();
        favoriteMovies = moviesDatabase.moviesDao().getAllFavoriteMovies();
    }

    public LiveData<List<FavoriteMovie>> getFavoriteMovies() {
        return favoriteMovies;
    }

    public Movie getMovieById(int id) {
        try {
            return new GetMovieTask().execute(id).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public FavoriteMovie getFavoriteMovieById(int id) {
        try {
            return new GetFavoriteMovieTask().execute(id).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteAllMovies() {
        new DeleteAllMoviesTask().execute();
    }

    public void insertMovie(Movie movie){
        new InsertMovieTask().execute(movie);
    }

    public void deleteMovie(Movie movie){
        new DeleteMovieTask().execute(movie);
    }

    public void insertFavoriteMovie(FavoriteMovie favoriteMovie){
        new InsertFavoriteMovieTask().execute(favoriteMovie);
        }

public void deleteFavoriteMovie(FavoriteMovie favoriteMovie){
        new DeleteFavoriteMovieTask().execute(favoriteMovie);
        }

private static class GetMovieTask extends AsyncTask<Integer, Void, Movie> {
    @Override
    protected Movie doInBackground(Integer... integers) {
        if(integers != null && integers.length > 0) {
            return moviesDatabase.moviesDao().getMovieById(integers[0]);
        }
        return null;
    }
}

    private static class GetFavoriteMovieTask extends AsyncTask<Integer, Void, FavoriteMovie> {
        @Override
        protected FavoriteMovie doInBackground(Integer... integers) {
            if(integers != null && integers.length > 0) {
                return moviesDatabase.moviesDao().getFavoriteMovieById(integers[0]);
            }
            return null;
        }
    }

    private static class DeleteAllMoviesTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            moviesDatabase.moviesDao().deleteAllMovies();
            return null;
        }
    }

    private static class InsertMovieTask extends AsyncTask<Movie, Void, Void>{
        @Override
        protected Void doInBackground(Movie... movies) {
            if(movies != null && movies.length > 0) {
                moviesDatabase.moviesDao().insertMovie(movies[0]);
            }
            return null;
        }
    }

    private static class DeleteMovieTask extends AsyncTask<Movie, Void, Void>{
        @Override
        protected Void doInBackground(Movie... movies) {
            if(movies != null && movies.length > 0) {
                moviesDatabase.moviesDao().deleteMovie(movies[0]);
            }
            return null;
        }
    }

    private static class InsertFavoriteMovieTask extends AsyncTask<FavoriteMovie, Void, Void> {
        @Override
        protected Void doInBackground(FavoriteMovie... favoriteMovies) {
            if(favoriteMovies != null && favoriteMovies.length > 0) {
                moviesDatabase.moviesDao().insertFavoriteMovie(favoriteMovies[0]);
            }
            return null;
        }
    }

    private static class DeleteFavoriteMovieTask extends AsyncTask<FavoriteMovie, Void, Void> {
        @Override
        protected Void doInBackground(FavoriteMovie... favoriteMovies) {
            if(favoriteMovies != null && favoriteMovies.length > 0) {
                moviesDatabase.moviesDao().deleteFavoriteMovie(favoriteMovies[0]);
            }
            return null;
        }
    }

}
