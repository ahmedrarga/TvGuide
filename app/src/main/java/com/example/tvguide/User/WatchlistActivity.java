package com.example.tvguide.User;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tvguide.BaseActivity;
import com.example.tvguide.HomePage.Videos;
import com.example.tvguide.HomePage.SearchResultsRecyclerAdapter;
import com.example.tvguide.R;
import com.example.tvguide.tmdb.Movie;
import com.example.tvguide.tmdb.Requests;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class WatchlistActivity extends BaseActivity {
    private Requests r;
    private ArrayList<Map<String, Object>> map;
    private static List<Movie> movies = new ArrayList<>();
    private RecyclerView rView;
    private boolean isRView = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watchlist);
        initToolbar("Watchlist");
        findViewById(R.id.linearChoose).setVisibility(View.INVISIBLE);
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        rView = findViewById(R.id.recWatch);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                String u = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                db.collection("watchlist").document(u).
                        get().
                        addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()){
                                    synchronized (this) {
                                        try {
                                            map = (ArrayList)task.getResult().getData().get("listOfMovies");
                                            r = new Requests();
                                            movies = r.getMoviesFromHashMap(map);
                                            initRList();

                                        }catch (NullPointerException e){
                                            TextView view = findViewById(R.id.error_watchList);
                                            view.setText("Add movies and TV shows to Watchlist");
                                            findViewById(R.id.progressBar).setVisibility(View.GONE);
                                            view.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }
                                else{
                                    Snackbar.make(getWindow().getDecorView().getRootView(), "Error in importing data", Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println(e.getMessage() + "ewfqqergkhjegfjWEHFGOWELFG ");
                    }
                });
            }
        });




    }
    @Override
    public void onBackPressed() {
        if(isRView){
            rView.setVisibility(View.INVISIBLE);
            findViewById(R.id.linearChoose).setVisibility(View.VISIBLE);
            isRView = false;
            initToolbar("Watchlist");
        }else {
            super.onBackPressed();
        }
    }
    public static List<Movie> getWatchList(final Videos.WatchlistListener listener){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String u = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        db.collection("watchlist").document(u).
                get().
                addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            synchronized (this) {
                                try {
                                    ArrayList<Map<String, Object>> map = (ArrayList)task.getResult().getData().get("listOfMovies");
                                    Requests r = new Requests();
                                    movies = r.getMoviesFromHashMap(map);
                                    listener.Watchlist(movies);
                                }catch (NullPointerException e){

                                }
                            }
                        }
                        else{
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println(e.getMessage() + "ewfqqergkhjegfjWEHFGOWELFG ");
            }
        });
        return movies;
    }

    public void initRList(){
        final List<Movie> movies = new ArrayList<>();
        final List<Movie> shows = new ArrayList<>();
        for(int i = 0; i < this.movies.size(); i++){
            if(this.movies.get(i).getMedia_type().equals("tv")){
                shows.add(this.movies.get(i));
            }
            else{
                movies.add(this.movies.get(i));
            }
        }

        final ImageView movies_card = findViewById(R.id.movies);
        final ImageView shows_card = findViewById(R.id.shows);
        Random random = new Random();
        if(movies.size() > 0)
            Picasso.get()
                    .load(movies.get(random.nextInt(movies.size())).getBackdrop_path())
                    .fit()
                    .into(movies_card);
        if(shows.size() > 0)
            Picasso.get()
                    .load(shows.get(random.nextInt(shows.size())).getBackdrop_path())
                    .fit()
                    .into(shows_card);


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
}
