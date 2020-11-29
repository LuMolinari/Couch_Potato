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

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.ViewHolder> {

    private final ArrayList<BookmarkItem> mBookmarkItems;
    private Context mContext;
    private final ItemListener mitemListener;

    public BookmarkAdapter(ArrayList<BookmarkItem> bookmarkItems, ItemListener itemListener) {
        mBookmarkItems = bookmarkItems;
        this.mitemListener= itemListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_bookmark, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, mitemListener);
        mContext = parent.getContext();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookmarkItem currentItem = mBookmarkItems.get(position);


//      need to use string and load image with glide
//      holder.moviePoster.setImageResource(currentItem.getMoviePoster());
        Glide.with(mContext)
                .load("https://image.tmdb.org/t/p/w154" + mBookmarkItems.get(position).getMoviePoster())
                .into(holder.moviePoster);


        holder.movieTitle.setText(currentItem.getMovieTitle());
        holder.releaseYear.setText(currentItem.getReleaseYear());
        holder.reviewScore.setText(currentItem.getReviewScore());

        if (currentItem.isBookMarked()){
            holder.bookmarkButton.setImageResource(R.drawable.ic_baseline_bookmark_blue_24);
        }
        if (currentItem.isFavoriteMovie()){
            holder.favoriteButton.setImageResource(R.drawable.ic_baseline_favorite_red_24) ;
        }


    }

//
    public interface ItemListener{
        void  onItemCLicked(int position);
        void favoriteClicked(int position);
        void bookmarkClicked(int position);

    void shareClicked(int position);
    }

    @Override
    public int getItemCount() {
        return mBookmarkItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView moviePoster;
        public TextView movieTitle, releaseYear, reviewScore;
        ItemListener itemListener;
        ImageView favoriteButton;
        ImageView bookmarkButton;
        ImageView shareButton;

        public ViewHolder(@NonNull View itemView, ItemListener itemListener) {
            super(itemView);
            moviePoster = itemView.findViewById(R.id.img_moviePoster);
            movieTitle = itemView.findViewById(R.id.tv_movieTitle);
            releaseYear = itemView.findViewById(R.id.tv_releaseYear);
            reviewScore = itemView.findViewById(R.id.tv_reviewScore);
            this.itemListener = itemListener;



            //instantiate other buttons
            favoriteButton = itemView.findViewById(R.id.img_like);
            bookmarkButton = itemView.findViewById(R.id.img_bookmark);
            shareButton = itemView.findViewById(R.id.img_share);



            //link them to on click listener
            itemView.setOnClickListener(this);
            favoriteButton.setOnClickListener(this);
            bookmarkButton.setOnClickListener(this);
            shareButton.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.img_like){
                itemListener.favoriteClicked(getAdapterPosition());
            } else if (view.getId() == R.id.img_bookmark) {
                itemListener.bookmarkClicked(getAdapterPosition());
            } else if (view.getId() == R.id.img_share) {
                itemListener.shareClicked(getAdapterPosition());
            } else {
                itemListener.onItemCLicked(getAdapterPosition());
            }
        }
    }
}
