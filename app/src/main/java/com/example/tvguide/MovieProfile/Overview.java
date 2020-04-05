package com.example.tvguide.MovieProfile;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tvguide.GalleryActivity;
import com.example.tvguide.HomePage.SearchResultsRecyclerAdapter;
import com.example.tvguide.R;
import com.example.tvguide.YoutubeAPI;
import com.example.tvguide.tmdb.Cast;
import com.example.tvguide.tmdb.Movie;
import com.example.tvguide.tmdb.Requests;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Overview.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Overview#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Overview extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private YouTubePlayer YPlayer;
    private static final int RECOVERY_DIALOG_REQUEST = 1;
    private List<String> posters;
    private List<String> backdrops;
    public static View v;
    ImageButton watched;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Overview() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Overview.
     */
    // TODO: Rename and change types and number of parameters
    public static Overview newInstance(String param1, String param2) {
        Overview fragment = new Overview();
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
        final View root = inflater.inflate(R.layout.fragment_overview, container, false);
        v = root;

        root.findViewById(R.id.overview_scroll).setVisibility(View.GONE);
        final MovieProfileActivity myActivity = (MovieProfileActivity)getActivity();

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                TextView overview = root.findViewById(R.id.overview);
                final MovieProfileActivity myActivity = (MovieProfileActivity)getActivity();
                overview.setText(myActivity.movie.getOverview());
                myActivity.checkWatchlist();
                if(myActivity.movie.getMedia_type().equals("movie")){
                    root.findViewById(R.id.toDiss).setVisibility(View.GONE);
                    root.findViewById(R.id.toDiss2).setVisibility(View.GONE);
                }
                watched = root.findViewById(R.id.watched);


                FloatingActionButton btn = root.findViewById(R.id.add);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        myActivity.addToWatchList(view);

                    }
                });
                String mail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("Tracking")
                            .document(mail)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    Tracking t = task.getResult().toObject(Tracking.class);
                                    if (myActivity.movie.getMedia_type().equals("tv")) {
                                        int episodes = myActivity.movie.getEpisodesNumber();
                                        if (t != null && t.tracking != null && t.tracking.get(String.valueOf(myActivity.movie.getId())) != null) {
                                            int count = 0;
                                            for (HashMap<String, String> map : t.tracking.get(String.valueOf(myActivity.movie.getId()))) {
                                                if(!map.get("season").equals("0"))
                                                    count++;
                                            }
                                            if (count == episodes) {
                                                Drawable img = getContext().getResources().getDrawable(R.drawable.ic_checked);
                                                watched.setImageDrawable(img);
                                                watched.setTooltipText("Watched");
                                                myActivity.movie.setWatched(true);
                                            }else{
                                                Drawable img = getContext().getResources().getDrawable(R.drawable.ic_round_uncheck);
                                                watched.setImageDrawable(img);
                                                myActivity.movie.setWatched(false);
                                            }
                                        }
                                    }else{
                                        if(t != null) {
                                            ArrayList<HashMap<String, String>> tmp = t.tracking.get(String.valueOf(myActivity.movie.getId()));
                                            if (tmp != null)
                                                if (tmp.size() == 1 && tmp.get(0).get("episode").equals("0")) {
                                                    Drawable img = getContext().getResources().getDrawable(R.drawable.ic_checked);
                                                    watched.setImageDrawable(img);
                                                    watched.setTooltipText("Watched");

                                                }
                                        }
                                    }
                                }
                            });

                watched.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(myActivity.movie.isWatched()){
                            myActivity.movie.markUnWatched();
                            Drawable img = getContext().getResources().getDrawable(R.drawable.ic_round_uncheck);
                            watched.setImageDrawable(img);

                        }else {
                            myActivity.movie.markWatched();
                            myActivity.addToWatchList(view);
                            Drawable img = getContext().getResources().getDrawable(R.drawable.ic_checked);
                            watched.setImageDrawable(img);
                            watched.setTooltipText("Watched");

                        }
                    }
                });
                ArrayList<String> arr = myActivity.movie.getGenres();
                // RecyclerView images = root.findViewById(R.id.images);
                Requests r = new Requests(myActivity.movie.getId(), myActivity.movie.getMedia_type());
                posters = myActivity.movie.getPosters();
                backdrops = myActivity.movie.getBackdrops();
                ImageView p = root.findViewById(R.id.posters);
                ImageView b = root.findViewById(R.id.backdrops);
                p.setOnClickListener(Overview.this);
                b.setOnClickListener(Overview.this);
                if(posters.size()>0)
                    Picasso.get()
                            .load(posters.get(0))
                            .error(R.mipmap.ic_launcher)
                            .fit()
                            .into(p);
                if(backdrops.size() > 0)
                    Picasso.get()
                            .load(backdrops.get(0))
                            .error(R.mipmap.ic_launcher)
                            .fit()
                            .into(b);


                //images.setAdapter(new ImagesAdapter(r.getImages(), getContext()));
                //  images.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                RecyclerView genres = root.findViewById(R.id.genres);
                genres.setAdapter(new GenresAdapter(myActivity.movie.getGenres(), getContext()));
                genres.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                Button home = root.findViewById(R.id.homepage);
                home.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String link = myActivity.movie.getHomePage();
                        if(link.length() > 4) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                            startActivity(browserIntent);
                        }else{
                            Snackbar.make(getView(), "Homepage isn't available", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
                try{
                    JSONObject obj = myActivity.movie.getLastEpisode();
                    if(obj.getBoolean("next")){
                        TextView t = root.findViewById(R.id.toDiss2);
                        t.setText("Next episode to air");
                    }else{
                        TextView t = root.findViewById(R.id.toDiss2);
                        t.setText("Last episode to air");
                    }
                    ImageView i = root.findViewById(R.id.image);
                    String im = obj.getString("still_path");
                    if(im.equals("null")){
                        Picasso.get()
                                .load(myActivity.movie.getBackdrop_path())
                                .into(i);
                    }else {
                        Picasso.get()
                                .load(myActivity.movie.IMAGE_PATH + im)
                                .into(i);
                    }
                    TextView s = root.findViewById(R.id.season);
                    s.setText(obj.getString("name"));
                    TextView a = root.findViewById(R.id.info2);
                    a.setVisibility(View.VISIBLE);
                    a.setText(obj.getString("air_date"));
                }catch (JSONException e){


                }
                RecyclerView cast = root.findViewById(R.id.cast);
                cast.setVisibility(View.GONE);
                ArrayList<Cast> c = myActivity.movie.getCast();
                if(c.size() != 0){
                    root.findViewById(R.id.cast_error).setVisibility(View.GONE);
                    cast.setVisibility(View.VISIBLE);
                }
                cast.setAdapter(new CastAdapter(c, getContext()));
                cast.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
                RecyclerView similar = root.findViewById(R.id.similar);
                similar.setVisibility(View.GONE);
                String media_type = "show";
                if(myActivity.movie.getMedia_type().equals("")){
                    media_type = "movie";
                }
                ArrayList<Movie> sim =  myActivity.movie.getSimilar();
                if(sim.size() != 0){
                    root.findViewById(R.id.similar_error).setVisibility(View.GONE);
                    similar.setVisibility(View.VISIBLE);
                }
                similar.setAdapter(new SearchResultsRecyclerAdapter(sim, getContext(), media_type));
                similar.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));

                RecyclerView videos = root.findViewById(R.id.videos);
                videos.setAdapter(new VideosAdapter(myActivity.movie.getVideos(), getContext()));
                videos.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
                root.findViewById(R.id.overview_scroll).setVisibility(View.VISIBLE);
                root.findViewById(R.id.progressBar2).setVisibility(View.GONE);

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
    public void onResume() {
        super.onResume();
        if(!((MovieProfileActivity)getActivity()).movie.isWatched()){
            Drawable img = getContext().getResources().getDrawable(R.drawable.ic_round_uncheck);
            if(watched == null){
                watched = v.findViewById(R.id.watched);
            }
            watched.setImageDrawable(img);
            watched.setTooltipText("Watched");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        ArrayList<String> toPut = new ArrayList<>();
        if(view.getId() == R.id.backdrops)
            toPut = (ArrayList)backdrops;
        else
            toPut = (ArrayList)posters;
        Intent intent = new Intent(getContext(), GalleryActivity.class);
        intent.putStringArrayListExtra("array", toPut);
        startActivity(intent);

    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href="http://developer.android.com/training/basics/fragments/communicating.html">Communicating with Other Fragments</a>
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
