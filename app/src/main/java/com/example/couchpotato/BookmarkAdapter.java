package com.example.couchpotato;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.ViewHolder>{

    private final ArrayList<BookmarkItem> mBookmarkItems;
    private Context mContext;
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
        mContext = parent.getContext();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookmarkItem currentItem = mBookmarkItems.get(position);


//      need to use string and load image with glide
//      holder.moviePoster.setImageResource(currentItem.getMoviePoster());
        Glide.with(mContext)
                .load("https://image.tmdb.org/t/p/w500" + mBookmarkItems.get(position).getMoviePoster())
                .into(holder.moviePoster);


        holder.movieTitle.setText(currentItem.getMovieTitle());
        holder.releaseYear.setText(currentItem.getReleaseYear());
        holder.reviewScore.setText(currentItem.getReviewScore());
    }

    @Override
    public int getItemCount() {
        return mBookmarkItems.size();
    }

}
