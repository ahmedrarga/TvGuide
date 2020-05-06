package com.example.tvguide;

import java.util.ArrayList;

public class UserPost {
    public ArrayList<String> paths = new ArrayList<>();

    public UserPost(){

    }
    public void setValue(String path){
        paths.add(path);
    }
}
