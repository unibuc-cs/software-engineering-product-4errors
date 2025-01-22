package com._errors.MovieMingle.seeder;

import com._errors.MovieMingle.model.AppUser;
import com._errors.MovieMingle.model.Movie;
import com._errors.MovieMingle.model.Rating;
import com._errors.MovieMingle.model.UserWatchedMovie;
import com._errors.MovieMingle.repository.MovieRepository;
import com._errors.MovieMingle.repository.RatingRepository;
import com._errors.MovieMingle.repository.AppUserRepository;
import com._errors.MovieMingle.repository.UserWatchedMovieRepository;
import com._errors.MovieMingle.service.MovieApiClient;
import com._errors.MovieMingle.dto.MovieDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class RatingSeeder {

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private UserWatchedMovieRepository userWatchedMovieRepository;

    @Autowired
    private MovieApiClient movieApiClient;

    private final Random random = new Random();
    private static final int REQUIRED_MOVIES = 300;
    private List<MovieDto> popularMovies;

    public void seedRatings() {
        popularMovies = new ArrayList<>();

        // Get top rated movies (5 pages = 100 movies)
        for(int page = 1; page <= 5; page++) {
            popularMovies.addAll(movieApiClient.getTopRatedMoviesPage(page));
        }

        System.out.println("Fetched " + popularMovies.size() + " diverse movies from TMDB");


        List<AppUser> users = userRepository.findAll();

        for (AppUser user : users) {
            // Check if user already has 20 ratings
            if (ratingRepository.countByUser_Id(user.getId()) >= 20){
                continue;
            }

            // Get existing rated movie IDs for this user
            Set<Long> userRatedMovieIds = new HashSet<>();
            List<Rating> existingRatings = ratingRepository.findByUser_Id(user.getId());
            for (Rating rating : existingRatings) {
                userRatedMovieIds.add(rating.getMovie().getMovieId());
            }

            // Rate movies until user has 20 ratings
            while (ratingRepository.countByUser_Id(user.getId()) < 20) {
                MovieDto movieDto = popularMovies.get(random.nextInt(popularMovies.size()));

                try {
                    // Find or create movie
                    Movie movie = movieRepository.findByTmdbId(movieDto.getId());

                    if (movie == null) {
                        movie = new Movie();
                        movie.setTMDBId(movieDto.getId());
                        movie.setSeriesTitle(movieDto.getTitle());
                        movie.setPosterLink(movieDto.getPosterPath());
                        movie.setOverview(movieDto.getDescription());
                        movie.setDirector(movieDto.getDirectorsNames());
                        movie.setRuntime(movieDto.getRuntime().toString());
                        movie = movieRepository.save(movie);
                    }

                    // Check if user hasn't rated this movie yet
                    if (!userRatedMovieIds.contains(movie.getMovieId())) {
                        // Create rating
                        Rating rating = new Rating();
                        rating.setUser(user);
                        rating.setMovie(movie);
                        rating.setRating(random.nextInt(10) + 1);
                        ratingRepository.save(rating);
                        userRatedMovieIds.add(movie.getMovieId());

                        // Create watched movie entry
                        UserWatchedMovie watchedMovie = new UserWatchedMovie();
                        watchedMovie.setUser(user);
                        watchedMovie.setMovie(movie);
                        // Set random watched date within last 30 days
                        LocalDateTime watchedDate = LocalDateTime.now().minusDays(random.nextInt(30));
                        watchedMovie.setWatchedAt(watchedDate);
                        userWatchedMovieRepository.save(watchedMovie);
                    }
                } catch (Exception e) {
                    System.out.println("Error processing movie: " + movieDto.getTitle() + ": " + e.getMessage());
                    continue;
                }
            }

            if ((users.indexOf(user) + 1) % 100 == 0) {
                System.out.println("Processed ratings for " + (users.indexOf(user) + 1) + " users");
            }
        }

        System.out.println("Rating seeding completed!");
        System.out.println("Total movies in database: " + movieRepository.count());
        System.out.println("Total ratings created: " + ratingRepository.count());
        System.out.println("Total watched movies entries: " + userWatchedMovieRepository.count());
    }
}