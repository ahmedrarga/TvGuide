package com.example.tvguide.RecommenderSystem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.Rating;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.tvguide.Account.RatingsActivity;
import com.example.tvguide.BaseActivity;
import com.example.tvguide.Account.rating;
import com.example.tvguide.HomePage.HomeActivity;
import com.example.tvguide.HomePage.PosterAdapter;
import com.example.tvguide.HomePage.SearchResultsRecyclerAdapter;
import com.example.tvguide.R;
import com.example.tvguide.tmdb.Movie;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class recommenderActivity extends BaseActivity {
    private RecyclerView recommendations;
    private ProgressBar bar;
    private String mail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommender);
        initToolbar("Recommendations");
        recommendations = findViewById(R.id.recommendations);
        bar = findViewById(R.id.recProg);
        bar.setVisibility(View.VISIBLE);
        recommendations.setVisibility(View.GONE);
        final Button btn = findViewById(R.id.add_ratings);
        btn.setVisibility(View.GONE);
        mail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        FirebaseFirestore.getInstance().collection("ratings")
                .document(mail)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        rating r = documentSnapshot.toObject(rating.class);
                        if(r != null && r.arrayList != null && r.arrayList.size() > 0){
                            ArrayList<Map<String,String>> tmp = new ArrayList<>();
                            for(Map<String,String> m : r.arrayList){
                                if(m.get("type").equals("movie")){
                                    tmp.add(m);
                                }
                            }
                            new task().execute(tmp);
                        } else{
                            btn.setVisibility(View.VISIBLE);
                            bar.setVisibility(View.GONE);
                            btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(getApplicationContext(), RatingsActivity.class);
                                    startActivity(intent);
                                }
                            });
                        }
                    }
                });
    }

    private class task extends AsyncTask<ArrayList<Map<String, String>>, Void, List<Movie>> {

        @Override
        protected List<Movie> doInBackground(ArrayList<Map<String, String>>... arrayLists) {
            Dictionary dict = new Dictionary(arrayLists[0]);
            List<Movie> movies = new ArrayList<>();
            String query = "https://recommender-sce.nw.r.appspot.com/api/" +
                    "recommendations?" +
                    "user=" + mail +
                    "&hist=" + dict.toString();
            Response response = setResponse(query);
            if(response != null && response.code() == 200){
                try{
                    JSONArray obj = new JSONArray(response.body().string());
                    System.out.println(obj);
                    for (int i = 0; i < obj.length(); i++){
                        String q = "https://api.themoviedb.org/3/movie/" +
                                String.valueOf(obj.getInt(i)) +
                                "?api_key=" + HomeActivity.api_key;
                        Response r = setResponse(q);
                        if(r != null && r.code() == 200){
                            movies.add(new Movie(new JSONObject(r.body().string()), "movie"));
                        }
                    }
                }catch(IOException e1){

                }catch (JSONException e2){
                    System.out.println(e2.getMessage() + "oerjfowergjnpweoirgjpeoirgjpeoirgjpeorgsoergjos;ergjopw;eirgewg ============================");
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
        protected void onPostExecute(List<Movie> movies) {
            super.onPostExecute(movies);
            recommendations.setAdapter(new SearchResultsRecyclerAdapter(movies, getApplicationContext(), "movie"));
            recommendations.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
            recommendations.setVisibility(View.VISIBLE);
            bar.setVisibility(View.GONE);
        }
    }
}
