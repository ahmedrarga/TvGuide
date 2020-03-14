package com.example.tvguide.User;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.tvguide.BaseActivity;
import com.example.tvguide.R;

public class ProfileActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initToolbar("Profile");

    }
}
