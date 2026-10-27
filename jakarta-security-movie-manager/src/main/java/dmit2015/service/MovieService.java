package dmit2015.service;

import dmit2015.entity.Movie;
import dmit2015.repository.MovieRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.SecurityContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class MovieService {

    @Inject
    private SecurityContext securityContext;

    @Inject
    private MovieRepository movieRepository;

    public void addMovie(Movie movie) {
        LocalDateTime now = LocalDateTime.now();

        movie.setCreateTime(now);
        movie.setUpdateTime(now);

        String username = securityContext.getCallerPrincipal().getName();
        movie.setUsername(username);

        movieRepository.insert(movie);
    }

    public void updateMovie(Movie movie) {
        movie.setUpdateTime(LocalDateTime.now());

        movieRepository.update(movie);
    }

    public void deleteMovie(Movie movie) {
        String username = securityContext.getCallerPrincipal().getName();
        if (username.equalsIgnoreCase("anonymous") ) {
            throw new RuntimeException("Access Denied. Anonymous users do not have permission to access this method.");
        }
        boolean hasRequiredRoles = securityContext.isCallerInRole("Administration");
        if (!hasRequiredRoles) {
            throw new RuntimeException("Access denied. You role does not have permission to access this method.");
        }
        movieRepository.delete(movie);
    }

    public Optional<Movie> findMovieById(Long movieId) {
        return movieRepository.findOptionalById(movieId);
    }

    public List<Movie> getAllMovies() {
        String username = securityContext.getCallerPrincipal().getName();
        if (username.equalsIgnoreCase("anonymous") ) {
            throw new RuntimeException("Access Denied. Anonymous users do not have permission to access this method.");
        }
        boolean hasRequiredRoles = securityContext.isCallerInRole("Sales")
                || securityContext.isCallerInRole("Administration");
        if (!hasRequiredRoles) {
            throw new RuntimeException("Access denied. You role does not have permission to access this method.");
        }
        boolean hasSalesRole = securityContext.isCallerInRole("Sales");
        if (hasSalesRole) {
            return movieRepository.findAllByUsername(username);
        }
        return movieRepository.findAll();
    }

    public long countMovies() {
        return movieRepository.count();
    }
}
