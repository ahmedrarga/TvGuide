package com.example.tvguide.HomePage;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.tvguide.Constants;
import com.example.tvguide.MovieProfile.MovieProfileActivity;
import com.example.tvguide.R;
import com.example.tvguide.User.ProfileActivity;
import com.example.tvguide.tmdb.Movie;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
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

                                holder.name.setText(n);
                                holder.movieName.setText(path[0]);
                            }

                        }
                    });
            StorageReference iRef = storage.getReference(post.get("path"));
            iRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get()
                            .load(uri)
                            // .placeholder(R.drawable.ic_full)
                            .resize(800, 800)
                            .centerCrop()
                            .into(holder.image);
                }
            });
        }

    }
    private boolean hasImage(@NonNull ImageView view) {
        Drawable drawable = view.getDrawable();
        boolean hasImage = (drawable != null);

        if (hasImage && (drawable instanceof BitmapDrawable)) {
            hasImage = ((BitmapDrawable)drawable).getBitmap() != null;
        }

        return hasImage;
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
        TextView movieName;
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
            image = itemView.findViewById(R.id.post_image);
            name = itemView.findViewById(R.id.name);
            time = itemView.findViewById(R.id.time);
            profile = itemView.findViewById(R.id.profile);
            movieName = itemView.findViewById(R.id.movie_name);
            user = itemView.findViewById(R.id.user);
            user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ProfileActivity.class);
                    context.startActivity(intent);
                }
            });
        }


    }

}
