package com._errors.MovieMingle.service.email.context;

import com._errors.MovieMingle.model.AppUser;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

public class AccountVerificationEmailContext extends AbstractEmailContext{
    private String token;


    @Override
    public <T> void init(T context){
        //we can do any common configuration setup here
        // like setting up some base URL and context
        AppUser user = (AppUser) context; // we pass the user information
        put("firstName", user.getFirstName());
        setTemplateLocation("email-verification");
        setSubject("Complete your registration on Movie Mingle");
        //setFrom("no-reply@moviemingle.com");
        setFrom("lauravanesalungu@gmail.com");
        setTo(user.getEmail());
    }

    public void setToken(String token) {
        this.token = token;
        put("token", token);
    }

    public void buildVerificationUrl(final String baseURL, final String token){
        final String url= UriComponentsBuilder.fromHttpUrl(baseURL)
                .path("/register/verify").queryParam("token", token).toUriString();
        put("verificationURL", url);
    }
}
