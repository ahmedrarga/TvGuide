package com.example.tvguide;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class EpisodeActivity extends BaseActivity {

    private int id;
    private String season;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episode);
        Intent intent = getIntent();
        season = intent.getStringExtra("title");
        id = intent.getIntExtra("id", 0);
        initToolbar(intent.getStringExtra("title"));
    }
}
