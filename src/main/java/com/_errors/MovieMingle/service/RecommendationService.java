package com._errors.MovieMingle.service;

import com._errors.MovieMingle.model.Movie;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RecommendationService {

    // Placeholder method for recommendations
    public List<Movie> getPlaceholderRecommendations() {
        // Add dummy data
        List<Movie> placeholderMovies = new ArrayList<>();
        placeholderMovies.add(new Movie(1L, "The Godfather", "Crime/Drama", 1972, "The aging patriarch of an organized crime dynasty transfers control of his clandestine empire to his reluctant son."));
        placeholderMovies.add(new Movie(2L, "The Dark Knight", "Action/Crime/Drama", 2008, "Batman faces the Joker, a criminal mastermind intent on sowing chaos in Gotham City."));
        placeholderMovies.add(new Movie(3L, "Forrest Gump", "Drama/Romance", 1994, "The story of Forrest Gump, a slow-witted but kind-hearted man from Alabama, who witnesses and influences historical events."));
        placeholderMovies.add(new Movie(4L, "Pulp Fiction", "Crime/Drama", 1994, "A series of interconnected stories revolving around crime, love, and redemption in Los Angeles."));
        placeholderMovies.add(new Movie(5L, "The Shawshank Redemption", "Drama", 1994, "Two imprisoned men bond over years, finding solace and eventual redemption through acts of common decency."));
        placeholderMovies.add(new Movie(6L, "Inception", "Action/Sci-Fi", 2010, "A skilled thief is given a chance at redemption if he can successfully plant an idea into a target's subconscious."));
        placeholderMovies.add(new Movie(7L, "The Matrix", "Action/Sci-Fi", 1999, "A hacker discovers the truth about his reality and his role in a war against its controllers."));
        placeholderMovies.add(new Movie(8L, "Interstellar", "Adventure/Drama/Sci-Fi", 2014, "A group of astronauts travel through a wormhole in search of a new home for humanity."));
        placeholderMovies.add(new Movie(9L, "Parasite", "Comedy/Drama/Thriller", 2019, "A poor family schemes to infiltrate a wealthy household by posing as unrelated, highly qualified individuals."));
        placeholderMovies.add(new Movie(10L, "The Lion King", "Animation/Adventure/Drama", 1994, "A young lion prince flees his kingdom after the death of his father, only to learn the true meaning of responsibility and bravery."));

        return placeholderMovies;
    }
}
