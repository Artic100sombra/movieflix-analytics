package org.caixaverso.movie.controller;

import lombok.AllArgsConstructor;
import org.caixaverso.movie.entity.Movie;
import org.caixaverso.movie.repository.ScifiMovieProjection;
import org.caixaverso.movie.service.MovieService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/movies")
@AllArgsConstructor
public class MovieController {

    private final MovieService service;

    @GetMapping
    public List<Movie> all() {
        return service.listAllMovies();
    }

    @PostMapping
    public Movie create(@RequestBody Movie movie) {
        return service.createMovie(movie);
    }

    @PostMapping("/{id}/rate")
    public Movie rate(@PathVariable Long id, @RequestParam double score,
                      @RequestParam Integer age, @RequestParam String country) {
        return service.addRatingToMovie(id, score, age, country);
    }

    @GetMapping("/{id}/analytics/average")
    public Double getAverage(@PathVariable Long id) {
        return service.getAverage(id);
    }

    @GetMapping("/{id}/analytics/country")
    public Map<String, Double> getAverageByCountry(@PathVariable Long id) {
        return service.getAverageByCountry(id);
    }

    @GetMapping("/{id}/analytics/age")
    public Map<String, Double> getAverageByAgeGroup(@PathVariable Long id) {
        return service.getAverageByAgeGroup(id);
    }

    @GetMapping("/top5-brazil")
    public List<Map<String,Object>> topFiveByBrazilians() {
        return service.getTopFiveByBrazilians();
    }

    @GetMapping("/top10-overall")
    public List<Map<String,Object>> topTenOverall() {
        return service.getTopTenOverall();
    }

    @GetMapping("/top5-young")
    public List<Map<String,Object>> topFiveByYoungAges() {
        return service.getTopFiveByYoungAges();
    }

    @GetMapping("/scifi")
    public List<ScifiMovieProjection> getSciFiMovies() {
        return service.getAllSciFiMovies();
    }


}
