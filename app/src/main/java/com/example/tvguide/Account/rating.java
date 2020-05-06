package com.example.tvguide.Account;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class rating {
    public ArrayList<Map<String, String>> arrayList = new ArrayList<>();

    public rating(){

    }
    public void setValue(String id, String rating, String media_type){
        Map<String, String> map = new HashMap<>();
        String type = "movie";
        if(media_type.equals("tv")) {
            type = "tv";
        }
        boolean flag = false;
        for (Map<String, String> m : arrayList){
            if(id.equals(m.get("id"))){
                m.put("rating", rating);
                flag = true;
            }
        }
        if(!flag) {
            map.put("id", id);
            map.put("rating", rating);
            map.put("type", type);
            arrayList.add(map);
        }
    }
}
