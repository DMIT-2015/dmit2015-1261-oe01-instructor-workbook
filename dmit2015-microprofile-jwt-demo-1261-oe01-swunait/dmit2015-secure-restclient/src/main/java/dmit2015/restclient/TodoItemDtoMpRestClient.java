package dmit2015.restclient;

import dmit2015.model.TodoItemDto;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;
import java.util.Optional;

/**
 * The baseUri for the web MpRestClient be set in either microprofile-config.properties (recommended)
 * or in this file using @RegisterRestClient(baseUri = "http://server/path").
 * <p>
 * To set the baseUri in microprofile-config.properties:
 * 1) Open src/main/resources/META-INF/microprofile-config.properties
 * 2) Add a key/value pair in the following format:
 * package-name.ClassName/mp-rest/url=baseUri
 * For example:
 * package-name:    dmit2015.restclient
 * ClassName:       TodoItemDtoMpRestClient
 * baseUri:         http://localhost:8080/contextName
 * The key/value pair you need to add is:
 * <code>
 * dmit2015.restclient.TodoItemDtoMpRestClient/mp-rest/url=http://localhost:8080/contextName
 * </code>
 * <p>
 * To use the client interface from an environment does support CDI, add @Inject and @RestClient before the field declaration such as:
 * <code>
 *
 * @Inject
 * @RestClient private TodoItemDtoMpRestClient _todoitemdtoMpRestClient;
 * </code>
 * <p>
 * To use the client interface from an environment that does not support CDI, you can use the RestClientBuilder class to programmatically build an instance as follows:
 * <code>
 * URI apiURI = new URI("http://sever/contextName");
 * TodoItemDtoMpRestClient _todoitemdtoMpRestClient = RestClientBuilder.newBuilder().baseUri(apiURi).build(TodoItemDtoMpRestClient.class);
 * </code>
 */
@RequestScoped
@RegisterProvider(TodoItemRestApiResponseMapper.class)
@RegisterRestClient  // baseUri is defined in `src/resources/META-INF/microprofile-config.properties
public interface TodoItemDtoMpRestClient {

    @POST
    Response create(TodoItemDto newTodoItemDto);

    @GET
    List<TodoItemDto> findAll();

    @GET
    @Path("/{id}")
    Optional<TodoItemDto> findById(@PathParam("id") Long id);

    @PUT
    @Path("/{id}")
    TodoItemDto update(@PathParam("id") Long id, TodoItemDto updatedTodoItemDto);

    @DELETE
    @Path("/{id}")
    void delete(@PathParam("id") Long id);

}