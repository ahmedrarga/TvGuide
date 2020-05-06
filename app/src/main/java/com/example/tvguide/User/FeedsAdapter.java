package com.example.tvguide.User;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.example.tvguide.R;
import com.example.tvguide.UserPost;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.Document;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class FeedsAdapter extends
        RecyclerView.Adapter<FeedsAdapter.ViewHolder> {

    private ArrayList<String> posts;
    private Context context;
    public FeedsAdapter(ArrayList<String> posts, Context context, String user){
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
    public void onBindViewHolder(@NonNull final FeedsAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        final String post = posts.get(position);
        String user = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        if(post != null) {
            final String[] path = post.split("/");
            holder.time.setText(path[path.length - 1]);
            StorageReference ref = FirebaseStorage.getInstance().getReference();
            StorageReference pRef = ref.child("images/" + user + "/profile.jpg");
            pRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Picasso.get()
                                .load(task.getResult())
                                .placeholder(R.drawable.ic_person)
                                .into(holder.profile);
                    }
                }
            });
            final FirebaseStorage storage = FirebaseStorage.getInstance();
            holder.name.setText(user);

            if (path[1].equals("images")) {
                holder.video.setVisibility(View.GONE);
                holder.image.setVisibility(View.VISIBLE);
                StorageReference iRef = storage.getReference(post);
                iRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get()
                                .load(uri)
                                .placeholder(R.drawable.ic_full)
                                .fit()
                                .into(holder.image);

                    }
                });
            } else {
                holder.view.setMinimumHeight(100);
                holder.video.setVisibility(View.VISIBLE);
                holder.image.setVisibility(View.GONE);
                holder.video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        mediaPlayer.start();
                        mediaPlayer.pause();
                    }
                });
                holder.video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        holder.video.start();
                    }
                });
                StorageReference vRef = storage.getReference(post);
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
        public CardView view;
        public LinearLayout user;
        private boolean isPlaying = false;
        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            setIsRecyclable(false);
            //nameTextView = (TextView) itemView.findViewById(R.id.movie_name);
            image =  itemView.findViewById(R.id.post_image);
            name = itemView.findViewById(R.id.name);
            time = itemView.findViewById(R.id.time);
            profile = itemView.findViewById(R.id.profile);
            user = itemView.findViewById(R.id.user);
            view = itemView.findViewById(R.id.image);
            user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ProfileActivity.class);
                    context.startActivity(intent);
                }
            });
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
