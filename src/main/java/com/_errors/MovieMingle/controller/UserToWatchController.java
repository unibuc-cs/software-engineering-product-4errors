package com._errors.MovieMingle.controller;

import com._errors.MovieMingle.service.user.UserFavouritesService;
import com._errors.MovieMingle.service.user.UserToWatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
@RestController
@RequestMapping("/api/movies/watchlist")
public class UserToWatchController {

    private final UserToWatchService userToWatchService;

    @Autowired
    public UserToWatchController(UserToWatchService userToWatchService) {
        this.userToWatchService = userToWatchService;
    }

    @PostMapping("/add")
    public String addMovieTotoWatch(@RequestBody Map<String, Object> request) {
        Long userId = Long.parseLong(request.get("userId").toString());
        Long tmdbId = Long.parseLong(request.get("tmdbId").toString());
        String title = request.get("title").toString();

        return userToWatchService.addMovieTotoWatch(userId, tmdbId, title);
    }
    @DeleteMapping("/remove")
    @ResponseBody
    public String removeMovieFromToWatch(
            @RequestParam("userId") Long userId,
            @RequestParam("tmdbId") Long tmdbId) {
        return userToWatchService.removeFromToWatch(userId, tmdbId);
    }
    @GetMapping("/check")
    public boolean isMovieToWatch(@RequestParam Long userId, @RequestParam Long tmdbId) {
        return userToWatchService.isMovieToWatch(userId, tmdbId);
    }
}
