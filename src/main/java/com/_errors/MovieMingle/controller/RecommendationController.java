package com._errors.MovieMingle.controller;

import com._errors.MovieMingle.model.Movie;
import com._errors.MovieMingle.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    @GetMapping("/recommendations")
    public String showRecommendations(Model model) {
        // Fetch recommended movies
        List<Movie> recommendedMovies = recommendationService.getRandomRecommendations();

        // Add the list of recommended movies to the model
        model.addAttribute("recommendedMovies", recommendedMovies);

        // Return the recommendations view
        return "recommendations";
    }
}

