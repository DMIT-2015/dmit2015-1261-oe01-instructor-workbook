package dmit2015.service;

import dmit2015.model.FirebaseAuthSignInResponsePayload;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.Dependent;
import jakarta.json.JsonObject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

import java.io.Serializable;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

/**
 * This class uses the
 * <a href="https://docs.oracle.com/en/java/javase/21/docs/api/java.net.http/java/net/http/HttpClient.html">HttpClient</a>
 * class to invoke
 * <a href="https://firebase.google.com/docs/reference/rest/auth">Firebase Auth REST API</a>
 * endpoints.
 * <p>
 * You can add your own methods to invoke other REST API endpoints such as the endpoint to
 * <a href="https://firebase.google.com/docs/reference/rest/auth#section-create-email-password">Sign up with email / password</a>.
 */
@Dependent
public class FirebaseAuthService implements Serializable {
    private HttpClient httpClient;

    @PostConstruct
    private void init() {
        httpClient = HttpClient.newHttpClient();
    }

    /**
     * Sign in a user with an email and password by issuing an HTTP POST request to the Auth verifyPassword endpoint.
     *
     * @link <a href="https://firebase.google.com/docs/reference/rest/auth#section-sign-in-email-password">...</a>
     * @param apiKey Firebase project Web API key
     * @param payload Request Body Payload with properties for: email, password, returnSecureToken
     * @return Response Payload with properties for: idToken, email, refreshToken, expiresIn, localId
     */
    public FirebaseAuthSignInResponsePayload signIn(String apiKey, JsonObject payload) {
        try (Jsonb jsonb = JsonbBuilder.create() ) {

            // Convert the `payload` Java object to serialized JSON data String
            String requestBodyJson = jsonb.toJson(payload);
            final String apiEndPoint = String.format("%s?key=%s","https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword", apiKey);
            // Create an Http Request for sending an Http POST request to push new data
            var httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(apiEndPoint))
                    .header("Content-Type","application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson, StandardCharsets.UTF_8))
                    .build();
            // Send the Http Request
            var httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            // Get the body of the Http Response
            var responseBodyJson = httpResponse.body();
            // Convert the JSON String to a JsonObject
            var responsePayload = jsonb.fromJson(responseBodyJson, FirebaseAuthSignInResponsePayload.class);
            // Check if the Http Request response is successful
            if (httpResponse.statusCode() == 200) {
                return responsePayload;
            } else {
                /* If the email and/or password is incorrect Firebase Auth will return the following response.
{
  "error": {
    "code": 400,
    "message": "INVALID_LOGIN_CREDENTIALS",
    "errors": [
      {
        "message": "INVALID_LOGIN_CREDENTIALS",
        "domain": "global",
        "reason": "invalid"
      }
    ]
  }
}
                 */
                // Extract from the response payload the data from the json path "error/message" using Jakarta JSON Processing
                var responsePayloadError = jsonb.fromJson(responseBodyJson, JsonObject.class);
                String message = responsePayloadError.getJsonObject("error").getString("message");
                String errorMessage = String.format("Sign in error: %s", message);
                throw new RuntimeException(errorMessage);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Refresh a Firebase ID token by issuing an HTTP POST request to the securetoken.googleapis.com endpoint.
     * The response body payload property idToken contains the token we need to use for future requests
     *
     * @link <a href="https://firebase.google.com/docs/reference/rest/auth#section-refresh-token">...</a>
     *
     * @param apiKey Firebase project Web API key
     * @param payload Request Body Payload with properties for: grant_type, refresh_token
     * @return Response Payload with properties for: expires_in, token_type, refresh_token, id_token, user_id, project_id
     */
    public JsonObject refreshToken(String apiKey, JsonObject payload) {
        try (Jsonb jsonb = JsonbBuilder.create() ) {

            //  Convert the `payload` Java object to serialized JSON data String
            String requestBodyJson = jsonb.toJson(payload);
            final String apiEndPoint = String.format("%s?key=%s","https://identitytoolkit.googleapis.com/v1/token", apiKey);
            // Create an Http Request for sending an Http POST request to push new data
            var httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(apiEndPoint))
                    .header("Content-Type","application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson, StandardCharsets.UTF_8))
                    .build();
            // Send the Http Request
            var httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            // Check if the Http Request response is successful
            if (httpResponse.statusCode() == 200) {
                // Get the body of the Http Response
                var responseBodyJson = httpResponse.body();
                // Convert the JSON data String to a JsonObject object
                return jsonb.fromJson(responseBodyJson, JsonObject.class);

            } else {
                String errorMessage = String.format("Exchange a refresh token for an ID token failed with status: %s", httpResponse.statusCode());
                throw new RuntimeException(errorMessage);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}