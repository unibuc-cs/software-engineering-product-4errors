package com._errors.MovieMingle.seeder;

import com._errors.MovieMingle.repository.AppUserRepository;
import com._errors.MovieMingle.repository.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private UserSeeder userSeeder;

    @Autowired
    private RatingSeeder ratingSeeder;

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String ddlAuto;

    @Override
    public void run(String... args) {
        //populam tabelul doar daca avem mai putin de 1000 de useri
        if (userRepository.count() <20) {
            userSeeder.seedUsers(20);
        }

        //populam tabelul de rating doar daca nu am facut-o deja
        if(ratingRepository.count()<401) {
            System.out.println("Starting ratings seeding...");
            ratingSeeder.seedRatings();
            System.out.println("Ratings seeding completed!");
        }
    }
}