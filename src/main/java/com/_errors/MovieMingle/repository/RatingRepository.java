package com._errors.MovieMingle.repository;

import com._errors.MovieMingle.model.AppUser;
import com._errors.MovieMingle.model.Movie;
import com._errors.MovieMingle.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    Optional<Rating> findByUserAndMovie(AppUser user, Movie movie);
    Rating findByUserIdAndMovie_MovieId(long userId,long movieId);

    List<Rating> findByMovie_MovieId(Long movieId);

    List<Rating> findByUser_Id(long userId);

    int countByUser_Id(long userId);
}
