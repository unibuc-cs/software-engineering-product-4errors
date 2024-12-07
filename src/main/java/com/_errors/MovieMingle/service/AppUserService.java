package com._errors.MovieMingle.service;

import com._errors.MovieMingle.model.AppUser;
import com._errors.MovieMingle.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AppUserService implements UserDetailsService {
    @Autowired
    private AppUserRepository repo;
    @Override
    public UserDetails loadUserByUsername (String email) throws UsernameNotFoundException{
        //TODO Auto-generated method stub

        AppUser appUser = repo.findByEmail(email);

        if (appUser != null){
            var springUser = User.withUsername(appUser.getEmail())
                    .password(appUser.getPassword())
                    .roles(appUser.getRole())
                    .build();

            return springUser;
        }
        return null;
    }

}
