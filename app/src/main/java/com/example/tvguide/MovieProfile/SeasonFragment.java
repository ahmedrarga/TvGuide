package com.example.tvguide.MovieProfile;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tvguide.R;
import com.example.tvguide.comment;
import com.example.tvguide.tmdb.Episode;
import com.example.tvguide.tmdb.Season;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SeasonFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SeasonFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SeasonFragment extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    RecyclerView episodes;
    List<Episode> data;
    CheckBox checkBox;
    public static View v;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public SeasonFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SeasonFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SeasonFragment newInstance(String param1, String param2) {
        SeasonFragment fragment = new SeasonFragment();
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
        return inflater.inflate(R.layout.fragment_season, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        v = view;
        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Enter Name");
        getDialog().setTitle(title);
        episodes = view.findViewById(R.id.episodes);
        data = ((MovieProfileActivity)getActivity()).movie.getEpisodes(Track.season);
        episodes.setAdapter(new EpisodeAdapter(data, getContext(), new Track.DialogListener() {
            @Override
            public void showDialog(Episode episode) {
                Track.episode = episode;
                showEpisodeDialog();
            }

            @Override
            public void showDialog(Season season) {

            }
        }));

        episodes.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        final Season s = Track.season;
        checkBox = view.findViewById(R.id.watched);
        Drawable img = getContext().getResources().getDrawable( R.drawable.ic_round_uncheck);
        checkBox.setButtonDrawable(img);
        TextView name = view.findViewById(R.id.episode_title);
        ImageView imageView = view.findViewById(R.id.episode_backdrop);
        TextView airDAte = view.findViewById(R.id.air_date);
        final TextView overview = view.findViewById(R.id.overview);
        if(s.isWatched() || ((MovieProfileActivity)getActivity()).movie.isWatched()){
            s.setWatched(true);
            checkBox.setText("Watched");
            img = getContext().getResources().getDrawable( R.drawable.ic_checked);
            checkBox.setButtonDrawable(img);
        }
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final String mail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!s.isWatched()) {
                    db.collection("Tracking")
                            .document(mail)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        Tracking t = task.getResult().toObject(Tracking.class);
                                        if (t == null) {
                                            t = new Tracking();
                                        }
                                        t.setSeason(s, data);
                                        db.collection("Tracking")
                                                .document(mail)
                                                .set(t);
                                        checkBox.setText("Watched");
                                        Drawable img = getContext().getResources().getDrawable( R.drawable.ic_checked);
                                        checkBox.setButtonDrawable(img);


                                    }
                                }
                            });
                } else{
                    db.collection("Tracking")
                            .document(mail)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        Tracking t = task.getResult().toObject(Tracking.class);
                                        if (t == null) {
                                            t = new Tracking();
                                        }
                                        t.removeSeason(s, data);
                                        db.collection("Tracking")
                                                .document(mail)
                                                .set(t);
                                        checkBox.setText("Set watched");
                                        Drawable img = getContext().getResources().getDrawable( R.drawable.ic_round_uncheck);
                                        checkBox.setButtonDrawable(img);
                                        ImageButton tmp = Overview.v.findViewById(R.id.watched);
                                        tmp.setImageDrawable(img);

                                    }
                                }
                            });

                }
            }
        });
        overview.setClickable(true);
        overview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(overview.getMaxLines() == 5){
                    overview.setMaxLines(40);
                }else{
                    overview.setMaxLines(5);
                }
            }
        });
        ImageView close = view.findViewById(R.id.close);
        String im = s.getImage();
        if(im.equals("")){
            Picasso.get()
                    .load(((MovieProfileActivity)getActivity()).movie.getPoster_path())
                    .fit()
                    .into(imageView);
        }else{
            Picasso.get()
                    .load(im)
                    .fit()
                    .into(imageView);
        }


        name.setText(s.getSeason());
        String tmp = s.getAirdate();
        airDAte.setText(tmp);
        TextView episodes = view.findViewById(R.id.episodes_number);
        tmp = s.getEpisodesNumber() + " episodes";
        episodes.setText(tmp);

        overview.setText(s.getOverview());
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });





    }
    @Override
    public void onResume() {
        super.onResume();
        if(!Track.season.isWatched()){
            Drawable img = getContext().getResources().getDrawable(R.drawable.ic_round_uncheck);
            checkBox.setButtonDrawable(img);
            checkBox.setTooltipText("Watched");
        }
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
}
