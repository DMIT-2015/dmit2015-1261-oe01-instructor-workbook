package dmit2015.service;

import dmit2015.entity.Movie;
import dmit2015.repository.MovieRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class MovieService {

    @Inject
    private MovieRepository movieRepository;

    public void addMovie(Movie movie) {
        LocalDateTime now = LocalDateTime.now();

        movie.setCreateTime(now);
        movie.setUpdateTime(now);

        movieRepository.insert(movie);
    }

    public void updateMovie(Movie movie) {
        movie.setUpdateTime(LocalDateTime.now());

        movieRepository.update(movie);
    }

    public void deleteMovie(Movie movie) {
        movieRepository.delete(movie);
    }

    public Optional<Movie> findMovieById(Long movieId) {
        return movieRepository.findOptionalById(movieId);
    }

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public long countMovies() {
        return movieRepository.count();
    }
}
