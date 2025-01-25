package com._errors.MovieMingle.service.user;

import com._errors.MovieMingle.dto.RegisterDto;
import com._errors.MovieMingle.exception.InvalidTokenException;
import com._errors.MovieMingle.exception.UnknownIdentifierException;
import com._errors.MovieMingle.exception.UserAlreadyExistsException;
import com._errors.MovieMingle.model.AppUser;
import com._errors.MovieMingle.model.SecureToken;
import com._errors.MovieMingle.repository.AppUserRepository;
import com._errors.MovieMingle.repository.SecureTokenRepository;
import com._errors.MovieMingle.service.SecureTokenService;
import com._errors.MovieMingle.service.email.EmailService;
import com._errors.MovieMingle.service.email.context.AccountVerificationEmailContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.util.Date;
import java.util.Objects;

@Service("userService")
public class DefaultAppUserService implements AppUserService {

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SecureTokenService secureTokenService;

    @Autowired
    SecureTokenRepository secureTokenRepository;

    @Value("${site.base.url.https}")
    private String baseURL;

//    @Autowired
//    private MFATokenManager mfaTokenManager;


    @Override
    public void register(RegisterDto user) throws UserAlreadyExistsException {
        if(checkIfUserExist(user.getEmail())){
            throw new UserAlreadyExistsException("User already exists for this email");
        }
        AppUser userEntity = new AppUser();
        BeanUtils.copyProperties(user, userEntity);
        userEntity.setRole("user");
        userEntity.setCreatedAt(new Date());
        encodePassword(user, userEntity);
        userEntity.setAvatar("general_avatar.png");
        //userEntity.setSecret(mfaTokenManager.generateSecretKey());
        userRepository.save(userEntity);
        sendRegistrationConfirmationEmail(userEntity);
    }

    @Override
    public boolean checkIfUserExist(String email) {
        return userRepository.findByEmail(email) != null;
    }

    @Override
    public void sendRegistrationConfirmationEmail(AppUser user) {
        SecureToken secureToken= secureTokenService.createSecureToken();
        secureToken.setUser(user);
        secureTokenRepository.save(secureToken);
        AccountVerificationEmailContext emailContext = new AccountVerificationEmailContext();
        emailContext.init(user);
        emailContext.setToken(secureToken.getToken());
        emailContext.buildVerificationUrl(baseURL, secureToken.getToken());
        try {
            emailService.sendMail(emailContext);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    @Transactional
    public boolean verifyUser(String token) throws InvalidTokenException {
        SecureToken secureToken = secureTokenService.findByToken(token);
        if(Objects.isNull(secureToken) || !StringUtils.equals(token, secureToken.getToken()) || secureToken.isExpired()){
            throw new InvalidTokenException("Token is not valid");
        }
        AppUser user = userRepository.getOne(secureToken.getUser().getId());
        user.setAccountVerified(true);
        userRepository.save(user); // let's save user details

        // we don't need invalid password now
        secureTokenService.removeToken(secureToken);
        return true;
    }

    @Override
    public AppUser getUserById(String id) throws UnknownIdentifierException {
        AppUser user= userRepository.findByEmail(id);
        if(user == null || BooleanUtils.isFalse(user.isAccountVerified())){
            // we will ignore in case account is not verified or account does not exist
            throw new UnknownIdentifierException("unable to find account or account is not active");
        }
        return user;
    }

    @Override
    public AppUser findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

//    @Override
//    public MfaTokenData mfaSetup(String email) throws UnknownIdentifierException, QrGenerationException {
//        AppUser user= userRepository.findByEmail(email);
//        if(user == null ){
//            // we will ignore in case account is not verified or account does not exist
//            throw new UnknownIdentifierException("unable to find account or account is not active");
//        }
//        return new MfaTokenData( mfaTokenManager.getQRCode( user.getSecret()), user.getSecret());
//    }

    private void encodePassword(RegisterDto source, AppUser target){
        target.setPassword(passwordEncoder.encode(source.getPassword()));
    }

}
