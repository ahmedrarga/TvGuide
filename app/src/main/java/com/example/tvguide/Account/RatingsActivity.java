package com.example.tvguide.Account;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.example.tvguide.HomePage.HomeActivity;
import com.example.tvguide.R;
import com.example.tvguide.tmdb.Movie;
import com.example.tvguide.tmdb.Requests;
import com.stepstone.stepper.StepperLayout;

import java.util.ArrayList;
import java.util.List;

public class RatingsActivity extends AppCompatActivity implements StepFragmentSample.OnFragmentInteractionListener{
    private StepperLayout mStepperLayout;
    public static List<Movie> movies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ratings);
        mStepperLayout = (StepperLayout) findViewById(R.id.stepperLayout);
        mStepperLayout.setAdapter(new StepAdapter(getSupportFragmentManager(), this));

        int popular[] = {13,278,680,274,603,11,329,197,280,424,550,862,1891,629,14,807,602,568,85,
                120,1892,238,5503,268,121,857,122,812,275,745,36955,63,8587,105,1637,98,808,607,581,
                954,3049,77,854,155,22,348,10020,562,1572,788};
        movies = new ArrayList<>();
        for (int i = 0; i < popular.length; i++) {
            Requests r = new Requests(popular[i], "movie");
            movies.add(r.getMovieById());

        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
    public void skip(View v){
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
