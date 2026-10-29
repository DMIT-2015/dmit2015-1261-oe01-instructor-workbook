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

    private String getAuthenticatedUsername() {
        return securityContext.getCallerPrincipal().getName();
    }

    private boolean hasContributorRole() {
        return securityContext.isCallerInRole("Sales");
    }

    private boolean hasAdministratorRole() {
        return securityContext.isCallerInRole("Administration");
    }

    private boolean hasRequiredRoles() {
        return hasContributorRole() || hasAdministratorRole();
    }

    private boolean isAuthenticated() {
        return !getAuthenticatedUsername().equalsIgnoreCase("anonymous");
    }

    public void addMovie(Movie movie) {
        if (!isAuthenticated()) {
            throw new RuntimeException("Access denied. Please login first.");
        }
        if (!hasContributorRole()) {
            throw new RuntimeException("Access denied. Your role does not have permission to create.");
        }
        movie.setUsername(getAuthenticatedUsername());

        LocalDateTime now = LocalDateTime.now();

        movie.setCreateTime(now);
        movie.setUpdateTime(now);

        movieRepository.insert(movie);
    }

    public void updateMovie(Movie movie) {
        if (!isAuthenticated()) {
            throw new RuntimeException("Access denied. Please login first.");
        }
        if (!hasContributorRole()) {
            throw new RuntimeException("Access denied. Your role does not have permission to create.");
        }
        if (!movie.getUsername().equalsIgnoreCase(getAuthenticatedUsername())) {
            throw new RuntimeException("Access denied. You are allowed to edit another users data");
        }

        movie.setUpdateTime(LocalDateTime.now());

        movieRepository.update(movie);
    }

    public void deleteMovie(Movie movie) {
        if (!isAuthenticated()) {
            throw new RuntimeException("Access denied. Please login first.");
        }
        if (!hasAdministratorRole()) {
            throw new RuntimeException("Access denied. Your role does have delete permission");
        }
        movieRepository.delete(movie);
    }

    public Optional<Movie> findMovieById(Long movieId) {
        if (!isAuthenticated()) {
            throw new RuntimeException("Access denied. Please login first.");
        }
        Optional<Movie> maybeMovie = movieRepository.findOptionalById(movieId);
//        if (!maybeMovie.orElseThrow().getUsername().equalsIgnoreCase(getAuthenticatedUsername())) {
//            throw new RuntimeException("Access denied. You are not allowed to access data from another user.");
//        }
        if (!hasRequiredRoles()) {
            throw new RuntimeException("Access denied. Your role does not have read permission");
        }

        return maybeMovie;
    }

    public List<Movie> getAllMovies() {
        if (!isAuthenticated()) {
            throw new RuntimeException("Access denied. Please login first.");
        }
        if (!hasRequiredRoles()) {
            throw new RuntimeException("Access denied. You role does not have permission to read.");
        }
        if (hasContributorRole()) {
            return movieRepository.findAllByUsername(getAuthenticatedUsername());
        }
        return movieRepository.findAll();
    }

    public long countMovies() {
        return movieRepository.count();
    }
}
