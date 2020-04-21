package com.example.tvguide.HomePage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tvguide.MovieProfile.MovieProfileActivity;
import com.example.tvguide.R;
import com.example.tvguide.tmdb.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PosterAdapter extends
        RecyclerView.Adapter<PosterAdapter.ViewHolder> {

    private List<Movie> movies;
    private Context context;
    private String cl;
    public PosterAdapter(List<Movie> movies, Context context, String cl){
        this.movies = movies;
        this.context = context;
        this.cl = cl;
    }
    public void updateData(List<Movie> list){
        movies = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final Context context = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View movieView = inflater.inflate(R.layout.poster_movie, parent, false);
        // Return a new holder instance
        final ViewHolder viewHolder = new ViewHolder(movieView);
        return viewHolder;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data model based on position
        Movie movie = movies.get(position);

        // Set item views based on your views and data model
        //TextView textView = holder.nameTextView;
        // textView.setText(movie.getName());
        ImageView image = holder.image;
        Picasso.get()
                .load(movie.getPoster_path())
                .fit()
                .into(holder.image);
        System.out.println(movie.getName() + "  sdl;knfaksldfnlKSDNL;DMVl;sdkjgf;lwE");
        holder.title.setText(movie.getName());



    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        //public TextView nameTextView;
        public ImageView image;
        public TextView title;
        public CardView poster;
        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            //nameTextView = (TextView) itemView.findViewById(R.id.movie_name);
            image =  itemView.findViewById(R.id.movie_image);
            title = itemView.findViewById(R.id.title);
            poster = itemView.findViewById(R.id.card_view_row);
            image.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            String media_type = "";
            if(cl.equals("movie"))
                media_type = "movie";
            else if (cl.equals("show")){
                media_type = "tv";
            }
            else
                media_type = movies.get(getPosition()).getMedia_type();
            Intent intent = new Intent(context, MovieProfileActivity.class);
            intent.putExtra("id", movies.get(this.getPosition()).getId());
            intent.putExtra("media_type",  media_type);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Now we can start the Activity, providing the activity options as a bundle
            //ActivityCompat.startActivity((Activity)context, intent, activityOptions.toBundle());
            context.startActivity(intent);
        }
    }

}
