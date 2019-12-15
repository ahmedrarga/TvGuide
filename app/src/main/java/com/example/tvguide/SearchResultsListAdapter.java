package com.example.tvguide;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.arlib.floatingsearchview.util.Util;
import com.example.tvguide.tmdb.Movie;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsListAdapter extends RecyclerView.Adapter<SearchResultsListAdapter.ViewHolder> {
    private List<Movie> movies = new ArrayList<>();
    private int lastAnimatedItemPosition = -1;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.suggestion_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Movie movie = movies.get(position);
        holder.movieName.setText(movie.getName());
        holder.movieId.setText(movie.getId());

        if(lastAnimatedItemPosition < position){
            animateItem(holder.itemView);
            lastAnimatedItemPosition = position;
        }

        if(mItemsOnClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemsOnClickListener.onClick(movies.get(position));
                }
            });
        }
    }
    private void animateItem(View view) {
        view.setTranslationY(Util.getScreenHeight((Activity) view.getContext()));
        view.animate()
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator(3.f))
                .setDuration(700)
                .start();
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public interface OnItemClickListener{
        void onClick(Movie movie);
    }

    private OnItemClickListener mItemsOnClickListener;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView movieName;
        public final TextView movieId;
        public final View mTextContainer;

        public ViewHolder(View view) {
            super(view);
            movieName = (TextView) view.findViewById(R.id.movie_name);
            movieId = (TextView) view.findViewById(R.id.movie_id);
            mTextContainer = view.findViewById(R.id.text_container);
        }
    }

}
