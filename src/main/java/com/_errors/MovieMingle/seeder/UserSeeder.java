package com._errors.MovieMingle.seeder;

import com._errors.MovieMingle.model.AppUser;
import com._errors.MovieMingle.repository.AppUserRepository;
import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Component
public class UserSeeder {

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final Faker faker = new Faker();
    private final Random random = new Random();

    private final String[] avatarPaths = {
            "resources/images/avatar1.jpg",
            "resources/images/avatar2.jpg",
            "resources/images/avatar3.jpg",
            "resources/images/avatar4.jpg"
    };

    public void seedUsers(int numberOfUsers) {
        List<AppUser> users = new ArrayList<>();

        for (int i = 0; i < numberOfUsers; i++) {
            AppUser user = new AppUser();
            user.setFirstName(faker.name().firstName());
            user.setLastName(faker.name().lastName());
            user.setEmail(faker.internet().emailAddress());
            user.setPassword(passwordEncoder.encode("password123")); // Default password
            user.setRole("user");
            user.setCreatedAt(new Date());
            user.setAccountVerified(true);
            user.setQuizCompleted(faker.random().nextBoolean());

            //selectam un avatar random
            String randomAvatar = avatarPaths[random.nextInt(avatarPaths.length)];
            user.setAvatar(randomAvatar);

            users.add(user);
        }

        userRepository.saveAll(users);
    }
}