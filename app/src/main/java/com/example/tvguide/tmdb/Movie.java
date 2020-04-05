package com.example.tvguide.tmdb;

import android.icu.text.DateFormat;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.tvguide.MovieProfile.Track;
import com.example.tvguide.MovieProfile.Tracking;
import com.example.tvguide.YoutubeAPI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Movie implements Parcelable {
    public final String IMAGE_PATH = "https://image.tmdb.org/t/p/w500";
    private int id;
    private String name;
    private String overview;
    private String poster_path;
    private String backdrop_path;
    private String media_type;
    private JSONObject obj;
    private ArrayList<Cast> cast;
    private boolean watched = false;
    boolean toRet  = false;

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
    public Movie(Parcel dest){
        name = dest.readString();
        id = dest.readInt();
        media_type = dest.readString();
        Requests r = new Requests(id, media_type);
        obj = r.getJson();

    }
    public Movie(int id, String name, String overview, String poster_path, String backdrop_path, String media_type){
        this.id = id;
        this.name = name;
        this.overview = overview;
        this.poster_path = IMAGE_PATH + poster_path;
        this.backdrop_path = IMAGE_PATH + backdrop_path;
        this.media_type = media_type;
        Requests r = new Requests(id, media_type);
        obj = r.getJson();

    }
    public Movie(JSONObject obj) {
        try {
            this.obj = obj;
            id = Integer.parseInt(obj.getString("id"));
            overview = obj.getString("overview");
            poster_path = IMAGE_PATH + obj.getString("poster_path");
            backdrop_path = IMAGE_PATH + obj.getString("backdrop_path");
            switch (obj.getString("media_type")) {
                case "movie":
                    name = obj.getString("title");
                    media_type = "movie";
                    break;
                case "tv":
                    name = obj.getString("name");
                    media_type = "tv";
                    break;
            }
        }catch (JSONException e){
            media_type = "";
        }
    }
    public boolean markWatched(){

        final String mail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final ArrayList<HashMap<String, String>>  arrayList = new ArrayList<>();
        HashMap<String,String> map = new HashMap<>();
        try{
            if(media_type.equals("tv")) {
                JSONArray array = obj.getJSONArray("seasons");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject tmp = array.getJSONObject(i);
                    int episodes = tmp.getInt("episode_count");
                    for (int j = 1; j <= episodes; j++) {
                        map.put("season", tmp.getString("season_number"));
                        map.put("episode", String.valueOf(j));
                        arrayList.add(map);
                        map = new HashMap<>();
                    }
                }


                db.collection("Tracking")
                        .document(mail)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    Tracking t = task.getResult().toObject(Tracking.class);
                                    if (t == null) {
                                        t = new Tracking();
                                    }
                                    if (t.tracking == null) {
                                        t.tracking = new HashMap<>();
                                    }
                                    t.tracking.put(String.valueOf(id), arrayList);
                                    db.collection("Tracking")
                                            .document(mail)
                                            .set(t);
                                    watched = true;
                                }
                            }
                        });
            }else{
                db.collection("Tracking")
                        .document(mail)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()){
                                    Tracking t = task.getResult().toObject(Tracking.class);
                                    if (t == null) {
                                        t = new Tracking();
                                    }
                                    if (t.tracking == null) {
                                        t.tracking = new HashMap<>();
                                    }
                                    ArrayList<HashMap<String,String>> arrayList1 = new ArrayList<>();
                                    HashMap<String,String> map = new HashMap<>();
                                    map.put("season", "0");
                                    map.put("episode", "0");
                                    arrayList1.add(map);
                                    t.tracking.put(String.valueOf(id), arrayList1);
                                    db.collection("Tracking")
                                            .document(mail)
                                            .set(t);
                                    watched = true;

                                }
                            }
                        });
            }
        }catch (JSONException e){

        }
        return toRet;
    }
    public void markUnWatched(){
        final String mail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Tracking")
                .document(mail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            Tracking t = task.getResult().toObject(Tracking.class);
                            if(t==null){
                                t = new Tracking();

                            }
                            ArrayList<HashMap<String, String>> m = new ArrayList<>(t.tracking.get(String.valueOf(id)));
                            if(m != null) {
                                for (HashMap<String, String> map : m) {
                                    t.tracking.get(String.valueOf(id)).remove(map);
                                }
                            }
                            db.collection("Tracking")
                                    .document(mail)
                                    .set(t);
                            watched = false;
                        }
                    }
                });

    }

    public void setWatched(boolean watched) {
        this.watched = watched;
    }

    public boolean isWatched() {
        return watched;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getMedia_type() {
        return media_type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeInt(id);
    }

    public String getPoster_path() {
        return poster_path;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public String getOverview() {
        return overview;
    }

    public double getRating(){
        try{
            return obj.getDouble("vote_average");
        }catch (JSONException e){
            return 0;
        }
    }
    public int getEpisodesNumber(){
        try{
            return obj.getInt("number_of_episodes");
        }catch (JSONException e){
            return 0;
        }
    }
    public String getAirDates(){
        String release = "";
        try {
            String first = obj.getString("first_air_date");
            String last = obj.getString("last_air_date");
            if(obj.getString("status").equals("Returning Series"))
                return first.substring(0, 4) + " - returning";
            else
                if (first.substring(0, 4).equals(last.substring(0, 4)))
                    return first.substring(0, 4);
                else
                     return first.substring(0, 4) + " - " + last.substring(0, 4);
        }catch (JSONException e){
            System.out.println(e.getMessage());
            try{
                return obj.getString("release_date").substring(0, 4);
            }catch (JSONException e1){

            }
        }
        return "";

    }
    public ArrayList<String> getGenres(){
        JSONArray genres;
        ArrayList<String> toRet = new ArrayList<>();
        try{
            genres = (JSONArray)obj.get("genres");
            for(int i = 0; i < genres.length(); i++){
                toRet.add(((JSONObject)genres.get(i)).getString("name"));
            }

        }catch (JSONException e){
            System.out.println(e.getMessage());
        }
        return toRet;
    }
    public ArrayList<Season> getSeasons(){
        ArrayList<Season> seasons = new ArrayList<>();
        try {
            JSONArray array = (JSONArray)obj.get("seasons");
            int n = array.length();
            for(int i = 0; i < array.length(); i++) {
                seasons.add(new Season(id, (JSONObject) array.get(i), ((JSONObject) array.get(i)).getInt("season_number")));

            }
        }catch (JSONException e){
            System.out.println(e.getMessage());
        }
        return seasons;
    }
    public List<Episode> getEpisodes(Season season){

        Requests r = new Requests();
        return r.getEpisodes(season.getSeasonNumber(), this.id);
    }
    public ArrayList<String> getDetails(){
        ArrayList<String> array = new ArrayList<>();
        Requests r = new Requests();
        try {
            JSONObject obj = r.getDetails(id, media_type);
            if(media_type.equals("tv")){
                ArrayList<String> tmp = new ArrayList<>();
                array.add("Episode run time: " + obj.getString("episode_run_time"));
                array.add("First air date: " + obj.getString("first_air_date"));
                array.add("Last air date: " + obj.getString("last_air_date"));
                array.add("Number of seasons: " + obj.getString("number_of_seasons"));
                array.add("Status: " + obj.getString("status"));
            }
            else{
                String production = "";
                String countries = "";
                for(int i = 0; i < ((JSONArray)obj.get("production_companies")).length(); i++){
                    JSONObject o = (JSONObject)((JSONArray)obj.get("production_companies")).get(i);
                    production = o.getString("name") + ", " + production;
                }
                for(int i = 0; i < ((JSONArray)obj.get("production_countries")).length(); i++){
                    JSONObject o = (JSONObject)((JSONArray)obj.get("production_countries")).get(i);
                    countries = o.getString("name") + ", " + countries;
                }

                array.add("Status: " + obj.getString("status"));
                array.add("Language: " + obj.getString("original_language"));
                array.add("Runtime: " + createHours(obj.getString("runtime")) + " hours");
                array.add("Production companies: " + production);
                array.add("Production countries: " + countries);
                array.add("");
            }
        }catch (IOException e){
            System.out.println(e.getMessage());
        }catch(JSONException e1){
            System.out.println(e1.getMessage());
        }

        return array;

    }
    private String createHours(String minutes){
        int hours = Integer.valueOf(minutes);
        int tmp1 = hours/60;
        int tmp2 = hours % 60;
        return  String.format(Locale.ENGLISH, "%d:%02d", tmp1, tmp2);

    }
    public String getHomePage(){
        Requests r = new Requests();
        try {
            JSONObject o = r.getDetails(id, media_type);
            return o.getString("homepage");
        }catch (IOException w){
            return "";
        }
        catch (JSONException e){
            return "";
        }
    }
    public JSONObject getLastEpisode() throws JSONException{
        try {
            return ((JSONObject) obj.get("next_episode_to_air")).put("next", true);
        }catch (JSONException e){
            return ((JSONObject) obj.get("last_episode_to_air")).put("next", false);
        }catch (ClassCastException e2){
            return ((JSONObject) obj.get("last_episode_to_air")).put("next", false);
        }
    }
    public ArrayList<Movie> getSimilar(){
        Requests r = new Requests();
        return r.getSimilar(id, media_type);
    }
    public ArrayList<Cast> getCast(){
        Requests r = new Requests();
        cast = new ArrayList<>();
        cast = r.getCast(id, media_type);
        return cast;
    }
    public List<YoutubeAPI> getVideos(){
        Requests r = new Requests();
        return r.getVideos(id, media_type);
    }

    public List<String> getPosters(){
        Requests r = new Requests();
        return r.getImages(true, id, media_type);
    }

    public List<String> getBackdrops(){
        Requests r = new Requests();
        return r.getImages(false, id, media_type);
    }


}
