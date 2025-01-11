package com._errors.MovieMingle.dto;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MovieCreditsDto {
    private List<ActorDto> cast;
    private List<DirectorDto> crew;

    public List<ActorDto> getActors() {
        return cast != null
                ? cast.stream()
                .limit(5) // Limităm rezultatul la primii 5 actori
                .map(actor -> new ActorDto(actor.getName(), actor.getCharacter(), actor.getId(), actor.getProfilePath()))
                .collect(Collectors.toList())
                : Collections.emptyList();
    }

    public List<DirectorDto> getDirectors() {
        return crew != null
                ? crew.stream()
                .filter(director -> director.getJob().equalsIgnoreCase("Director"))
                .map(director -> new DirectorDto(director.getName(), director.getId()))
                .collect(Collectors.toList())
                : Collections.emptyList();
    }

    // Noua metodă care returnează toți actorii din lista 'cast'
    public List<ActorDto> getAllActors() {
        return cast != null
                ? cast.stream()
                .map(actor -> new ActorDto(actor.getName(), actor.getCharacter(), actor.getId(), actor.getProfilePath()))
                .collect(Collectors.toList())
                : Collections.emptyList();
    }

    public List<ActorDto> getCast() {
        return cast;
    }

    public void setCast(List<ActorDto> cast) {
        this.cast = cast;
    }

    public List<DirectorDto> getCrew() {
        return crew;
    }

    public void setCrew(List<DirectorDto> crew) {
        this.crew = crew;
    }

    @Override
    public String toString() {
        // Generăm un String pentru actorii și regizorii din echipa de producție
        String actorsList = (cast != null && !cast.isEmpty()) ? cast.stream()
                .map(ActorDto::getName) // Presupunem că `ActorDto` are un `getName()` pentru nume
                .collect(Collectors.joining(", ")) : "No actors available";

        String directorsList = (crew != null && !crew.isEmpty()) ? crew.stream()
                .map(DirectorDto::getName) // Presupunem că `DirectorDto` are un `getName()` pentru nume
                .collect(Collectors.joining(", ")) : "No directors available";

        return "MovieCreditsDto{" +
                "cast=[" + actorsList + "], " +
                "crew=[" + directorsList + "]" +
                "}";
    }
}
