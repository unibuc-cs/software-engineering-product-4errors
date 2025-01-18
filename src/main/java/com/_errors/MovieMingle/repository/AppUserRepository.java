package com._errors.MovieMingle.repository;

import com._errors.MovieMingle.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, Integer> {
    public AppUser findByEmail(String email);
    public AppUser findById(Long id);
}
