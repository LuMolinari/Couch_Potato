package com.example.couchpotato;

import java.util.ArrayList;

public class MovieModelClass {
    private String id;
    private String title;
    private String img;
    private String reviewScore;
    private ArrayList<Integer> similarMoviesIds;

    public MovieModelClass(String id, String title, String img, String reviewScore, ArrayList<Integer> similarMoviesIds) {
        this.id = id;
        this.title = title;
        this.img = img;
        this.reviewScore = reviewScore;
        this.similarMoviesIds = similarMoviesIds;
    }

    public MovieModelClass() {

    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getImg() {
        return img;
    }

    public String getReviewScore() {
        return reviewScore;
    }

    public ArrayList<Integer> getSimilarMoviesIds() {
        return similarMoviesIds;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setReviewScore(String reviewScore) {
        this.reviewScore = reviewScore;
    }

    public void setSimilarMoviesIds(ArrayList<Integer> similarMoviesIds) {
        this.similarMoviesIds = similarMoviesIds;
    }
}
