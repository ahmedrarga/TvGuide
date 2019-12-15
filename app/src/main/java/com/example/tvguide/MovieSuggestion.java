package com.example.tvguide;

import android.os.Parcel;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

public class MovieSuggestion implements SearchSuggestion {
    private String name;
    private int id;
    private String media_type;
    private boolean history = false;
    public static final Creator<MovieSuggestion> CREATOR = new Creator<MovieSuggestion>() {
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
    @Override
    public String getBody() {
        return name;
    }
    public int getId(){
        return id;
    }
    public String getMedia_type(){
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
        parcel.writeString(media_type);
        parcel.writeInt(history ? 1 : 0);
    }
}
