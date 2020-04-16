package com.example.tvguide.RecommenderSystem;

import android.os.AsyncTask;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.util.List;

public class Recommendations {


    private class Request extends AsyncTask<String, Void, Response> {
        Response response;
        @Override
        protected Response doInBackground(String... strings) {
            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/octet-stream");
            RequestBody body = RequestBody.create(mediaType, "{}");
            com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                    .url(strings[0])
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
    }/*
    public List<Integer> getPopularMovies(){
        String query = "http://127.0.0.1:5000/api/popular_movies";
        try {
            Response response = new Request().execute().get();
        }catch (Exception e){

        }

    }*/
}
