package com._errors.MovieMingle.controller;

import com._errors.MovieMingle.dto.ActorDto;
import com._errors.MovieMingle.dto.MovieDto;
import com._errors.MovieMingle.service.MovieApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/movies")
public class MovieController {


    @Autowired
    private MovieApiClient movieApiClient;

    // Obține detaliile unui film
    @GetMapping("/{id}")
    public MovieDto getMovieDetails(@PathVariable Long id) {
        return movieApiClient.getMovie(id); // Întoarcem datele filmului
    }

    // Obține filmele populare
    @GetMapping("/popular")
    public List<MovieDto> getPopularMovies() {
        return movieApiClient.getPopularMovies();
    }

    @GetMapping("/top-rated")
    public List<MovieDto> getTopRatedMovies() {
        return movieApiClient.getTopRatedMovies();
    }

    @GetMapping("/{id}/recommendations")
    public List<MovieDto> getRecommendationsMovies(@PathVariable Long id) {
        return movieApiClient.getRecommendationsMovies(id);
    }

    @GetMapping("{id}/movie-cast")
    public List<ActorDto> getMovieCast(@PathVariable Long id) {
        return movieApiClient.getMovieCast(id);
    }

    @GetMapping("/page")
    public List<MovieDto> getMoviesByPage(@RequestParam int page) {
        return movieApiClient.getMoviesByPage(page);
    }

    @GetMapping("/search")
    public List<MovieDto> getMoviesBySearch(@RequestParam String query, @RequestParam int page) {
        return movieApiClient.getMoviesBySearch(query,page);
    }

    @GetMapping("/genres")
    public List<MovieDto> getMoviesByGenre(@RequestParam String genres, @RequestParam int page) {
        return movieApiClient.getMoviesByGenre(genres,page);
    }

}
