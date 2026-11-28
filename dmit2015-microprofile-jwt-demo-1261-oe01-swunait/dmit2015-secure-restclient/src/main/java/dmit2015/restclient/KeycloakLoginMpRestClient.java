package dmit2015.restclient;

import jakarta.enterprise.context.RequestScoped;
import jakarta.json.JsonObject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RequestScoped
@RegisterRestClient(baseUri = "http://localhost:8180/realms/dmit2015-realm/protocol/openid-connect/token")
public interface KeycloakLoginMpRestClient {
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    JsonObject authenticate(
            @FormParam("username") String username,
            @FormParam("password") String password,
            @FormParam("client_id") String jwtClient,
            @FormParam("client_secret") String jwtClientSecret,
            @FormParam("grant_type") @DefaultValue("password") String grantType
            );

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    JsonObject refreshToken(
            @FormParam("refresh_token") String refreshToken,
            @FormParam("client_id") String jwtClient,
            @FormParam("client_secret") String jwtClientSecret,
            @FormParam("grant_type") @DefaultValue("refresh_token") String grantType
    );
}