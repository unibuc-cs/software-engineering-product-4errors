package com._errors.MovieMingle.service.user;

import com._errors.MovieMingle.dto.RegisterDto;
import com._errors.MovieMingle.exception.InvalidTokenException;
import com._errors.MovieMingle.exception.UnknownIdentifierException;
import com._errors.MovieMingle.exception.UserAlreadyExistsException;
import com._errors.MovieMingle.model.AppUser;

public interface AppUserService {

    void register(final RegisterDto user) throws UserAlreadyExistsException;
    boolean checkIfUserExist(final String email);
    void sendRegistrationConfirmationEmail(final AppUser user);
    boolean verifyUser(final String token) throws InvalidTokenException;
    AppUser getUserById(final String id) throws UnknownIdentifierException;
    AppUser findByEmail(String email);
    AppUser processOAuth2User(String email, String firstName, String lastName, String provider);
}
