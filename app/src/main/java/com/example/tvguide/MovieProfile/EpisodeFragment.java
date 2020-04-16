package com.example.tvguide.MovieProfile;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.Image;
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
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tvguide.R;
import com.example.tvguide.comment;
import com.example.tvguide.tmdb.Episode;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EpisodeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EpisodeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EpisodeFragment extends DialogFragment implements View.OnClickListener{


    private String title;
    private TextView mEditText;
    private comment c;
    RecyclerView comments;
    ArrayList<Map<String, Object>> data;


    private OnFragmentInteractionListener mListener;

    public EpisodeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment EpisodeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EpisodeFragment newInstance(String title) {
        EpisodeFragment fragment = new EpisodeFragment();
        Bundle args = new Bundle();
        args.putString("title", title);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString("title");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_episode, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view

        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Enter Name");
        getDialog().setTitle(title);
        final Episode e = Track.episode;
        final CheckBox checkBox = view.findViewById(R.id.watched);
        Drawable img = getContext().getResources().getDrawable( R.drawable.ic_round_uncheck);

        checkBox.setButtonDrawable(img);
        if(e.isWatched() ){
            e.setWatched(true);
            img = getContext().getResources().getDrawable( R.drawable.ic_checked);
            checkBox.setButtonDrawable(img);
            checkBox.setText("Watched");
        }
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final String mail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!e.isWatched()) {
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
                                        t.setEpisode(e.getId(), e.getS_id(), e);
                                        db.collection("Tracking")
                                                .document(mail)
                                                .set(t);
                                        checkBox.setText("Watched");
                                        Drawable img = getContext().getResources().getDrawable(R.drawable.ic_checked);
                                        checkBox.setButtonDrawable(img);


                                    }

                                }
                            });
                }else{
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
                                        t.removeEpisode(e.getId(), e.getS_id(), e);
                                        db.collection("Tracking")
                                                .document(mail)
                                                .set(t);
                                        checkBox.setText("Set watched");
                                        Drawable img = getContext().getResources().getDrawable(R.drawable.ic_round_uncheck);
                                        checkBox.setButtonDrawable(img);
                                        e.setWatched(false);
                                        ((MovieProfileActivity)getActivity()).movie.setWatched(false);
                                        Track.season.setWatched(false);
                                        CheckBox tmp = SeasonFragment.v.findViewById(R.id.watched);
                                        tmp.setButtonDrawable(img);


                                    }

                                }
                            });
                }
            }
        });
        TextView name = view.findViewById(R.id.episode_title);
        ImageView imageView = view.findViewById(R.id.episode_backdrop);
        TextView airDAte = view.findViewById(R.id.air_date);
        TextView rating = view.findViewById(R.id.rating);
        TextView overview = view.findViewById(R.id.overview);
        comments = view.findViewById(R.id.comments);
        data = new ArrayList<>();
        comments.setAdapter(new CommentAdapter(data, getContext()));
        getComments(new EndListener() {
            @Override
            public void ended(comment c) {
                for(Map<String, Object> m : c.arrayList){
                    if((long)m.get("season") == e.getS_id() && (long)m.get("episode") == e.getEpisodeNumber()) {
                        data.add(m);
                        view.findViewById(R.id.error).setVisibility(View.GONE);
                    }
                }
                comments.getAdapter().notifyDataSetChanged();
            }
        });
        comments.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        ImageView close = view.findViewById(R.id.close);
        Button add = view.findViewById(R.id.add);
        String im = e.getImage();
        if(im.equals("")){
            Picasso.get()
                    .load(((MovieProfileActivity)getActivity()).movie.getBackdrop_path())
                    .fit()
                    .error(R.drawable.ic_person)
                    .into(imageView);
        }else {
            Picasso.get()
                    .load(im)
                    .fit()
                    .error(R.drawable.ic_person)
                    .into(imageView);
        }
        name.setText(e.getEpisode());
        String tmp = e.getAirDate() + " | ";
        airDAte.setText(tmp);
        DecimalFormat df = new DecimalFormat("#.#");
        rating.setText(df.format(Float.parseFloat(e.getRating())));
        overview.setText(e.getOverview());
        close.setOnClickListener(this);
        add.setOnClickListener(this);
        Button post = view.findViewById(R.id.post);
        post.setOnClickListener(this);
        Button cancel = view.findViewById(R.id.cancel);
        cancel.setOnClickListener(this);



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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.close:
                getDialog().dismiss();
                break;
            case R.id.add:
                getView().findViewById(R.id.info).setVisibility(View.GONE);
                getView().findViewById(R.id.comment).setVisibility(View.VISIBLE);
                break;

            case R.id.cancel:
                getView().findViewById(R.id.info).setVisibility(View.VISIBLE);
                getView().findViewById(R.id.comment).setVisibility(View.GONE);
                break;
            case R.id.post:
                final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                final String user = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                final EditText text = getView().findViewById(R.id.comment_Text);
                if(!text.getText().toString().isEmpty()) {
                    ((MovieProfileActivity)getActivity()).hideKeyboard(view);
                    final Episode e = Track.episode;
                    firestore.collection("comments")
                            .document(String.valueOf(e.getId()))
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    comment c = documentSnapshot.toObject(comment.class);
                                    if (c == null) {
                                        c = new comment();
                                    }
                                    Map<String,Object> map = c.setValues(e.getS_id(), e.getEpisodeNumber(), text.getText().toString(), user);
                                    firestore.collection("comments")
                                            .document(String.valueOf(e.getId()))
                                            .set(c);
                                    data.add(map);
                                    comments.getAdapter().notifyDataSetChanged();

                                    Snackbar.make(getView(), "Comment added", Snackbar.LENGTH_SHORT).show();
                                    text.setText("");
                                    getView().findViewById(R.id.info).setVisibility(View.VISIBLE);
                                    getView().findViewById(R.id.comment).setVisibility(View.GONE);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            System.out.println(e.getMessage());
                            Snackbar.make(getView(), "An error occured", Snackbar.LENGTH_SHORT).show();
                        }
                    });
                }
                break;
            default:
                break;
        }
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
        void onFragmentInteraction(Uri uri);
    }

    public comment getComments(final EndListener e){
        String id = String.valueOf(Track.episode.getId());
        c = new comment();
        FirebaseFirestore.getInstance()
                .collection("comments")
                .document(id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        c = documentSnapshot.toObject(comment.class);
                        if(c == null){
                            c = new comment();
                        }
                        e.ended(c);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println(e.getMessage());
                Snackbar.make(getView(), "An error occured" , Snackbar.LENGTH_SHORT).show();

            }
        });
        if(c.arrayList == null){
            c.arrayList = new ArrayList<>();
        }
        return c;
    }
    public interface EndListener{
        void ended(comment c);
    }


}
