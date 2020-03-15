package com.example.tvguide.tmdb;


import androidx.annotation.NonNull;

import com.example.tvguide.MovieProfile.Track;
import com.example.tvguide.MovieProfile.Tracking;
import com.example.tvguide.MovieProfile.TrackingAdapter;
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
        db.collection("Tracking")
                .document(m)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            Tracking t = task.getResult().toObject(Tracking.class);
                            if(t == null){
                                watched = false;
                            }else{
                                if(t.tracking == null){
                                    watched = false;
                                }else {
                                    if(t.tracking.get(String.valueOf(id)) == null){
                                        watched = false;
                                    }else{
                                        ArrayList<HashMap<String, String>> tmp = new ArrayList<>();
                                        for(HashMap<String, String> hashMap : t.tracking.get(String.valueOf(id))){
                                            if(hashMap.get("season").equals(String.valueOf(season_id))){
                                                tmp.add(hashMap);
                                            }
                                        }
                                        if(tmp.size() == getEpisodesNumber()){
                                            watched = true;
                                        }
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
