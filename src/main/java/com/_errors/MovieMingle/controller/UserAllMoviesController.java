package com._errors.MovieMingle.controller;
import com._errors.MovieMingle.model.AppUser;
import com._errors.MovieMingle.model.Movie;
import com._errors.MovieMingle.model.UserToWatch;
import com._errors.MovieMingle.repository.AppUserRepository;
import com._errors.MovieMingle.repository.RatingRepository;
import com._errors.MovieMingle.service.RatingService;
import com._errors.MovieMingle.service.user.UserFavouritesService;
import com._errors.MovieMingle.service.user.UserToWatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com._errors.MovieMingle.service.user.UserWatchedMovieService;
import java.util.List;


@Controller
@RequestMapping("/mylists")
public class UserAllMoviesController {
    @Autowired
    AppUserRepository appUserRepository;
    private final UserWatchedMovieService watchedService;
    @Autowired
    private RatingRepository ratingRepository;
    @Autowired
    private RatingService ratingService;

    @Autowired
    public UserAllMoviesController(UserWatchedMovieService watchedService) {
        this.watchedService = watchedService;
    }
    @Autowired
    private UserFavouritesService favouritesService;
    @Autowired
    private UserToWatchService toWatchService;

    @GetMapping
    public String myLists(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        AppUser currentUser = appUserRepository.findByEmail(userEmail);
        int userId= currentUser.getId();

        List<Movie> watched= watchedService.getUserWatchedMovies(userId);
        List<Movie> favourites= favouritesService.getUserFavouriteMovies(userId);
        List<Movie> towatchmovies= toWatchService.getUserToWatchMovies(userId);
        List<Movie> ratedMovies = ratingService.getUserRatedMovies(userId);
        model.addAttribute("user", currentUser);
        model.addAttribute("watched", watched);
        model.addAttribute("watchlist", towatchmovies);
        model.addAttribute("favourites",favourites);
        model.addAttribute("rated", ratedMovies);
        return "mylists";
    }
}