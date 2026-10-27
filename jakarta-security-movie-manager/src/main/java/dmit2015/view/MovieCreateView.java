package dmit2015.view;

import dmit2015.entity.Movie;
import dmit2015.service.MovieService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import org.omnifaces.util.Messages;

@Named("currentMovieCreateView")
@RequestScoped
public class MovieCreateView {

    @Inject
    private MovieService movieService;

    @Getter
    private Movie newMovie = new Movie();

    public String onCreateNew() {
        String nextPage = "";
        try {
            movieService.addMovie(newMovie);
            Messages.addFlashGlobalInfo("Create was successful. {0}", newMovie.getId());
            nextPage = "index?faces-redirect=true";
        } catch (RuntimeException ex) {
            Messages.addGlobalWarn(ex.getMessage());
        } catch (Exception e) {
            Messages.addGlobalError("Create was not successful. {0}", e.getMessage());
        }
        return nextPage;
    }

}