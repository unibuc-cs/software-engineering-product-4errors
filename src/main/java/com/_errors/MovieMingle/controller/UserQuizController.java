package com._errors.MovieMingle.controller;

import com._errors.MovieMingle.dto.UserRatingRequest;
import com._errors.MovieMingle.model.AppUser;
import com._errors.MovieMingle.model.Movie;
import com._errors.MovieMingle.repository.AppUserRepository;
import com._errors.MovieMingle.service.UserQuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;



import java.util.List;

@Controller
public class UserQuizController {

    @Autowired
    private UserQuizService userQuizService;

    @Autowired
    private AppUserRepository userRepository;

    @GetMapping("/quiz")
    public String showQuizPage(Model model) {

        List<Movie> randomMovies = userQuizService.getRandomRecommendations();
        model.addAttribute("quizMovies", randomMovies);
        return "quiz";
    }

    @PostMapping("/submitRatings")
    public String submitRatings(@ModelAttribute UserRatingRequest userRatingRequest) {
        AppUser currentUser = getCurrentUser();
        currentUser.setQuizCompleted(true);
        userRepository.save(currentUser);
        userQuizService.saveUserRatingsAsync(currentUser, userRatingRequest.getMovies(), userRatingRequest.getRatings());

        return "exit-quiz";
    }

    private AppUser getCurrentUser() {
        String loggedUser = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(loggedUser);
    }
}
