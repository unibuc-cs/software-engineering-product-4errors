package com._errors.MovieMingle.service;

import com._errors.MovieMingle.model.SecureToken;

public interface SecureTokenService {
    SecureToken createSecureToken();
    void saveSecureToken(final SecureToken token);
    SecureToken findByToken(final String token);
    void removeToken(final SecureToken token);
    void removeTokenByToken(final String token);
}
