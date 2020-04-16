package com.example.tvguide.HomePage;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.example.tvguide.Constants;
import com.example.tvguide.MediaObject;
import com.example.tvguide.R;
import com.example.tvguide.User.ProfileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Map;

public class PlayerViewHolder extends RecyclerView.ViewHolder {
    /**
     * below view have public modifier because
     * we have access PlayerViewHolder inside the ExoPlayerRecyclerView
     */
    Context context;
    public FrameLayout mediaContainer;
    public ImageView mediaCoverImage, volumeControl;
    public ProgressBar progressBar;
    public RequestManager requestManager;
    private TextView title, userHandle;
    ImageView user;
    LinearLayout user_im_name;
    private View parent;
    public PlayerViewHolder(@NonNull View itemView, Context cont) {
        super(itemView);
        parent = itemView;
        context = cont;
        mediaContainer = itemView.findViewById(R.id.mediaContainer);
        mediaCoverImage = itemView.findViewById(R.id.ivMediaCoverImage);
        title = itemView.findViewById(R.id.tvTitle);
        userHandle = itemView.findViewById(R.id.tvUserHandle);
        progressBar = itemView.findViewById(R.id.progressBar);
        volumeControl = itemView.findViewById(R.id.ivVolumeControl);
        user = itemView.findViewById(R.id.userImage);
        user_im_name = itemView.findViewById(R.id.card);
        user_im_name.setClickable(true);
        user_im_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProfileActivity.class);
                context.startActivity(intent);
            }
        });
    }
    void onBind(MediaObject mediaObject, RequestManager requestManager) {
        this.requestManager = requestManager;
        parent.setTag(this);
        String[] splitted = mediaObject.getPath().split("/");
        title.setText(splitted[0]);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .whereEqualTo("EMAIL", mediaObject.getUser())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String n = document.getData().get("FIRST_NAME").toString() + " " + document.getData().get("LAST_NAME").toString();
                                userHandle.setText(n);
                            }
                        } else {
                            //Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        StorageReference ref = FirebaseStorage.getInstance().getReference();
        StorageReference pRef = ref.child("images/" + mediaObject.getUser() + "/profile.jpg");
        pRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Picasso.get()
                            .load(task.getResult())
                            .fit()
                            .into(user);
                }
            }
        });
    }
}