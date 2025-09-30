package org.caixaverso.movie.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.caixaverso.rating.entity.Rating;

import java.util.List;

@Entity
@Getter
@Setter
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private Integer year;
    private String genre;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Rating> ratings;

    @Transient
    public double getAverageRating() {
        return ratings.isEmpty() ? 0.0 :
                ratings.stream().mapToDouble(Rating::getScore).average().orElse(0.0);
    }
}
