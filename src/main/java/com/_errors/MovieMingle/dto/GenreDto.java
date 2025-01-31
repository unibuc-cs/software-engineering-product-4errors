package com._errors.MovieMingle.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL) // Ignoră câmpurile null în timpul serializării
public class GenreDto {
    private Long id;
    private String name;

    // Getter și Setter pentru id
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Getter și Setter pentru name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GenreDto(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "GenreDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
