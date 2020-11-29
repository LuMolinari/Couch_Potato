package com.example.couchpotato;

public class BookmarkItem {
    private final String mMoviePoster;
    private final String mMovieTitle;
    private final String mReleaseYear;
    private final String mReviewScore;
    private boolean isBookMarked;



    private boolean isFavoriteMovie;


    public BookmarkItem(String moviePoster, String movieTitle, String releaseYear, String reviewScore, boolean isBookmarked ,boolean isFavoriteMovie){
        mMoviePoster = moviePoster;
        mMovieTitle = movieTitle;
        mReleaseYear = releaseYear;
        mReviewScore = reviewScore;
        this.isBookMarked = isBookmarked;
        this.isFavoriteMovie = isFavoriteMovie;
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

    public boolean isBookMarked() {
        return isBookMarked;
    }

    public void setBookMarked(boolean isBookMarked){
        this.isBookMarked = isBookMarked;
    }

    public boolean isFavoriteMovie() {
        return isFavoriteMovie;
    }

    public void setFavoriteMovie(boolean favoriteMovie) {
        isFavoriteMovie = favoriteMovie;
    }
}
