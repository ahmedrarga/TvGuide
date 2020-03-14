package com.example.tvguide;

import org.json.JSONException;
import org.json.JSONObject;

public class YoutubeAPI {
    public static String api_key = "AIzaSyBq-5NSsOe06XANJ1enIZFewlMAdo5reXQ";
    private JSONObject obj;
    private static String youtube = "https://www.youtube.com/watch?v=";

    public YoutubeAPI(JSONObject video){
        obj = video;
    }

    public String getId() {
        try {
            return obj.getString("key");
        }catch (JSONException e){
            System.out.println(e.getMessage());
            return "";
        }

    }

    public String getTitle(){
        try {
            return obj.getString("name");
        }catch (JSONException e){
            System.out.println(e.getMessage());
            return "";
        }
    }
}
