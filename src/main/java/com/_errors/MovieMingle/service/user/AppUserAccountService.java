package com._errors.MovieMingle.service.user;

import com._errors.MovieMingle.exception.InvalidTokenException;
import com._errors.MovieMingle.exception.UnknownIdentifierException;

public interface AppUserAccountService {
    void forgottenPassword(final String userName) throws UnknownIdentifierException;
    void updatePassword(final String password, final String token) throws InvalidTokenException, UnknownIdentifierException;
}
