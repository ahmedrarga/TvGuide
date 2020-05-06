package com.example.tvguide.RecommenderSystem;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Dictionary {
    private ArrayList<Map<String, String>> dict;

    public Dictionary(ArrayList<Map<String, String>> dict) {
        this.dict = dict;
    }

    @NonNull
    @Override
    public String toString() {
        String toRet = "{";
        int i = 0;
        for (Map<String, String> m : dict) {
            if (i < dict.size() - 1) {
                toRet += m.get("id") + ":" + m.get("rating") + ",";
            } else {
                toRet += m.get("id") + ":" + m.get("rating");
            }
            i++;
        }
        toRet += "}";
        return toRet;
    }
}
