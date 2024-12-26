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

    public List<Movie> getRandomRecommendations() {

        //placeholder function for the recommendation algorithm
        List<Movie> allMovies = movieRepository.findAll();
        List<Movie> randomMovies = new ArrayList<>();

        if (!allMovies.isEmpty()) {
            Collections.shuffle(allMovies);
            for (int i = 0; i < 6 && i < allMovies.size(); i++) {
                randomMovies.add(allMovies.get(i));
            }
        }

        return randomMovies;
    }
}
