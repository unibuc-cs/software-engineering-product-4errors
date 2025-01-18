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
        return new ArrayList<>();
    }
}
