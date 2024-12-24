package com._errors.MovieMingle.dto;

import com._errors.MovieMingle.model.Movie;
import java.util.List;

public class UserRatingRequest {

    private List<Movie> movies;
    private List<Integer> ratings;

    // Getters and setters
    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    public List<Integer> getRatings() {
        return ratings;
    }

    public void setRatings(List<Integer> ratings) {
        this.ratings = ratings;
    }
}

