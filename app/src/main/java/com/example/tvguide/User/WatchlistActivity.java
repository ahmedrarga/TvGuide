package com.example.tvguide.User;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.tvguide.BaseActivity;
import com.example.tvguide.HomePage.Feeds;
import com.example.tvguide.HomePage.HomeActivity;
import com.example.tvguide.HomePage.PosterAdapter;
import com.example.tvguide.HomePage.SearchResultsRecyclerAdapter;
import com.example.tvguide.MovieProfile.Tracking;
import com.example.tvguide.R;
import com.example.tvguide.tmdb.Movie;
import com.example.tvguide.tmdb.Requests;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class WatchlistActivity extends BaseActivity {
    private Requests r;
    private ArrayList<Map<String, Object>> map;
    private static List<Movie> movies = new ArrayList<>();
    private RecyclerView waiting;
    private RecyclerView watching;
    private RecyclerView stremaing;
    private RecyclerView moviesOnly;
    private RecyclerView showsOnly;
    private List<Movie> waitingLst = new ArrayList<>();
    private List<Movie> watchingLst = new ArrayList<>();
    private List<Movie> streamingLst = new ArrayList<>();
    private List<Movie> moviesLst = new ArrayList<>();
    private List<Movie> showsLst = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watchlist);
        initToolbar("Watchlist");
        findViewById(R.id.layout).setVisibility(View.INVISIBLE);
        findViewById(R.id.progress).setVisibility(View.VISIBLE);
        findViewById(R.id.add).setVisibility(View.GONE);
        moviesOnly = init(moviesOnly, R.id.movies, moviesLst);
        showsOnly = init(showsOnly, R.id.shows, showsLst);
        stremaing = init(stremaing, R.id.streaming, streamingLst);
        waiting = init(waiting, R.id.waiting, waitingLst);
        watching = init(watching, R.id.watching, watchingLst);
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
                                if (task.isSuccessful()) {
                                    try {
                                        map = (ArrayList) task.getResult().getData().get("listOfMovies");
                                        new task().execute(map);


                                    } catch (NullPointerException e) {
                                        Button btn = findViewById(R.id.add);
                                        btn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Intent intent = new Intent(getApplicationContext(), DiscoverActivity.class);
                                                startActivity(intent);
                                            }
                                        });
                                        findViewById(R.id.progress).setVisibility(View.GONE);
                                        btn.setVisibility(View.VISIBLE);
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


    public void initRList(List<Movie> movies){
       for (Movie m : movies){
           if(m.getMedia_type().equals("tv")){
               showsLst.add(m);
           }else{
               moviesLst.add(m);
           }
       }

       for(final Movie m : showsLst){
            if(m.isStreaming()){
                streamingLst.add(m);
                stremaing.getAdapter().notifyItemInserted(streamingLst.size() - 1);
            }else if(m.isWaiting()){
                waitingLst.add(m);
                waiting.getAdapter().notifyItemInserted(waitingLst.size() - 1);
            }
            FirebaseFirestore.getInstance().collection("Tracking")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Tracking t = documentSnapshot.toObject(Tracking.class);
                    if(t != null && t.tracking != null &&
                            t.tracking.get(String.valueOf(m.getId())) != null
                                && !m.isWatched()){
                        watchingLst.add(m);
                        watching.getAdapter().notifyItemInserted(watchingLst.size() - 1);
                    }
                }
            });
       }
       findViewById(R.id.progress).setVisibility(View.GONE);
       findViewById(R.id.layout).setVisibility(View.VISIBLE);

    }
    private RecyclerView init(RecyclerView r, int id, List<Movie> movies){
        r = findViewById(id);
        r.setAdapter(new PosterAdapter(movies, getApplicationContext(), "tv"));
        r.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        return r;
    }
    private class task extends AsyncTask<ArrayList<Map<String, Object>>, Void, List<Movie>> {

        @Override
        protected List<Movie> doInBackground(ArrayList<Map<String, Object>>... arrayLists) {
            List<Movie> movies = new ArrayList<>();
            for(Map<String, Object> m : arrayLists[0]){
                String query = "https://api.themoviedb.org/3/" +
                        (String)m.get("media_type") +
                        "/" + m.get("id") +
                        "?api_key=" + HomeActivity.api_key;
                Response response = setResponse(query);
                if(response != null && response.code() == 200){
                    try {
                        movies.add(new Movie(new JSONObject(response.body().string()), (String)m.get("media_type")));
                    }catch (IOException e1){

                    }catch (JSONException e2){

                    }

                }
            }
            return movies;
        }


        private Response setResponse(String query){
            Response response = null;
            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/octet-stream");
            RequestBody body = RequestBody.create(mediaType, "{}");
            Request request = new Request.Builder()
                    .url(query)
                    .get()
                    .build();

            try {
                response = client.newCall(request).execute();
                return response;

            } catch (Exception e) {
                System.out.println("Error in doInBackground");
                System.out.println("Error:" + e.getMessage());
                return null;
            }
        }



        @Override
        protected void onPostExecute(List<Movie> movies) {
            super.onPostExecute(movies);
            if(movies.size() == 0){
                findViewById(R.id.layout).setVisibility(View.GONE);
                findViewById(R.id.progress).setVisibility(View.GONE);
                findViewById(R.id.add).setVisibility(View.VISIBLE);
            }else {
                initRList(movies);
            }


        }
    }
}
