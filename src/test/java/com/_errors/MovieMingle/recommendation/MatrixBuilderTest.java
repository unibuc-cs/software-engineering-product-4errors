package com._errors.MovieMingle.recommendation;

import com._errors.MovieMingle.model.AppUser;
import com._errors.MovieMingle.model.Movie;
import com._errors.MovieMingle.model.Rating;
import com._errors.MovieMingle.repository.AppUserRepository;
import com._errors.MovieMingle.repository.MovieRepository;
import com._errors.MovieMingle.repository.RatingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MatrixBuilderTest {

    @Mock
    private AppUserRepository userRepository;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private RatingRepository ratingRepository;

    @InjectMocks
    private MatrixBuilder matrixBuilder;

    private AppUser user;
    private Movie movie;
    private Rating rating;

    @BeforeEach
    public void setUp() {
        user = new AppUser();
        user.setId(1);

        movie = new Movie();
        movie.setMovieId(1L);

        rating = new Rating();
        rating.setUser(user);
        rating.setMovie(movie);
        rating.setRating((int) 8.0);
    }

    @Test
    public void testBuildMatrix() {
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));
        when(movieRepository.findAll()).thenReturn(Collections.singletonList(movie));
        when(ratingRepository.findByUser_Id(user.getId())).thenReturn(Collections.singletonList(rating));

        RatingMatrix ratingMatrix = matrixBuilder.buildMatrix();

        assertNotNull(ratingMatrix);
        assertEquals(1, ratingMatrix.getMatrix().length);
        assertEquals(1, ratingMatrix.getMatrix()[0].length);
        assertEquals(8.0, ratingMatrix.getMatrix()[0][0]);
        assertEquals(user.getId(), ratingMatrix.getUserIds().get(0));
        assertEquals(movie.getMovieId(), ratingMatrix.getMovieIds().get(0));

        verify(userRepository, times(1)).findAll();
        verify(movieRepository, times(1)).findAll();
        verify(ratingRepository, times(1)).findByUser_Id(user.getId());
    }
}