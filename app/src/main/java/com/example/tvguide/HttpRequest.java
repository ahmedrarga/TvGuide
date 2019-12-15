package com.example.tvguide;

import android.os.AsyncTask;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class HttpRequest extends AsyncTask<String, Void, Response> {

    private Response response;

    @Override
    protected Response doInBackground(String... strings) {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/octet-stream");
        RequestBody body = RequestBody.create(mediaType, "{}");
        Request request = new Request.Builder()
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

    @Override
    protected void onPostExecute(Response response) {
        super.onPostExecute(response);
    }

}
