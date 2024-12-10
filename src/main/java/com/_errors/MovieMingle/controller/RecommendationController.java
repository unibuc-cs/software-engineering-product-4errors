package com._errors.MovieMingle.controller;

import com._errors.MovieMingle.service.RecommendationService;
import com._errors.MovieMingle.model.Movie;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    // Endpoint for recommendations
    @GetMapping("/recommendations")
    public String showRecommendations(Model model) {
        // Placeholder movies from the service
        List<Movie> recommendedMovies = recommendationService.getPlaceholderRecommendations();

        // Add placeholder data to the model
        model.addAttribute("recommendedMovies", recommendedMovies);
        return "recommendations";
    }
}
