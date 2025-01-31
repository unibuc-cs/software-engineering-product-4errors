package com._errors.MovieMingle.service.user;

import com._errors.MovieMingle.dto.MovieDto;
import com._errors.MovieMingle.model.AppUser;
import com._errors.MovieMingle.model.Movie;
import com._errors.MovieMingle.model.UserFavourite;
import com._errors.MovieMingle.repository.AppUserRepository;
import com._errors.MovieMingle.repository.MovieRepository;
import com._errors.MovieMingle.repository.UserFavouritesRepository;
import com._errors.MovieMingle.service.MovieApiClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserFavouritesServiceTest {

    @Mock
    private AppUserRepository userRepository;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private UserFavouritesRepository userFavouritesRepository;

    @Mock
    private MovieApiClient movieApiClient;

    @InjectMocks
    private UserFavouritesService userFavouritesService;

    private AppUser user;
    private Movie movie;
    private UserFavourite favourite;

    @BeforeEach
    public void setUp() {
        user = new AppUser();
        user.setId(1);
        user.setEmail("test@example.com");

        movie = new Movie();
        movie.setMovieId(1L);
        movie.setTMDBId(123L);
        movie.setSeriesTitle("Test Movie");

        favourite = new UserFavourite();
        favourite.setUser(user);
        favourite.setMovie(movie);
        favourite.setAddedAt(LocalDateTime.now());
    }

    @Test
    public void testAddMovieToFavourites_UserNotFound() {
        
        when(userRepository.findById(1L)).thenReturn(null);

        
        String result = userFavouritesService.addMovieToFavourites(1L, 123L, "Test Movie");

        
        assertEquals("User not found", result);
        verify(userRepository, times(1)).findById(1L);
        verify(movieRepository, never()).findByTmdbId(any());
        verify(userFavouritesRepository, never()).save(any());
    }

    @Test
    public void testAddMovieToFavourites_MovieAlreadyInFavourites() {
        
        when(userRepository.findById(1L)).thenReturn(user);
        when(movieRepository.findByTmdbId(123L)).thenReturn(movie);
        when(userFavouritesRepository.existsByUserIdAndMovie_MovieId(1L, 1L)).thenReturn(true);

        
        String result = userFavouritesService.addMovieToFavourites(1L, 123L, "Test Movie");

        
        assertEquals("Movie is already in favourites.", result);
        verify(userRepository, times(1)).findById(1L);
        verify(movieRepository, times(1)).findByTmdbId(123L);
        verify(userFavouritesRepository, times(1)).existsByUserIdAndMovie_MovieId(1L, 1L);
        verify(userFavouritesRepository, never()).save(any());
    }

    @Test
    public void testAddMovieToFavourites_NewMovie() {
        when(userRepository.findById(1L)).thenReturn(user);
        when(movieRepository.findByTmdbId(123L)).thenReturn(null);

        MovieDto movieDto = new MovieDto();
        movieDto.setTitle("Test Movie");
        movieDto.setPosterPath("/poster.jpg");
        movieDto.setDescription("Overview");
        movieDto.setRuntime(120);

        when(movieApiClient.getMovie(123L)).thenReturn(movieDto);

        when(movieRepository.save(any(Movie.class))).thenReturn(movie);
        when(userFavouritesRepository.save(any(UserFavourite.class))).thenReturn(favourite);

        String result = userFavouritesService.addMovieToFavourites(1L, 123L, "Test Movie");

        assertEquals("Movie added to favourites.", result);
        verify(userRepository, times(1)).findById(1L);
        verify(movieRepository, times(1)).findByTmdbId(123L);
        verify(movieApiClient, times(1)).getMovie(123L);
        verify(movieRepository, times(1)).save(any(Movie.class));
        verify(userFavouritesRepository, times(1)).save(any(UserFavourite.class));
    }


    @Test
    public void testIsMovieFavourite_MovieNotInFavourites() {
        
        when(movieRepository.findByTmdbId(123L)).thenReturn(movie);
        when(userFavouritesRepository.existsByUserIdAndMovie_MovieId(1L, 1L)).thenReturn(false);

        
        boolean result = userFavouritesService.isMovieFavourite(1L, 123L);

        
        assertFalse(result);
        verify(movieRepository, times(1)).findByTmdbId(123L);
        verify(userFavouritesRepository, times(1)).existsByUserIdAndMovie_MovieId(1L, 1L);
    }

    @Test
    public void testIsMovieFavourite_MovieInFavourites() {
        
        when(movieRepository.findByTmdbId(123L)).thenReturn(movie);
        when(userFavouritesRepository.existsByUserIdAndMovie_MovieId(1L, 1L)).thenReturn(true);

        
        boolean result = userFavouritesService.isMovieFavourite(1L, 123L);

        
        assertTrue(result);
        verify(movieRepository, times(1)).findByTmdbId(123L);
        verify(userFavouritesRepository, times(1)).existsByUserIdAndMovie_MovieId(1L, 1L);
    }

    @Test
    public void testRemoveFromFavourites_UserNotFound() {
        
        when(userRepository.findById(1L)).thenReturn(null);

        
        String result = userFavouritesService.removeFromFavourites(1L, 123L);

        
        assertEquals("User not found", result);
        verify(userRepository, times(1)).findById(1L);
        verify(movieRepository, never()).findByTmdbId(any());
        verify(userFavouritesRepository, never()).delete(any());
    }

    @Test
    public void testRemoveFromFavourites_MovieNotFound() {
        
        when(userRepository.findById(1L)).thenReturn(user);
        when(movieRepository.findByTmdbId(123L)).thenReturn(null);

        
        String result = userFavouritesService.removeFromFavourites(1L, 123L);

        
        assertEquals("Movie not found", result);
        verify(userRepository, times(1)).findById(1L);
        verify(movieRepository, times(1)).findByTmdbId(123L);
        verify(userFavouritesRepository, never()).delete(any());
    }

    @Test
    public void testRemoveFromFavourites_MovieNotInFavourites() {
        
        when(userRepository.findById(1L)).thenReturn(user);
        when(movieRepository.findByTmdbId(123L)).thenReturn(movie);
        when(userFavouritesRepository.findByUserIdAndMovie_MovieId(1L, 1L)).thenReturn(null);

        
        String result = userFavouritesService.removeFromFavourites(1L, 123L);

        
        assertEquals("Movie is not in your favourites.", result);
        verify(userRepository, times(1)).findById(1L);
        verify(movieRepository, times(1)).findByTmdbId(123L);
        verify(userFavouritesRepository, times(1)).findByUserIdAndMovie_MovieId(1L, 1L);
        verify(userFavouritesRepository, never()).delete(any());
    }

    @Test
    public void testRemoveFromFavourites_Success() {
        
        when(userRepository.findById(1L)).thenReturn(user);
        when(movieRepository.findByTmdbId(123L)).thenReturn(movie);
        when(userFavouritesRepository.findByUserIdAndMovie_MovieId(1L, 1L)).thenReturn(favourite);

        
        String result = userFavouritesService.removeFromFavourites(1L, 123L);

        
        assertEquals("Movie removed from favourites.", result);
        verify(userRepository, times(1)).findById(1L);
        verify(movieRepository, times(1)).findByTmdbId(123L);
        verify(userFavouritesRepository, times(1)).findByUserIdAndMovie_MovieId(1L, 1L);
        verify(userFavouritesRepository, times(1)).delete(favourite);
    }

    @Test
    public void testGetUserFavouriteMovies() {
        
        List<UserFavourite> favourites = new ArrayList<>();
        favourites.add(favourite);
        when(userFavouritesRepository.findByUser_Id(1L)).thenReturn(favourites);

        
        List<Movie> result = userFavouritesService.getUserFavouriteMovies(1L);

        
        assertEquals(1, result.size());
        assertEquals(movie, result.get(0));
        verify(userFavouritesRepository, times(1)).findByUser_Id(1L);
    }
}