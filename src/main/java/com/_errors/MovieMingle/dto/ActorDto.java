package com._errors.MovieMingle.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ActorDto {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("character")  //personajul interpretat
    private String character;

    @JsonProperty("profile_path")
    private String profilePath;

    public ActorDto(String name, String character, Long id, String profilePath) {
        this.name = name;
        this.character = character;
        this.id = id;
        this.profilePath = profilePath;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public String getProfilePath() {
        return profilePath;
    }

    public void setProfilePath(String profilePath) {
        this.profilePath = profilePath;
    }
}
