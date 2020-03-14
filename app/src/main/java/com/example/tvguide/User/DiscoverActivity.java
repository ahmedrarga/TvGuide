package com.example.tvguide.User;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;

import com.example.tvguide.BaseActivity;
import com.example.tvguide.HomePage.SearchResultsRecyclerAdapter;
import com.example.tvguide.R;
import com.example.tvguide.tmdb.Movie;
import com.example.tvguide.tmdb.Requests;
import com.squareup.picasso.Picasso;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Random;


public class DiscoverActivity extends BaseActivity {
    private List<Movie> movies;
    private List<Movie> shows;
    private boolean isRView = false;
    private RecyclerView rView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);
        initToolbar("Discover");
        findViewById(R.id.linearChoose).setVisibility(View.INVISIBLE);
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        final ImageView movies_card = findViewById(R.id.movies);
        final ImageView shows_card = findViewById(R.id.shows);
        Handler uiHandler = new Handler(Looper.getMainLooper());
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                Requests request = new Requests();
                movies = request.discoverMovies();
                shows = request.discoverShows();
                Random rnd = new Random();
                int rand = rnd.nextInt(movies.size());
                Picasso
                        .get()
                        .load(movies.get(rand).getBackdrop_path())
                        .fit()
                        .into(movies_card);
                Picasso
                        .get()
                        .load(shows.get(rand).getBackdrop_path())
                        .fit()
                        .into(shows_card);
                rView = findViewById(R.id.discoverRec);

                movies_card.setClickable(true);
                movies_card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        findViewById(R.id.linearChoose).setVisibility(View.INVISIBLE);
                        rView.setVisibility(View.VISIBLE);
                        rView.setAdapter(new SearchResultsRecyclerAdapter(movies, getApplicationContext(), "movie"));
                        rView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
                        isRView = true;
                        initToolbar("Movies");
                    }
                });
                shows_card.setClickable(true);
                shows_card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        findViewById(R.id.linearChoose).setVisibility(View.INVISIBLE);
                        rView.setVisibility(View.VISIBLE);
                        rView.setAdapter(new SearchResultsRecyclerAdapter(shows, getApplicationContext(), "show"));
                        rView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
                        isRView = true;
                        initToolbar("Shows");
                    }
                });
                findViewById(R.id.linearChoose).setVisibility(View.VISIBLE);
                findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
            }
        });

    }

    @Override
    public void onBackPressed() {
        if(isRView){
            rView.setVisibility(View.INVISIBLE);
            findViewById(R.id.linearChoose).setVisibility(View.VISIBLE);
            isRView = false;
            initToolbar("Discover");
        }else {
            super.onBackPressed();
        }
    }
}