package com._errors.MovieMingle.recommendation;
import org.apache.commons.math3.linear.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com._errors.MovieMingle.model.Movie;
import com._errors.MovieMingle.repository.MovieRepository;
import java.util.*;

@Service
public class SVDRecommendationService {

    private final MatrixBuilder matrixBuilder;
    private final MovieRepository movieRepository;
    private RatingMatrix ratingMatrix;
    private RealMatrix U;

    public RealMatrix getU() {
        return U;
    }

    public RealMatrix getS() {
        return S;
    }

    public RealMatrix getVt() {
        return Vt;
    }

    private RealMatrix S;
    private RealMatrix Vt;

    @Autowired
    public SVDRecommendationService(MatrixBuilder matrixBuilder, MovieRepository movieRepository) {
        this.matrixBuilder = matrixBuilder;
        this.movieRepository = movieRepository;
    }

    public void trainModel() {
        //matricea de ratinguri
        this.ratingMatrix = matrixBuilder.buildMatrix();
        RealMatrix matrix = MatrixUtils.createRealMatrix(ratingMatrix.getMatrix());

        //svd
        SingularValueDecomposition svd = new SingularValueDecomposition(matrix);

        //descompunerea matricei de rating in U,S,V transpusa
        this.U = svd.getU();
        this.S = svd.getS();
        this.Vt = svd.getV().transpose();
    }

    public List<Movie> recommendMovies(Long userId, int numRecommendations) {
        trainModel();
        //indexul userului
        int userIndex = ratingMatrix.getUserIds().indexOf(userId);
        if (userIndex == -1) {
            throw new IllegalArgumentException("User not found in training data");
        }


        //calculam ratingurile prezise
        RealMatrix userVector = U.getRowMatrix(userIndex);
        RealMatrix predictedRatings = userVector.multiply(S).multiply(Vt);
        double[] userRatings = predictedRatings.getRow(0);

        //gasim filmele nevizionate
        double[][] originalRatings = ratingMatrix.getMatrix();
        List<MoviePrediction> moviePredictions = new ArrayList<>();

        for (int i = 0; i < userRatings.length; i++) {
            if (originalRatings[userIndex][i] == 0) { // Film nevizionat
                Long movieId = ratingMatrix.getMovieIds().get(i);
                moviePredictions.add(new MoviePrediction(movieId, userRatings[i]));
            }
        }

        //sortam in functie de rating
        moviePredictions.sort((a, b) -> Double.compare(b.getPredictedRating(), a.getPredictedRating()));

        List<Movie> recommendations = new ArrayList<>();
        for (int i = 0; i < Math.min(numRecommendations, moviePredictions.size()); i++) {
            Long movieId = moviePredictions.get(i).getMovieId();
            Movie movie = movieRepository.findById(movieId).orElse(null);
            if (movie != null) {
                recommendations.add(movie);
            }
        }

        return recommendations;
    }

    //afisarea matricei
    public void printRatingMatrix() {
        RatingMatrix matrix = matrixBuilder.buildMatrix();
        double[][] ratings = matrix.getMatrix();
        List<Long> userIds = matrix.getUserIds();
        List<Long> movieIds = matrix.getMovieIds();

        // Print header with movie IDs
        System.out.print("User/Movie\t");
        for (Long movieId : movieIds) {
            System.out.print(movieId + "\t");
        }
        System.out.println();

        // Print matrix with user IDs
        for (int i = 0; i < ratings.length; i++) {
            System.out.print(userIds.get(i) + "\t");
            for (int j = 0; j < ratings[i].length; j++) {
                System.out.printf("%.1f\t", ratings[i][j]);
            }
            System.out.println();
        }
    }
    public void updateRating() {
        //reconstruiește matricea de rating din baza de date
        this.ratingMatrix = matrixBuilder.buildMatrix();

        //re-antrenează modelul
        trainModel();
    }


    private static class MoviePrediction {
        private final Long movieId;
        private final double predictedRating;

        public MoviePrediction(Long movieId, double predictedRating) {
            this.movieId = movieId;
            this.predictedRating = predictedRating;
        }

        public Long getMovieId() {
            return movieId;
        }

        public double getPredictedRating() {
            return predictedRating;
        }
    }
}