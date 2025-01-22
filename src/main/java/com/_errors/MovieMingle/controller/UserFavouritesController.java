package com._errors.MovieMingle.controller;
import com._errors.MovieMingle.service.user.UserFavouritesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
@RestController
@RequestMapping("/api/movies/favourites")
public class UserFavouritesController {
    private final UserFavouritesService userFavouritesService;

    @Autowired
    public UserFavouritesController(UserFavouritesService userFavouritesService) {
        this.userFavouritesService = userFavouritesService;
    }

    @PostMapping("/add")
    public String addMovieToFavourites(@RequestBody Map<String, Object> request) {
        Long userId = Long.parseLong(request.get("userId").toString());
        Long tmdbId = Long.parseLong(request.get("tmdbId").toString());
        String title = request.get("title").toString();

        return userFavouritesService.addMovieToFavourites(userId, tmdbId, title);
    }
    @DeleteMapping("/remove")
    @ResponseBody
    public String removeMovieFromFavourites(
            @RequestParam("userId") Long userId,
            @RequestParam("tmdbId") Long tmdbId) {
        return userFavouritesService.removeFromFavourites(userId, tmdbId);
    }
    @GetMapping("/check")
    public boolean isMovieFavourite(@RequestParam Long userId, @RequestParam Long tmdbId) {
        return userFavouritesService.isMovieFavourite(userId, tmdbId);
    }
}
