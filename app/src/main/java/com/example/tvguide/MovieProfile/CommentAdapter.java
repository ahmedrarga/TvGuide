package com.example.tvguide.MovieProfile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tvguide.R;
import com.example.tvguide.comment;
import com.example.tvguide.tmdb.Movie;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommentAdapter extends
        RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private ArrayList<Map<String, Object>> com;
    private Context context;
    private String cl;
    public CommentAdapter(ArrayList<Map<String, Object>> com, Context context){
        this.com = com;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final Context context = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View movieView = inflater.inflate(R.layout.comment_layout, parent, false);
        // Return a new holder instance
        final ViewHolder viewHolder = new ViewHolder(movieView);
        return viewHolder;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        // Get the data model based on position
        Map<String, Object> comment = com.get(position);

        StorageReference ref = FirebaseStorage.getInstance().getReference();
        StorageReference pRef = ref.child("images/" + comment.get("user") + "/profile.jpg");
        pRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Picasso.get()
                            .load(task.getResult())
                            .resize(60, 60)
                            .centerCrop()
                            .placeholder(R.drawable.ic_person)
                            .error(R.drawable.ic_person)
                            .into(holder.profile);
                }else{
                    holder.profile.setImageResource(R.drawable.ic_person);
                }
            }
        });
        FirebaseFirestore.getInstance().collection("users")
                .whereEqualTo("EMAIL", comment.get("user"))
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String n = document.getData().get("FIRST_NAME").toString() + " " + document.getData().get("LAST_NAME").toString();
                            String text = n;
                            holder.name.setText(text);
                        }

                    }
                });
        holder.comment.setText((String)comment.get("comment"));




    }

    @Override
    public int getItemCount() {
        return com.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        //public TextView nameTextView;
        public ImageView profile;
        public TextView name;
        public TextView comment;
        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            profile = itemView.findViewById(R.id.profile);
            name = itemView.findViewById(R.id.name);
            comment = itemView.findViewById(R.id.comment_text);


        }


    }

}
