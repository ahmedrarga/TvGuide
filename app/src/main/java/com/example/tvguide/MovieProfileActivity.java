package com.example.tvguide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tvguide.tmdb.Movie;
import com.example.tvguide.tmdb.Requests;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MovieProfileActivity extends AppCompatActivity {
    private int id;
    private ImageView image;
    private TextView movie_name;
    private String media_type;
    Movie movie;
    private boolean isInWatchlist = false;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_profile);
        Intent intent = getIntent();
        id = intent.getIntExtra("id", 0);
        media_type = intent.getStringExtra("media_type");
        image = findViewById(R.id.backdrop);
        movie_name = findViewById(R.id.title);
        ImageView back = findViewById(R.id.back);
        back.setClickable(true);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        Requests requests = new Requests(id, media_type);
        movie = requests.getMovieById();
        Picasso.get().load(movie.getBackdrop_path())
                .into(image);
        String name = "  " + movie.getName() + "  ";
        movie_name.setText(name);
        TextView overview = findViewById(R.id.overview);
        overview.setText(movie.getOverview());
        checkWatchlist();

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkWatchlist();

    }
    private void checkWatchlist(){
        String u = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        db.collection("watchlist").document(u).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    try {
                        System.out.println(task.getResult().getData()+ "ef;aiweFGHLwieghf   weiGAEURHGvliweuhgv;WEFHUpwieuoghflsdKHFisdghfpwoiEGHFPOWTPERHC");
                        ArrayList<Map<String, Object>> map = (ArrayList)task.getResult().getData().get("listOfMovies");
                        for(int i = 0; i < map.size(); i++){
                            if((long)map.get(i).get("id") == (long)id && map.get(i).get("media_type").equals(media_type)){
                                isInWatchlist = true;
                                Button btn = findViewById(R.id.add);
                                btn.setText("Added to watchlist");
                                break;
                            }
                        }

                    }catch (NullPointerException e){
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("error: " + e.getMessage() + "------------------------------------------------------");
            }
        });
    }

    private final class watchlist{
        private int id;
        private String media_type;
        public watchlist(int id, String media_type){
            this.id = id;
            this.media_type = media_type;
        }

        public String getMedia_type() {
            return media_type;
        }

        public int getId() {
            return id;
        }
        public HashMap<String, Object> get(){
            HashMap<String, Object> map = new HashMap<>();
            map.put("id", this.id);
            map.put("media_type", this.media_type);
            return map;
        }
    }

    public void addToWatchList(View v){
        if(!isInWatchlist) {
            final View view = v;
            ArrayList<HashMap<String, Object>> w = new ArrayList<>();
            w.add(new watchlist(id, media_type).get());
            final Map<String, Object> toAdd = new HashMap<>();
            toAdd.put("listOfMovies", w);
            db = FirebaseFirestore.getInstance();

            final CollectionReference ref = db.collection("watchlist");
            final String u = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            ref.document(u).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        DocumentSnapshot doc = task.getResult();
                        if (doc.exists()) {
                            System.out.println("DocumentSnapshot data: " +  doc.getData().get("listOfMovies"));
                            ArrayList<HashMap<String, Object>> arrayList = (ArrayList<HashMap<String, Object>>)doc.getData().get("listOfMovies");
                            arrayList.add(new watchlist(id, media_type).get());
                            Map <String, Object> map = new HashMap<>();
                            map.put("listOfMovies", arrayList);
                            ref.document(u).set(map);
                        } else {
                            System.out.println("No such document");
                            ref.document(u).set(toAdd);
                        }
                        isInWatchlist = true;
                        Button btn = findViewById(R.id.add);
                        btn.setText("Added to watchlist");
                    }
                }
            });
        }
    }

}
