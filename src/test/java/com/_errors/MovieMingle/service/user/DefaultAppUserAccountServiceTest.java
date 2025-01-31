package com._errors.MovieMingle.service.user;

import com._errors.MovieMingle.exception.InvalidTokenException;
import com._errors.MovieMingle.exception.UnknownIdentifierException;
import com._errors.MovieMingle.model.AppUser;
import com._errors.MovieMingle.model.SecureToken;
import com._errors.MovieMingle.repository.AppUserRepository;
import com._errors.MovieMingle.repository.SecureTokenRepository;
import com._errors.MovieMingle.service.SecureTokenService;
import com._errors.MovieMingle.service.email.EmailService;
import com._errors.MovieMingle.service.email.context.ForgotPasswordEmailContext;
import jakarta.mail.MessagingException;
import org.apache.commons.codec.binary.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DefaultAppUserAccountServiceTest {

    @Mock
    private AppUserService userService;

    @Mock
    private SecureTokenService secureTokenService;

    @Mock
    private SecureTokenRepository secureTokenRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private AppUserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private DefaultAppUserAccountService userAccountService;

    private AppUser appUser;
    private SecureToken secureToken;

    @BeforeEach
    public void setUp() {
        appUser = new AppUser();
        appUser.setId(123);
        appUser.setEmail("test@example.com");
        appUser.setPassword("oldPassword");

        secureToken = new SecureToken();
        secureToken.setToken("valid-token");
        secureToken.setUser(appUser);

        ReflectionTestUtils.setField(userAccountService, "baseURL", "http://localhost:8080");
    }

    @Test
    public void testForgottenPassword_Success() throws UnknownIdentifierException, MessagingException {
        when(userService.getUserById("test@example.com")).thenReturn(appUser);
        when(secureTokenService.createSecureToken()).thenReturn(secureToken);
        when(secureTokenRepository.save(any(SecureToken.class))).thenReturn(secureToken);
        doNothing().when(emailService).sendMail(any(ForgotPasswordEmailContext.class));

        userAccountService.forgottenPassword("test@example.com");

        verify(userService, times(1)).getUserById("test@example.com");
        verify(secureTokenService, times(1)).createSecureToken();
        verify(secureTokenRepository, times(1)).save(any(SecureToken.class));
        verify(emailService, times(1)).sendMail(any(ForgotPasswordEmailContext.class));
    }

    @Test
    public void testForgottenPassword_UserNotFound() throws MessagingException, UnknownIdentifierException {
        when(userService.getUserById("nonexistent@example.com")).thenThrow(new UnknownIdentifierException("User not found"));

        assertThrows(UnknownIdentifierException.class, () -> {
            userAccountService.forgottenPassword("nonexistent@example.com");
        });

        verify(userService, times(1)).getUserById("nonexistent@example.com");
        verify(secureTokenService, never()).createSecureToken();
        verify(emailService, never()).sendMail(any(ForgotPasswordEmailContext.class));
    }

    @Test
    public void testUpdatePassword_Success() throws InvalidTokenException, UnknownIdentifierException {
        when(secureTokenService.findByToken("valid-token")).thenReturn(secureToken);
        when(userRepository.findById(123)).thenReturn(Optional.of(appUser));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedPassword");

        userAccountService.updatePassword("newPassword", "valid-token");

        verify(secureTokenService, times(1)).findByToken("valid-token");
        verify(userRepository, times(1)).findById(123);
        verify(passwordEncoder, times(1)).encode("newPassword");
        verify(secureTokenService, times(1)).removeToken(secureToken);
        verify(userRepository, times(1)).save(appUser);

        assertEquals("encodedPassword", appUser.getPassword());
    }

    @Test
    public void testUpdatePassword_InvalidToken() {
        when(secureTokenService.findByToken("invalid-token")).thenReturn(null);

        assertThrows(InvalidTokenException.class, () -> {
            userAccountService.updatePassword("newPassword", "invalid-token");
        });

        verify(secureTokenService, times(1)).findByToken("invalid-token");
        verify(userRepository, never()).findById((Long) any());
        verify(passwordEncoder, never()).encode(any());
        verify(secureTokenService, never()).removeToken(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    public void testUpdatePassword_ExpiredToken() {
        secureToken.setExpired(true);
        when(secureTokenService.findByToken("expired-token")).thenReturn(secureToken);

        assertThrows(InvalidTokenException.class, () -> {
            userAccountService.updatePassword("newPassword", "expired-token");
        });

        verify(secureTokenService, times(1)).findByToken("expired-token");
        verify(userRepository, never()).findById((Long) any());
        verify(passwordEncoder, never()).encode(any());
        verify(secureTokenService, never()).removeToken(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    public void testUpdatePassword_UserNotFound() {
        when(secureTokenService.findByToken("valid-token")).thenReturn(secureToken);
        when(userRepository.findById(123)).thenReturn(Optional.empty());

        assertThrows(UnknownIdentifierException.class, () -> {
            userAccountService.updatePassword("newPassword", "valid-token");
        });

        verify(secureTokenService, times(1)).findByToken("valid-token");
        verify(userRepository, times(1)).findById(123);
        verify(passwordEncoder, never()).encode(any());
        verify(secureTokenService, never()).removeToken(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    public void testSendResetPasswordEmail_Success() throws MessagingException {
        when(secureTokenService.createSecureToken()).thenReturn(secureToken);
        when(secureTokenRepository.save(any(SecureToken.class))).thenReturn(secureToken);
        doNothing().when(emailService).sendMail(any(ForgotPasswordEmailContext.class));

        userAccountService.sendResetPasswordEmail(appUser);

        verify(secureTokenService, times(1)).createSecureToken();
        verify(secureTokenRepository, times(1)).save(any(SecureToken.class));
        verify(emailService, times(1)).sendMail(any(ForgotPasswordEmailContext.class));
    }
}