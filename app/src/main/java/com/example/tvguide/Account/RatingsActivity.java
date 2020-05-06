package com.example.tvguide.Account;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.example.tvguide.HomePage.HomeActivity;
import com.example.tvguide.R;
import com.example.tvguide.tmdb.Movie;
import com.example.tvguide.tmdb.Requests;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.stepstone.stepper.StepperLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RatingsActivity extends AppCompatActivity implements StepFragmentSample.OnFragmentInteractionListener{
    private StepperLayout mStepperLayout;
    public static List<Movie> movies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ratings);
        Integer popular[] = {13,278,680,274,603,11,329,197,280,424,550,862,1891,629,14,807,602,568,85,
                120};
        new task().execute(popular);




    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
    public void skip(View v){
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
    private class task extends AsyncTask<Integer[], Void, List<Movie>> {

        @Override
        protected List<Movie> doInBackground(Integer[]... integers) {
            Integer popular[] = integers[0];
            List<Movie> movies = new ArrayList<>();
            for (int i = 0; i < popular.length; i++) {
                String query = "https://api.themoviedb.org/3/movie/" +
                        String.valueOf(popular[i]) +
                        "?api_key=" + HomeActivity.api_key;
                Response response = setResponse(query);
                if(response != null && response.code() == 200){
                    try {
                        movies.add(new Movie(new JSONObject(response.body().string()), "movie"));
                    }catch (IOException e1){

                    }catch (JSONException e2){

                    }
                }
            }
            return movies;
        }
        private Response setResponse(String query){
            Response response = null;
            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/octet-stream");
            RequestBody body = RequestBody.create(mediaType, "{}");
            Request request = new Request.Builder()
                    .url(query)
                    .get()
                    .build();

            try {
                response = client.newCall(request).execute();
                return response;

            } catch (Exception e) {
                System.out.println("Error in doInBackground");
                System.out.println("Error:" + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Movie> m) {
            super.onPostExecute(movies);
            movies = m;
            mStepperLayout =  findViewById(R.id.stepperLayout);
            mStepperLayout.setAdapter(new StepAdapter(getSupportFragmentManager(), getApplicationContext()));
            findViewById(R.id.load).setVisibility(View.GONE);
            findViewById(R.id.layout).setVisibility(View.VISIBLE);
            findViewById(R.id.message).setVisibility(View.GONE);

        }
    }
}
