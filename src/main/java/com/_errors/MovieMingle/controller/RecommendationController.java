package com._errors.MovieMingle.controller;

import com._errors.MovieMingle.model.AppUser;
import com._errors.MovieMingle.model.Movie;
import com._errors.MovieMingle.repository.AppUserRepository;
import com._errors.MovieMingle.service.RecommendationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;
    @Autowired
    private AppUserRepository userRepository;

    @GetMapping("/recommendations")
    public String showRecommendations(Model model, HttpSession session) {
        String loggedUser = SecurityContextHolder.getContext().getAuthentication().getName();
        AppUser user = userRepository.findByEmail(loggedUser);

        Boolean isNewLogin = (Boolean) session.getAttribute("isNewLogin");
        List<Movie> recommendedMovies;

        //show new recommendations only if it is a new login
        if (isNewLogin == null || isNewLogin) {
            recommendedMovies = recommendationService.getRandomRecommendations();
            session.setAttribute("cachedRecommendations", recommendedMovies);
            session.setAttribute("isNewLogin", false);
        } else {

            recommendedMovies = (List<Movie>) session.getAttribute("cachedRecommendations");
            if (recommendedMovies == null) {
                recommendedMovies = recommendationService.getRandomRecommendations();
                session.setAttribute("cachedRecommendations", recommendedMovies);
            }
        }

        model.addAttribute("recommendedMovies", recommendedMovies);
        return "recommendations";
    }
}

