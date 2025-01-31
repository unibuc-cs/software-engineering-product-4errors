package com._errors.MovieMingle;

import com._errors.MovieMingle.dto.MovieDto;
import com._errors.MovieMingle.dto.RegisterDto;
import com._errors.MovieMingle.dto.UserDashboardDto;
import com._errors.MovieMingle.exception.InvalidTokenException;
import com._errors.MovieMingle.exception.UnknownIdentifierException;
import com._errors.MovieMingle.model.*;
import com._errors.MovieMingle.recommendation.MatrixBuilder;
import com._errors.MovieMingle.repository.*;
import com._errors.MovieMingle.service.DashboardService;
import com._errors.MovieMingle.service.MovieApiClient;
import com._errors.MovieMingle.service.SecureTokenService;
import com._errors.MovieMingle.service.user.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
public class IntegrationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private UserWatchedMovieService userWatchedMovieService;

    @Autowired
    private UserWatchedMovieRepository userWatchedMovieRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private MovieApiClient movieApiClient;

    @Autowired
    private MatrixBuilder matrixBuilder;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private AppUserAccountService appUserAccountService;

    @Autowired
    private SecureTokenRepository secureTokenRepository;

    @Autowired
    private SecureTokenService secureTokenService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private UserToWatchService userToWatchService;

    @Test
    public void testUserLogin() {
        // cream user si salvam in db
        AppUser user = new AppUser();
        user.setEmail("testuser@example.com");
        user.setPassword(passwordEncoder.encode("Test1234"));
        user.setRole("user");
        user.setAccountVerified(true);
        userRepository.save(user);

        // request pt login
        MultiValueMap<String, String> loginForm = new LinkedMultiValueMap<>();
        loginForm.add("username", "testuser@example.com");
        loginForm.add("password", "Test1234");

        HttpEntity<MultiValueMap<String, String>> loginRequest = new HttpEntity<>(loginForm, new HttpHeaders());
        ResponseEntity<String> loginResponse = restTemplate.postForEntity("/login", loginRequest, String.class);

        // verificam raspunsul de la request
        assertEquals(HttpStatus.FOUND, loginResponse.getStatusCode());

        // urmarim redirect-ul catre homepage si verificam iar statusul raspunsului
        String redirectUrl = loginResponse.getHeaders().getLocation().toString();
        ResponseEntity<String> homepageResponse = restTemplate.getForEntity(redirectUrl, String.class);

        assertEquals(HttpStatus.FOUND, homepageResponse.getStatusCode());
    }
    @Test
    public void testGetUserByEmail() {
        // adaugam user in db
        AppUser user = new AppUser();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("johndoe@example.com");
        user.setPassword("EncodedPassword123");
        userRepository.save(user);

        // apelam API-ul si luam raspunsul
        ResponseEntity<String> response = restTemplate.getForEntity("/users/email/johndoe@example.com", String.class);

        // verificam statusul
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testIsMovieWatched() {
        // cream user si il salvam in db
        AppUser user = new AppUser();
        user.setEmail("testuser@example.com");
        user.setPassword("EncodedPassword123");
        user.setRole("user");
        user.setAccountVerified(true);
        userRepository.save(user);

        // luam un exemplu de film care exista in lista din API
        Long tmdbId = 550L;
        MovieDto movieDto = movieApiClient.getMovie(tmdbId);  // request pentru detalii

        // verificam daca filmul exista deja in db
        Movie movie = movieRepository.findByTmdbId(tmdbId);
        if (movie == null) {
            movie = new Movie();
            movie.setTMDBId(tmdbId);
            movie.setSeriesTitle(movieDto.getTitle());
            movie.setPosterLink(movieDto.getPosterPath());
            movie.setOverview(movieDto.getDescription());
            movie.setDirector(movieDto.getDirectorsNames() != null ? movieDto.getDirectorsNames() : "Unknown");
            movie.setRuntime(movieDto.getRuntime() != null ? movieDto.getRuntime().toString() : "0");
            movieRepository.save(movie);
        }

        // request sa adaugam in lista
        String result = userWatchedMovieService.addMovieToWatched(Long.valueOf(user.getId()), movie.getTmdbId(), movie.getSeriesTitle());
        assertEquals("Movie added to watched list.", result); // Verifică dacă filmul a fost adăugat cu succes

        // verificam daca acum e marcat ca watched
        boolean isMovieWatched = userWatchedMovieService.isMovieWatched(Long.valueOf(user.getId()), movie.getTmdbId());
        assertTrue(isMovieWatched);
    }

    @Test
    public void testAddMovieToWatched() {
        // cream user si salvam in db
        AppUser user = new AppUser();
        user.setEmail("testuser@example.com");
        user.setPassword("EncodedPassword123");
        user.setRole("user");
        user.setAccountVerified(true);
        userRepository.save(user);

        // adaugam film in db
        Movie movie = new Movie();
        movie.setTMDBId(12345L);
        movie.setSeriesTitle("Test Movie");
        movie.setDirector("Test Director");
        movie.setRuntime("120");
        movie.setOverview("Overview");
        movie.setPosterLink("link");

        movieRepository.save(movie);

        // request sa adaugam filmul in lista
        String result = userWatchedMovieService.addMovieToWatched(Long.valueOf(user.getId()), movie.getTmdbId(), movie.getSeriesTitle());

        // verificam rezultatul
        assertEquals("Movie added to watched list.", result);

        // verificam ca a fost inserat in db corect
        boolean isMovieWatched = userWatchedMovieService.isMovieWatched(Long.valueOf(user.getId()), movie.getTmdbId());
        assertTrue(isMovieWatched);
    }

    @Test
    public void testAddMovieToWatched_AlreadyWatched() {
        // cream user si insert in db
        AppUser user = new AppUser();
        user.setEmail("testuser@example.com");
        user.setPassword("EncodedPassword123");
        user.setRole("user");
        user.setAccountVerified(true);
        userRepository.save(user);


        Long tmdbId = 550L;
        MovieDto movieDto = movieApiClient.getMovie(tmdbId);

        Movie movie = movieRepository.findByTmdbId(tmdbId);
        if (movie == null) {
            movie = new Movie();
            movie.setTMDBId(tmdbId);
            movie.setSeriesTitle(movieDto.getTitle());
            movie.setPosterLink(movieDto.getPosterPath());
            movie.setOverview(movieDto.getDescription());
            movie.setDirector(movieDto.getDirectorsNames() != null ? movieDto.getDirectorsNames() : "Unknown");
            movie.setRuntime(movieDto.getRuntime() != null ? movieDto.getRuntime().toString() : "0");
            movieRepository.save(movie);
        }

        // request sa adaugam filmul in lista
        userWatchedMovieService.addMovieToWatched(Long.valueOf(user.getId()), movie.getTmdbId(), movie.getSeriesTitle());

        // request sa adaugam filmul in lista din nou
        String result = userWatchedMovieService.addMovieToWatched(Long.valueOf(user.getId()), movie.getTmdbId(), movie.getSeriesTitle());

        // verificam rezultatul
        assertEquals("Movie is already marked as watched.", result);
    }

    @Test
    public void testLoadUserByUsername_UserExists() {
        // cream user si insert in db
        AppUser user = new AppUser();
        user.setEmail("testuser@example.com");
        user.setPassword("EncodedPassword123");
        user.setRole("user");
        user.setAccountVerified(true);
        userRepository.save(user);

        // request sa incarcam detaliile user-ului
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("testuser@example.com");

        // verificam detaliile user-ului
        assertNotNull(userDetails);
        assertEquals("testuser@example.com", userDetails.getUsername());
        assertEquals("EncodedPassword123", userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
    }

    @Test
    public void testLoadUserByUsername_UserDoesNotExist() {
        // 1. Încearcă să încarci detaliile unui utilizator inexistent
        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername("nonexistent@example.com");
        });
    }

    @Test
    public void testGetUserDashboardStats() {
        // cream user si il salvam in db
        AppUser user = new AppUser();
        user.setEmail("testuser@example.com");
        user.setPassword("EncodedPassword123");
        user.setRole("user");
        user.setAccountVerified(true);
        userRepository.save(user);

        Long tmdbId = 550L;
        MovieDto movieDto = movieApiClient.getMovie(tmdbId);

        Movie movie = movieRepository.findByTmdbId(tmdbId);
        if (movie == null) {
            movie = new Movie();
            movie.setTMDBId(tmdbId);
            movie.setSeriesTitle(movieDto.getTitle());
            movie.setPosterLink(movieDto.getPosterPath());
            movie.setOverview(movieDto.getDescription());
            movie.setDirector(movieDto.getDirectorsNames() != null ? movieDto.getDirectorsNames() : "Unknown");
            movie.setRuntime(movieDto.getRuntime() != null ? movieDto.getRuntime().toString() : "0");
            movieRepository.save(movie);
        }

        // adaugam filmul la lista de filme vizionate
        UserWatchedMovie watchedMovie = new UserWatchedMovie();
        watchedMovie.setUser(user);
        watchedMovie.setMovie(movie);
        watchedMovie.setWatchedAt(LocalDateTime.now());
        userWatchedMovieRepository.save(watchedMovie);

        // adaugam un rating pentru film
        Rating rating = new Rating();
        rating.setUser(user);
        rating.setMovie(movie);
        rating.setRating(8);
        ratingRepository.save(rating);

        // request pt a obtine statistica
        UserDashboardDto stats = dashboardService.getUserDashboardStats(Long.valueOf(user.getId()));

        // verificam statistica
        assertNotNull(stats);
        assertEquals(1, stats.getTotalMoviesWatched());
        assertEquals(8.0, stats.getAverageRating());
        assertEquals("Fight Club", stats.getTopRatedMovie());
        assertEquals("Fight Club", stats.getLowestRatedMovie());
        assertEquals(2, stats.getTotalHoursWatched()); // 139 minute ≈ 2.31 ore (rotunjit la 2 ore)
        assertNotNull(stats.getMoviesPerMonth());
        assertNotNull(stats.getGenresWatched());

        // verificam daca genul filmului este inclus
        assertTrue(stats.getGenresWatched().containsKey("Drama"));
    }

    @Test
    public void testForgottenPassword_UserDoesNotExist() {
        assertThrows(UnknownIdentifierException.class, () -> {
            appUserAccountService.forgottenPassword("nonexistent@example.com");
        });
    }

    @Test
    public void testUpdatePassword_ValidToken() throws InvalidTokenException, UnknownIdentifierException {
        // cream user si salvam in db
        AppUser user = new AppUser();
        user.setEmail("testuser@example.com");
        user.setPassword("EncodedPassword123");
        user.setRole("user");
        user.setAccountVerified(true);
        userRepository.save(user);

        // generam token pt resetarea parolei
        SecureToken secureToken = secureTokenService.createSecureToken();
        secureToken.setUser(user);
        secureTokenRepository.save(secureToken);

        // request sa actualizam parola folosind token-ul valid
        appUserAccountService.updatePassword("NewPassword123", secureToken.getToken());

        // verificam daca parola a fost actualizata
        AppUser updatedUser = userRepository.findByEmail("testuser@example.com");
        assertTrue(passwordEncoder.matches("NewPassword123", updatedUser.getPassword()));
    }

    @Test
    public void testUpdatePassword_InvalidToken() {
        assertThrows(InvalidTokenException.class, () -> {
            appUserAccountService.updatePassword("NewPassword123", "invalid-token");
        });
    }




    @Test
    @WithMockUser(username = "testuser@example.com", roles = {"USER"}) //pt Spring Security
    public void testGetProfilePage_UserAuthenticated() throws Exception {
        // cream user si salvam in db
        AppUser user = new AppUser();
        user.setEmail("testuser@example.com");
        user.setPassword("EncodedPassword123");
        user.setRole("user");
        user.setAccountVerified(true);
        appUserRepository.save(user);

        // simulam o cerere GET catre /profile
        mockMvc.perform(get("/profile"))
                .andExpect(status().isOk()) // verificam ca raspunsul are codul 200
                .andExpect(view().name("profile")) // verificam ca se returnează view-ul "profile"
                .andExpect(model().attributeExists("user")) // verificam ca modelul conține atributul "user"
                .andExpect(model().attribute("user", user)); // verificam ca user-ul este corect
    }

    @Test
    @WithMockUser(username = "testuser@example.com", roles = {"USER"})
    public void testUpdateAvatar_ValidRequest() throws Exception {
        // cream user si salvam in db
        AppUser user = new AppUser();
        user.setEmail("testuser@example.com");
        user.setPassword("EncodedPassword123");
        user.setRole("user");
        user.setAccountVerified(true);
        appUserRepository.save(user);

        // simulam o cerere POST catre /profile/update-avatar
        String newAvatarPath = "/path/to/new/avatar.png";
        mockMvc.perform(post("/profile/update-avatar")
                        .contentType("application/json")
                        .content("{\"avatar\": \"" + newAvatarPath + "\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Avatar actualizat cu succes!"));

        // verificam ca avatarul a fost actualizat in db cu numele corespuznator
        AppUser updatedUser = appUserRepository.findByEmail("testuser@example.com");
        assertEquals("avatar.png", updatedUser.getAvatar());
    }

    @Test
    @WithMockUser(username = "testuser@example.com", roles = {"USER"})
    public void testUpdateAvatar_InvalidRequest() throws Exception {
        // cream user si salvam in db
        AppUser user = new AppUser();
        user.setEmail("testuser@example.com");
        user.setPassword("EncodedPassword123");
        user.setRole("user");
        user.setAccountVerified(true);
        appUserRepository.save(user);

        // simulam o cerere POST catre /profile/update-avatar fara calea avatarului
        mockMvc.perform(post("/profile/update-avatar")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isOk()) // verificam statusul si mesajul
                .andExpect(content().string("Eroare: Utilizatorul nu a fost găsit sau datele trimise sunt invalide."));
    }

    @Test
    public void testGetProfilePage_UserNotAuthenticated() throws Exception {
        // simulam o cerere GET catre /profile fara autentificare
        mockMvc.perform(get("/profile"))
                .andExpect(status().is3xxRedirection()) // verificam ca raspunsul este o redirectionare
                .andExpect(redirectedUrlPattern("**/login")); // verificam ca utilizatorul este redirectionat catre pagina de login
    }

    @Test
    @WithMockUser(username = "testuser@example.com", roles = {"USER"})
    public void testIsMovieToWatch_True() throws Exception {
        AppUser testUser = new AppUser();
        testUser.setEmail("testuser@example.com");
        testUser.setPassword("EncodedPassword123");
        testUser.setRole("user");
        testUser.setAccountVerified(true);
        appUserRepository.save(testUser);

        Long tmdbId = 550L;
        MovieDto movieDto = movieApiClient.getMovie(tmdbId);  // request pentru detalii

        // verificam daca filmul exista deja in db
        Movie movie = movieRepository.findByTmdbId(tmdbId);
        if (movie == null) {
            movie = new Movie();
            movie.setTMDBId(tmdbId);
            movie.setSeriesTitle(movieDto.getTitle());
            movie.setPosterLink(movieDto.getPosterPath());
            movie.setOverview(movieDto.getDescription());
            movie.setDirector(movieDto.getDirectorsNames() != null ? movieDto.getDirectorsNames() : "Unknown");
            movie.setRuntime(movieDto.getRuntime() != null ? movieDto.getRuntime().toString() : "0");
            movieRepository.save(movie);
        }

        // adaugam filmul in lista de filme de vizionat
        userToWatchService.addMovieTotoWatch(Long.valueOf(testUser.getId()), movie.getTmdbId(), movie.getSeriesTitle());

        mockMvc.perform(get("/api/movies/watchlist/check")
                        .param("userId", String.valueOf(testUser.getId()))
                        .param("tmdbId", movie.getTmdbId().toString()))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @WithMockUser(username = "testuser@example.com", roles = {"USER"})
    public void testAddMovieTotoWatch_Success() throws Exception {

        AppUser testUser = new AppUser();
        testUser.setEmail("testuser@example.com");
        testUser.setPassword("EncodedPassword123");
        testUser.setRole("user");
        testUser.setAccountVerified(true);
        appUserRepository.save(testUser);

        Long tmdbId = 550L;
        MovieDto movieDto = movieApiClient.getMovie(tmdbId);  // request pentru detalii

        // verificam daca filmul exista deja in db
        Movie movie = movieRepository.findByTmdbId(tmdbId);
        if (movie == null) {
            movie = new Movie();
            movie.setTMDBId(tmdbId);
            movie.setSeriesTitle(movieDto.getTitle());
            movie.setPosterLink(movieDto.getPosterPath());
            movie.setOverview(movieDto.getDescription());
            movie.setDirector(movieDto.getDirectorsNames() != null ? movieDto.getDirectorsNames() : "Unknown");
            movie.setRuntime(movieDto.getRuntime() != null ? movieDto.getRuntime().toString() : "0");
            movieRepository.save(movie);
        }
        
        Map<String, Object> request = new HashMap<>();
        request.put("userId", testUser.getId());
        request.put("tmdbId", movie.getTmdbId());
        request.put("title", movie.getSeriesTitle());

        mockMvc.perform(post("/api/movies/watchlist/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\": " + testUser.getId() + ", \"tmdbId\": " + movie.getTmdbId() + ", \"title\": \"" + movie.getSeriesTitle() + "\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Movie added to to-watch.")); // verificam raspuns

        // verificam daca filmul a fost adaugat in lista
        boolean isMovieInWatchlist = userToWatchService.isMovieToWatch(Long.valueOf(testUser.getId()), movie.getTmdbId());
        assertTrue(isMovieInWatchlist);
    }


}