package dmit2015.restclient;

import dmit2015.model.TodoItem;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;
import java.util.Optional;

@RequestScoped
@RegisterProvider(BadRequestResponseMapper.class)
@RegisterRestClient
public interface TodoItemMpRestClient {

    @POST
    TodoItem create(TodoItem newTodoItem);

    @GET
    List<TodoItem> findAll();

    @GET
    @Path("/{id}")
    Optional<TodoItem> findById(@PathParam("id") Long id);

    @PUT
    @Path("/{id}")
    TodoItem update(@PathParam("id") Long id, TodoItem updatedTodoItem);

    @DELETE
    @Path("/{id}")
    void delete(@PathParam("id") Long id);

}
