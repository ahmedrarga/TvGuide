package com.example.tvguide.MovieProfile;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.tvguide.Account.rating;
import com.example.tvguide.GalleryActivity;
import com.example.tvguide.HomePage.PosterAdapter;
import com.example.tvguide.HomePage.SearchResultsRecyclerAdapter;
import com.example.tvguide.R;
import com.example.tvguide.tmdb.Cast;
import com.example.tvguide.tmdb.Movie;
import com.example.tvguide.tmdb.Requests;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import com.google.android.youtube.player.YouTubePlayer;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

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
    LinearLayout home;
    ImageButton watched;
    Movie movie;
    double rated = 0.5;
    Button rate;

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
                while (movie == null){
                    movie = mListener.onFragmentInteraction(root);
                    System.out.println("null");
                }

                TextView overview = root.findViewById(R.id.overview);
                final MovieProfileActivity myActivity = (MovieProfileActivity)getActivity();
                overview.setText(movie.getOverview());
                myActivity.checkWatchlist();
                if(movie.getMedia_type().equals("movie")){
                    root.findViewById(R.id.toDiss).setVisibility(View.GONE);
                    root.findViewById(R.id.toDiss2).setVisibility(View.GONE);
                }


                FloatingActionButton btn = root.findViewById(R.id.add);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        myActivity.addToWatchList(view);

                    }
                });
                rate = root.findViewById(R.id.rate);
                rate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openDialog(view);
                    }
                });
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                String m = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                db.collection("ratings")
                        .document(m)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                rating r = documentSnapshot.toObject(rating.class);
                                if(r != null && r.arrayList != null){
                                    for (Map<String, String> m : r.arrayList){
                                        if(m.get("id").equals(String.valueOf(movie.getId()))){
                                            String t = "Rated - " + m.get("rating");
                                            rate.setText(t);
                                            rated = Double.parseDouble(m.get("rating"));

                                        }
                                    }
                                }
                            }
                        });

                ArrayList<String> arr = movie.getGenres();
                // RecyclerView images = root.findViewById(R.id.images);
                Requests r = new Requests(movie.getId(), movie.getMedia_type());
                posters = movie.getPosters();
                backdrops = movie.getBackdrops();
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
                genres.setAdapter(new GenresAdapter(movie.getGenres(), getContext()));
                genres.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                home = root.findViewById(R.id.home);
                HashMap<String,String> map = movie.getNetwork();
                String n = "";
                if(map != null){
                    n = "Watch on " + map.get("name");
                    TextView text = root.findViewById(R.id.watch);
                    text.setText(n);
                    ImageView imageView = root.findViewById(R.id.network);
                    Picasso.get()
                            .load(map.get("logo"))
                            .into(imageView);


                }

                home.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String link = movie.getHomePage();
                        if(link.length() > 4) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                            startActivity(browserIntent);
                        }else{
                            Snackbar.make(getView(), "Homepage isn't available", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
                try{
                    JSONObject obj = movie.getLastEpisode();
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
                                .load(movie.getBackdrop_path())
                                .into(i);
                    }else {
                        Picasso.get()
                                .load(movie.IMAGE_PATH + im)
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
                ArrayList<Cast> c = movie.getCast();
                if(c.size() != 0){
                    root.findViewById(R.id.cast_error).setVisibility(View.GONE);
                    cast.setVisibility(View.VISIBLE);
                }
                cast.setAdapter(new CastAdapter(c, getContext()));
                cast.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
                RecyclerView similar = root.findViewById(R.id.similar);
                similar.setVisibility(View.GONE);
                String media_type = "show";
                if(movie.getMedia_type().equals("")){
                    media_type = "movie";
                }
                ArrayList<Movie> sim =  movie.getSimilar();
                if(sim.size() != 0){
                    root.findViewById(R.id.similar_error).setVisibility(View.GONE);
                    similar.setVisibility(View.VISIBLE);
                }
                similar.setAdapter(new PosterAdapter(sim, getContext(), media_type));
                similar.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));

                RecyclerView videos = root.findViewById(R.id.videos);
                videos.setAdapter(new VideosAdapter(movie.getVideos(), getContext()));
                videos.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
                root.findViewById(R.id.overview_scroll).setVisibility(View.VISIBLE);
                root.findViewById(R.id.progressBar2).setVisibility(View.GONE);

            }
        });


        return root;
    }
    public void openDialog(View view) {
        View v = getLayoutInflater().inflate(R.layout.rate_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        AlertDialog dialog = builder.setIcon(R.drawable.ic_star)
                .setPositiveButton("Ok", null)
                .setView(v)
                .create();


        dialog.show();
        TextView textView = v.findViewById(R.id.textView12);
        String t = "Rate " + movie.getName();
        textView.setText(t);
        final RatingBar ratingBar = v.findViewById(R.id.ratingBar2);
        ratingBar.setRating((float)rated);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, final float v, boolean b) {
                final FirebaseFirestore db = FirebaseFirestore.getInstance();
                final String m = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                db.collection("ratings")
                        .document(m)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                rating r = documentSnapshot.toObject(rating.class);
                                if(r == null){
                                    r = new rating();
                                    r.arrayList = new ArrayList<>();
                                }
                                r.setValue(String.valueOf(movie.getId()), String.valueOf(v));
                                db.collection("ratings").document(m).set(r);
                                rated = v;
                                String t = "Rated - " + String.valueOf(v);
                                rate.setText(t);

                            }
                        });
            }
        });
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
        Movie onFragmentInteraction(View view);

    }

}
