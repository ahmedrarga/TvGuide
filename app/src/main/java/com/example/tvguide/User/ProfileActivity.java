package com.example.tvguide.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.tvguide.BaseActivity;
import com.example.tvguide.HomePage.HomeActivity;
import com.example.tvguide.HomePage.PosterAdapter;
import com.example.tvguide.MovieProfile.Tracking;
import com.example.tvguide.R;
import com.example.tvguide.UserPost;
import com.example.tvguide.tmdb.Movie;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    ImageView cover;
    ImageView profile;
    TextView name;
    private ArrayList<Map<String, Object>> map;
    TextView email;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    RecyclerView posts;
    RecyclerView watching;
    ProgressBar watchingBar;
    TextView watchingMessage;
    List<String> postsArray = new ArrayList<>();
    String userName = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ImageView btn = findViewById(R.id.back_butt);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProfileActivity.super.onBackPressed();
            }
        });
        posts = findViewById(R.id.posts);
        watching = findViewById(R.id.watching);
        watchingBar = findViewById(R.id.progressBar5);
        watchingMessage = findViewById(R.id.textView23);
        watchingBar.setVisibility(View.VISIBLE);
        watchingMessage.setVisibility(View.GONE);
        watching.setVisibility(View.GONE);
        Handler handler = new Handler(getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                FirebaseUser user = mAuth.getCurrentUser();
                final String m = user.getEmail();
                System.out.println(m);
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("users")
                        .whereEqualTo("EMAIL", m)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {

                                        name = findViewById(R.id.name);
                                        email = findViewById(R.id.email);
                                        userName = document.getData().get("FIRST_NAME").toString() + " " + document.getData().get("LAST_NAME").toString();
                                        name.setText(userName);
                                        email.setText(m);
                                    }
                                } else {
                                }
                            }
                        });
                StorageReference ref = FirebaseStorage.getInstance().getReference();
                StorageReference pRef = ref.child("images/" + m + "/profile.jpg");
                final long ONE_MEGABYTE = 1024 * 1024;
                pRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            profile = findViewById(R.id.profile);
                            Picasso.get()
                                    .load(task.getResult())
                                    .fit()
                                    .into(profile);
                        }
                    }
                });
                pRef = ref.child("images/" + m + "/cover.jpg");
                pRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        cover = findViewById(R.id.cover);
                        Picasso.get()
                                .load(uri)
                                .fit()
                                .into(cover);
                    }
                });
            }
        });
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
        posts.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
        String m = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        db.collection("userPosts")
                .document(m)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        UserPost post = documentSnapshot.toObject(UserPost.class);
                        if(post == null){
                            post = new UserPost();
                        }
                        posts.setAdapter(new FeedsAdapter(post.paths, getApplicationContext(), userName));
                    }
                });
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
            final List<Movie> watch = new ArrayList<>();
            watching.setAdapter(new PosterAdapter(watch, getApplicationContext(), "show"));
            watching.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false));
            for (final Movie m : movies) {
                FirebaseFirestore.getInstance().collection("Tracking")
                        .document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Tracking t = documentSnapshot.toObject(Tracking.class);
                        if (t != null && t.tracking != null &&
                                t.tracking.get(String.valueOf(m.getId())) != null
                                && !m.isWatched()) {
                            watching.setVisibility(View.VISIBLE);
                            watchingBar.setVisibility(View.GONE);
                            watchingMessage.setVisibility(View.GONE);
                            watch.add(m);
                            watching.getAdapter().notifyItemInserted(watch.size() - 1);
                        }
                    }
                });
            }
            if(watch.size() == 0){
                watchingMessage.setVisibility(View.VISIBLE);
                watchingBar.setVisibility(View.GONE);
                watching.setVisibility(View.GONE);
            }
        }
    }

}
