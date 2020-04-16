package com.example.tvguide.User;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;

import com.example.tvguide.BaseActivity;
import com.example.tvguide.HomePage.PosterAdapter;
import com.example.tvguide.HomePage.SearchResultsRecyclerAdapter;
import com.example.tvguide.R;
import com.example.tvguide.tmdb.Movie;
import com.example.tvguide.tmdb.Requests;
import com.squareup.picasso.Picasso;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Random;


public class DiscoverActivity extends BaseActivity {
    private RecyclerView nowPlaying;
    private RecyclerView popularM;
    private RecyclerView popularS;
    private RecyclerView topRatedM;
    private RecyclerView topRatedS;
    private RecyclerView Upcoming;
    private RecyclerView trending;
    private RecyclerView trendingS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);
        initToolbar("Discover");
        findViewById(R.id.news).setVisibility(View.GONE);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Requests r = new Requests();
                trending = findViewById(R.id.trendingMovies);
                trendingS = findViewById(R.id.trendingShows);
                nowPlaying = findViewById(R.id.nowPlayingRView);
                Upcoming = findViewById(R.id.upcoming);
                popularM = findViewById(R.id.popularMovies);
                popularS = findViewById(R.id.popularShows);
                topRatedM = findViewById(R.id.topRatedMovies);
                topRatedS = findViewById(R.id.topRatedShows);
                initRList(trending, r.getTrendingMovies(), "movie");
                initRList(trendingS, r.getTrendingShows(), "show");
                initRList(nowPlaying, r.getNowPlaying(), "movie");
                initRList(Upcoming, r.getUpcoming(), "movie");
                initRList(popularM, r.getpopularMovies(), "movie");
                initRList(popularS, r.getpopularShows(), "show");
                initRList(topRatedM, r.getTopRatedMovies(), "movie");
                initRList(topRatedS, r.getTopRatedShows(), "show");
                findViewById(R.id.news).setVisibility(View.VISIBLE);
                findViewById(R.id.progressBar3).setVisibility(View.GONE);

            }
        });
    }



    private void initRList(RecyclerView r, List<Movie> movies, String cl){
        r.setAdapter(new PosterAdapter(movies, getApplicationContext(), cl));
        r.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
    }
}