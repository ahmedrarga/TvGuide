package com.example.tvguide;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.tvguide.MovieProfile.ImagesAdapter;

import java.util.ArrayList;

public class GalleryActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        initToolbar("Gallery");
        Intent intent = getIntent();
        ArrayList<String> arrayList = intent.getStringArrayListExtra("array");
        if(arrayList != null){
            RecyclerView view = findViewById(R.id.images);
            view.setAdapter(new ImagesAdapter(arrayList, getApplicationContext()));
            view.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        }
    }
}
