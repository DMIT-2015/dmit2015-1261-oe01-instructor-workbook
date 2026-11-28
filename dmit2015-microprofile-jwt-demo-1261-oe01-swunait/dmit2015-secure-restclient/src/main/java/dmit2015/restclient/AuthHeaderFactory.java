package dmit2015.restclient;

import dmit2015.view.LoginSession;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MultivaluedMap;
import org.eclipse.microprofile.rest.client.ext.ClientHeadersFactory;

@ApplicationScoped
public class AuthHeaderFactory implements ClientHeadersFactory {

    @Inject
    private LoginSession loginSession;

    @Override
    public MultivaluedMap<String, String> update(
            MultivaluedMap<String, String> incomingHeaders,
            MultivaluedMap<String, String> clientOutgoingHeaders) {

        if (loginSession.getAuthorization() != null) {
            // Replaces any existing value for the "Authorization" header with a single new value.
            clientOutgoingHeaders.putSingle("Authorization", loginSession.getAuthorization());
        }
        return clientOutgoingHeaders;
    }
}