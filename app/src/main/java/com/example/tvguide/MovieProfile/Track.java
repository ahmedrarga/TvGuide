package com.example.tvguide.MovieProfile;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tvguide.R;
import com.example.tvguide.tmdb.Episode;
import com.example.tvguide.tmdb.Requests;
import com.example.tvguide.tmdb.Season;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Track.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Track#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Track extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static Episode episode;
    public static Season season;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private MovieProfileActivity myActivity;

    private OnFragmentInteractionListener mListener;

    public Track() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Track.
     */
    // TODO: Rename and change types and number of parameters
    public static Track newInstance(String param1, String param2) {
        Track fragment = new Track();
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
        // Inflate the layout for this fragment
        final View root = inflater.inflate(R.layout.fragment_track, container, false);
        myActivity = (MovieProfileActivity)getActivity();
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                final RecyclerView track = root.findViewById(R.id.tracking);
                final RecyclerView episodes = root.findViewById(R.id.episodes);
                episodes.setVisibility(View.GONE);
                final ArrayList<Season> seasons = myActivity.movie.getSeasons();
                track.setAdapter(new TrackingAdapter(seasons, getContext(), myActivity.movie.getId(), new RowListener() {
                    @Override
                    public void rowClicked(Season season) {
                        Track.season = season;
                        showSeasonDialog();
                    }
                }));
                track.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
            }
        });

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
    public void showEpisodeDialog() {
        EpisodeFragment editNameDialogFragment = EpisodeFragment.newInstance("Episode");
        editNameDialogFragment.show(getFragmentManager(), "fragment_episode");
    }

    public void showSeasonDialog(){
        SeasonFragment season = SeasonFragment.newInstance("Season", "");
        season.show(getFragmentManager(), "fragment_season");
    }
    public interface DialogListener{
        void showDialog(Episode episode);
        void showDialog(Season season);
    }


}
