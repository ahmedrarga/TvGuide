package com.example.tvguide.tmdb;

import androidx.annotation.NonNull;

import com.example.tvguide.MovieProfile.TrackingEpisodes;
import com.example.tvguide.MovieProfile.TrackingSeasons;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class Episode {
    private int id;
    private int s_id;
    private JSONObject obj;
    private static String IMAGE_PATH = "https://image.tmdb.org/t/p/w500";
    private boolean watched = false;
    private boolean isSeasonWatched = false;

    public Episode(int id, int s_id, JSONObject obj){
        this.id = id;
        this.s_id = s_id;
        this.obj = obj;
    }

    public String getImage(){
        try{
            return IMAGE_PATH + obj.getString("still_path");
        }catch (JSONException e){
            System.out.println(e.getMessage());
            return "";
        }
    }

    public String getEpisode(){
        try{
            return obj.getString("name");
        }catch (JSONException e){
            System.out.println(e.getMessage());
            return "";
        }
    }
    public int getEpisodeNumber(){
        try{
            return obj.getInt("episode_number");
        }catch (JSONException e){
            System.out.println(e.getMessage());
            return 0;
        }
    }
    public String getAirDate(){
        try{
            return obj.getString("air_date");
        }catch (JSONException e){
            System.out.println(e.getMessage());
            return "";
        }
    }
    public String getRating(){
        try{
            return obj.getString("vote_average");
        }catch (JSONException e){
            System.out.println(e.getMessage());
            return "";
        }
    }
    public String getOverview(){
        try{
            return obj.getString("overview");
        }catch (JSONException e){
            System.out.println(e.getMessage());
            return "";
        }
    }

    public boolean isWatched() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String m = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        db.collection("trackingEpisodes")
                .document(m)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            TrackingEpisodes t = task.getResult().toObject(TrackingEpisodes.class);
                            if(t == null){
                                watched = false;
                            }
                            else{
                                for(Map<String, Integer> m : t.arrayList){
                                    System.out.println(m);
                                    if(m.get("id") == id && m.get("season") == s_id && m.get("episode") == getEpisodeNumber() ||
                                         isSeasonWatched()){
                                        watched = true;
                                    }
                                }
                            }
                        }
                    }
                });
        return watched;

    }
    boolean isSeasonWatched(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("trackingSeasons")
                .document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            TrackingSeasons t = task.getResult().toObject(TrackingSeasons.class);
                            if(t == null){
                                isSeasonWatched = false;
                            }else{
                                for(Map<String, Integer> m : t.arrayList){
                                    if(m.get("id") == id && m.get("season") == s_id){
                                        isSeasonWatched = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                });
        return isSeasonWatched;
    }

    public int getId() {
        return id;
    }

    public int getS_id() {
        return s_id;
    }

    public void setWatched(boolean watched) {
        this.watched = watched;
    }
}
