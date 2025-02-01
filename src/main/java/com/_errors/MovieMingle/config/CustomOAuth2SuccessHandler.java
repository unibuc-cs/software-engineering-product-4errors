package com._errors.MovieMingle.security;

import com._errors.MovieMingle.service.user.AppUserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AppUserService appUserService;

    public CustomOAuth2SuccessHandler(@Lazy AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        if (authentication.getPrincipal() instanceof OAuth2User oAuth2User) {
            String email = (String) oAuth2User.getAttribute("email");
            String name = (String) oAuth2User.getAttribute("name");
            String firstName = "";
            String lastName = "";
            if (name != null) {
                String[] nameParts = name.split(" ", 2);
                firstName = nameParts[0];
                if (nameParts.length > 1) {
                    lastName = nameParts[1];
                }
            }
            appUserService.processOAuth2User(email, firstName, lastName, "google");
        }
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
