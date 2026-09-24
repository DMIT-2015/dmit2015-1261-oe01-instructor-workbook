package dmit2015.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * This class contains the response payload for the Firebase Authentication REST API endpoint
 * to sign in with email/password.
 *
 * @link <a href="https://firebase.google.com/docs/reference/rest/auth/#section-sign-in-email-password">Sign in with email / password</a>
 */
@Data
public class FirebaseAuthSignInResponsePayload {

    private String localId;

    private String email;

    private String idToken;

    private String refreshToken;

    private String expiresIn;

    private LocalDateTime expiresInDateTime;

    public void setExpiresIn(String expiresIn) {
        this.expiresIn = expiresIn;
        int expiresInSeconds = Integer.parseInt(expiresIn);
        this.expiresInDateTime = LocalDateTime.now().plusSeconds(expiresInSeconds);
    }

}