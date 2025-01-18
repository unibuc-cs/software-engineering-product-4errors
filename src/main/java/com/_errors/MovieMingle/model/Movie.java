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

    @Column(name = "released_year")
    private int releasedYear;

    @Column(name = "certificate")
    private String certificate;

    @Column(name = "runtime", nullable = false)
    private String runtime;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "movie_genres", joinColumns = @JoinColumn(name = "movie_id"))
    @Column(name = "genre")
    private List<String> genre;

    @Column(name = "imdb_rating")
    private double imdbRating;

    @Column(name = "overview", nullable = false, columnDefinition = "TEXT")
    private String overview;

    @Column(name = "meta_score")
    private Integer metaScore;

    @Column(name = "director", nullable = false)
    private String director;


    @Column(name = "no_of_votes")
    private int noOfVotes;

    @Column(name = "gross")
    private Long gross;



    // Default constructor
    public Movie() {}

    // Updated constructor
    public Movie(Long movieId, Long tmdbId,String posterLink, String seriesTitle, int releasedYear, String certificate,
                 String runtime, List<String> genre, double imdbRating, String overview, Integer metaScore,
                 String director, int noOfVotes, Long gross) {
        this.movieId = movieId;
        this.tmdbId=tmdbId;
        this.posterLink = posterLink;
        this.seriesTitle = seriesTitle;
        this.releasedYear = releasedYear;
        this.certificate = certificate;
        this.runtime = runtime;
        this.genre = genre;  // Now it's a List<String>
        this.imdbRating = imdbRating;
        this.overview = overview;
        this.metaScore = metaScore;
        this.director = director;
        this.noOfVotes = noOfVotes;
        this.gross = gross;
    }

    // Getters and setters
    public Long getMovieId() {
        return movieId;
    }

    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }

    public Long getTMDBId() {
        return tmdbId;
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

    public int getReleasedYear() {
        return releasedYear;
    }

    public void setReleasedYear(int releasedYear) {
        this.releasedYear = releasedYear;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    public List<String> getGenre() {
        return genre;
    }

    public void setGenre(List<String> genre) {
        this.genre = genre;
    }

    public double getImdbRating() {
        return imdbRating;
    }

    public void setImdbRating(double imdbRating) {
        this.imdbRating = imdbRating;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public Integer getMetaScore() {
        return metaScore;
    }

    public void setMetaScore(Integer metaScore) {
        this.metaScore = metaScore;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }


    public int getNoOfVotes() {
        return noOfVotes;
    }

    public void setNoOfVotes(int noOfVotes) {
        this.noOfVotes = noOfVotes;
    }

    public Long getGross() {
        return gross;
    }

    public void setGross(Long gross) {
        this.gross = gross;
    }
}
