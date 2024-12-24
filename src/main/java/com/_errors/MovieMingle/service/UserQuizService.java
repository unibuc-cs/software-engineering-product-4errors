package com._errors.MovieMingle.service;

import com._errors.MovieMingle.model.AppUser;
import com._errors.MovieMingle.model.Movie;
import com._errors.MovieMingle.model.Rating;
import com._errors.MovieMingle.repository.MovieRepository;
import com._errors.MovieMingle.repository.RatingRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class UserQuizService {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private RatingRepository ratingRepository;

    public List<Movie> getRandomRecommendations() {
        return movieRepository.findRandomMovies();
    }

    @Async
    public CompletableFuture<Void> saveUserRatingsAsync(AppUser user, List<Movie> movies, List<Integer> ratings) {
        saveUserRatings(user, movies, ratings);
        return CompletableFuture.completedFuture(null);
    }
    @Transactional
    public void saveUserRatings(AppUser user, List<Movie> movies, List<Integer> ratings) {
        List<Rating> ratingList = new ArrayList<>();
        for (int i = 0; i < movies.size(); i++) {
            Movie movie = movies.get(i);
            int newRatingValue = ratings.get(i);

            // Check if a rating already exists for this user and movie
            Optional<Rating> existingRating = ratingRepository.findByUserAndMovie(user, movie);

            if (existingRating.isPresent()) {
                // Update the existing rating
                Rating rating = existingRating.get();
                rating.setRating(newRatingValue);
                ratingList.add(rating);
            } else {
                // Create a new rating
                Rating rating = new Rating();
                rating.setUser(user);
                rating.setMovie(movie);
                rating.setRating(newRatingValue);
                ratingList.add(rating);
            }
        }

        // Save all updates and new ratings in a batch
        ratingRepository.saveAll(ratingList);
    }



}
