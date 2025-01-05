package com._errors.MovieMingle.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
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
                        .ignoringRequestMatchers("/profile/update-avatar"))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/index", "/register", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/search").permitAll()
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/register/verify").permitAll()
                        .requestMatchers("/logout").permitAll()
                        .requestMatchers("/recommendations").permitAll()
                        .requestMatchers("/quiz").permitAll()
                        .requestMatchers("/profile").authenticated()
                        .requestMatchers("/profile/update-avatar").authenticated()
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
}
