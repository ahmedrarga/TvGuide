package com.example.tvguide.Account;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class rating {
    public ArrayList<Map<String, String>> arrayList = new ArrayList<>();

    public rating(){

    }
    public void setValue(String id, String rating){
        Map<String, String> map = new HashMap<>();
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
            arrayList.add(map);
        }
    }
}
