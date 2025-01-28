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
import com.github.javafaker.App;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import org.apache.commons.codec.binary.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class DefaultAppUserAccountService implements AppUserAccountService {

    @Resource
    AppUserService userService;

    @Resource
    private SecureTokenService secureTokenService;

    @Resource
    SecureTokenRepository secureTokenRepository;

    @Value("${site.base.url.https}")
    private String baseURL;

    @Resource
    private EmailService emailService;

    @Resource
    private AppUserRepository userRepository;

    @Resource
    private PasswordEncoder passwordEncoder;


    @Override
    public void forgottenPassword(String userName) throws UnknownIdentifierException {
        AppUser user = userService.getUserById(userName);
        sendResetPasswordEmail(user);
    }

    @Override
    @Transactional
    public void updatePassword(String password, String token) throws InvalidTokenException, UnknownIdentifierException {
        SecureToken secureToken = secureTokenService.findByToken(token);
        if (Objects.isNull(secureToken) || !StringUtils.equals(token, secureToken.getToken()) || secureToken.isExpired()) {
            throw new InvalidTokenException("Token is not valid");
        }
        AppUser user = userRepository.findById(secureToken.getUser().getId())
                .orElseThrow(() -> new UnknownIdentifierException("Unable to find user for the token"));
        secureTokenService.removeToken(secureToken);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }


    protected void sendResetPasswordEmail(AppUser user) {
        SecureToken secureToken= secureTokenService.createSecureToken();
        secureToken.setUser(user);
        secureTokenRepository.save(secureToken);
        ForgotPasswordEmailContext emailContext = new ForgotPasswordEmailContext();
        emailContext.init(user);
        emailContext.setToken(secureToken.getToken());
        emailContext.buildVerificationUrl(baseURL, secureToken.getToken());
        try {
            emailService.sendMail(emailContext);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }


}
