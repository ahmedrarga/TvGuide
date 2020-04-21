package com.example.tvguide.HomePage;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tvguide.R;
import com.example.tvguide.tmdb.HttpRequest;
import com.example.tvguide.tmdb.Movie;
import com.example.tvguide.tmdb.Requests;
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
import java.util.List;
import java.util.concurrent.ExecutionException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link News.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link News#newInstance} factory method to
 * create an instance of this fragment.
 */
public class News extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final String api_key = "f98d888dd7ebd466329c6a26f1018a55";
    View view;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView NowPlaying;
    private RecyclerView popularM;
    private RecyclerView popularS;
    private RecyclerView topRatedM;
    private RecyclerView topRatedS;
    private RecyclerView Upcoming;
    private RecyclerView trending;
    private RecyclerView trendingS;
    public static String media_type = "";
    private OnFragmentInteractionListener mListener;

    public News() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment News.
     */
    // TODO: Rename and change types and number of parameters
    public static News newInstance(String param1, String param2) {
        News fragment = new News();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_news, container, false);
        view = root;
        root.findViewById(R.id.news).setVisibility(View.GONE);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                trending = root.findViewById(R.id.trendingMovies);
                trendingS = root.findViewById(R.id.trendingShows);
                NowPlaying = root.findViewById(R.id.nowPlayingRView);
                Upcoming = root.findViewById(R.id.upcoming);
                popularM = root.findViewById(R.id.popularMovies);
                popularS = root.findViewById(R.id.popularShows);
                topRatedM = root.findViewById(R.id.topRatedMovies);
                topRatedS = root.findViewById(R.id.topRatedShows);
                /*initRList(trending, r.getTrendingMovies(), "movie");
                initRList(trendingS, r.getTrendingShows(), "show");
                initRList(nowPlaying, r.getNowPlaying(), "movie");
                initRList(Upcoming, r.getUpcoming(), "movie");
                initRList(popularM, r.getpopularMovies(), "movie");
                initRList(popularS, r.getpopularShows(), "show");
                initRList(topRatedM, r.getTopRatedMovies(), "movie");
                initRList(topRatedS, r.getTopRatedShows(), "show");*/
                new task().execute("upcoming");
                new task().execute("nowPlaying");
                new task().execute("trendingMovies");
                new task().execute("trendingShows");
                new task().execute("popualrMovies");
                new task().execute("popularShows");
                new task().execute("topRatedMovies");
                new task().execute("topRatedShows");


            }
        });




        // Inflate the layout for this fragment
        return root;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    private void initRList(RecyclerView r, List<Movie> movies, String cl){
        r.setAdapter(new PosterAdapter(movies, getContext(), cl));
        r.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public class task extends AsyncTask<String, Void, List<Object>> {
        String trendingMovies = "https://api.themoviedb.org/3/trending/movie/day?" +
                "api_key=" + api_key;
        String trendingShows = "https://api.themoviedb.org/3/trending/tv/day?" +
                "api_key=" + api_key;
        String popularMovies = "https://api.themoviedb.org/3/movie/popular?" +
                        "api_key=" + api_key +
                        "&language=en-US&page=1";
        String popularShows = "https://api.themoviedb.org/3/tv/popular?" +
                "api_key=" + api_key +
                "&language=en-US&page=1";
        String topRatedMovies ="https://api.themoviedb.org/3/movie/top_rated?" +
                        "api_key=" + api_key +
                        "&language=en-US&page=1";
        String topRatedShows = "https://api.themoviedb.org/3/tv/top_rated?" +
                "api_key=" + api_key +
                "&language=en-US&page=1";
        String upcoming = "https://api.themoviedb.org/3/movie/upcoming?" +
                "api_key=" + api_key +
                "&language=en-US&page=1";
        String nowPlaying = "https://api.themoviedb.org/3/movie/" +
                "now_playing?api_key=" + api_key +
                "&language=en-US&page=1";

        @Override
        protected List<Object> doInBackground(String... strings) {
            List<Movie> toRet = new ArrayList<>();
            List<Object> obj = new ArrayList<>();
            Response response = null;
            if(strings[0].equals("trendingMovies")){
                response = setResponse(trendingMovies);
            } else if(strings[0].equals("trendingShows")){
                response = setResponse(trendingShows);
            } else if(strings[0].equals("upcoming")){
                response = setResponse(upcoming);

            } else if(strings[0].equals("nowPlaying")){
                response = setResponse(nowPlaying);

            } else if(strings[0].equals("popualrMovies")){
                response = setResponse(popularMovies);

            } else if(strings[0].equals("popularShows")){
                response = setResponse(popularShows);

            } else if(strings[0].equals("topRatedMovies")){
                response = setResponse(topRatedMovies);

            } else if(strings[0].equals("topRatedShows")){
                response = setResponse(topRatedShows);
            }
            String media_type = "movie";
            if(strings[0].contains("Shows")){
                media_type = "tv";
            }
            if(response != null){
                try{
                    JSONArray array = new JSONObject(response.body().string()).getJSONArray("results");
                    for (int i = 0; i < array.length(); i++){
                        toRet.add(new Movie(array.getJSONObject(i), media_type));
                    }
                    obj.add((Object)toRet);
                    obj.add(strings[0]);
                }catch (IOException e1){

                }catch (JSONException e2){

                }
            }
            return obj;

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
        protected void onProgressUpdate(Void... values) {
            view.findViewById(R.id.news).setVisibility(View.GONE);
            view.findViewById(R.id.progressBar3).setVisibility(View.VISIBLE);
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(List<Object> obj) {
            super.onPostExecute(obj);
            if(obj.size() == 2){
                if(((String)obj.get(1)).equals("trendingMovies")){
                    initRList(trending, (ArrayList)obj.get(0), "movie");
                } else if(((String)obj.get(1)).equals("trendingShows")){
                    initRList(trendingS, (ArrayList)obj.get(0), "show");
                } else if(((String)obj.get(1)).equals("upcoming")){
                    initRList(Upcoming, (ArrayList)obj.get(0), "movie");

                } else if(((String)obj.get(1)).equals("nowPlaying")){
                    initRList(NowPlaying, (ArrayList)obj.get(0), "movie");

                } else if(((String)obj.get(1)).equals("popualrMovies")){
                    initRList(popularM, (ArrayList)obj.get(0), "movie");

                } else if(((String)obj.get(1)).equals("popularShows")){
                    initRList(popularS, (ArrayList)obj.get(0), "show");

                } else if(((String)obj.get(1)).equals("topRatedMovies")){
                    initRList(topRatedM, (ArrayList)obj.get(0), "movie");

                } else if(((String)obj.get(1)).equals("topRatedShows")){
                    initRList(topRatedS, (ArrayList)obj.get(0), "show");
                }
                view.findViewById(R.id.news).setVisibility(View.VISIBLE);
                view.findViewById(R.id.progressBar3).setVisibility(View.GONE);
            }

        }
    }
}
