package com._errors.MovieMingle.service;

import com._errors.MovieMingle.dto.GenreDto;
import com._errors.MovieMingle.dto.MovieDto;
import com._errors.MovieMingle.dto.UserDashboardDto;
import com._errors.MovieMingle.model.Movie;
import com._errors.MovieMingle.model.Rating;
import com._errors.MovieMingle.model.UserWatchedMovie;
import com._errors.MovieMingle.repository.RatingRepository;
import com._errors.MovieMingle.repository.UserWatchedMovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DashboardServiceTest {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private UserWatchedMovieRepository watchedMovieRepository;

    @Mock
    private MovieApiClient movieApiClient;

    @InjectMocks
    private DashboardService dashboardService;

    private List<Rating> ratings;
    private List<UserWatchedMovie> watchedMovies;
    private Movie movie1, movie2;

    @BeforeEach
    public void setUp() {
        movie1 = new Movie();
        movie1.setMovieId(1L);
        movie1.setSeriesTitle("Movie 1");
        movie1.setRuntime("120"); // 2 ore

        movie2 = new Movie();
        movie2.setMovieId(2L);
        movie2.setSeriesTitle("Movie 2");
        movie2.setRuntime("90"); // 1.5 ore

        ratings = Arrays.asList(
                new Rating(1L, movie1, (int) 8),
                new Rating(2L, movie2, (int) 6)
        );

        watchedMovies = Arrays.asList(
                new UserWatchedMovie(1L, movie1, LocalDate.now().atStartOfDay()),
                new UserWatchedMovie(2L, movie2, LocalDate.now().minusMonths(1).atStartOfDay())
        );
    }

    @Test
    public void testGetUserDashboardStats() {
        Long userId = 1L;

        movie1.setTMDBId(1L);
        movie2.setTMDBId(2L);

        // simulam comportamentul repository-urilor
        when(ratingRepository.findByUser_Id(userId)).thenReturn(ratings);
        when(watchedMovieRepository.findByUser_Id(userId)).thenReturn(watchedMovies);

        // simulam comportamentul MovieApiClient
        MovieDto movieDetails1 = new MovieDto();
        movieDetails1.setGenres(Arrays.asList(new GenreDto("Action"), new GenreDto("Adventure")));

        MovieDto movieDetails2 = new MovieDto();
        movieDetails2.setGenres(Arrays.asList(new GenreDto("Drama")));

        when(movieApiClient.getMovie(1L)).thenReturn(movieDetails1);
        when(movieApiClient.getMovie(2L)).thenReturn(movieDetails2);

        UserDashboardDto result = dashboardService.getUserDashboardStats(userId);

        assertNotNull(result);
        assertEquals(2, result.getTotalMoviesWatched()); // 2 filme evaluate
        assertEquals(7, result.getAverageRating()); // (8 + 6) / 2 = 7
        assertEquals("Movie 1", result.getTopRatedMovie());
        assertEquals("Movie 2", result.getLowestRatedMovie());

        // total ore vizionate: (120 + 90) / 60 = 3.5 ore
        assertEquals(3, result.getTotalHoursWatched());

        // verificam statisticile pe luni
        Map<String, Integer> expectedMoviesPerMonth = new HashMap<>();
        expectedMoviesPerMonth.put(LocalDate.now().getMonth().getDisplayName(java.time.format.TextStyle.FULL, Locale.ENGLISH), 1);
        expectedMoviesPerMonth.put(LocalDate.now().minusMonths(1).getMonth().getDisplayName(java.time.format.TextStyle.FULL, Locale.ENGLISH), 1);
        assertEquals(expectedMoviesPerMonth, result.getMoviesPerMonth());

        // verificam genurile vizionate
        Map<String, Integer> expectedGenresWatched = new HashMap<>();
        expectedGenresWatched.put("Action", 1);
        expectedGenresWatched.put("Adventure", 1);
        expectedGenresWatched.put("Drama", 1);
        assertEquals(expectedGenresWatched, result.getGenresWatched());

        // verificam ca metodele au fost apelate
        verify(ratingRepository, times(1)).findByUser_Id(userId);
        verify(watchedMovieRepository, times(1)).findByUser_Id(userId);
        verify(movieApiClient, times(2)).getMovie(anyLong()); // apelat pentru fiecare film
    }


    @Test
    public void testGetUserDashboardStats_NoData() {
        Long userId = 1L;

        // simulăm ca nu există date
        when(ratingRepository.findByUser_Id(userId)).thenReturn(Collections.emptyList());
        when(watchedMovieRepository.findByUser_Id(userId)).thenReturn(Collections.emptyList());

        UserDashboardDto result = dashboardService.getUserDashboardStats(userId);

        assertNotNull(result);
        assertEquals(0, result.getTotalMoviesWatched());
        assertEquals(0.0, result.getAverageRating());
        assertEquals("No Data", result.getTopRatedMovie());
        assertEquals("No Data", result.getLowestRatedMovie());

        assertEquals(0, result.getTotalHoursWatched());
        assertTrue(result.getMoviesPerMonth().isEmpty());
        assertTrue(result.getGenresWatched().isEmpty());

        verify(ratingRepository, times(1)).findByUser_Id(userId);
        verify(watchedMovieRepository, times(1)).findByUser_Id(userId);
        verify(movieApiClient, never()).getMovie(anyLong());
    }
}