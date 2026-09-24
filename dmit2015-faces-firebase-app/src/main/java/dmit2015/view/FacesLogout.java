package dmit2015.view;

import org.omnifaces.util.Faces;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import jakarta.servlet.ServletException;

/**
 * This Jakarta Faces backing bean is to sign out from the application.
 */
@Named("facesLogout")
@RequestScoped
public class FacesLogout {

    public void submit() throws ServletException {
        Faces.invalidateSession();
        Faces.redirect(Faces.getRequestContextPath() + "/firebaseAuthSignIn.xhtml?faces-redirect=true");
    }

}