package com.example.tvguide;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tvguide.tmdb.Movie;
import com.example.tvguide.tmdb.Requests;
import com.squareup.picasso.Picasso;

public class MovieProfileActivity extends AppCompatActivity {
    private int id;
    private ImageView image;
    private TextView movie_name;
    private String media_type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_profile);
        Intent intent = getIntent();
        id = intent.getIntExtra("id", 0);
        media_type = intent.getStringExtra("media_type");
        image = findViewById(R.id.movie_backdrop);
        movie_name = findViewById(R.id.movie_name);
        Requests requests = new Requests(id, media_type);
        Movie v = requests.getMovieById();
        Picasso.get().
                load(v.getBackdrop_path()).
                into(image);
        movie_name.setText(v.getName());
    }

}
