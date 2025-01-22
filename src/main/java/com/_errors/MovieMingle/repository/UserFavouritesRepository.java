package com._errors.MovieMingle.repository;

import com._errors.MovieMingle.model.UserFavourite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserFavouritesRepository extends JpaRepository<UserFavourite, Long> {

    boolean existsByUserIdAndMovie_MovieId(Long userId, Long movieId);
    UserFavourite findByUserIdAndMovie_MovieId(Long userId, Long movieId);

    List<UserFavourite> findByUser_Id(Long userId);
}
