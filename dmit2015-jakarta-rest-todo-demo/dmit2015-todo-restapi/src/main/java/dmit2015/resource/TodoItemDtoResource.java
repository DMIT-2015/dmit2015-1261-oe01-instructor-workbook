package dmit2015.resource;

import common.validation.JavaBeanValidator;
import dmit2015.entity.TodoItem;
import dmit2015.dto.TodoItemDto;
import dmit2015.mapper.TodoItemMapper;
import dmit2015.repository.TodoItemRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.OptimisticLockException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import java.net.URI;
import java.util.stream.Collectors;

/**
 * This Jakarta RESTful Web Services root resource class provides common REST API endpoints to
 * perform CRUD operations on the DTO (Data Transfer Object) for a Jakarta Persistence entity.
 */
@ApplicationScoped
@Path("todos")                // All methods in this class are associated this URL path
@Consumes(MediaType.APPLICATION_JSON)
// All methods in this class expects method parameters to contain data in JSON format
@Produces(MediaType.APPLICATION_JSON)    // All methods in this class returns data in JSON format
public class TodoItemDtoResource {

    @Inject
    private TodoItemRepository todoItemRepository;

    @GET    // This method only accepts HTTP GET requests.
    public Response findAllTodoItemsTodoItems() {
        return Response.ok(
                todoItemRepository
                        .findAll()
                        .stream()
                        .map(TodoItemMapper.INSTANCE::toDto)
                        .collect(Collectors.toList())
        ).build();
    }

    @Path("{id}")
    @GET    // This method only accepts HTTP GET requests.
    public Response findTodoItemByIdTodoItemById(@PathParam("id") Long id) {
        TodoItem existingTodoItem = todoItemRepository.findById(id).orElseThrow(NotFoundException::new);

        TodoItemDto dto = TodoItemMapper.INSTANCE.toDto(existingTodoItem);

        return Response.ok(dto).build();
    }

    @POST    // This method only accepts HTTP POST requests.
    public Response createTodoItemTodoItem(TodoItemDto dto, @Context UriInfo uriInfo) {
        TodoItem newTodoItem = TodoItemMapper.INSTANCE.toEntity(dto);

        String errorMessage = JavaBeanValidator.validateBean(newTodoItem);
        if (errorMessage != null) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(errorMessage)
                    .build();
        }

        try {
            // Persist the new TodoItem into the database
            todoItemRepository.add(newTodoItem);
        } catch (Exception ex) {
            // Return a HTTP status of "500 Internal Server Error" containing the exception message
            return Response.
                    serverError()
                    .entity(ex.getMessage())
                    .build();
        }

        // uriInfo is injected via @Context parameter to this method
        URI location = UriBuilder
                .fromPath(uriInfo.getPath())
                .path("{id}")
                .build(newTodoItem.getId());

        // Set the location path of the new entity with its identifier
        // Returns an HTTP status of "201 Created" if the TodoItem was created.
        return Response
                .created(location)
                .build();
    }

    @PUT            // This method only accepts HTTP PUT requests.
    @Path("{id}")    // This method accepts a path parameter and gives it a name of id
    public Response updateTodoItemTodoItem(@PathParam("id") Long id, TodoItemDto dto) {
        if (!id.equals(dto.getId())) {
            throw new BadRequestException();
        }

        TodoItem existingTodoItem = todoItemRepository
                .findById(id)
                .orElseThrow(NotFoundException::new);

        TodoItem updatedTodoItem = TodoItemMapper.INSTANCE.toEntity(dto);

        String errorMessage = JavaBeanValidator.validateBean(updatedTodoItem);
        if (errorMessage != null) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(errorMessage)
                    .build();
        }

        existingTodoItem.setVersion(updatedTodoItem.getVersion());
        existingTodoItem.setTask(updatedTodoItem.getTask());
        existingTodoItem.setDone(updatedTodoItem.isDone());

        try {
            todoItemRepository.update(existingTodoItem);
        } catch (OptimisticLockException ex) {
            return Response
                    .status(Response.Status.CONFLICT)
                    .entity("The data you are trying to update has changed since your last read request.")
                    .build();
        } catch (Exception ex) {
            // Return an HTTP status of "500 Internal Server Error" containing the exception message
            return Response.
                    serverError()
                    .entity(ex.getMessage())
                    .build();
        }

        // Returns an HTTP status "200 OK" and include in the body of the response the object that was updated
        TodoItemDto updatedDto = TodoItemMapper.INSTANCE.toDto(existingTodoItem);
        return Response.ok(updatedDto).build();
    }

    @DELETE            // This method only accepts HTTP DELETE requests.
    @Path("{id}")    // This method accepts a path parameter and gives it a name of id
    public Response deleteTodoItemTodoItem(@PathParam("id") Long id) {

        TodoItem existingTodoItem = todoItemRepository
                .findById(id)
                .orElseThrow(NotFoundException::new);

        try {
            todoItemRepository.delete(existingTodoItem);    // Removes the TodoItem from being persisted
        } catch (Exception ex) {
            // Return a HTTP status of "500 Internal Server Error" containing the exception message
            return Response
                    .serverError()
                    .encoding(ex.getMessage())
                    .build();
        }

        // Returns an HTTP status "204 No Content" to indicate the resource was deleted
        return Response.noContent().build();

    }

}