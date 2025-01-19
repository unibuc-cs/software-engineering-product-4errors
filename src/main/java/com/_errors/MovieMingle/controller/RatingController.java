package com._errors.MovieMingle.controller;

import com._errors.MovieMingle.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movies/ratings")
public class RatingController {
    @Autowired
    private RatingService ratingService;

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
        return ratingService.getUserRating(userId, movieId);
    }
}