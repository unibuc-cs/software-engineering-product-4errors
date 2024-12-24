package com._errors.MovieMingle.service;

import com._errors.MovieMingle.model.Movie;
import com._errors.MovieMingle.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class RecommendationService {

    @Autowired
    private MovieRepository movieRepository;

    // Get 5 random recommendations
    public List<Movie> getRandomRecommendations() {
        List<Movie> allMovies = movieRepository.findAll();  // Fetch all movies from the database
        List<Movie> randomMovies = new ArrayList<>();

        if (!allMovies.isEmpty()) {
            Collections.shuffle(allMovies);  // Shuffle the list randomly
            for (int i = 0; i < 5 && i < allMovies.size(); i++) {
                randomMovies.add(allMovies.get(i));  // Add the first 5 movies from the shuffled list
            }
        }

        return randomMovies;  // Return the random movie list
    }
}
