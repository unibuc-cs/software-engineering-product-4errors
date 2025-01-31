package com._errors.MovieMingle.service;

import com._errors.MovieMingle.dto.MovieDto;
import com._errors.MovieMingle.model.AppUser;
import com._errors.MovieMingle.model.Movie;
import com._errors.MovieMingle.model.Rating;
import com._errors.MovieMingle.recommendation.SVDRecommendationService;
import com._errors.MovieMingle.repository.AppUserRepository;
import com._errors.MovieMingle.repository.MovieRepository;
import com._errors.MovieMingle.repository.RatingRepository;
import com._errors.MovieMingle.service.user.UserWatchedMovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private UserWatchedMovieService userWatchedMovieService;

    @Mock
    private AppUserRepository userRepository;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private MovieApiClient movieApiClient;

    @Mock
    private SVDRecommendationService svdRecommendationService;

    @InjectMocks
    private RatingService ratingService;

    private AppUser user;
    private Movie movie;
    private Rating rating;

    @BeforeEach
    public void setUp() {
        user = new AppUser();
        user.setId(1);
        user.setEmail("test@example.com");

        movie = new Movie();
        movie.setMovieId(1L);
        movie.setTMDBId(123L);
        movie.setSeriesTitle("Test Movie");

        rating = new Rating();
        rating.setId(1L);
        rating.setUser(user);
        rating.setMovie(movie);
        rating.setRating(8);
    }

    @Test
    public void testAddRating_UserNotFound() {
        
        when(userRepository.findById(1L)).thenReturn(null);

        
        String result = ratingService.addRating(1L, 123L, 8);

        
        assertEquals("User not found", result);
        verify(userRepository, times(1)).findById(1L);
        verify(movieRepository, never()).findByTmdbId(anyLong());
        verify(ratingRepository, never()).save(any());
    }

    @Test
    public void testAddRating_NewMovie() {
        
        when(userRepository.findById(1L)).thenReturn(user);
        when(movieRepository.findByTmdbId(123L)).thenReturn(null);

        MovieDto movieDto = new MovieDto();
        movieDto.setTitle("Test Movie");
        movieDto.setPosterPath("/poster.jpg");
        movieDto.setDirectors(List.of());
        movieDto.setRuntime(120);

        when(movieApiClient.getMovie(123L)).thenReturn(movieDto);

        // configuram movieRepository.save sa returneze un film cu movieId setat
        when(movieRepository.save(any(Movie.class))).thenAnswer(invocation -> {
            Movie savedMovie = invocation.getArgument(0); // obtinem filmul salvat
            savedMovie.setMovieId(1L); // setam movieId
            return savedMovie;
        });

        when(ratingRepository.findByUserIdAndMovie_MovieId(1L, 1L)).thenReturn(null);
        when(ratingRepository.save(any(Rating.class))).thenReturn(rating);
        when(userWatchedMovieService.isMovieWatched(1L, 123L)).thenReturn(false);

        
        String result = ratingService.addRating(1L, 123L, 8);

        
        assertEquals("Rating added successfully", result);
        verify(userRepository, times(1)).findById(1L);
        verify(movieRepository, times(1)).findByTmdbId(123L);
        verify(movieApiClient, times(1)).getMovie(123L);
        verify(movieRepository, times(1)).save(any(Movie.class));
        verify(ratingRepository, times(1)).save(any(Rating.class));
        verify(userWatchedMovieService, times(1)).addMovieToWatched(1L, 123L, "Test Movie");
        verify(svdRecommendationService, times(1)).updateRating();
    }

    @Test
    public void testAddRating_ExistingMovie() {
        
        when(userRepository.findById(1L)).thenReturn(user);
        when(movieRepository.findByTmdbId(123L)).thenReturn(movie);
        when(ratingRepository.findByUserIdAndMovie_MovieId(1L, 1L)).thenReturn(rating);
        when(ratingRepository.save(any(Rating.class))).thenReturn(rating);
        when(userWatchedMovieService.isMovieWatched(1L, 123L)).thenReturn(true);

        
        String result = ratingService.addRating(1L, 123L, 9);

        
        assertEquals("Rating added successfully", result);
        verify(userRepository, times(1)).findById(1L);
        verify(movieRepository, times(1)).findByTmdbId(123L);
        verify(ratingRepository, times(1)).save(rating);
        verify(userWatchedMovieService, never()).addMovieToWatched(anyLong(), anyLong(), anyString());
        verify(svdRecommendationService, times(1)).updateRating();
    }

    @Test
    public void testRemoveRating_UserNotFound() {
        
        when(userRepository.findById(1L)).thenReturn(null);

        
        String result = ratingService.removeRating(1L, 123L);

        
        assertEquals("User not found", result);
        verify(userRepository, times(1)).findById(1L);
        verify(movieRepository, never()).findByTmdbId(anyLong());
        verify(ratingRepository, never()).delete(any());
    }

    @Test
    public void testRemoveRating_MovieNotFound() {
        
        when(userRepository.findById(1L)).thenReturn(user);
        when(movieRepository.findByTmdbId(123L)).thenReturn(null);

        
        String result = ratingService.removeRating(1L, 123L);

        
        assertEquals("Movie not found", result);
        verify(userRepository, times(1)).findById(1L);
        verify(movieRepository, times(1)).findByTmdbId(123L);
        verify(ratingRepository, never()).delete(any());
    }

    @Test
    public void testRemoveRating_RatingNotFound() {
        
        when(userRepository.findById(1L)).thenReturn(user);
        when(movieRepository.findByTmdbId(123L)).thenReturn(movie);
        when(ratingRepository.findByUserIdAndMovie_MovieId(1L, 1L)).thenReturn(null);

        
        String result = ratingService.removeRating(1L, 123L);

        
        assertEquals("Rating not found", result);
        verify(userRepository, times(1)).findById(1L);
        verify(movieRepository, times(1)).findByTmdbId(123L);
        verify(ratingRepository, times(1)).findByUserIdAndMovie_MovieId(1L, 1L);
        verify(ratingRepository, never()).delete(any());
    }

    @Test
    public void testRemoveRating_Success() {
        
        when(userRepository.findById(1L)).thenReturn(user);
        when(movieRepository.findByTmdbId(123L)).thenReturn(movie);
        when(ratingRepository.findByUserIdAndMovie_MovieId(1L, 1L)).thenReturn(rating);

        
        String result = ratingService.removeRating(1L, 123L);

        
        assertEquals("Rating removed successfully", result);
        verify(userRepository, times(1)).findById(1L);
        verify(movieRepository, times(1)).findByTmdbId(123L);
        verify(ratingRepository, times(1)).findByUserIdAndMovie_MovieId(1L, 1L);
        verify(ratingRepository, times(1)).delete(rating);
    }

    @Test
    public void testGetAverageRating_NoRatings() {
        
        when(ratingRepository.findByMovie_MovieId(1L)).thenReturn(Collections.emptyList());

        
        Double result = ratingService.getAverageRating(1L);

        
        assertEquals(0.0, result);
        verify(ratingRepository, times(1)).findByMovie_MovieId(1L);
    }

    @Test
    public void testGetAverageRating_WithRatings() {
        
        List<Rating> ratings = Arrays.asList(
                new Rating(1L, movie, 8),
                new Rating(2L, movie, 6)
        );
        when(ratingRepository.findByMovie_MovieId(1L)).thenReturn(ratings);

        
        Double result = ratingService.getAverageRating(1L);

        
        assertEquals(7.0, result);
        verify(ratingRepository, times(1)).findByMovie_MovieId(1L);
    }

    @Test
    public void testGetUserRating_RatingNotFound() {
        
        when(ratingRepository.findByUserIdAndMovie_MovieId(1L, 1L)).thenReturn(null);

        
        Integer result = ratingService.getUserRating(1L, 1L);

        
        assertNull(result);
        verify(ratingRepository, times(1)).findByUserIdAndMovie_MovieId(1L, 1L);
    }

    @Test
    public void testGetUserRating_Success() {
        
        when(ratingRepository.findByUserIdAndMovie_MovieId(1L, 1L)).thenReturn(rating);

        
        Integer result = ratingService.getUserRating(1L, 1L);

        
        assertEquals(8, result);
        verify(ratingRepository, times(1)).findByUserIdAndMovie_MovieId(1L, 1L);
    }

    @Test
    public void testGetUserRatedMovies_NoRatings() {
        
        when(ratingRepository.findByUser_Id(1L)).thenReturn(Collections.emptyList());

        
        List<Movie> result = ratingService.getUserRatedMovies(1);

        
        assertTrue(result.isEmpty());
        verify(ratingRepository, times(1)).findByUser_Id(1L);
    }

    @Test
    public void testGetUserRatedMovies_WithRatings() {
        
        List<Rating> ratings = Arrays.asList(
                new Rating(1L, movie, 8),
                new Rating(2L, movie, 6)
        );
        when(ratingRepository.findByUser_Id(1L)).thenReturn(ratings);

        
        List<Movie> result = ratingService.getUserRatedMovies(1);

        
        assertEquals(2, result.size());
        assertEquals(movie, result.get(0));
        assertEquals(movie, result.get(1));
        verify(ratingRepository, times(1)).findByUser_Id(1L);
    }
}