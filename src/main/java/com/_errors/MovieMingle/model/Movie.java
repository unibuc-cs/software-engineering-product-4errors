package com._errors.MovieMingle.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "movie")
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_id")
    private Long movieId;

    @Column(name = "TMDB_id")
    private Long tmdbId;

    @Column(name = "poster_link", nullable = false)
    private String posterLink;

    @Column(name = "series_title", nullable = false)
    private String seriesTitle;


    @Column(name = "runtime", nullable = false)
    private String runtime;


    @Column(name = "overview", nullable = false, columnDefinition = "TEXT")
    private String overview;

    @Column(name = "director", nullable = false)
    private String director;


    // Default constructor
    public Movie() {}

    public Movie(Long movieId, Long tmdbId, String posterLink, String seriesTitle, String runtime, String overview, String director) {
        this.movieId = movieId;
        this.tmdbId = tmdbId;
        this.posterLink = posterLink;
        this.seriesTitle = seriesTitle;
        this.runtime = runtime;
        this.overview = overview;
        this.director = director;
    }

    // Getters and setters
    public Long getMovieId() {
        return movieId;
    }

    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }

    public Long getTmdbId() {
        return this.tmdbId;
    }

    public void setTMDBId(Long TMBDId) {
        this.tmdbId = TMBDId;
    }
    public String getPosterLink() {
        return posterLink;
    }

    public void setPosterLink(String posterLink) {
        this.posterLink = posterLink;
    }

    public String getSeriesTitle() {
        return seriesTitle;
    }

    public void setSeriesTitle(String seriesTitle) {
        this.seriesTitle = seriesTitle;
    }


    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }


    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

}