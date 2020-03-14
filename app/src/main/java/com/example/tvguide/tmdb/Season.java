package com.example.tvguide.tmdb;


import androidx.annotation.NonNull;

import com.example.tvguide.MovieProfile.TrackingAdapter;
import com.example.tvguide.MovieProfile.TrackingSeasons;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Season {
    private static String url = "https://image.tmdb.org/t/p/w500";
    private JSONObject obj;
    private int id;
    private boolean watched = false;
    private int season_id;
    public Season(final int id, JSONObject obj, final int season_id){
        this.id = id;
        this.obj = obj;
        this.season_id = season_id;
        String m = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("trackingSeasons")
                .document(m)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            TrackingSeasons t = task.getResult().toObject(TrackingSeasons.class);
                            if(t == null){
                                watched = false;
                            }else{
                                for(Map<String, Integer> m : t.arrayList){
                                    if(m.get("id") == id && m.get("season") == season_id){
                                        watched = true;
                                    }
                                }
                            }
                        }
                    }
                });
    }

    public int getId() {
        return id;
    }
    public String getImage(){
        try{
            String path = obj.getString("poster_path");
            return url + path;
        }catch (JSONException e){
            return "";
        }
    }
    public String getSeason(){
        try{
            String path = obj.getString("name");
            return path;
        }catch (JSONException e){
            return "";
        }
    }
    public int getEpisodesNumber(){
        try {
            return obj.getInt("episode_count");
        }catch (JSONException e){
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public boolean isWatched() {
        return watched;
    }
    public String getAirdate(){
        try{
            String path = obj.getString("air_date");
            return path;
        }catch (JSONException e){
            return "";
        }
    }
    public String getOverview(){
        try{
            String path = obj.getString("overview");
            return path;
        }catch (JSONException e){
            return "";
        }
    }


    public Season setWatched(boolean watched) {
        this.watched = watched;
        return this;
    }

    public int getSeasonNumber() {
        return season_id;
    }
}
