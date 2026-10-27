package dmit2015.view;

import dmit2015.entity.Movie;
import dmit2015.service.MovieService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import org.omnifaces.util.Messages;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Named("currentMovieListView")
@ViewScoped
public class MovieListView implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Inject
    private MovieService movieService;

    @Getter
    private List<Movie> movies;

//    @PostConstruct  // After @Inject is complete
    public void init() {
        try {
            movies = movieService.getAllMovies();
        } catch (RuntimeException ex) {
            Messages.addGlobalWarn(ex.getMessage());
        } catch (Exception ex) {
            Messages.addGlobalError(ex.getMessage());
        }
    }
}