package com._errors.MovieMingle.recommendation;
import com._errors.MovieMingle.repository.AppUserRepository;
import com._errors.MovieMingle.repository.MovieRepository;
import com._errors.MovieMingle.repository.RatingRepository;
import com._errors.MovieMingle.model.AppUser;
import com._errors.MovieMingle.model.Movie;
import com._errors.MovieMingle.model.Rating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

@Component
public class MatrixBuilder {

    @Autowired
    private final AppUserRepository userRepository;
    @Autowired
    private final MovieRepository movieRepository;
    @Autowired
    private final RatingRepository ratingRepository;

    @Autowired
    public MatrixBuilder(AppUserRepository userRepository,
                         MovieRepository movieRepository,
                         RatingRepository ratingRepository) {
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
        this.ratingRepository = ratingRepository;
    }

    public RatingMatrix buildMatrix() {
        //toate entitatile din db
        List<AppUser> users = userRepository.findAll();
        List<Movie> movies = movieRepository.findAll();

        //mapari pt index
        Map<Long, Integer> userIndexMap = new HashMap<>();
        Map<Long, Integer> movieIndexMap = new HashMap<>();
        List<Long> userIds = new ArrayList<>();
        List<Long> movieIds = new ArrayList<>();


        for (int i = 0; i < users.size(); i++) {
            Long userId = (long)users.get(i).getId();
            userIndexMap.put(userId, i);
            userIds.add(userId);
        }


        for (int i = 0; i < movies.size(); i++) {
            Long movieId = movies.get(i).getMovieId();
            movieIndexMap.put(movieId, i);
            movieIds.add(movieId);
        }

        //initializam matricea
        double[][] ratingMatrix = new double[users.size()][movies.size()];

        //valori default
        for (int i = 0; i < users.size(); i++) {
            for (int j = 0; j < movies.size(); j++) {
                ratingMatrix[i][j] = 0.0;
            }
        }

        //populeaza matricea cu rating-uri
        for (AppUser user : users) {
            List<Rating> userRatings = ratingRepository.findByUser_Id(user.getId());
            for (Rating rating : userRatings) {
                Long userId = (long)user.getId();
                Long movieId = rating.getMovie().getMovieId();

                Integer userIndex = userIndexMap.get(userId);
                Integer movieIndex = movieIndexMap.get(movieId);

                if (userIndex != null && movieIndex != null) {
                    ratingMatrix[userIndex][movieIndex] = rating.getRating();
                } else {
                    System.out.println("Null index found - userIndex: " + userIndex +
                            ", movieIndex: " + movieIndex);
                }
            }
        }

        return new RatingMatrix(ratingMatrix, userIds, movieIds);
    }
}