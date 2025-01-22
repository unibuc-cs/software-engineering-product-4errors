package com._errors.MovieMingle.controller;

import com._errors.MovieMingle.model.AppUser;
import com._errors.MovieMingle.model.Movie;
import com._errors.MovieMingle.recommendation.SVDRecommendationService;
import com._errors.MovieMingle.repository.AppUserRepository;
import com._errors.MovieMingle.service.RecommendationService;
import com._errors.MovieMingle.service.user.AppUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;
    @Autowired
    private AppUserRepository userRepository;
    @Autowired
    private AppUserService userService;
    @Autowired
    private SVDRecommendationService svdRecommendationService; // Adăugăm serviciul SVD

    @GetMapping("/recommendations")
    public String showRecommendations(Model model, Principal principal) {
        AppUser user;
        List<Movie> recommendations = new ArrayList<>();

        if (principal != null) {
            user = userService.findByEmail(principal.getName());
            System.out.println("User found: " + user.getId()); // Debug log

            if (user.getAvatar() == null) {
                user.setAvatar("general_avatar.png");
            }

            try {

                recommendations = svdRecommendationService.recommendMovies((long) user.getId(), 10);

            } catch (Exception e) {
                System.out.println("Error getting recommendations: " + e.getMessage()); // Debug log
                e.printStackTrace();
            }
        } else {
            user = new AppUser();
            user.setAvatar("general_avatar.png");
        }

        model.addAttribute("user", user);
        model.addAttribute("recommendedMovies", recommendations);
        return "recommendations";
    }
}