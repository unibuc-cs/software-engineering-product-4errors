package com._errors.MovieMingle.service;

import com._errors.MovieMingle.model.Movie;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovieService {

    // Mocked list of movies (simulating database content)
    private List<Movie> movies = new ArrayList<>();

    // Constructor to initialize mock data with descriptions
    public MovieService() {
        movies.add(new Movie(1L, "Inception", "Sci-Fi", 2010, "A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a CEO."));
        movies.add(new Movie(2L, "The Matrix", "Action/Sci-Fi", 1999, "A computer hacker learns from mysterious rebels about the true nature of his reality and his role in the war against its controllers."));
        movies.add(new Movie(3L, "Interstellar", "Sci-Fi", 2014, "A team of explorers must travel through a wormhole in space in an attempt to ensure humanity's survival."));
        movies.add(new Movie(4L, "The Dark Knight", "Action/Drama", 2008, "When the menace known as the Joker emerges from his mysterious past, he wreaks havoc and chaos on the people of Gotham."));
        movies.add(new Movie(5L, "The Shawshank Redemption", "Drama", 1994, "Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency."));
    }

    // Method to search movies by title (case-insensitive)
    public List<Movie> searchMovies(String query) {
        return movies.stream()
                .filter(movie -> movie.getTitle().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }

    // Method to return all movies
    public List<Movie> getAllMovies() {
        return movies;
    }
}
