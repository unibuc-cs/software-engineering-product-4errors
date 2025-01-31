package com._errors.MovieMingle.service;

import com._errors.MovieMingle.dto.GenreDto;
import com._errors.MovieMingle.dto.MovieDto;
import com._errors.MovieMingle.dto.UserDashboardDto;
import com._errors.MovieMingle.model.Rating;
import com._errors.MovieMingle.model.UserWatchedMovie;
import com._errors.MovieMingle.repository.RatingRepository;
import com._errors.MovieMingle.repository.UserWatchedMovieRepository;
import org.springframework.stereotype.Service;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.util.Locale;

@Service
public class DashboardService {
    private final RatingRepository ratingRepository;
    private final UserWatchedMovieRepository watchedMovieRepository;
    private final MovieApiClient movieApiClient;

    public DashboardService(RatingRepository ratingRepository, UserWatchedMovieRepository watchedMovieRepository, MovieApiClient movieApiClient) {
        this.ratingRepository = ratingRepository;
        this.watchedMovieRepository = watchedMovieRepository;
        this.movieApiClient = movieApiClient;
    }

    public UserDashboardDto getUserDashboardStats(Long userId) {
        // Fetching user ratings
        List<Rating> ratings = ratingRepository.findByUser_Id(userId);

        // Fetching all user watch history
        List<UserWatchedMovie> watchedMovies = watchedMovieRepository.findByUser_Id(userId);

        UserDashboardDto stats = new UserDashboardDto();

        // Chart 1: Statistics based on ratings
        stats.setTotalMoviesWatched(ratings.size());

        stats.setAverageRating(ratings.stream()
                .mapToDouble(Rating::getRating)
                .average().orElse(0));

        stats.setTopRatedMovie(ratings.stream()
                .max(Comparator.comparing(Rating::getRating))
                .map(r -> r.getMovie().getSeriesTitle()).orElse("No Data"));

        stats.setLowestRatedMovie(ratings.stream()
                .min(Comparator.comparing(Rating::getRating))
                .map(r -> r.getMovie().getSeriesTitle()).orElse("No Data"));

        // Chart 2: Statistics based on watch history
        stats.setTotalHoursWatched(watchedMovies.stream()
                .mapToInt(w -> (w.getMovie().getRuntime() != null) ? Integer.parseInt(w.getMovie().getRuntime()) : 0)
                .sum() / 60);

        // Movies watched per month
        Map<String, Integer> moviesPerMonth = watchedMovies.stream()
                .collect(Collectors.groupingBy(
                        w -> w.getWatchedAt().getMonth()
                                .getDisplayName(TextStyle.FULL, Locale.ENGLISH),
                        Collectors.summingInt(w -> 1)
                ));
        stats.setMoviesPerMonth(moviesPerMonth);

        // Chart 3: Most watched genres (obtained from TMDB)
        Map<String, Integer> genresWatched = new HashMap<>();

        for (UserWatchedMovie watchedMovie : watchedMovies) {
            Long tmdbId = watchedMovie.getMovie().getTmdbId(); // TMDB ID
            if (tmdbId != null) {
                MovieDto movieDetails = movieApiClient.getMovie(tmdbId);
                if (movieDetails != null && movieDetails.getGenres() != null) {
                    for (GenreDto genre : movieDetails.getGenres()) {
                        genresWatched.put(genre.getName(), genresWatched.getOrDefault(genre.getName(), 0) + 1);
                    }
                }
            }
        }

        stats.setGenresWatched(genresWatched);
        return stats;
    }
}
