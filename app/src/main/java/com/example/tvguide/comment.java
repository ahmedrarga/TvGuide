package com.example.tvguide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class comment {
    public ArrayList<Map<String, Object>> arrayList = new ArrayList<>();

    public comment(){

    }
    public Map<String, Object> setValues(int s_id, int e_id, String comment, String user){
        if(arrayList == null){
            arrayList = new ArrayList<>();
        }
        Map<String,Object> map = new HashMap<>();
        map.put("season", s_id);
        map.put("episode", e_id);
        map.put("comment", comment);
        map.put("user", user);
        arrayList.add(map);
        return map;

    }
}