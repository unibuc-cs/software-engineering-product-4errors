package com._errors.MovieMingle.repository;

import com._errors.MovieMingle.model.UserFavourite;
import com._errors.MovieMingle.model.UserToWatch;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserToWatchRepository extends JpaRepository<UserToWatch, Long> {

    boolean existsByUserIdAndMovie_MovieId(Long userId, Long movieId);
    UserToWatch findByUserIdAndMovie_MovieId(Long userId, Long movieId);

    List<UserToWatch> findByUser_Id(Long userId);
}
