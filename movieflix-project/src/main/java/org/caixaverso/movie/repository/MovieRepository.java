package org.caixaverso.movie.repository;

import org.caixaverso.movie.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie,Long> {

    @Query(value = "SELECT title, year, score FROM scifi_movie", nativeQuery = true)
    List<ScifiMovieProjection> findAllSciFiMovies();
}
