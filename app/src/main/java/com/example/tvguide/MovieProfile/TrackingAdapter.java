package com.example.tvguide.MovieProfile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tvguide.EpisodeActivity;
import com.example.tvguide.R;
import com.example.tvguide.tmdb.Movie;
import com.example.tvguide.tmdb.Requests;
import com.example.tvguide.tmdb.Season;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class TrackingAdapter extends
        RecyclerView.Adapter<TrackingAdapter.ViewHolder> {

    private static List<Season> seasons;
    private Context context;
    private int id;
    private FirebaseFirestore db;
    RowListener listener;
    String mail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    public TrackingAdapter(List<Season> seasons, Context context, int id, RowListener listener){
        this.seasons = seasons;
        this.context = context;
        this.id = id;
        this.listener = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final Context context = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View movieView = inflater.inflate(R.layout.track_layout, parent, false);
        // Return a new holder instance
        final ViewHolder viewHolder = new ViewHolder(movieView);
        return viewHolder;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.setIsRecyclable(false);
        ImageView image = holder.image;
        final TextView season = holder.season;
        final TextView episodes = holder.episodes;
        final CheckBox check = holder.check;
        CardView row = holder.row;
        row.setClickable(true);
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.rowClicked(seasons.get(position));
            }
        });

        final Season s = seasons.get(position);
        String e = s.getEpisodesNumber() + " episodes";
        episodes.setVisibility(View.VISIBLE);
        episodes.setText(e);
        holder.info1.setVisibility(View.VISIBLE);
        holder.info1.setText(s.getAirdate());

        Picasso.get()
                .load(s.getImage())
                .into(image);

        season.setText(s.getSeason());
        if(seasons.get(position).isWatched()){
            check.setChecked(true);
            Drawable img = context.getResources().getDrawable( R.drawable.ic_checked);
            check.setButtonDrawable(img);
        }else{
            check.setChecked(false);
            Drawable img = context.getResources().getDrawable( R.drawable.ic_round_uncheck);
            check.setButtonDrawable(img);
        }



    }




    @Override
    public int getItemCount() {
        return seasons.size();
    }





    public class ViewHolder extends RecyclerView.ViewHolder{
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        //public TextView nameTextView;
        public ImageView image;
        public TextView season;
        public TextView episodes;
        public CheckBox check;
        public CardView row;
        public TextView info1;
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
            final RowListener rowListener = listener;
            info1 = itemView.findViewById(R.id.info1);
            episodes = itemView.findViewById(R.id.info2);
            row.setClickable(true);
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    rowListener.rowClicked(seasons.get(getPosition()));
                }
            });
            db = FirebaseFirestore.getInstance();

            check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    db = FirebaseFirestore.getInstance();
                    db.collection("trackingSeasons")
                            .document(mail)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()){
                                        TrackingSeasons t = task.getResult().toObject(TrackingSeasons.class);
                                        if(t == null){
                                            t = new TrackingSeasons();
                                        }
                                        t.setValue(id, seasons.get(getPosition()).getSeasonNumber());
                                        db.collection("trackingSeasons")
                                                .document(mail)
                                                .set(t);
                                        check.setChecked(true);
                                        Drawable img = context.getResources().getDrawable( R.drawable.ic_checked);
                                        check.setButtonDrawable(img);
                                        seasons.get(getPosition()).setWatched(true);
                                    }
                                }
                            });
                }
            });

        }


    }


}
