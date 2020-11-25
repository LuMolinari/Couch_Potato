package com.example.couchpotato;

public class BookmarkItem {
    private final String mMoviePoster;
    private final String mMovieTitle;
    private final String mReleaseYear;
    private final String mReviewScore;

    public BookmarkItem (String moviePoster, String movieTitle, String releaseYear, String reviewScore){
        mMoviePoster = moviePoster;
        mMovieTitle = movieTitle;
        mReleaseYear = releaseYear;
        mReviewScore = reviewScore;
    }


    public String getMoviePoster() {
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
