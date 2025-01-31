package com._errors.MovieMingle.service;

import com._errors.MovieMingle.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class MovieApiClient {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${tmdb.api.key}")
    private String apiKey; // Injectăm cheia API din application.properties

    @Value("${tmdb.bearer.token")
    private String bearerToken;

    private final String API_BASE_URL = "https://api.themoviedb.org/3"; // URL-ul de bază pentru TMDB

    // Metoda pentru completarea detaliilor unui film
    private void populateMovieDetails(MovieDto movie) {
        try {
            // Detalii film
            String detailsUrl = API_BASE_URL + "/movie/" + movie.getId() + "?api_key=" + apiKey;
            MovieDto detailedMovie = restTemplate.getForObject(detailsUrl, MovieDto.class);

            if (detailedMovie != null) {
                movie.setDescription(detailedMovie.getDescription());
                movie.setRuntime(detailedMovie.getRuntime());
                movie.setGenres(detailedMovie.getGenres());
                movie.setBackdropPath(detailedMovie.getBackdropPath());
                movie.setPosterPath(detailedMovie.getPosterPath());
                movie.setRating(detailedMovie.getRating());
                movie.setReleaseDate(detailedMovie.getReleaseDate());
                movie.setTitle(detailedMovie.getTitle());
                movie.setTrailerPath(detailedMovie.getTrailerPath());
            }

            // Preia trailer-ul (dacă există)
            String trailerUrl = API_BASE_URL + "/movie/" + movie.getId() + "/videos?api_key=" + apiKey;
            TrailerApiResponse trailerResponse = restTemplate.getForObject(trailerUrl, TrailerApiResponse.class);

            if (trailerResponse != null && !trailerResponse.getResults().isEmpty()) {
                movie.setTrailerPath(trailerResponse.getResults().get(0).getKey());  // Preia primul trailer
            }

            // Obținem actorii și regizorii in credits si apoi populam campurile aferente cu date
            String creditsUrl = API_BASE_URL + "/movie/" + movie.getId() + "/credits?api_key=" + apiKey;
            MovieCreditsDto credits = restTemplate.getForObject(creditsUrl, MovieCreditsDto.class);

            if (credits != null) {
                movie.setCredits(credits);
            }
        } catch (Exception e) {
            e.printStackTrace();  // Aruncă eroarea pentru debug
        }
    }

    // Metoda pentru obținerea detaliilor unui film
    public MovieDto getMovie(Long movieId) {

        MovieDto movie = null;
        String url = API_BASE_URL + "/movie/" + movieId + "?api_key=" + apiKey;

        // Configurarea antetului cu Bearer Token
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + bearerToken); // Setează Bearer Token
        headers.set("accept", "application/json"); // Acceptă răspuns JSON

        HttpEntity<String> entity = new HttpEntity<>(headers); // Creează entitatea cu antetul

        // Instanțiază RestTemplate și trimite cererea
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<MovieDto> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                MovieDto.class
        );

        movie = responseEntity.getBody();

        // Preia trailer-ul (dacă există)
        String trailerUrl = API_BASE_URL + "/movie/" + movie.getId() + "/videos?api_key=" + apiKey;
        TrailerApiResponse trailerResponse = restTemplate.getForObject(trailerUrl, TrailerApiResponse.class);

        if (trailerResponse != null && !trailerResponse.getResults().isEmpty()) {
            // Filtrează lista pentru a găsi un trailer cu tipul "Trailer"
            Optional<TrailerApiResult> trailer = trailerResponse.getResults().stream()
                    .filter(result -> "Trailer".equalsIgnoreCase(result.getType()) && "YouTube".equalsIgnoreCase(result.getSite()))
                    .findFirst();

            // Setează path-ul trailerului dacă este găsit
            MovieDto finalMovie = movie;
            trailer.ifPresent(result -> finalMovie.setTrailerPath(result.getKey()));
        }

        // Obținem actorii și regizorii in credits si apoi populam campurile aferente cu date
        String creditsUrl = API_BASE_URL + "/movie/" + movie.getId() + "/credits?api_key=" + apiKey;
        MovieCreditsDto credits = restTemplate.getForObject(creditsUrl, MovieCreditsDto.class);

        if (credits != null) {
            movie.setCredits(credits);
        }

        return movie;
    }


    // Metoda pentru obținerea filmelor populare
    public List<MovieDto> getPopularMovies() {
        String url = API_BASE_URL + "/movie/popular?api_key=" + apiKey;
        MovieApiResponse response = restTemplate.getForObject(url, MovieApiResponse.class);
        List<MovieDto> movies = response != null ? response.getResults() : Arrays.asList(); // Extragem lista de filme din "results"

        // Completăm detaliile fiecărui film
        movies.forEach(this::populateMovieDetails);
        return movies; // Returnăm lista completă de filme
    }



    //functii pentru popularea tabelului de rating
    public List<MovieDto> getPopularMoviesPage(int page) {
        String url = API_BASE_URL + "/movie/popular?api_key=" + apiKey + "&page=" + page;
        MovieApiResponse response = restTemplate.getForObject(url, MovieApiResponse.class);
        List<MovieDto> movies = response != null ? response.getResults() : Arrays.asList();

        // Completăm detaliile fiecărui film
        movies.forEach(this::populateMovieDetails);
        return movies;
    }
    public List<MovieDto> getTopRatedMoviesPage(int page) {
        String url = API_BASE_URL + "/movie/top_rated?api_key=" + apiKey + "&page=" + page;
        MovieApiResponse response = restTemplate.getForObject(url, MovieApiResponse.class);
        List<MovieDto> movies = response != null ? response.getResults() : Arrays.asList();
        movies.forEach(this::populateMovieDetails);
        return movies;
    }

    public List<MovieDto> getTrendingMoviesPage(int page) {
        String url = API_BASE_URL + "/trending/movie/week?api_key=" + apiKey + "&page=" + page;
        MovieApiResponse response = restTemplate.getForObject(url, MovieApiResponse.class);
        List<MovieDto> movies = response != null ? response.getResults() : Arrays.asList();
        movies.forEach(this::populateMovieDetails);
        return movies;
    }

    // Metoda pentru obținerea celor mai bine cotate filme
    public List<MovieDto> getTopRatedMovies() {
        String url = API_BASE_URL + "/movie/top_rated?api_key=" + apiKey;
        MovieApiResponse response = restTemplate.getForObject(url, MovieApiResponse.class);
        List<MovieDto> movies = response != null ? response.getResults() : Arrays.asList(); // Extragem lista de filme din "results"

        // Completăm detaliile fiecărui film
        movies.forEach(this::populateMovieDetails);
        return movies; // Returnăm lista completă de filme
    }

    // Metoda pentru obținerea filmelor similare unui film
    public List<MovieDto> getRecommendationsMovies(Long movieId) {
        String url = API_BASE_URL + "/movie/" + movieId + "/recommendations?api_key=" + apiKey;
        MovieApiResponse response = restTemplate.getForObject(url, MovieApiResponse.class);
        List<MovieDto> movies = response != null ? response.getResults() : Arrays.asList(); // Extragem lista de filme din "results"

        movies.forEach(this::populateMovieDetails);
        return movies;
    }

    public List<ActorDto> getMovieCast(Long movieId) {
        // Obținem actorii și regizorii in credits si apoi populam campurile aferente cu date
        String creditsUrl = API_BASE_URL + "/movie/" + movieId + "/credits?api_key=" + apiKey;
        MovieCreditsDto credits = restTemplate.getForObject(creditsUrl, MovieCreditsDto.class);
        List<ActorDto> actors = credits != null ? credits.getAllActors() : List.of();
        return actors;
    }


    public List<MovieDto> getMoviesByPage(int page) {
        List<MovieDto> movies = new ArrayList<>();

        LocalDate today = LocalDate.now();

        // URL-ul complet al cererii
        String url = "https://api.themoviedb.org/3/discover/movie?api_key=" + apiKey +"&certification=PG-13&certification.lte=PG-13&include_adult=false&include_video=false&language=en-US&page=" + page + "&primary_release_date.lte="+ today +"&sort_by=popularity.desc";


        // Configurarea antetului cu Bearer Token
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + bearerToken); // Setează Bearer Token
        headers.set("accept", "application/json"); // Acceptă răspuns JSON

        HttpEntity<String> entity = new HttpEntity<>(headers); // Creează entitatea cu antetul

        // Instanțiază RestTemplate și trimite cererea
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<MovieApiResponse> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                MovieApiResponse.class
        );

        // Obține răspunsul și adaugă filmele în listă
        MovieApiResponse response = responseEntity.getBody();
        if (response != null && response.getResults() != null) {
            movies.addAll(response.getResults());
        }

        // Completează detaliile filmelor și le afișează în consolă
        movies.forEach(this::populateMovieDetails);
        return movies;
    }



    public List<MovieDto> getMoviesBySearch(String search, int page) {
        List<MovieDto> movies = new ArrayList<>();

        // Asigură-te că termenul de căutare este codificat corect
        String encodedSearch = URLEncoder.encode(search, StandardCharsets.UTF_8);
        String url = "https://api.themoviedb.org/3/search/movie?api_key=" + apiKey +
                "&query=" + search +
                "&include_adult=false&language=en-US&page=" + page;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + bearerToken);
        headers.set("accept", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<MovieApiResponse> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                MovieApiResponse.class
        );

        MovieApiResponse response = responseEntity.getBody();
        if (response != null && response.getResults() != null) {
            movies.addAll(response.getResults());
        }

        movies.forEach(this::populateMovieDetails);
        return movies;
    }

    public List<MovieDto> getMoviesByGenre(String qenres, int page) {
        List<MovieDto> movies = new ArrayList<>();

        // Asigură-te că termenul de căutare este codificat corect
        //String encodedSearch = URLEncoder.encode(qenres, StandardCharsets.UTF_8);
        String encodedSearch = qenres.replaceAll(" ", "%20"); // Codificăm doar spațiile (dacă există)

        String url = "https://api.themoviedb.org/3/discover/movie?api_key=" + apiKey +
                "&include_adult=false&language=en-US&page=" + page +
                "&sort_by=popularity.desc&with_genres="+encodedSearch;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + bearerToken);
        headers.set("accept", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<MovieApiResponse> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                MovieApiResponse.class
        );

        MovieApiResponse response = responseEntity.getBody();
        if (response != null && response.getResults() != null) {
            movies.addAll(response.getResults());
        }

        movies.forEach(this::populateMovieDetails);
        return movies;
    }

}
