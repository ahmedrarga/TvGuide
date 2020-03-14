package com.example.tvguide.HomePage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tvguide.Constants;
import com.example.tvguide.MovieProfile.MovieProfileActivity;
import com.example.tvguide.R;
import com.example.tvguide.tmdb.Movie;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.Document;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedsAdapter extends
        RecyclerView.Adapter<FeedsAdapter.ViewHolder> {

    private ArrayList<Map<String,String>> posts;
    private Context context;
    public FeedsAdapter(ArrayList<Map<String,String>> posts, Context context){
        this.posts= posts;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final Context context = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View movieView = inflater.inflate(R.layout.feeds_row, parent, false);
        // Return a new holder instance
        final ViewHolder viewHolder = new ViewHolder(movieView);
        return viewHolder;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        // Get the data model based on position
        final Map<String,String> post = posts.get(position);
        if(post != null) {
            final String[] path = post.get("path").split("/");
            holder.time.setText(path[path.length - 1]);
            StorageReference ref = FirebaseStorage.getInstance().getReference();
            StorageReference pRef = ref.child("images/" + post.get("user") + "/profile.jpg");
            pRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Picasso.get()
                                .load(task.getResult())
                                .resize(60, 60)
                                .centerCrop()
                                .placeholder(R.drawable.ic_person)
                                .into(holder.profile);
                    }
                }
            });
            FirebaseStorage storage = FirebaseStorage.getInstance();
            FirebaseFirestore.getInstance().collection("users")
                    .whereEqualTo("EMAIL", post.get("user"))
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                String n = document.getData().get("FIRST_NAME").toString() + " " + document.getData().get("LAST_NAME").toString();
                                String text = n + " • " + path[0];
                                holder.name.setText(text);
                            }

                        }
                    });
            if (path[1].equals("images")) {
                holder.video.setVisibility(View.GONE);
                holder.image.setVisibility(View.VISIBLE);
                StorageReference iRef = storage.getReference(post.get("path"));
                iRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get()
                                .load(uri)
                                .placeholder(R.drawable.ic_full)
                                .resize(800, 800)
                                .centerCrop()
                                .into(holder.image);

                    }
                });
            } else {
                holder.video.setVisibility(View.VISIBLE);
                holder.image.setVisibility(View.GONE);
                StorageReference vRef = storage.getReference(post.get("path"));
                vRef.getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                holder.video.setVideoURI(uri);
                            }
                        });
            }
        }

    }
    public void updateData(ArrayList<Map<String, String>> map){
        posts.addAll(map);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        //public TextView nameTextView;
        public ImageView image;
        public TextView name;
        public TextView time;
        public ImageView profile;
        public VideoView video;
        private boolean isPlaying = false;
        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            //nameTextView = (TextView) itemView.findViewById(R.id.movie_name);
            image =  itemView.findViewById(R.id.post_image);
            name = itemView.findViewById(R.id.name);
            time = itemView.findViewById(R.id.time);
            profile = itemView.findViewById(R.id.profile);
            video = itemView.findViewById(R.id.video);
            video.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(video.isPlaying()) {
                        video.pause();
                    }else{
                        video.start();
                    }

                }
            });
        }


    }

}
