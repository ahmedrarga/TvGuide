package com.example.tvguide.tmdb;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class Movie implements Parcelable {
    private final String IMAGE_PATH = "https://image.tmdb.org/t/p/w500";
    private int id;
    private String name;
    private String overview;
    private String poster_path;
    private String backdrop_path;
    private String media_type;

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
    public Movie(Parcel dest){
        name = dest.readString();
        id = dest.readInt();
        media_type = dest.readString();
    }
    public Movie(int id, String name, String overview, String poster_path, String backdrop_path, String media_type){
        this.id = id;
        this.name = name;
        this.overview = overview;
        this.poster_path = IMAGE_PATH + poster_path;
        this.backdrop_path = IMAGE_PATH + backdrop_path;
        this.media_type = media_type;
    }
    public Movie(JSONObject obj) throws JSONException {
        id = Integer.parseInt(obj.getString("id"));
        overview = obj.getString("overview");
        poster_path = IMAGE_PATH +  obj.getString("poster_path");
        backdrop_path = IMAGE_PATH + obj.getString("backdrop_path");
        switch (obj.getString("media_type")){
            case "movie":
                name = obj.getString("original_title");
                media_type = "movie";
                break;
            case "tv":
                name = obj.getString("name");
                media_type = "tv";
                break;
        }
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getMedia_type() {
        return media_type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeInt(id);
    }

    public String getPoster_path() {
        return poster_path;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }
}
