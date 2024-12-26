package com._errors.MovieMingle.controller;

import com._errors.MovieMingle.model.Movie;
import com._errors.MovieMingle.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class MovieController {

    @Autowired
    private MovieService movieService;

    @GetMapping("/search")
    public String searchMovies(
            @RequestParam(name = "query", required = false) String query,
            Model model) {


        List<Movie> movies;

        if (query != null && !query.trim().isEmpty()) {

            movies = movieService.searchMovies(query);
        } else {
            movies = movieService.getAllMovies();
        }

        model.addAttribute("movies", movies != null ? movies : List.of());
        model.addAttribute("query", query);

        return "search";
    }
}
