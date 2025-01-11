package com._errors.MovieMingle.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.ALWAYS)
public class MovieDto {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("release_date")
    private String releaseDate; // Sau LocalDate dacă vrei să folosești un tip de dată

    @JsonProperty("overview")
    private String description;

    @JsonProperty("vote_average")
    private Double rating;

    @JsonProperty("runtime")
    private Integer runtime;

    @JsonProperty("poster_path")
    private String posterPath;

    @JsonProperty("backdrop_path")
    private String backdropPath;

    private String trailerPath;

    private String directorsNames;

    @JsonProperty("genres")
    private List<GenreDto> genres;

    private List<ActorDto> actors;
    private List<DirectorDto> directors;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setCredits(MovieCreditsDto credits) {
        // Verificăm dacă există actori și regizori și îi setăm în listele corespunzătoare
        if (credits != null) {
            //this.actors = credits.getActors();
            this.directors = credits.getDirectors();
            directorsNames = getDirectorsNames();
        } else {
            //this.actors = Collections.emptyList();
            this.directors = Collections.emptyList();
        }

    }

    public String getDirectorsNames() {
        if (directors != null && !directors.isEmpty()) {
            return directors.stream()
                    .map(DirectorDto::getName)
                    .collect(Collectors.joining(", "));
        }
        return "Unknown Director";
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Integer getRuntime() {
        return runtime;
    }

    public void setRuntime(Integer runtime) {
        this.runtime = runtime;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public String getTrailerPath() {
        return trailerPath;
    }

    public void setTrailerPath(String trailerPath) {
        this.trailerPath = trailerPath;
    }

    public List<DirectorDto> getDirectors() {
        return this.directors;
    }

    public void setDirectors(List<DirectorDto> directors) {
        this.directors = directors;
    }

    public List<GenreDto> getGenres() {
        return genres;
    }

    public void setGenres(List<GenreDto> genres) {
        this.genres = genres;
    }

    public List<ActorDto> getActors() {
        return this.actors;
    }

    public void setActors(List<ActorDto> actors) {
        this.actors = actors;
    }

    // Implementare metodă toString()
    @Override
    public String toString() {
        // Extragem numele actorilor și regizorilor pentru a le afisa într-un mod mai clar
        String actorsList = actors != null ? actors.stream()
                .map(ActorDto::getName)  // Preluăm doar numele actorilor
                .collect(Collectors.joining(", "))  // Concatenăm numele într-un singur șir
                : "N/A";  // Dacă nu există actori, afișăm "N/A"

        String directorsList = directors != null ? directors.stream()
                .map(DirectorDto::getName)  // Preluăm doar numele regizorilor
                .collect(Collectors.joining(", "))  // Concatenăm numele într-un singur șir
                : "N/A";  // Dacă nu există regizori, afișăm "N/A"

        return "MovieDto{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                ", runtime=" + runtime +
                ", rating='" + rating + '\'' +
                ", backdropPath='" + backdropPath + '\'' +
                ", posterPath='" + posterPath + '\'' +
                ", genres=" + genres +
                ", actors=" + actorsList +  // Afișăm lista de actori
                ", directors=" + directorsList +  // Afișăm lista de regizori
                ", trailerPath='" + trailerPath + '\'' +
                '}';
    }

}
