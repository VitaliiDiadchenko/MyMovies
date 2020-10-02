package com.VitaliiDiadchenko.mymovies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.VitaliiDiadchenko.mymovies.adapter.MoviesAdapter;
import com.VitaliiDiadchenko.mymovies.data.FavoriteMovie;
import com.VitaliiDiadchenko.mymovies.data.MainViewModel;
import com.VitaliiDiadchenko.mymovies.data.Movie;

import java.util.ArrayList;
import java.util.List;

public class FavoriteActivity extends AppCompatActivity {

    private RecyclerView recyclerViewFavoriteMovies;
    private MoviesAdapter adapter;
    private MainViewModel mainViewModel;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.itemMain:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.itemFavorite:
                Intent intentToFavorite = new Intent(this, FavoriteActivity.class);
                startActivity(intentToFavorite);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private int getColumnCount() {
        int smallPosterSize = 185;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = (int) (displayMetrics.widthPixels / displayMetrics.density);
        return width / smallPosterSize > 2 ? width / smallPosterSize : 2;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        recyclerViewFavoriteMovies = findViewById(R.id.recyclerViewFavoriteMovies);
        recyclerViewFavoriteMovies.setLayoutManager(new GridLayoutManager(this, getColumnCount()));
        adapter = new MoviesAdapter();
        recyclerViewFavoriteMovies.setAdapter(adapter);
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        LiveData<List<FavoriteMovie>> favoriteMovies = mainViewModel.getFavoriteMovies();
        favoriteMovies.observe(this, new Observer<List<FavoriteMovie>>() {
            @Override
            public void onChanged(List<FavoriteMovie> favoriteMovies) {
                List<Movie> movies = new ArrayList<>();
                if(favoriteMovies != null) {
                    movies.addAll(favoriteMovies);
                    adapter.setMovies(movies);
                }

            }
        });
        adapter.setOnPosterClickListener(new MoviesAdapter.OnPosterClickListener() {
            @Override
            public void onPosterClick(int position) {
                Movie movie = adapter.getMovies().get(position);
                Intent intent = new Intent(FavoriteActivity.this, DetailActivity.class);
                intent.putExtra("id", movie.getId());
                startActivity(intent);
            }
        });

    }
}
