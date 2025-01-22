package com._errors.MovieMingle.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
@Entity
@Table(name = "user_to_watch")
public class UserToWatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false,foreignKey = @ForeignKey(name = "FK_movie_ref"))
    private Movie movie;

    @Column(name = "added_date")
    private LocalDateTime addedAt;

    public UserToWatch(){}
    public UserToWatch(Long id, AppUser user, Movie movie, LocalDateTime watchedAt) {
        this.id = id;
        this.user = user;
        this.movie = movie;
        this.addedAt = watchedAt;
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

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDateTime watchedAt) {
        this.addedAt = watchedAt;
    }
}
