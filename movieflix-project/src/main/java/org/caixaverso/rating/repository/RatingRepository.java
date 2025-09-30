package org.caixaverso.rating.repository;

import org.caixaverso.rating.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByCountry(String country);
    List<Rating> findByAgeBetween(int min, int max);

    @Query("SELECT AVG(r.score) FROM Rating r WHERE r.movie.id = :movieId")
    Double averageByMovie(@Param("movieId") Long movieId);

    @Query("SELECT r.country, AVG(r.score) FROM Rating r WHERE r.movie.id = :movieId GROUP BY r.country")
    List<Object[]> averageByCountry(@Param("movieId") Long movieId);

    @Query("SELECT " +
            "CASE " +
            " WHEN r.age BETWEEN 0 AND 17 THEN '0-17' " +
            " WHEN r.age BETWEEN 18 AND 29 THEN '18-29' " +
            " WHEN r.age BETWEEN 30 AND 44 THEN '30-44' " +
            " WHEN r.age BETWEEN 45 AND 60 THEN '45-60' " +
            " ELSE '60+' END as ageGroup, " +
            "AVG(r.score) " +
            "FROM Rating r WHERE r.movie.id = :movieId GROUP BY ageGroup")
    List<Object[]> averageByAgeGroup(@Param("movieId") Long movieId);

    @Query(value = """
        SELECT m.title, AVG(r.score) AS avg_score
        FROM movie m
        JOIN rating r ON r.movie_id = m.id
        WHERE r.country ILIKE 'Brazil'
        GROUP BY m.title
        ORDER BY avg_score DESC
        LIMIT 5
    """, nativeQuery = true)
    List<Object[]> topFiveByBrazilians();

    @Query(value = """
        SELECT m.title, AVG(r.score) AS avg_score
        FROM movie m
        JOIN rating r ON r.movie_id = m.id
        GROUP BY m.title
        ORDER BY avg_score DESC
        LIMIT 10
    """, nativeQuery = true)
    List<Object[]> topTenOverall();

    @Query(value = """
        SELECT m.title, AVG(r.score) AS avg_score
        FROM movie m
        JOIN rating r ON r.movie_id = m.id
        WHERE r.age BETWEEN 0 AND 17
        GROUP BY m.title
        ORDER BY avg_score DESC
        LIMIT 5
    """, nativeQuery = true)
    List<Object[]> topFiveByYoungAges();

}

