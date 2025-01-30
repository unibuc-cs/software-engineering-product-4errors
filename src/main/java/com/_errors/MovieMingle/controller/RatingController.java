package com._errors.MovieMingle.controller;

import com._errors.MovieMingle.model.Movie;
import com._errors.MovieMingle.repository.MovieRepository;
import com._errors.MovieMingle.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movies/ratings")
public class RatingController {
    @Autowired
    private RatingService ratingService;
    @Autowired
    private MovieRepository movieRepository;

    @PostMapping("/add")
    public String addRating(
            @RequestParam Long userId,
            @RequestParam Long tmdbId,
            @RequestParam int rating) {
        return ratingService.addRating(userId, tmdbId, rating);
    }

    @GetMapping("/average/{movieId}")
    public Double getAverageRating(@PathVariable Long movieId) {
        return ratingService.getAverageRating(movieId);
    }

    @DeleteMapping("/remove")
    public String removeRating(
            @RequestParam Long userId,
            @RequestParam Long tmdbId) {
        return ratingService.removeRating(userId, tmdbId);
    }
    @GetMapping("/user")
    public Integer getUserRating(
            @RequestParam Long userId,
            @RequestParam Long movieId) {

        Movie movie = movieRepository.findByTmdbId(movieId);

        if (movie == null) {
            System.out.println("⚠ Movie with TMDB ID " + movieId + " not found! Returning default rating...");
            return 0; // Sau return 0 dacă vrei să semnalizezi că nu există rating
        }

        return ratingService.getUserRating(userId, movie.getMovieId());
    }



}