package com.example.tvguide.MovieProfile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.tvguide.R;
import com.example.tvguide.tmdb.Movie;
import com.example.tvguide.tmdb.Requests;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MovieProfileActivity extends AppCompatActivity implements Overview.OnFragmentInteractionListener,
        Posts.OnFragmentInteractionListener, Track.OnFragmentInteractionListener, EpisodeFragment.OnFragmentInteractionListener
        , SeasonFragment.OnFragmentInteractionListener{
    private int id;
    private ImageView image;
    private TextView movie_name;
    private String media_type;
    Movie movie;
    private boolean isInWatchlist = false;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TabLayout tabs;
    private ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_profile);
        tabs = findViewById(R.id.movieProfileTab);
        pager = findViewById(R.id.viewPager);
        Intent intent = getIntent();
        id = intent.getIntExtra("id", 0);
        media_type = intent.getStringExtra("media_type");
        if(media_type.equals("movie"))
            tabs.removeTabAt(1);
        final FragmentAdapter adapter = new FragmentAdapter(this,getSupportFragmentManager(), tabs.getTabCount(), media_type);
        pager.setAdapter(adapter);


        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



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
        Picasso.get()
                .load(movie.getPoster_path())
                .resize(300, 450)
                .into((ImageView)findViewById(R.id.poster));
        String name = movie.getName();
        if(name.length() > 45){
            name = name.substring(0, 45) + "...";
        }
        movie_name.setText(name);
        TextView rating = findViewById(R.id.rating);
        rating.setText(String.valueOf(movie.getRating()));
        TextView air_dates = findViewById(R.id.air_dates);
        String air = movie.getAirDates() + " | ";
        air_dates.setText(air);


    }

    @Override
    protected void onResume() {
        super.onResume();
        checkWatchlist();

    }
    protected void checkWatchlist(){
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
                                FloatingActionButton btn = findViewById(R.id.add);
                                Drawable img = getApplicationContext().getResources().getDrawable( R.drawable.ic_done);
                                btn.setImageDrawable(img);
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

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private final class watchlist{
        private int id;
        private String media_type;
        public watchlist(int id, String media_type){
            this.id = id;
            this.media_type = media_type;
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
                        FloatingActionButton btn = findViewById(R.id.add);
                        Drawable img = getApplicationContext().getResources().getDrawable( R.drawable.ic_done);
                        btn.setImageDrawable(img);
                    }
                }
            });
        }
    }
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
