package com.example.tvguide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Post {
    public ArrayList<Map<String, String>> arrayList = new ArrayList<>();

    public Post(){

    }
    public void setValue(String user, String path, String title){
        Map<String, String> map = new HashMap<>();
        map.put("path", path);
        map.put("user", user);
        map.put("title", title);
        arrayList.add(map);
    }

}
