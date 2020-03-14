package com.example.tvguide;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

    }

    public void backPressed(View v) {
        onBackPressed();
    }

    public void initToolbar(String title){
        TextView t = findViewById(R.id.title);
        t.setText(title);
    }


}
