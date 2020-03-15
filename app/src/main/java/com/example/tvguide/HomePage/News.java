package com.example.tvguide.HomePage;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
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
import com.example.tvguide.tmdb.Movie;
import com.example.tvguide.tmdb.Requests;

import java.util.List;


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

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView nowPlaying;
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
        root.findViewById(R.id.news).setVisibility(View.GONE);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Requests r = new Requests();
                trending = root.findViewById(R.id.trendingMovies);
                trendingS = root.findViewById(R.id.trendingShows);
                nowPlaying = root.findViewById(R.id.nowPlayingRView);
                Upcoming = root.findViewById(R.id.upcoming);
                popularM = root.findViewById(R.id.popularMovies);
                popularS = root.findViewById(R.id.popularShows);
                topRatedM = root.findViewById(R.id.topRatedMovies);
                topRatedS = root.findViewById(R.id.topRatedShows);
                initRList(trending, r.getTrendingMovies(), "movie");
                initRList(trendingS, r.getTrendingShows(), "show");
                initRList(nowPlaying, r.getNowPlaying(), "movie");
                initRList(Upcoming, r.getUpcoming(), "movie");
                initRList(popularM, r.getpopularMovies(), "movie");
                initRList(popularS, r.getpopularShows(), "show");
                initRList(topRatedM, r.getTopRatedMovies(), "movie");
                initRList(topRatedS, r.getTopRatedShows(), "show");
                root.findViewById(R.id.news).setVisibility(View.VISIBLE);
                root.findViewById(R.id.progressBar3).setVisibility(View.GONE);
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
        r.setAdapter(new SearchResultsRecyclerAdapter(movies, getContext(), cl));
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
}
