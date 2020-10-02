package com.VitaliiDiadchenko.mymovies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.VitaliiDiadchenko.mymovies.data.FavoriteMovie;
import com.VitaliiDiadchenko.mymovies.data.MainViewModel;
import com.VitaliiDiadchenko.mymovies.data.Review;
import com.VitaliiDiadchenko.mymovies.data.Trailer;
import com.VitaliiDiadchenko.mymovies.adapter.ReviewAdapter;
import com.VitaliiDiadchenko.mymovies.adapter.TrailerAdapter;
import com.VitaliiDiadchenko.mymovies.data.Movie;
import com.VitaliiDiadchenko.mymovies.utils.JSONUtils;
import com.VitaliiDiadchenko.mymovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    private ImageView imageViewBigPoster;
    private ImageView imageViewAddToFavorite;
    private TextView textViewTitle;
    private TextView textViewOriginalTitle;
    private TextView textViewRating;
    private TextView textViewReleaseDate;
    private TextView textViewOverview;
    private ScrollView scrollViewInfo;

    private RecyclerView recyclerViewTrailers;
    private RecyclerView recyclerViewReviews;
    private TrailerAdapter trailerAdapter;
    private ReviewAdapter reviewAdapter;

    private int id;
    private Movie movie;
    private FavoriteMovie favoriteMovie;

    private MainViewModel mainViewModel;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        lang = Locale.getDefault().getLanguage();
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        imageViewBigPoster = findViewById(R.id.imageViewBigPoster);
        imageViewAddToFavorite = findViewById(R.id.imageViewAddToFavorite);
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewOriginalTitle = findViewById(R.id.textViewOriginalTitle);
        textViewRating = findViewById(R.id.textViewRating);
        textViewReleaseDate = findViewById(R.id.textViewReleaseDate);
        textViewOverview = findViewById(R.id.textViewOverview);
        scrollViewInfo = findViewById(R.id.scrollViewInfo);
        Intent intent = getIntent();
        if(intent != null && intent.hasExtra("id")) {
            id = intent.getIntExtra("id", -1);
        } else {
            finish();
        }
        movie = mainViewModel.getMovieById(id);
        Picasso.get().load(movie.getBigPosterPath()).into(imageViewBigPoster);
        textViewTitle.setText(movie.getTitle());
        textViewOriginalTitle.setText(movie.getOriginalTitle());
        textViewRating.setText(Double.toString(movie.getVoteAverage()));
        textViewReleaseDate.setText(movie.getReleaseDate());
        textViewOverview.setText(movie.getOverView());
        setFavorite();
        recyclerViewTrailers = findViewById(R.id.recyclerViewTrailers);
        recyclerViewReviews = findViewById(R.id.recyclerViewReviews);
        trailerAdapter = new TrailerAdapter();
        reviewAdapter = new ReviewAdapter();
        trailerAdapter.setOnTrailerClickListener(new TrailerAdapter.OnTrailerClickListener() {
            @Override
            public void onTrailerClick(String url) {
                Intent intentToTrailer = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intentToTrailer);
            }
        });
        recyclerViewTrailers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTrailers.setAdapter(trailerAdapter);
        recyclerViewReviews.setAdapter(reviewAdapter);
        JSONObject jsonObjectTrailers = NetworkUtils.getJSONForVideos(movie.getId(), lang);
        JSONObject jsonObjectReviews = NetworkUtils.getJSONForReviews(movie.getId(), lang);
        ArrayList<Trailer> trailers = JSONUtils.getTrailersFromJSON(jsonObjectTrailers);
        ArrayList<Review> reviews = JSONUtils.getReviewsFromJSON(jsonObjectReviews);
        reviewAdapter.setReviews(reviews);
        trailerAdapter.setTrailers(trailers);
        scrollViewInfo.smoothScrollTo(0, 0);
    }

    public void onChangeFavorite(View view) {
        if(favoriteMovie == null) {
            mainViewModel.insertFavoriteMovie(new FavoriteMovie(movie));
            Toast.makeText(this, R.string.add_to_favorite, Toast.LENGTH_SHORT).show();
        } else {
            mainViewModel.deleteFavoriteMovie(favoriteMovie);
            Toast.makeText(this, R.string.remove_from_favorite, Toast.LENGTH_SHORT).show();
        }
        setFavorite();
    }

    private void setFavorite() {
        favoriteMovie = mainViewModel.getFavoriteMovieById(id);
        if(favoriteMovie == null) {
            imageViewAddToFavorite.setImageResource(R.drawable.grey_star);
        } else {
            imageViewAddToFavorite.setImageResource(R.drawable.star);
        }
    }
}
