package dmit2015.repository;

import dmit2015.entity.Movie;
import jakarta.data.repository.*;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository {

    @Insert
    void insert(Movie movie);

    @Query("select count(m) from Movie m")
    long count();

    @Delete
    void deleteAll();

    @Find
    List<Movie> findAll();

    @Find
    List<Movie> findAllByUsername(String username);

    @Find
    Optional<Movie> findOptionalById(long id);

    @Update
    void update(Movie existing);

    @Delete
    void delete(Movie updated);
}
