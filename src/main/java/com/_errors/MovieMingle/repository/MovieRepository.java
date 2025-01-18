package com._errors.MovieMingle.repository;

import com._errors.MovieMingle.model.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;
import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    List<Movie> findBySeriesTitleContainingIgnoreCase(String seriesTitle);
    @Query(value = "SELECT * FROM movie_test ORDER BY RAND() LIMIT 15", nativeQuery = true)
    List<Movie> findRandomMovies();

    @Query(value = "SELECT * FROM movie_test where imdb_rating> 8.5", nativeQuery = true)
    ArrayList<Movie> findHighRated();

    Movie findByTmdbId(Long tmdbId);

}
