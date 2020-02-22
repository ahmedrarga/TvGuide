package com.example.tvguide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pools;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.tvguide.tmdb.Movie;
import com.example.tvguide.tmdb.Requests;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WatchlistActivity extends BaseActivity {
    private Requests r;
    private ArrayList<Map<String, Object>> map;
    private List<Movie> movies;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watchlist);
        initToolbar("Watchlist");
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
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
        }, 2000);



    }
    public void initRList(){
        RecyclerView view = findViewById(R.id.recWatch);
        view.setAdapter(new SearchResultsRecyclerAdapter(movies, getApplicationContext(), "Watchlist"));
        view.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        view.setVisibility(View.VISIBLE);
    }
}
