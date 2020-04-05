package com.example.tvguide.MovieProfile;

import com.example.tvguide.tmdb.Episode;
import com.example.tvguide.tmdb.Season;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Tracking {
    public HashMap<String, ArrayList<HashMap<String, String>>> tracking = new HashMap<>();

    public Tracking(){

    }

    public void setSeason(Season season, List<Episode> episodes){
        if(tracking == null){
            tracking = new HashMap<>();
        }
        ArrayList<HashMap<String,String>> movie = tracking.get(String.valueOf(season.getId()));
        if(movie == null){
            movie = new ArrayList<>();
        }
        HashMap<String, String> t = new HashMap<>();
        for(Episode e : episodes){
            if(!e.isWatched()) {
                t.put("season", String.valueOf(season.getSeasonNumber()));
                t.put("episode", String.valueOf(e.getEpisodeNumber()));
                e.setWatched(true);
                movie.add(t);
                t = new HashMap<>();
            }
        }
        season.setWatched(true);
        tracking.put(String.valueOf(season.getId()), movie);


    }
    public void setEpisode(int movie_id, int season, Episode e){
        if(tracking == null){
            tracking = new HashMap<>();
        }
        ArrayList<HashMap<String,String>> movie = tracking.get(String.valueOf(movie_id));
        if(movie == null){
            movie = new ArrayList<>();
        }
        HashMap<String, String> t = new HashMap<>();
        if(!e.isWatched()) {
            t.put("season", String.valueOf(season));
            t.put("episode", String.valueOf(e.getEpisodeNumber()));
            e.setWatched(true);
            movie.add(t);
            t = new HashMap<>();
        }
        e.setWatched(true);
        tracking.put(String.valueOf(movie_id), movie);
    }
    public void removeEpisode(int movie_id, int season, Episode e){
        HashMap<String, String> map = new HashMap<>();
        map.put("season", String.valueOf(season));
        map.put("episode", String.valueOf(e.getEpisodeNumber()));
        this.tracking.get(String.valueOf(movie_id)).remove(map);
        e.setWatched(false);

    }
    public void removeSeason(Season season, List<Episode> episodes){
        if(tracking == null){
            tracking = new HashMap<>();
        }

        HashMap<String, String> t = new HashMap<>();
        ArrayList<HashMap<String,String>> array = new ArrayList<>(tracking.get(String.valueOf(season.getId())));

        for(HashMap<String,String> m : array){
            if(m.get("season").equals(String.valueOf(season.getSeasonNumber()))){
                tracking.get(String.valueOf(season.getId())).remove(m);

            }
        }
        for(Episode e : episodes){
            e.setWatched(false);
        }
        season.setWatched(false);


    }

}
