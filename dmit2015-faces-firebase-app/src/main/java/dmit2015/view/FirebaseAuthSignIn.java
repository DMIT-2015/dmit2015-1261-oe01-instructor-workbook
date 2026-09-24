package dmit2015.view;

import dmit2015.model.FirebaseAuthSignInResponsePayload;
import dmit2015.service.FirebaseAuthService;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.omnifaces.cdi.Param;
import org.omnifaces.util.Messages;

import java.io.Serializable;

/**
 * This Jakarta Faces backing bean is for sign in to Firebase Auth
 * using a username and password then storing the response payload
 * in session scope using a injected FirebaseAuthSignInSession instance.
 * <p>
 * This class uses Microprofile Config to read from
 * `src/main/resources/META-INF/microprofile-config.properties`
 * a key named `firebase.web.api.key` that contains the Firebase project Web API Key.
 */
@Named
@ViewScoped
public class FirebaseAuthSignIn implements Serializable {

    @Inject
    private FirebaseAuthService loginService;

    @NotBlank(message = "Username value is required.")
    @Getter
    @Setter
    private String username;

    @NotBlank(message = "Password value is required.")
    @Getter
    @Setter
    private String password;

    @Inject
    private FirebaseAuthSignInSession firebaseAuthSignInSession;

    @Inject
    @ConfigProperty(name = "firebase.web.api.key")
    private String firebaserestapiKey;

    @Param
    private String requestURI;

    public String submit() {
        // https://firebase.google.com/docs/reference/rest/auth#section-sign-in-email-password
        JsonObject credentials = Json.createObjectBuilder()
                .add("email", username)
                .add("password", password)
                .add("returnSecureToken", true)
                .build();
        try {
            FirebaseAuthSignInResponsePayload loginFirebaseAuthSignInResponsePayload = loginService.signIn(firebaserestapiKey, credentials);
            firebaseAuthSignInSession.setFirebaseAuthSignInResponsePayload(loginFirebaseAuthSignInResponsePayload);
            firebaseAuthSignInSession.setUsername(username);

            if (requestURI != null && !requestURI.isBlank()) {
                return requestURI + "?faces-redirect=true";
            } else {
                return "/index?faces-redirect=true";
            }
        } catch (Exception e) {
            Messages.addGlobalError(e.getMessage().replace("java.lang.RuntimeException: ", ""));
        }

        return null;
    }

}