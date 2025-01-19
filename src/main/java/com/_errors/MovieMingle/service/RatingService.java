package com._errors.MovieMingle.service;

import com._errors.MovieMingle.dto.MovieDto;
import com._errors.MovieMingle.model.AppUser;
import com._errors.MovieMingle.model.Movie;
import com._errors.MovieMingle.model.Rating;
import com._errors.MovieMingle.repository.AppUserRepository;
import com._errors.MovieMingle.repository.MovieRepository;
import com._errors.MovieMingle.repository.RatingRepository;
import com._errors.MovieMingle.service.user.UserWatchedMovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RatingService {
    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private UserWatchedMovieService userWatchedMovieService;

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private MovieApiClient movieApiClient;

    public String addRating(Long userId, Long tmdbId, int rating) {
        // Find user
        AppUser user = userRepository.findById(userId);
        if (user == null) {
            return "User not found";
        }

        // Găsim sau creăm filmul
        Movie movie = movieRepository.findByTmdbId(tmdbId);

        //daca nu am salvat inca filmul in db il cream
        if(movie==null)
        {
            MovieDto movieDto=movieApiClient.getMovie(tmdbId);
            // Salvăm filmul în baza de date
            movie = new Movie();
            movie.setTMDBId(tmdbId);
            movie.setSeriesTitle(movieDto.getTitle());
            movie.setPosterLink(movieDto.getPosterPath());
            movie.setOverview(movieDto.getDescription());
            movie.setDirector(movieDto.getDirectorsNames());
            movie.setRuntime(movieDto.getRuntime().toString());
            movieRepository.save(movie);

        }

        // Check if rating already exists
        Rating existingRating = ratingRepository.findByUserIdAndMovie_MovieId(userId, movie.getMovieId());

        if (existingRating != null) {
            // Update existing rating
            existingRating.setRating(rating);
            ratingRepository.save(existingRating);
        } else {
            // Create new rating
            Rating newRating = new Rating();
            newRating.setUser(user);
            newRating.setMovie(movie);
            newRating.setRating(rating);
            ratingRepository.save(newRating);
        }

        // Add to watched list if not already there
        boolean isWatched = userWatchedMovieService.isMovieWatched(userId, tmdbId);
        if (!isWatched) {
            userWatchedMovieService.addMovieToWatched(userId, tmdbId, movie.getSeriesTitle());
        }

        return "Rating added successfully";
    }

    public String removeRating(Long userId, Long tmdbId) {
        // Find user
        AppUser user = userRepository.findById(userId);
        if (user == null) {
            return "User not found";
        }

        // Find movie
        Movie movie = movieRepository.findByTmdbId(tmdbId);
        if (movie == null) {
            return "Movie not found";
        }

        // Find and remove rating
        Rating existingRating = ratingRepository.findByUserIdAndMovie_MovieId(userId, movie.getMovieId());
        if (existingRating != null) {
            ratingRepository.delete(existingRating);
            return "Rating removed successfully";
        }

        return "Rating not found";
    }

    public Double getAverageRating(Long movieId) {
        List<Rating> ratings = ratingRepository.findByMovie_MovieId(movieId);
        if (ratings.isEmpty()) {
            return 0.0;
        }

        double sum = ratings.stream()
                .mapToInt(Rating::getRating)
                .sum();
        return sum / ratings.size();
    }

    public Integer getUserRating(Long userId, Long movieId) {
        Rating rating = ratingRepository.findByUserIdAndMovie_MovieId(userId, movieId);
        return rating != null ? rating.getRating() : null;
    }
}
