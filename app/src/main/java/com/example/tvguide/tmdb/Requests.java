package com.example.tvguide.tmdb;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.example.tvguide.HttpRequest;
import com.example.tvguide.MovieSuggestion;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Requests {
    private final String api_key = "f98d888dd7ebd466329c6a26f1018a55";
    private String url;
    private Response response;
    private String media_type = "movie";
    private int id;

    public Requests(String query, String media_type){
        this.media_type = media_type;
        this.id = id;
        url = "https://api.themoviedb.org/3/" + media_type + "/" +
                query +
                "?api_key=" + api_key;
        try {
            response = new HttpRequest().execute(url).get();
        } catch(ExecutionException e1){
            System.out.println("Error: in Requests class" + e1.getMessage());
        }catch (InterruptedException e2){
            System.out.println("Error: in Requests class" + e2.getMessage());
        }
    }
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


    private JSONArray getResults(int page){
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
        JSONArray array = getResults(page);
        for (int i = 0; i < array.length(); i++){
            try {
                list.add(new Movie((JSONObject) array.get(i)));
            }catch (JSONException e){
                System.out.println("Error in getMovies" + e.getMessage());
                break;
            }
        }
        return list;
    }
    public List<SearchSuggestion> getSuggestions(){
        List<SearchSuggestion> list = new ArrayList<>();
        if(response == null){
            return null;
        }
        int page = 1;
        JSONArray array = getResults(page);
        for (int i = 0; i <  min(array.length(), 7); i++){
            try {
                int id;
                String media_type;
                switch (((JSONObject)array.get(i)).getString("media_type")){
                    case "movie":
                        id = Integer.parseInt(((JSONObject) array.get(i)).getString("id"));
                        media_type = ((JSONObject) array.get(i)).getString("media_type");
                        list.add(new MovieSuggestion((((JSONObject) array.get(i)).getString("original_title")), id, media_type));
                        break;
                    case "tv":
                        media_type = ((JSONObject) array.get(i)).getString("media_type");
                        id = Integer.parseInt(((JSONObject) array.get(i)).getString("id"));
                        list.add(new MovieSuggestion((((JSONObject) array.get(i)).getString("name")), id, media_type));
                        break;

                }
            }catch (JSONException e){
                System.out.println("Error in getMovies" + e.getMessage());
                break;
            }
        }
        return list;
    }
    private int min(int a, int b){
        if (a < b){
            return a;
        }
        return b;
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
                            null);
                } else {
                    v = new Movie(id, obj.getString("original_title"),
                            obj.getString("overview"),
                            obj.getString("poster_path"),
                            obj.getString("backdrop_path"),
                            null);
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
}
