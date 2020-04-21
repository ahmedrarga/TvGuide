package com.example.tvguide.User;

import com.example.tvguide.tmdb.Movie;

import java.util.List;

public interface OnFinished {
    void finished(List<Movie> movies);
}
