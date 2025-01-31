package com._errors.MovieMingle.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_watched_movies")
public class UserWatchedMovie {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false,foreignKey = @ForeignKey(name = "FK_movie_ref"))
    private Movie movie;

    @Column(name = "watched_date")
    private LocalDateTime watchedAt;

    public UserWatchedMovie(){}
    public UserWatchedMovie(Long id, AppUser user, Movie movie, LocalDateTime watchedAt) {
        this.id = id;
        this.user = user;
        this.movie = movie;
        this.watchedAt = watchedAt;
    }

    public UserWatchedMovie(Long id, Movie movie, LocalDateTime watchedAt) {
        this.id = id;
        this.movie = movie;
        this.watchedAt = watchedAt;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public LocalDateTime getWatchedAt() {
        return watchedAt;
    }

    public void setWatchedAt(LocalDateTime watchedAt) {
        this.watchedAt = watchedAt;
    }


}

