package com.example.tvguide.HomePage;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.tvguide.Post;
import com.example.tvguide.R;
import com.example.tvguide.User.WatchlistActivity;
import com.example.tvguide.tmdb.Movie;
import com.example.tvguide.tmdb.Requests;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Images.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Images#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Images extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    final List<Movie> watchlist = new ArrayList<>();
    final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    final FirebaseStorage storage = FirebaseStorage.getInstance();
    static RecyclerView feeds;
    static ArrayList<Map<String,String>> data = new ArrayList<>();
    static ProgressBar progressBar;

    private OnFragmentInteractionListener mListener;

    public Images() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Images.
     */
    // TODO: Rename and change types and number of parameters
    public static Images newInstance(String param1, String param2) {
        Images fragment = new Images();
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

        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_news, container, false);
        feeds = root.findViewById(R.id.feeds_rec);
        feeds.setAdapter(new FeedsAdapter(data, getContext()));
        feeds.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        progressBar = root.findViewById(R.id.progressBar5);
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    if (HomeActivity.countImages > 0) {
                        progressBar.setVisibility(View.GONE);
                        root.findViewById(R.id.message).setVisibility(View.VISIBLE);
                    }
                    if (HomeActivity.countImages == 0) {
                        progressBar.setVisibility(View.GONE);
                        root.findViewById(R.id.message).setVisibility(View.VISIBLE);
                    }
                }

            }
        }).start();*/
        // todo: Split posts to videos and images






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
