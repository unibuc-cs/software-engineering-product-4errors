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

        // Initialize the movies list
        List<Movie> movies;

        if (query != null && !query.trim().isEmpty()) {
            // Perform the search with the query string
            movies = movieService.searchMovies(query);
        } else {
            // If no query is entered, show all movies
            movies = movieService.getAllMovies();
        }

        // Add the movies list and query to the model
        model.addAttribute("movies", movies != null ? movies : List.of()); // Ensure empty list if null
        model.addAttribute("query", query);

        return "search"; // Return the search page view
    }
}
