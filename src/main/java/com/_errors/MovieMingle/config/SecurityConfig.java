package com._errors.MovieMingle.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        return http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/profile/update-avatar","/api/movies/watched/add","/api/movies/watched/remove","/api/movies/watched/check","/api/movies/ratings/**","/api/movies/favourites/**"))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/homepage", "/register", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/search").permitAll()
                        .requestMatchers("/movie-details**").permitAll()
                        .requestMatchers("/mylists").permitAll()
                        .requestMatchers("/movie-details/**").permitAll()
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/register/verify").permitAll()
                        .requestMatchers("/logout").permitAll()
                        .requestMatchers("/recommendations").permitAll()
                        .requestMatchers("/quiz").permitAll()
                        .requestMatchers("/profile").authenticated()
                        .requestMatchers("/profile/update-avatar").authenticated()
                        // Permite accesul la API-urile /api/** fără autentificare
                        .requestMatchers("/api/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error=true")
                )
                .logout(config -> config.logoutSuccessUrl("/"))
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000); // 5 secunde
        factory.setReadTimeout(10000);   // 10 secunde
        return new RestTemplate(factory);
    }

}
