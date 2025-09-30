package org.caixaverso.rating.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.caixaverso.movie.entity.Movie;

@Entity
@Getter
@Setter
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double score;
    private Integer age;
    private String country;

    @ManyToOne
    @JoinColumn(name = "movie_id")
    @JsonBackReference
    private Movie movie;
}

