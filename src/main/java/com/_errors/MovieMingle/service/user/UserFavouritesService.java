package com._errors.MovieMingle.service.user;

import com._errors.MovieMingle.dto.MovieDto;
import com._errors.MovieMingle.model.AppUser;
import com._errors.MovieMingle.model.Movie;
import com._errors.MovieMingle.model.UserFavourite;
import com._errors.MovieMingle.repository.AppUserRepository;
import com._errors.MovieMingle.repository.MovieRepository;
import com._errors.MovieMingle.repository.UserFavouritesRepository;
import com._errors.MovieMingle.service.MovieApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserFavouritesService {
    @Autowired
    private AppUserRepository userRepository;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private UserFavouritesRepository userFavouritesRepository;
    @Autowired
    private MovieApiClient movieApiClient;

    public String addMovieToFavourites(Long userId, Long tmdbId, String title) {
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

        if (userFavouritesRepository.existsByUserIdAndMovie_MovieId(userId, movie.getMovieId())) {
            return "Movie is already in favourites.";
        }

        UserFavourite favourite = new UserFavourite();
        favourite.setUser(user);
        favourite.setMovie(movie);
        favourite.setAddedAt(LocalDateTime.now());
        userFavouritesRepository.save(favourite);

        return "Movie added to favourites.";
    }

    public boolean isMovieFavourite(Long userId, Long tmdbId) {
        Movie movie = movieRepository.findByTmdbId(tmdbId);
        if (movie == null) return false;
        return userFavouritesRepository.existsByUserIdAndMovie_MovieId(userId, movie.getMovieId());
    }

    public String removeFromFavourites(Long userId, Long tmdbId) {
        AppUser user = userRepository.findById(userId);
        if (user == null) return "User not found";

        Movie movie = movieRepository.findByTmdbId(tmdbId);
        if (movie == null) return "Movie not found";

        UserFavourite favourite = userFavouritesRepository.findByUserIdAndMovie_MovieId(userId, movie.getMovieId());
        if (favourite == null) return "Movie is not in your favourites.";

        userFavouritesRepository.delete(favourite);
        return "Movie removed from favourites.";
    }

    public List<Movie> getUserFavouriteMovies(long userId) {
        List<Movie> favouriteMovies = new ArrayList<>();
        List<UserFavourite> favourites = userFavouritesRepository.findByUser_Id(userId);

        for (UserFavourite favourite : favourites) {
            Movie movie = favourite.getMovie();
            if (movie != null) {
                favouriteMovies.add(movie);
            }
        }
        return favouriteMovies;
    }
}