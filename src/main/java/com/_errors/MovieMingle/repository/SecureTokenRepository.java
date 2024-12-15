package com._errors.MovieMingle.repository;

import com._errors.MovieMingle.model.SecureToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecureTokenRepository extends JpaRepository<SecureToken, Long> {
    SecureToken findByToken(final String token);
    Long removeByToken(String token);

}
