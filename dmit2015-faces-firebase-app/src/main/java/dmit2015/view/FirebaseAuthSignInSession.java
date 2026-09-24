package dmit2015.view;

import dmit2015.model.FirebaseAuthSignInResponsePayload;
import dmit2015.service.FirebaseAuthService;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.omnifaces.util.Faces;
import org.omnifaces.util.Utils;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * This Jakarta Faces backing bean stores the response payload data
 * from a Firebase Auth session.
 * <p>
 * This class uses Microprofile Config to read from
 * `src/main/resources/META-INF/microprofile-config.properties`
 * a key named `firebase.web.api.key` that contains the Firebase project Web API Key.
 */
@Named("firebaseAuthSignInSession")
@SessionScoped
public class FirebaseAuthSignInSession implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Inject
    private FirebaseAuthService loginService;

    @Inject
    @ConfigProperty(name = "firebase.web.api.key")
    private String _firebaserestapiKey;

    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
    private FirebaseAuthSignInResponsePayload firebaseAuthSignInResponsePayload;

    public String checkForToken() {

        if (firebaseAuthSignInResponsePayload == null || firebaseAuthSignInResponsePayload.getIdToken() == null) {
            return "/firebaseAuthSignIn?requestURI=" + Utils.encodeURI(Faces.getRequestURI());
        } else if (firebaseAuthSignInResponsePayload.getExpiresInDateTime().isAfter(LocalDateTime.now())) {
            JsonObject requestBodyPayload = Json.createObjectBuilder()
                    .add("grant_type", "refresh_token")
                    .add("refresh_token", firebaseAuthSignInResponsePayload.getRefreshToken())
                    .build();
            JsonObject responsePayload = loginService.refreshToken(_firebaserestapiKey, requestBodyPayload);
            firebaseAuthSignInResponsePayload.setExpiresIn(responsePayload.getString("expires_in"));
            firebaseAuthSignInResponsePayload.setRefreshToken(responsePayload.getString("refresh_token"));
            firebaseAuthSignInResponsePayload.setIdToken(responsePayload.getString("id_token"));
            firebaseAuthSignInResponsePayload.setLocalId(responsePayload.getString("user_id"));
        }

        return null;
    }
}