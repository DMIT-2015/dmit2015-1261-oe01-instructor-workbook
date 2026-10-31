package common.filter;

import jakarta.annotation.Priority;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;

/**
 * To inform the browser which origins are allowed and what request headers the client can pass to the server.
 *
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS">Cross Origin Resource Sharing (CORS)</a>
 *
 */
@Provider
@Priority(100_000)
public class CorsProcessingFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        if (requestContext.getMethod().equalsIgnoreCase("OPTIONS")) {
            responseContext.setStatus(200);
        }

        if (responseContext.getHeaderString("Access-Control-Allow-Origin") == null) {
            MultivaluedMap<String, Object> headers = responseContext.getHeaders();
            headers.add("Access-Control-Allow-Origin", "*");
            headers.add("Access-Control-Allow-Credentials", "true");
            headers.add("Access-Control-Allow-Headers", "Origin, Content-Type, Accept, Authorization");
            headers.add("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS, HEAD");
        }
    }
}