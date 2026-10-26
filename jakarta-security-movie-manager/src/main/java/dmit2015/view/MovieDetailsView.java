package dmit2015.view;

import dmit2015.entity.Movie;
import dmit2015.service.MovieService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.annotation.ManagedProperty;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.omnifaces.util.Faces;

import java.io.Serial;
import java.io.Serializable;
import java.util.Optional;

@Named("currentMovieDetailsView")
@ViewScoped
public class MovieDetailsView implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Inject
    private MovieService movieService;

    @Inject
    @ManagedProperty("#{param.editId}")
    @Getter
    @Setter
    private Long editId;

    @Getter
    private Movie existingMovie;

    @PostConstruct
    public void init() {
        Optional<Movie> maybeMovie = movieService.findMovieById(editId);
        if (maybeMovie.isPresent()) {
            existingMovie = maybeMovie.orElseThrow();
        } else {
            Faces.redirect(Faces.getRequestURI().substring(0, Faces.getRequestURI().lastIndexOf("/")) + "/index.xhtml");
        }
    }
}