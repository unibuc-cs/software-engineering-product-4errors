package com._errors.MovieMingle.service.user;

import com._errors.MovieMingle.model.AppUser;
import com._errors.MovieMingle.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private AppUserRepository repository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        final AppUser appUser =  repository.findByEmail(email);
        if(appUser==null){
            throw new UsernameNotFoundException(email);
        }
        boolean enabled = !appUser.isAccountVerified();
        return User.withUsername(appUser.getEmail())
                .password(appUser.getPassword())
                .disabled(enabled)
                .authorities("USER").build();
    }
}
