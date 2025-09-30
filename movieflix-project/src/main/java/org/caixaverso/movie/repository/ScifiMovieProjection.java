package org.caixaverso.movie.repository;

import org.caixaverso.movie.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ScifiMovieProjection {

    String getTitle();
    Integer getYear();
    Double getScore();
}
