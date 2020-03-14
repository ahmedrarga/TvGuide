package com.example.tvguide.MovieProfile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tvguide.R;
import com.example.tvguide.tmdb.Cast;
import com.example.tvguide.tmdb.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CastAdapter extends
        RecyclerView.Adapter<CastAdapter.ViewHolder> {

    private List<Cast> cast;
    private Context context;
    private String cl;
    public CastAdapter(List<Cast> cast, Context context){
        this.cast = cast;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final Context context = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View movieView = inflater.inflate(R.layout.layout_cast_view, parent, false);
        // Return a new holder instance
        final ViewHolder viewHolder = new ViewHolder(movieView);
        return viewHolder;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data model based on position
        Cast character = cast.get(position);

        Picasso.get()
                .load(character.getProfileImage())
                .into(holder.image);
        String text = character.getName();
        holder.text.setText(text);

    }

    @Override
    public int getItemCount() {
        return cast.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        //public TextView nameTextView;
        public TextView text;
        public ImageView image;
        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            setIsRecyclable(false);
            //nameTextView = (TextView) itemView.findViewById(R.id.movie_name);
            text =  itemView.findViewById(R.id.character);
            image = itemView.findViewById(R.id.image);
        }

    }

}
