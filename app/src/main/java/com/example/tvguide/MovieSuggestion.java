package com.example.tvguide;

import android.os.Parcel;
import android.os.Parcelable;


public class MovieSuggestion{
    private String name;
    private int id;
    private String media_type;
    private boolean history = false;
    public static final Parcelable.Creator<MovieSuggestion> CREATOR = new Parcelable.Creator<MovieSuggestion>() {
        @Override
        public MovieSuggestion createFromParcel(Parcel in) {
            return new MovieSuggestion(in);
        }

        @Override
        public MovieSuggestion[] newArray(int size) {
            return new MovieSuggestion[size];
        }
    };

    public MovieSuggestion(String name, int id, String media_type){
        this.name = name;
        this.id = id;
        this.media_type = media_type;
    }
    public MovieSuggestion(Parcel source){
        name = source.readString();
        id = source.readInt();
        media_type = source.readString();
    }

    public int getId(){
        return id;
    }
    public String getMedia_type(){
        return media_type;
    }

}
