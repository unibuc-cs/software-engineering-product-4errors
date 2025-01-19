package com._errors.MovieMingle.repository;

import com._errors.MovieMingle.model.Movie;
import com._errors.MovieMingle.model.UserWatchedMovie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserWatchedMovieRepository extends JpaRepository<UserWatchedMovie, Long> {
    boolean existsByUserIdAndMovie_MovieId(Long userId, Long movieId);
    UserWatchedMovie findByUserIdAndMovie_MovieId(Long userId, Long movieId);
}
