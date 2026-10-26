package dmit2015.service;

import dmit2015.entity.Movie;
import dmit2015.service.MovieService;
import jakarta.annotation.Resource;
import jakarta.inject.Inject;
import jakarta.transaction.NotSupportedException;
import jakarta.transaction.SystemException;
import jakarta.transaction.UserTransaction;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.junit.jupiter.api.*;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ArquillianTest
public class MovieServiceIT {

    @Inject
    private MovieService movieService;

    @Resource
    private UserTransaction userTransaction;

    private static final LocalDateTime NOW = LocalDateTime.now(); // optional; see notes below

    @Deployment
    static WebArchive createDeployment() {
        PomEquippedResolveStage pom = Maven.resolver().loadPomFromFile("pom.xml");
        return ShrinkWrap.create(WebArchive.class, "integration-test.war")
                .addAsLibraries(pom.resolve("org.hamcrest:hamcrest").withTransitivity().asFile())
                .addAsLibraries(pom.resolve("org.assertj:assertj-core").withTransitivity().asFile())
                .addAsLibraries(pom.resolve("com.h2database:h2").withTransitivity().asFile())
//                .addAsLibraries(pom.resolve("com.microsoft.sqlserver:mssql-jdbc").withTransitivity().asFile())
//                .addAsLibraries(pom.resolve("com.oracle.database.jdbc:ojdbc17").withTransitivity().asFile())
//                .addAsLibraries(pom.resolve("org.postgresql:postgresql").withTransitivity().asFile())
//                .addAsLibraries(pom.resolve("org.mariadb.jdbc:mariadb-java-client").withTransitivity().asFile())
                .addPackages(true,
                        "dmit2015.config",
                        "dmit2015.entity",
                        "dmit2015.repository",
                        "dmit2015.service"
                )
                .addAsResource("data/csv/movies.csv")
                .addAsResource("META-INF/persistence.xml")
                .addAsResource("META-INF/beans.xml");
    }

    @Order(1)
    @Test
    void findAll_whenSeeded_returnsFourMoviesInExpectedOrder() {
        // Act
        List<Movie> movies = movieService.getAllMovies();

        // Assert
        assertEquals(4, movies.size());

        Movie first = movies.getFirst();
        assertAll("first movie",
                () -> assertEquals("When Harry Met Sally", first.getTitle()),
                () -> assertEquals("Romantic Comedy", first.getGenre()),
                () -> assertEquals(0, BigDecimal.valueOf(7.99).compareTo(first.getPrice())),
                () -> assertEquals("G", first.getRating()),
                () -> assertEquals(LocalDate.parse("1989-02-12"), first.getReleaseDate())
        );

        Movie last = movies.getLast();
        assertAll("last movie",
                () -> assertEquals("Rio Bravo", last.getTitle()),
                () -> assertEquals("Western", last.getGenre()),
                () -> assertEquals(0, BigDecimal.valueOf(7.99).compareTo(last.getPrice())),
                () -> assertEquals("PG-13", last.getRating()),
                () -> assertEquals(LocalDate.parse("1959-04-15"), last.getReleaseDate())
        );
    }

    @Order(2)
    @Test
    void findOptionalById_whenMovieExists_returnsMovie() {
        // Arrange
        long id = 3L; // Ghostbusters 2

        // Act
        Movie movie = movieService.findMovieById(id).orElseThrow();

        // Assert
        assertAll("movie 3",
                () -> assertEquals("Ghostbusters 2", movie.getTitle()),
                () -> assertEquals("Comedy", movie.getGenre()),
                () -> assertEquals(0, BigDecimal.valueOf(9.99).compareTo(movie.getPrice())),
                () -> assertEquals("PG", movie.getRating()),
                () -> assertEquals(LocalDate.parse("1986-02-23"), movie.getReleaseDate()),
                () -> assertNotNull(movie.getCreateTime())
        );
    }

    @Order(3)
    @Test
    void add_whenValid_persistsMovieAndSetsCreateTime() throws SystemException, NotSupportedException {
        userTransaction.begin();
        try {
            // Arrange
            Movie newMovie = new Movie();
            newMovie.setGenre("Horror");
            newMovie.setPrice(BigDecimal.valueOf(19.99));
            newMovie.setRating("NC-17");
            newMovie.setTitle("The Return of the Java Master");
            newMovie.setReleaseDate(LocalDate.parse("2021-09-14"));

            // Act
            movieService.addMovie(newMovie);

            // Assert
            Movie saved = movieService.findMovieById(newMovie.getId()).orElseThrow();

            assertAll("saved movie",
                    () -> assertEquals(newMovie.getTitle(), saved.getTitle()),
                    () -> assertEquals(newMovie.getGenre(), saved.getGenre()),
                    () -> assertEquals(0, newMovie.getPrice().compareTo(saved.getPrice())),
                    () -> assertEquals(newMovie.getRating(), saved.getRating()),
                    () -> assertEquals(newMovie.getReleaseDate(), saved.getReleaseDate()),
                    () -> assertNotNull(saved.getCreateTime()),
                    () -> assertNotNull(saved.getUpdateTime())
            );

            long minutesSinceCreate = saved.getCreateTime().until(LocalDateTime.now(), ChronoUnit.MINUTES);
            assertEquals(0, minutesSinceCreate);

        } finally {
            userTransaction.rollback(); // ensure DB state is not polluted if assertion fails
        }
    }

    @Order(4)
    @Test
    void update_whenMovieExists_updatesFieldsAndSetsUpdateTime() {
        // Arrange - create a movie then update it
        Movie movie = new Movie();
        movie.setGenre("Adventure");
        movie.setPrice(BigDecimal.valueOf(29.99));
        movie.setRating("PG");
        movie.setTitle("JDK 11 Release Party");
        movie.setReleaseDate(LocalDate.parse("2023-09-19"));
        movieService.addMovie(movie);

        Movie existing = movieService.findMovieById(movie.getId()).orElseThrow();

        // Act
        existing.setGenre("Action");
        existing.setTitle("JDK 25 Release Party");
        existing.setRating("PG-13");
        existing.setPrice(BigDecimal.valueOf(19.99));
        existing.setReleaseDate(LocalDate.parse("2023-09-16"));
        movieService.updateMovie(existing);

        // Assert
        Movie updated = movieService.findMovieById(existing.getId()).orElseThrow();

        assertAll("updated movie",
                () -> assertEquals(existing.getTitle(), updated.getTitle()),
                () -> assertEquals(existing.getGenre(), updated.getGenre()),
                () -> assertEquals(0, existing.getPrice().compareTo(updated.getPrice())),
                () -> assertEquals(existing.getRating(), updated.getRating()),
                () -> assertEquals(existing.getReleaseDate(), updated.getReleaseDate()),
                () -> assertNotNull(updated.getUpdateTime())
        );

        long minutesSinceUpdate = updated.getUpdateTime().until(LocalDateTime.now(), ChronoUnit.MINUTES);
        assertEquals(0, minutesSinceUpdate);

        // Cleanup
        movieService.deleteMovie(updated);
    }

    @Order(5)
    @Test
    void delete_whenMovieExists_removesMovie() throws SystemException, NotSupportedException {
        userTransaction.begin();
        try {
            // Arrange
            long id = 3L; // Ghostbusters 2
            Movie existing = movieService.findMovieById(id).orElseThrow();

            // Act
            movieService.deleteMovie(existing);

            // Assert
            assertTrue(movieService.findMovieById(id).isEmpty());
        } finally {
            userTransaction.rollback(); // rollback so other ordered tests still see seeded data
        }
    }
}
