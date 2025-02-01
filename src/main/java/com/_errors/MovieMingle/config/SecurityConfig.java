package com._errors.MovieMingle.config;

import com._errors.MovieMingle.service.user.AppUserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com._errors.MovieMingle.security.CustomOAuth2SuccessHandler;
import java.io.IOException;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;

    public SecurityConfig(CustomOAuth2SuccessHandler customOAuth2SuccessHandler) {
        this.customOAuth2SuccessHandler = customOAuth2SuccessHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(
                                "/profile/update-avatar", "/api/movies/watched/add", "/api/movies/watched/remove",
                                "/api/movies/watched/check", "/api/movies/ratings/**", "/api/movies/favourites/**",
                                "/api/movies/watchlist/**"))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/homepage", "/register", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/search").permitAll()
                        .requestMatchers("/forgotPassword").permitAll()
                        .requestMatchers("/password").permitAll()
                        .requestMatchers("/password/**").permitAll()
                        .requestMatchers("/password/request").permitAll()
                        .requestMatchers("/password/change").permitAll()
                        .requestMatchers("/movie-details**").permitAll()
                        .requestMatchers("/mylists").permitAll()
                        .requestMatchers("/movie-details/**").permitAll()
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/register/verify").permitAll()
                        .requestMatchers("/logout").permitAll()
                        .requestMatchers("/recommendations").permitAll()
                        .requestMatchers("/quiz").permitAll()
                        .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll() // Permite accesul la rutele OAuth2
                        .requestMatchers("/profile").authenticated()
                        .requestMatchers("/profile/update-avatar").authenticated()
                        .requestMatchers("/api/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error=true")
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login") // Folosește aceeași pagină de login pentru OAuth2
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error=true")
                        .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserService()))
                        .successHandler(customOAuth2SuccessHandler) // Adaugă handler-ul de succes personalizat
                )

                .logout(config -> config.logoutSuccessUrl("/"))
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000); // 5 secunde
        factory.setReadTimeout(10000);   // 10 secunde
        return new RestTemplate(factory);
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService() {
        return userRequest -> {
            OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);

            // Extrage email-ul utilizatorului din atribute
            String email = oAuth2User.getAttribute("email");

            if (email == null) {
                throw new IllegalArgumentException("Email not found in OAuth2 user attributes");
            }

            // Setează email-ul ca atribut principal
            Map<String, Object> attributes = oAuth2User.getAttributes();
            return new DefaultOAuth2User(
                    oAuth2User.getAuthorities(),
                    attributes,
                    "email" // Folosește email-ul ca atribut principal
            );
        };
    }
}
