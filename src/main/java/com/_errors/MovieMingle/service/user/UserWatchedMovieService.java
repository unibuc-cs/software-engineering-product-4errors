package com._errors.MovieMingle.service.user;

import com._errors.MovieMingle.dto.MovieDto;
import com._errors.MovieMingle.model.AppUser;
import com._errors.MovieMingle.model.Movie;
import com._errors.MovieMingle.model.UserWatchedMovie;
import com._errors.MovieMingle.repository.AppUserRepository;
import com._errors.MovieMingle.repository.MovieRepository;
import com._errors.MovieMingle.repository.UserWatchedMovieRepository;
import com._errors.MovieMingle.service.MovieApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserWatchedMovieService {
    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private UserWatchedMovieRepository userWatchedMovieRepository;
    @Autowired
    private MovieApiClient movieApiClient;

    public String addMovieToWatched(Long userId, Long tmdbId, String title) {
        // Găsim utilizatorul
        AppUser user = userRepository.findById(userId);
        if (user==null)
            return ("User not found");

        // Găsim sau creăm filmul
        Movie movie = movieRepository.findByTmdbId(tmdbId);

        //daca nu am salvat inca filmul in db il cream
        if(movie==null)
        {
            MovieDto movieDto=movieApiClient.getMovie(tmdbId);
            // Salvăm filmul în baza de date
            movie = new Movie();
            movie.setTMDBId(tmdbId);
            movie.setSeriesTitle(movieDto.getTitle());
            movie.setPosterLink(movieDto.getPosterPath());
            movie.setOverview(movieDto.getDescription());
            movie.setDirector(movieDto.getDirectorsNames());
            movie.setRuntime(movieDto.getRuntime().toString());
            movieRepository.save(movie);

        }


        // Verificăm dacă filmul este deja marcat ca "watched"
        boolean alreadyWatched = userWatchedMovieRepository.existsByUserIdAndMovie_MovieId(userId, movie.getMovieId());
        if (alreadyWatched) {
            return "Movie is already marked as watched.";
        }

        // Salvăm în `user_watched_movies`
        UserWatchedMovie watchedEntry = new UserWatchedMovie();
        watchedEntry.setUser(user);
        watchedEntry.setMovie(movie);
        watchedEntry.setWatchedAt(LocalDateTime.now());
        userWatchedMovieRepository.save(watchedEntry);

        return "Movie added to watched list.";
    }

    public boolean isMovieWatched(Long userId, Long tmdbId) {
        Movie movie = movieRepository.findByTmdbId(tmdbId);
        if (movie == null) return false;
        return userWatchedMovieRepository.existsByUserIdAndMovie_MovieId(userId, movie.getMovieId());
    }
    public String removeFromWatched(Long userId, Long tmdbId) {
        // Find the user
        AppUser user = userRepository.findById(userId);
        if (user == null) {
            return "User not found";
        }

        // Find the movie
        Movie movie = movieRepository.findByTmdbId(tmdbId);
        if (movie == null) {
            return "Movie not found";
        }

        // Check if the movie is in the watched list
        UserWatchedMovie watchedMovie = userWatchedMovieRepository.findByUserIdAndMovie_MovieId(userId, movie.getMovieId());
        if (watchedMovie == null) {
            return "Movie is not in your watched list.";
        }

        // Delete the entry
        userWatchedMovieRepository.delete(watchedMovie);
        return "Movie removed from watched list.";
    }


}

