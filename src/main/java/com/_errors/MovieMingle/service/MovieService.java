package com._errors.MovieMingle.service;

import com._errors.MovieMingle.model.Movie;
import com._errors.MovieMingle.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;

    // Search movies by title (case-insensitive)
    public List<Movie> searchMovies(String query) {
        return movieRepository.findBySeriesTitleContainingIgnoreCase(query);
    }

    // Get all movies from the database
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public ArrayList<Movie> getBestRated(int number) {
        ArrayList<Movie> randomBestRated = new ArrayList<>();
        ArrayList<Movie> highRatedMovies = movieRepository.findHighRated();

        if (highRatedMovies == null || highRatedMovies.isEmpty()) {
            return randomBestRated;
        }

        Collections.shuffle(highRatedMovies);

        number = Math.min(number, highRatedMovies.size());
        for (int i = 0; i < number; i++) {
            randomBestRated.add(highRatedMovies.get(i));
        }

        return randomBestRated;
    }

}
