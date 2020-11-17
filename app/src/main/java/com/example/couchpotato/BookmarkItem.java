package com.example.couchpotato;

import android.media.Image;

public class BookmarkItem {
    private int mMoviePoster;
    private String mMovieTitle, mReleaseYear, mReviewScore;

    public BookmarkItem (int moviePoster, String movieTitle, String releaseYear, String reviewScore){
        mMoviePoster = moviePoster;
        mMovieTitle = movieTitle;
        mReleaseYear = releaseYear;
        mReviewScore = reviewScore;
    }


    public int getMoviePoster() {
        return mMoviePoster;
    }

    public String getMovieTitle() {
        return mMovieTitle;
    }

    public String getReleaseYear() {
        return mReleaseYear;
    }

    public String getReviewScore() {
        return mReviewScore;
    }
}
