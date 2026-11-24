package dmit2015.restclient;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;
import org.omnifaces.util.Messages;

import java.util.HashMap;
import java.util.Map;

/**
 * This custom Microprofile REST Client ResponseExceptionMapper can be used to add custom Faces error messages
 * when a POST request returns http status 400 (Bad Request) by convert the JSON object to a collection
 * of error messages for each property in the JSON object.
 *
 * You can register this provider class in a MicroProfile REST Client interface by applying the `@RegisterProvider`
 * annotation.
 * {@snippet :
 * @RegisterProvider(TodoItemRestApiResponseMapper.class)
 * @RegisterRestClient
 * public interface TodoItemMpRestClient { }
 * }
 */
public class TodoItemRestApiResponseMapper implements ResponseExceptionMapper<WebApplicationException> {
    @Override
    public WebApplicationException toThrowable(Response response) {
        String responseBodyJson = response.readEntity(String.class);
        Map<String, String> responseBodyMap;
        try (Jsonb jsonb = JsonbBuilder.create()) {
            responseBodyMap = jsonb.fromJson(responseBodyJson, new HashMap<String, String>() {}.getClass().getGenericSuperclass());
            responseBodyMap.forEach((key, value) -> Messages.create(value).error().add());
        } catch (Exception ignored) {

        }
        return null;
    }

    @Override
    public boolean handles(int status, MultivaluedMap<String, Object> headers) {
        return status == 400;
    }
}