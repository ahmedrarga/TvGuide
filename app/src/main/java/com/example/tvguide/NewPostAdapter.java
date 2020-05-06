package com.example.tvguide;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tvguide.R;
import com.example.tvguide.tmdb.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;


public class NewPostAdapter extends
        RecyclerView.Adapter<NewPostAdapter.ViewHolder> {

    private List<Movie> movies;
    private Context context;
    private String cl;
    NewPostListener mListener;
    public NewPostAdapter(List<Movie> movies, Context context, NewPostListener listener){
        this.movies = movies;
        this.context = context;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final Context context = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View movieView = inflater.inflate(R.layout.search_results_row, parent, false);
        // Return a new holder instance
        final ViewHolder viewHolder = new ViewHolder(movieView, mListener);
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
        ImageView image = holder.text;
        Picasso.get()
                .load(movie.getPoster_path())
                .fit()
                .into(image);



    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        //public TextView nameTextView;
        public ImageView text;
        private NewPostListener mListener;
        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView, NewPostListener listener) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            //nameTextView = (TextView) itemView.findViewById(R.id.movie_name);
            text =  itemView.findViewById(R.id.movie_image);
            text.setOnClickListener(this);
            mListener = listener;
        }

        @Override
        public void onClick(View view) {
            mListener.clicked(movies.get(getPosition()));

        }
    }

}
