package com.example.tvguide.tmdb;

import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.tvguide.HttpRequest;
import com.example.tvguide.MovieSuggestion;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Requests {
    private final String api_key = "f98d888dd7ebd466329c6a26f1018a55";
    private String url;
    private Response response;
    private String media_type = "movie";
    private int id;
    private List <Movie> movies = new ArrayList<>();

    public Requests(int id, String media_type){
        this.media_type = media_type;
        this.id = id;
        url = "https://api.themoviedb.org/3/" + media_type + "/" +
                String.valueOf(id) +
                "?api_key=" + api_key;
        try{
            response = new HttpRequest().execute(url).get();
        } catch(ExecutionException e1){
            System.out.println("Error: in Requests class" + e1.getMessage());
        } catch (InterruptedException e2){
            System.out.println("Error: in Requests class" + e2.getMessage());
        }
    }
    public Requests(String query){
         url = "https://api.themoviedb.org/3/search/multi?" +
                 "api_key=" + api_key +
                 "&query=" + query +
                 "&language=en-US";
         try{
             response = new HttpRequest().execute(url).get();
         } catch(ExecutionException e1){
             System.out.println("Error: in Requests class" + e1.getMessage());
         } catch (InterruptedException e2){
             System.out.println("Error: in Requests class" + e2.getMessage());
         }
     }
     public Requests(){
     }
    public List<Movie> getMoviesFromHashMap(ArrayList<Map<String, Object>> map){
        for(int i = 0; i < map.size(); i++){
            long id = (long)map.get(i).get("id");
            String media_type = (String)map.get(i).get("media_type");
            Requests r = new Requests((int)id, media_type);
            movies.add(r.getMovieById());
            System.out.println(id);
        }
        return movies;
    }
    private JSONArray getResults(){
        /* TO-DO
        * include page searching result
        * */
         if(response == null){
            return null;
        }
        JSONArray array;
        try {
            array = (JSONArray) (new JSONObject(response.body().string()).get("results"));
        } catch (IOException e){
            System.out.println("Error in getResults" + e.getMessage());
            return null;
        }catch (JSONException e){
            System.out.println("Error in getResults" + e.getMessage());
            return null;
        }
        return array;
    }
    public List<Movie> getMovies(){
        List<Movie> list = new ArrayList<>();
        if(response == null){
            return null;
        }
        int page = 1;
        JSONArray array = getResults();
        try {
            for (int i = 0; i < array.length(); i++) {
                try {
                    System.out.println(((JSONObject)array.get(i)).getString("poster_path"));
                    list.add(new Movie((JSONObject) array.get(i)));
                } catch (JSONException e) {
                    System.out.println("Error in getMovies " + e.getMessage());
                }
            }
        } catch (NullPointerException e) {
            return list;
        }
        return list;
    }
    public Movie getMovieById() {
        Movie v = null;
        JSONObject obj = getResultsById();
        if (obj == null) {
            return v;
        } else {
            try {
                if (media_type.equals("tv")) {
                    v = new Movie(id, obj.getString("name"),
                            obj.getString("overview"),
                            obj.getString("poster_path"),
                            obj.getString("backdrop_path"),
                            media_type);
                } else {
                    v = new Movie(id, obj.getString("original_title"),
                            obj.getString("overview"),
                            obj.getString("poster_path"),
                            obj.getString("backdrop_path"),
                            media_type);
                }
            } catch (JSONException e) {

            }

        }
        return v;
    }
    private JSONObject getResultsById(){
        JSONObject obj = null;
        try{
            obj = new JSONObject(response.body().string());
        } catch (IOException e1){

        }catch (JSONException e2){

        }
        return obj;
    }
    public List<Movie> discoverMovies(){
        String query = "https://api.themoviedb.org/3/discover/movie?" +
                "api_key=" + api_key +
                "&language=en-US&sort_by=popularity.desc";
        try {
            response = new HttpRequest().execute(query).get();
        }catch (InterruptedException e1){
            System.out.println(e1.getMessage());
            return null;
        }catch (ExecutionException e2){
            System.out.println(e2.getMessage());
            return null;
        }
        return getMovies();
    }
    public List<Movie> discoverShows(){
        String query = "https://api.themoviedb.org/3/discover/tv?" +
                "api_key=" + api_key +
                "&language=en-US&sort_by=popularity.desc&include_adult=true";
        try {
            response = new HttpRequest().execute(query).get();
        }catch (InterruptedException e1){
            System.out.println(e1.getMessage());
            return null;
        }catch (ExecutionException e2){
            System.out.println(e2.getMessage());
            return null;
        }
        return getMovies();
    }
    public List<Movie> getNowPlaying(){
        String query = "https://api.themoviedb.org/3/movie/" +
                "now_playing?api_key=" + api_key +
                "&language=en-US&page=1";
        try {
            response = new HttpRequest().execute(query).get();
        }catch (InterruptedException e1){
            System.out.println(e1.getMessage());
            return null;
        }catch (ExecutionException e2){
            System.out.println(e2.getMessage());
            return null;
        }
        return getMovies();


    }
    public List<Movie> getUpcoming(){
        String query = "https://api.themoviedb.org/3/movie/upcoming?" +
                "api_key=" + api_key +
                "&language=en-US&page=1";
        setResponse(query);
        return getMovies();

    }
    public List<Movie> getpopularMovies(){
        String query = "https://api.themoviedb.org/3/movie/popular?" +
                "api_key=" + api_key +
                "&language=en-US&page=1";
        setResponse(query);
        return getMovies();
    }
    public List<Movie> getpopularShows(){
        String query = "https://api.themoviedb.org/3/tv/popular?" +
                "api_key=" + api_key +
                "&language=en-US&page=1";
        setResponse(query);
        return getMovies();
    }
    public List<Movie> getTopRatedMovies(){
        String query = "https://api.themoviedb.org/3/movie/top_rated?" +
                "api_key=" + api_key +
                "&language=en-US&page=1";
        setResponse(query);
        return getMovies();
    }
    public List<Movie> getTopRatedShows(){
        String query = "https://api.themoviedb.org/3/tv/top_rated?" +
                "api_key=" + api_key +
                "&language=en-US&page=1";
        setResponse(query);
        return getMovies();
    }


    private void setResponse(String query){
        try {
            response = new HttpRequest().execute(query).get();
        }catch (InterruptedException e1){
            System.out.println(e1.getMessage());
        }catch (ExecutionException e2){
            System.out.println(e2.getMessage());
        }
    }

}
