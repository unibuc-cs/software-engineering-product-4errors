package com._errors.MovieMingle.recommendation;

import com._errors.MovieMingle.model.Movie;
import com._errors.MovieMingle.repository.MovieRepository;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularValueDecomposition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SVDRecommendationServiceTest {

    @Mock
    private MatrixBuilder matrixBuilder;

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private SVDRecommendationService svdRecommendationService;

    private RatingMatrix ratingMatrix;
    private RealMatrix U;
    private RealMatrix S;
    private RealMatrix Vt;

    @BeforeEach
    public void setUp() {
        // matricea de rating-uri de test
        double[][] matrix = {{5.0, 3.0}, {4.0, 0.0}};
        List<Long> userIds = Arrays.asList(1L, 2L);
        List<Long> movieIds = Arrays.asList(101L, 102L);
        ratingMatrix = new RatingMatrix(matrix, userIds, movieIds);

        // descompunere SVD de test
        RealMatrix realMatrix = MatrixUtils.createRealMatrix(matrix);
        SingularValueDecomposition svd = new SingularValueDecomposition(realMatrix);
        U = svd.getU();
        S = svd.getS();
        Vt = svd.getV().transpose();
    }

    @Test
    public void testTrainModel() {
        
        when(matrixBuilder.buildMatrix()).thenReturn(ratingMatrix);

        
        svdRecommendationService.trainModel();

        
        assertNotNull(svdRecommendationService.getU());
        assertNotNull(svdRecommendationService.getS());
        assertNotNull(svdRecommendationService.getVt());
    }

    @Test
    public void testRecommendMovies() {
        
        when(matrixBuilder.buildMatrix()).thenReturn(ratingMatrix);

        // filmul cu ID-ul 102 există in db
        Movie movie = new Movie();
        movie.setMovieId(102L);
        when(movieRepository.findById(102L)).thenReturn(Optional.of(movie));

        
        List<Movie> recommendations = svdRecommendationService.recommendMovies(2L, 1); // testam pentru user 2L, care are 0.0 in matrice pt filmul 2L

        
        assertNotNull(recommendations);
        assertEquals(1, recommendations.size());
        assertEquals(102L, recommendations.get(0).getMovieId()); // verificam ID-ul filmului recomandat
        verify(movieRepository, times(1)).findById(102L);
    }

    @Test
    public void testUpdateRating() {
        
        when(matrixBuilder.buildMatrix()).thenReturn(ratingMatrix);

        
        svdRecommendationService.updateRating();

        
        verify(matrixBuilder, times(2)).buildMatrix();
        assertNotNull(svdRecommendationService.getU());
        assertNotNull(svdRecommendationService.getS());
        assertNotNull(svdRecommendationService.getVt());
    }
}