package com._errors.MovieMingle.controller;
import com._errors.MovieMingle.service.user.UserWatchedMovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/movies/watched")
public class UserWatchedMovieController {

    private final UserWatchedMovieService userWatchedMovieService;

    @Autowired
    public UserWatchedMovieController(UserWatchedMovieService userWatchedMovieService) {
        this.userWatchedMovieService = userWatchedMovieService;
    }

    @PostMapping("/add")
    public String addMovieToWatched(@RequestBody Map<String, Object> request) {
        Long userId = Long.parseLong(request.get("userId").toString());
        Long tmdbId = Long.parseLong(request.get("tmdbId").toString());
        String title = request.get("title").toString();

        return userWatchedMovieService.addMovieToWatched(userId, tmdbId, title);
    }
}
