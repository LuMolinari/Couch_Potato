package com.example.couchpotato;

import java.util.List;

public class MovieSingleton {

    private static MovieSingleton movieSingleton = new MovieSingleton();
    private static String movieId;
    private static MovieModelClass movieModelClass;
    private MovieSingleton() {

    }

    public static MovieSingleton getInstance() {
        return movieSingleton;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getMovieId() {
        return movieId;
    }

    public MovieModelClass getMovieModelClass() {
        return movieModelClass;
    }

    public void setMovieModelClass(MovieModelClass movieModelClass) {
        MovieSingleton.movieModelClass = movieModelClass;
    }
}
