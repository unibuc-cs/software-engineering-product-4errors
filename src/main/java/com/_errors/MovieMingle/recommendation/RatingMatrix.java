package com._errors.MovieMingle.recommendation;

import java.util.List;

class RatingMatrix {
    private final double[][] matrix;
    private final List<Long> userIds;
    private final List<Long> movieIds;

    public RatingMatrix(double[][] matrix, List<Long> userIds, List<Long> movieIds) {
        this.matrix = matrix;
        this.userIds = userIds;
        this.movieIds = movieIds;
    }

    public double[][] getMatrix() {
        return matrix;
    }

    public List<Long> getUserIds() {
        return userIds;
    }

    public List<Long> getMovieIds() {
        return movieIds;
    }

    // Metodă helper pentru a obține rating pentru un user și film specific
    public double getRating(Long userId, Long movieId) {
        int userIndex = userIds.indexOf(userId);
        int movieIndex = movieIds.indexOf(movieId);

        if (userIndex != -1 && movieIndex != -1) {
            return matrix[userIndex][movieIndex];
        }
        return 0.0;
    }
}