package com.example.tvguide.MovieProfile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tvguide.R;
import com.example.tvguide.tmdb.Episode;
import com.example.tvguide.tmdb.Movie;
import com.example.tvguide.tmdb.Season;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EpisodeAdapter extends
        RecyclerView.Adapter<EpisodeAdapter.ViewHolder> {

    private List<Episode> episodes;
    private Context context;
    private String cl;
    Track.DialogListener listener;
    public EpisodeAdapter(List<Episode> episodes, Context context, Track.DialogListener listener){
        this.episodes = episodes;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final Context context = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View movieView = inflater.inflate(R.layout.episode_track, parent, false);
        // Return a new holder instance
        final ViewHolder viewHolder = new ViewHolder(movieView);
        return viewHolder;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data model based on position
        ImageView image = holder.image;
        final TextView season = holder.season;
        final TextView episodes = holder.e;
        final CheckBox check = holder.check;

        final Episode e = this.episodes.get(position);
        Picasso.get()
                .load(e.getImage())
                .into(image);

        season.setText(e.getEpisode());
        String ep = e.getAirDate();
        episodes.setText(ep);
        if(!e.isWatched()){
            check.setChecked(false);
            Drawable img = context.getResources().getDrawable( R.drawable.ic_round_uncheck);
            check.setButtonDrawable(img);
        }else{
            check.setChecked(true);
            Drawable img = context.getResources().getDrawable( R.drawable.ic_checked);
            check.setButtonDrawable(img);
        }




    }

    @Override
    public int getItemCount() {
        return episodes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        //public TextView nameTextView;
        public ImageView image;
        public TextView season;
        public CheckBox check;
        public CardView row;
        public TextView e;
        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            //nameTextView = (TextView) itemView.findViewById(R.id.movie_name);
            image =  itemView.findViewById(R.id.image);
            season = itemView.findViewById(R.id.season);
            check = itemView.findViewById(R.id.checkBox);
            row = itemView.findViewById(R.id.row);
            e = itemView.findViewById(R.id.air_Date);
            //final RowListener rowListener = listener;
            row.setClickable(true);
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.showDialog(episodes.get(getPosition()));
                }
            });
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.showDialog(episodes.get(getPosition()));
                }
            });
            check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    final String mail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                    db = FirebaseFirestore.getInstance();
                    final FirebaseFirestore finalDb = db;
                    db.collection("trackingEpisodes")
                            .document(mail)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()){
                                        TrackingEpisodes t = task.getResult().toObject(TrackingEpisodes.class);
                                        if(t == null){
                                            t = new TrackingEpisodes();
                                        }
                                        t.setValue(episodes.get(getPosition()).getId(), episodes.get(getPosition()).getS_id(), episodes.get(getPosition()).getEpisodeNumber());
                                        finalDb.collection("trackingEpisodes")
                                                .document(mail)
                                                .set(t);
                                        check.setChecked(true);
                                        Drawable img = context.getResources().getDrawable( R.drawable.ic_checked);
                                        check.setButtonDrawable(img);
                                        episodes.get(getPosition()).setWatched(true);
                                    }
                                }
                            });
                }
            });
        }

    }



}
