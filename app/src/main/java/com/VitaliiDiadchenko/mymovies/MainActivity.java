package com.VitaliiDiadchenko.mymovies;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import com.VitaliiDiadchenko.mymovies.adapter.MoviesAdapter;
import com.VitaliiDiadchenko.mymovies.data.MainViewModel;
import com.VitaliiDiadchenko.mymovies.data.Movie;
import com.VitaliiDiadchenko.mymovies.utils.JSONUtils;
import com.VitaliiDiadchenko.mymovies.utils.NetworkUtils;
import org.json.JSONObject;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<JSONObject> {

     private RecyclerView recyclerViewSmallPoster;
     private MoviesAdapter adapter;
     private Switch switchSortBy;
     private MainViewModel mainViewModel;
     private TextView textViewPopularity;
     private TextView textViewTopRated;
     private ProgressBar progressBarLoading;

     private static int page = 1;
     private static int methodOfSort;
     private static boolean isLoading = false;

     private static final int LOADER_ID = 133;
     private LoaderManager loaderManager;

     private static String lang;

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
        setContentView(R.layout.activity_main);
        lang = Locale.getDefault().getLanguage();
        loaderManager = LoaderManager.getInstance(this);
        switchSortBy = findViewById(R.id.switchSortBy);
        adapter = new MoviesAdapter();
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        progressBarLoading = findViewById(R.id.progressBarLoading);
        textViewPopularity = findViewById(R.id.textViewPopularity);
        textViewTopRated = findViewById(R.id.textViewTopRated);
        recyclerViewSmallPoster = findViewById(R.id.recyclerViewSmallPoster);
        recyclerViewSmallPoster.setLayoutManager(new GridLayoutManager(this, getColumnCount()));
        recyclerViewSmallPoster.setAdapter(adapter);
        switchSortBy.setChecked(true);
        switchSortBy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                page = 1;
                setMethodOfSort(b);
            }
        });
        switchSortBy.setChecked(false);
        adapter.setOnPosterClickListener(new MoviesAdapter.OnPosterClickListener() {
            @Override
            public void onPosterClick(int position) {
                Movie movie = adapter.getMovies().get(position);
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("id", movie.getId());
                startActivity(intent);
            }
        });
        adapter.setOnReachEndListener(new MoviesAdapter.OnReachEndListener() {
            @Override
            public void onReachEnd() {
                if(!isLoading) {
                    downloadData(methodOfSort, page);
                }
            }
        });
        LiveData<List<Movie>> moviesFromLiveData = mainViewModel.getMovies();
        moviesFromLiveData.observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(List<Movie> movies) {
                if(page == 1) {
                    adapter.setMovies(movies);
                }
            }
        });
    }

    private void setMethodOfSort(boolean isTopRated) {
        if(isTopRated) {
            methodOfSort = NetworkUtils.TOP_RATED;
            textViewTopRated.setTextColor(getResources().getColor(R.color.colorAccent));
            textViewPopularity.setTextColor(getResources().getColor(R.color.white_color));
        } else {
            methodOfSort = NetworkUtils.POPULARITY;
            textViewPopularity.setTextColor(getResources().getColor(R.color.colorAccent));
            textViewTopRated.setTextColor(getResources().getColor(R.color.white_color));
        }
        downloadData(methodOfSort, page);
    }

    public void onClickSetPopularity(View view) {
        setMethodOfSort(false);
        switchSortBy.setChecked(false);
    }

    public void onClickSetTopRated(View view) {
        setMethodOfSort(true);
        switchSortBy.setChecked(true);
    }

    private void downloadData(int methodOfSort, int page) {
        URL url = NetworkUtils.buildURL(methodOfSort, page, lang);
        Bundle bundle = new Bundle();
        bundle.putString("url", url.toString());
        loaderManager.restartLoader(LOADER_ID, bundle, this);
    }
    @NonNull
    @Override
    public Loader<JSONObject> onCreateLoader(int id, @Nullable Bundle args) {
        NetworkUtils.JSONLoader jsonLoader = new NetworkUtils.JSONLoader(this, args);
        jsonLoader.setOnStartLoadingListener(new NetworkUtils.JSONLoader.OnStartLoadingListener() {
            @Override
            public void onStartLoading() {
                progressBarLoading.setVisibility(View.VISIBLE);
                isLoading = true;
            }
        });
        return jsonLoader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<JSONObject> loader, JSONObject data) {
        ArrayList<Movie> movies = JSONUtils.getMoviesFromJSON(data);
        if(movies != null && !movies.isEmpty()) {
            if (page == 1) {
                mainViewModel.deleteAllMovies();
                adapter.clear();
            }
            for(Movie movie : movies) {
                mainViewModel.insertMovie(movie);
            }
            adapter.addMovies(movies);
            page++;
        }
        isLoading = false;
        progressBarLoading.setVisibility(View.INVISIBLE);
        loaderManager.destroyLoader(LOADER_ID);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<JSONObject> loader) {

    }
}