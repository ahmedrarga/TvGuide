package com.example.tvguide.MovieProfile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TrackingEpisodes {
    public ArrayList<Map<String,Integer>> arrayList = new ArrayList<>();

    public void setValue(int id, int season, int episode) {
        Map<String, Integer> map = new HashMap<>();
        map.put("id", id);
        map.put("episode", episode);
        map.put("season", season);
        boolean flag = false;
        for (Map<String, Integer> m : arrayList){
            if(m.get("id") == id && m.get("season") == season && m.get("episode") == episode){
                flag = true;
            }
        }
        if(!flag)
            arrayList.add(map);
    }

}
