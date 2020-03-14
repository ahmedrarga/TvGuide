package com.example.tvguide.tmdb;

import org.json.JSONException;
import org.json.JSONObject;

public class Cast {
    private JSONObject obj;
    public final String IMAGE_PATH = "https://image.tmdb.org/t/p/w500";


    public Cast(JSONObject obj){
        this.obj = obj;
    }

    public String getName(){
        try{
            return obj.getString("name");
        }catch (JSONException e){
            return "";
        }
    }
    public String getProfileImage(){
        try{
            return IMAGE_PATH + obj.getString("profile_path");
        }catch (JSONException e){
            return "";
        }
    }

}
