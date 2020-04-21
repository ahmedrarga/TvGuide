package com.example.tvguide.tmdb;

import android.app.PendingIntent;
import android.os.Handler;
import android.os.Looper;

import com.example.tvguide.User.OnFinished;
import com.example.tvguide.YoutubeAPI;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
    public List<Movie> getMoviesFromHashMap(ArrayList<Map<String, Object>> map, final OnFinished finished){
        for(int i = 0; i < map.size(); i++){
            final long id = (long)map.get(i).get("id");
            final String media_type = (String)map.get(i).get("media_type");
            Requests r = new Requests((int)id, media_type);
            movies.add(r.getMovieById());
            System.out.println(id);


        }
        finished.finished(movies);
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
    public List<Movie> getMovies(String media_type){
        List<Movie> list = new ArrayList<>();
        if(response == null){
            return null;
        }
        int page = 1;
        JSONArray array = getResults();
        try {
            for (int i = 0; i < array.length(); i++) {
                try {
                    list.add(new Movie((JSONObject) array.get(i), media_type));
                } catch (JSONException e) {
                    System.out.println("Error in getMovies " + e.getMessage());
                }
            }
        } catch (NullPointerException e) {
            return list;
        }
        return list;
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
    public JSONObject getJson() {
        JSONObject toRet;
        try{
            toRet = new JSONObject(response.body().string());
            return toRet;
        }catch (IOException e){
            return null;
        }catch (JSONException e1){
            return null;
        }
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
                    v = new Movie(id, obj.getString("title"),
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
        return getMovies("movie");
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
        return getMovies("movie");
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
        return getMovies("movie");


    }
    public List<Movie> getUpcoming(){
        String query = "https://api.themoviedb.org/3/movie/upcoming?" +
                "api_key=" + api_key +
                "&language=en-US&page=1";
        setResponse(query);
        return getMovies("movie");

    }
    public List<Movie> getpopularMovies(){
        String query = "https://api.themoviedb.org/3/movie/popular?" +
                "api_key=" + api_key +
                "&language=en-US&page=1";
        setResponse(query);
        return getMovies("movie");
    }
    public List<Movie> getpopularShows(){
        String query = "https://api.themoviedb.org/3/tv/popular?" +
                "api_key=" + api_key +
                "&language=en-US&page=1";
        setResponse(query);
        return getMovies("tv");
    }
    public List<Movie> getTopRatedMovies(){
        String query = "https://api.themoviedb.org/3/movie/top_rated?" +
                "api_key=" + api_key +
                "&language=en-US&page=1";
        setResponse(query);
        return getMovies("tv");
    }
    public List<Movie> getTopRatedShows(){
        String query = "https://api.themoviedb.org/3/tv/top_rated?" +
                "api_key=" + api_key +
                "&language=en-US&page=1";
        setResponse(query);
        return getMovies("tv");
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

    public ArrayList<String> getImages(boolean poster, int id, String media_type){
        String query = "";
        if(media_type.equals("tv"))
            query = "https://api.themoviedb.org/3/tv/" +
                    id + "/images?" +
                    "api_key=" + api_key;
        else
            query = "https://api.themoviedb.org/3/movie/" +
                    id +
                    "/images?api_key=" + api_key;
        String path = "https://image.tmdb.org/t/p/w500";
        setResponse(query);
        ArrayList<String> list = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(response.body().string());
            if(!poster) {
                JSONArray backdrops = obj.getJSONArray("backdrops");
                for (int i = 0; i < backdrops.length(); i++) {
                    list.add(path + (backdrops.getJSONObject(i).getString("file_path")));
                }
            }
            if(poster) {
                JSONArray posters = obj.getJSONArray("posters");
                for (int i = 0; i < posters.length(); i++) {
                    list.add(path + (posters.getJSONObject(i).getString("file_path")));
                }
            }

        }catch (JSONException e){
            System.out.println(e.getMessage());
        }catch (IOException e1){
            System.out.println(e1.getMessage());
        }
        return list;
    }

    public List<Episode> getEpisodes(int season_id, int movie_id){
        List<Episode> episodes = new ArrayList<>();
        String query = "https://api.themoviedb.org/3/tv/" +
                movie_id +
                "/season/" + season_id +
                "?api_key=f98d888dd7ebd466329c6a26f1018a55" +
                "&language=en-US";
        setResponse(query);
        try {
            JSONArray array = (JSONArray) new JSONObject(response.body().string()).get("episodes");
            for (int i = 0; i < array.length(); i++){
                episodes.add(new Episode(movie_id, season_id, (JSONObject)array.get(i)));
            }

        }catch (JSONException e){
            System.out.println(e.getMessage());
        } catch (IOException e1){
            System.out.println(e1.getMessage());
        }
        return episodes;
    }
    public JSONObject getDetails(int id, String media_type) throws IOException, JSONException{
        String query = "";
        if(media_type.equals("tv"))
            query = "https://api.themoviedb.org/3/" +
                    "tv" +
                    "/" + id +
                    "?api_key=" + api_key +
                    "&language=en-US";
        else
            query = "https://api.themoviedb.org/3/" +
                    "movie" +
                    "/" + id +
                    "?api_key=" + api_key +
                    "&language=en-US";

        setResponse(query);

        return new JSONObject(response.body().string());


    }
    public ArrayList<Movie> getSimilar(int id, String media_type){
        String query = "";
        if(media_type.equals("tv")) {
            query = "https://api.themoviedb.org/3/tv/" +
                    id +
                    "/similar?api_key=" + api_key +
                    "&language=en-US&page=1";
        }else{
            query = "https://api.themoviedb.org/3/movie/" +
                    id +
                    "/similar?api_key=" + api_key +
                    "&language=en-US&page=1";
        }
        setResponse(query);
        ArrayList<Movie> movies = new ArrayList<>();
        try{
            JSONObject obj = new JSONObject(response.body().string());
            JSONArray array = ((JSONArray)obj.get("results"));
            for(int i = 0; i < array.length(); i++){
                JSONObject tmp = (JSONObject)array.getJSONObject(i);
                movies.add(new Movie(tmp, media_type));
            }
        }catch (IOException e1){

        }catch (JSONException e2){

        }
        return movies;
    }
    public ArrayList<Cast> getCast(int id, String media_type){
        ArrayList<Cast> cast = new ArrayList<>();
        String query = "";
        if(media_type.equals("movie")){
            query = "https://api.themoviedb.org/3/movie/" +
                    id +
                    "/credits?" +
                    "api_key=" + api_key + "&language=en-US";
        }else{
            query = "https://api.themoviedb.org/3/tv/" +
                    id +
                    "/credits?api_key=" + api_key + "&language=en-US";
        }
        setResponse(query);
        try{
            JSONObject object = new JSONObject(response.body().string());
            JSONArray array = object.getJSONArray("cast");
            for (int i = 0; i < array.length(); i++){
                cast.add(new Cast(array.getJSONObject(i)));
            }

        }catch (IOException e1){

        }catch (JSONException e2){

        }
        return cast;
    }
    public List<YoutubeAPI> getVideos(int  id, String media_type){
        String query = "";
        if(media_type.equals("tv")){
            query = "https://api.themoviedb.org/3/tv/" +
                    id +
                    "/videos?" +
                    "api_key=" + api_key +
                    "&language=en-US";
        }else{
            query = "https://api.themoviedb.org/3/movie/" +
                    id +
                    "/videos?api_key=" + api_key +
                    "&language=en-US";
        }
        setResponse(query);
        List<YoutubeAPI> toRet = new ArrayList<>();
        try {
            JSONArray array = new JSONObject(response.body().string()).getJSONArray("results");
            for (int i = 0; i < array.length(); i++){
                toRet.add(new YoutubeAPI(array.getJSONObject(i)));
            }
        }catch (IOException e1){
            System.out.println(e1.getMessage());
        }catch (JSONException e2){
            System.out.println(e2.getMessage());
        }
        return toRet;
    }
    public List<Movie> getTrendingMovies(){
        List<Movie> toRet = new ArrayList<>();
        String query = "https://api.themoviedb.org/3/trending/movie/day?" +
                "api_key=" + api_key;

        setResponse(query);
        try{
            JSONArray array = new JSONObject(response.body().string()).getJSONArray("results");
            for (int i = 0; i < array.length(); i++){
                toRet.add(new Movie(array.getJSONObject(i)));
            }
        }catch (IOException e1){

        }catch (JSONException e2){

        }
        return toRet;
    }
    public List<Movie> getTrendingShows(){
        List<Movie> toRet = new ArrayList<>();
        String query = "https://api.themoviedb.org/3/trending/tv/day?" +
                "api_key=" + api_key;

        setResponse(query);
        try{
            JSONArray array = new JSONObject(response.body().string()).getJSONArray("results");
            for (int i = 0; i < array.length(); i++){
                toRet.add(new Movie(array.getJSONObject(i)));
            }
        }catch (IOException e1){

        }catch (JSONException e2){

        }
        return toRet;
    }

}
