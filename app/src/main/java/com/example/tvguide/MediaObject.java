package com.example.tvguide;

public class MediaObject {
    String path;
    String user;

    public MediaObject(String path, String user){
        this.path = path;
        this.user = user;
    }

    public String getPath() {
        return path;
    }

    public String getUser() {
        return user;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setUser(String user) {
        this.user = user;
    }
}

