package com._errors.MovieMingle.service.user;

import com._errors.MovieMingle.dto.MovieDto;
import com._errors.MovieMingle.model.AppUser;
import com._errors.MovieMingle.model.Movie;
import com._errors.MovieMingle.model.UserFavourite;
import com._errors.MovieMingle.model.UserToWatch;
import com._errors.MovieMingle.repository.AppUserRepository;
import com._errors.MovieMingle.repository.MovieRepository;
import com._errors.MovieMingle.repository.UserFavouritesRepository;
import com._errors.MovieMingle.repository.UserToWatchRepository;
import com._errors.MovieMingle.service.MovieApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Service
public class UserToWatchService {

    @Autowired
    private AppUserRepository userRepository;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private UserToWatchRepository userToWatchRepository;
    @Autowired
    private MovieApiClient movieApiClient;

    public String addMovieTotoWatch(Long userId, Long tmdbId, String title) {
        AppUser user = userRepository.findById(userId);
        if (user == null) return "User not found";

        Movie movie = movieRepository.findByTmdbId(tmdbId);
        if (movie == null) {
            MovieDto movieDto = movieApiClient.getMovie(tmdbId);
            movie = new Movie();
            movie.setTMDBId(tmdbId);
            movie.setSeriesTitle(movieDto.getTitle());
            movie.setPosterLink(movieDto.getPosterPath());
            movie.setOverview(movieDto.getDescription());
            movie.setDirector(movieDto.getDirectorsNames());
            movie.setRuntime(movieDto.getRuntime().toString());
            movieRepository.save(movie);
        }

        if (userToWatchRepository.existsByUserIdAndMovie_MovieId(userId, movie.getMovieId())) {
            return "Movie is already in to-watch.";
        }

        UserToWatch towatch = new UserToWatch();
        towatch.setUser(user);
        towatch.setMovie(movie);
        towatch.setAddedAt(LocalDateTime.now());
        userToWatchRepository.save(towatch);

        return "Movie added to to-watch.";
    }

    public boolean isMovieToWatch(Long userId, Long tmdbId) {
        Movie movie = movieRepository.findByTmdbId(tmdbId);
        if (movie == null) return false;
        return userToWatchRepository.existsByUserIdAndMovie_MovieId(userId, movie.getMovieId());
    }

    public String removeFromToWatch(Long userId, Long tmdbId) {
        AppUser user = userRepository.findById(userId);
        if (user == null) return "User not found";

        Movie movie = movieRepository.findByTmdbId(tmdbId);
        if (movie == null) return "Movie not found";

        UserToWatch towatch = userToWatchRepository.findByUserIdAndMovie_MovieId(userId, movie.getMovieId());
        if (towatch == null) return "Movie is not in your to-watch.";

        userToWatchRepository.delete(towatch);
        return "Movie removed from to-watch.";
    }

    public List<Movie> getUserToWatchMovies(int userId) {
        List<Movie> towatchMovies = new ArrayList<>();
        List<UserToWatch> towatchmovies = userToWatchRepository.findByUser_Id((long)userId);

        for (UserToWatch towatch : towatchmovies) {
            Movie movie = towatch.getMovie();
            if (movie != null) {
                towatchMovies.add(movie);
            }
        }
        return towatchMovies;
    }
}