package dmit2015.config;

import dmit2015.entity.Movie;
import dmit2015.repository.MovieRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class MovieInitializer {

    private static final Logger LOGGER = Logger.getLogger(MovieInitializer.class.getName());

    private final MovieRepository movieRepository;

    @Inject
    public MovieInitializer(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Transactional
    public void initialize(@Observes @Initialized(ApplicationScoped.class) Object event) {
        LOGGER.info("Initializing movies");

        try {
            movieRepository.deleteAll();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Failed to delete existing movies during initialization.", ex);
            return;
        }

        long count;
        try {
            count = movieRepository.count();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Failed to count movies during initialization.", ex);
            return;
        }

        if (count == 0) {
            seedFromCsv("/data/csv/movies.csv");
        } else {
            LOGGER.fine(() -> "Skipping seed; repository already contains " + count + " movies.");
        }
    }

    private void seedFromCsv(String resourcePath) {
        try (InputStream in = getClass().getResourceAsStream(resourcePath)) {
            if (in == null) {
                LOGGER.warning("Seed CSV not found on classpath: " + resourcePath);
                return;
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                // Skip header row
                reader.readLine();

                String line;
                while ((line = reader.readLine()) != null) {
                    Optional<Movie> maybeMovie = Movie.parseCsv(line);
                    if (maybeMovie.isPresent()) {
                        movieRepository.insert(maybeMovie.get());
                    } else {
                        LOGGER.fine("Skipping invalid CSV row: " + line);
                    }
                }
            }

            LOGGER.info("Movie seeding complete from " + resourcePath);

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Failed to seed movies from CSV: " + resourcePath, ex);
        }
    }
}