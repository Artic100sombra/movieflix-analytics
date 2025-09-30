package org.caixaverso.movie.service;

import lombok.AllArgsConstructor;
import org.caixaverso.movie.entity.Movie;
import org.caixaverso.movie.repository.MovieRepository;
import org.caixaverso.movie.repository.ScifiMovieProjection;
import org.caixaverso.rating.entity.Rating;
import org.caixaverso.rating.repository.RatingRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
@AllArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final RatingRepository ratingRepository;

    public List<Movie> listAllMovies() {
        return movieRepository.findAll();
    }

    public Movie createMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    public Movie addRatingToMovie(Long id, double score, Integer age, String country) {
        if (score < 0.0 || score > 10.0) {
            throw new IllegalArgumentException("A nota deve ser entre 0.0 e 10.0");
        }
        BigDecimal ratingFixed = BigDecimal.valueOf(score).setScale(1, RoundingMode.HALF_UP);
        double roundedRating = ratingFixed.doubleValue();
        Movie movie = movieRepository.findById(id).orElseThrow(() -> new RuntimeException("Filme não encontrado"));
        Rating rating = new Rating();
        rating.setScore(roundedRating);
        rating.setAge(age);
        rating.setCountry(country);
        rating.setMovie(movie);

        ratingRepository.save(rating);

        return movie;
    }

    public Double getAverage(Long movieId) {
        return ratingRepository.averageByMovie(movieId);
    }

    public Map<String, Double> getAverageByCountry(Long movieId) {
        Map<String, Double> result = new HashMap<>();
        for (Object[] row : ratingRepository.averageByCountry(movieId)) {
            result.put((String) row[0], (Double) row[1]);
        }
        return result;
    }

    public Map<String, Double> getAverageByAgeGroup(Long movieId) {
        Map<String, Double> result = new HashMap<>();
        for (Object[] row : ratingRepository.averageByAgeGroup(movieId)) {
            result.put((String) row[0], (Double) row[1]);
        }
        return result;
    }

    private List<Map<String, Object>> mapToListOfMaps(List<Object[]> rows) {
        List<Map<String,Object>> result = new ArrayList<>();
        for (Object[] row : rows) {
            Map<String,Object> map = new HashMap<>();
            map.put("title", row[0]);
            map.put("avgScore", ((Number) row[1]).doubleValue());
            result.add(map);
        }
        return result;
    }

    public List<Map<String,Object>> getTopFiveByBrazilians() {
        return mapToListOfMaps(ratingRepository.topFiveByBrazilians());
    }

    public List<Map<String,Object>> getTopTenOverall() {
        return mapToListOfMaps(ratingRepository.topTenOverall());
    }

    public List<Map<String,Object>> getTopFiveByYoungAges() {
        return mapToListOfMaps(ratingRepository.topFiveByYoungAges());
    }

    public List<ScifiMovieProjection> getAllSciFiMovies() {
        return movieRepository.findAllSciFiMovies();
    }

}
