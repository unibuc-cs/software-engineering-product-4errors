package com._errors.MovieMingle.dto;

import java.util.Map;

public class UserDashboardDto {
    private int totalMoviesWatched;
    private int totalHoursWatched;
    private double averageRating;
    private String topRatedMovie;
    private String lowestRatedMovie;

    private Map<String, Integer> moviesPerMonth;
    private Map<String, Double> ratingsPerMonth;

    private Map<String, Integer> genresWatched;

    // Getters and Setters
    public int getTotalMoviesWatched() {
        return totalMoviesWatched;
    }

    public void setTotalMoviesWatched(int totalMoviesWatched) {
        this.totalMoviesWatched = totalMoviesWatched;
    }

    public int getTotalHoursWatched() {
        return totalHoursWatched;
    }

    public void setTotalHoursWatched(int totalHoursWatched) {
        this.totalHoursWatched = totalHoursWatched;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public String getTopRatedMovie() {
        return topRatedMovie;
    }

    public void setTopRatedMovie(String topRatedMovie) {
        this.topRatedMovie = topRatedMovie;
    }

    public String getLowestRatedMovie() {
        return lowestRatedMovie;
    }

    public void setLowestRatedMovie(String lowestRatedMovie) {
        this.lowestRatedMovie = lowestRatedMovie;
    }

    public Map<String, Integer> getMoviesPerMonth() {
        return moviesPerMonth;
    }

    public void setMoviesPerMonth(Map<String, Integer> moviesPerMonth) {
        this.moviesPerMonth = moviesPerMonth;
    }

    public Map<String, Double> getRatingsPerMonth() {
        return ratingsPerMonth;
    }

    public void setRatingsPerMonth(Map<String, Double> ratingsPerMonth) {
        this.ratingsPerMonth = ratingsPerMonth;
    }

    public Map<String, Integer> getGenresWatched() {
        return genresWatched;
    }

    public void setGenresWatched(Map<String, Integer> genresWatched) {
        this.genresWatched = genresWatched;
    }
}
