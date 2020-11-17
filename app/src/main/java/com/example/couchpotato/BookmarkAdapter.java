package com.example.couchpotato;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.ViewHolder>{

    private ArrayList<BookmarkItem> mBookmarkItems;

    public BookmarkAdapter(ArrayList<BookmarkItem> bookmarkItems){
        mBookmarkItems = bookmarkItems;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView moviePoster;
        public TextView movieTitle, releaseYear, reviewScore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            moviePoster = itemView.findViewById(R.id.img_moviePoster);
            movieTitle = itemView.findViewById(R.id.tv_movieTitle);
            releaseYear = itemView.findViewById(R.id.tv_releaseYear);
            reviewScore = itemView.findViewById(R.id.tv_reviewScore);

        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_bookmark, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookmarkItem currentItem = mBookmarkItems.get(position);
        holder.moviePoster.setImageResource(currentItem.getMoviePoster());
        holder.movieTitle.setText(currentItem.getMovieTitle());
        holder.releaseYear.setText(currentItem.getReleaseYear());
        holder.reviewScore.setText(currentItem.getReviewScore());
    }

    @Override
    public int getItemCount() {
        return mBookmarkItems.size();
    }

}
