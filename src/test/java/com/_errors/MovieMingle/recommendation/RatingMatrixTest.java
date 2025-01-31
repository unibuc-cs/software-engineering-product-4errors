package com._errors.MovieMingle.recommendation;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RatingMatrixTest {

    @Test
    public void testRatingMatrix() {
        double[][] matrix = {{5.0, 3.0}, {4.0, 0.0}};
        List<Long> userIds = Arrays.asList(1L, 2L);
        List<Long> movieIds = Arrays.asList(101L, 102L);

        RatingMatrix ratingMatrix = new RatingMatrix(matrix, userIds, movieIds);

        assertArrayEquals(matrix, ratingMatrix.getMatrix());
        assertEquals(userIds, ratingMatrix.getUserIds());
        assertEquals(movieIds, ratingMatrix.getMovieIds());
        assertEquals(5.0, ratingMatrix.getRating(1L, 101L));
        assertEquals(0.0, ratingMatrix.getRating(2L, 102L));
    }
}