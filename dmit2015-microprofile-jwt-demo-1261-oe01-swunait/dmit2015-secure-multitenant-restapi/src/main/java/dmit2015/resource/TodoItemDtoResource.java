package dmit2015.resource;

import common.validation.JavaBeanValidator;
import dmit2015.dto.TodoItemDto;
import dmit2015.mapper.TodoItemMapper;
import dmit2015.entity.TodoItem;
import dmit2015.repository.TodoItemRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.OptimisticLockException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
// This is a CDI-managed bean that is created only once during the life cycle of the application
@Path("todos")	        // All methods of this class are associated this URL path
@Consumes(MediaType.APPLICATION_JSON)	// All methods this class accept only JSON format data
@Produces(MediaType.APPLICATION_JSON)	// All methods returns data that has been converted to JSON format
public class TodoItemDtoResource {

    @Inject
    private UriInfo uriInfo;

    @Inject
    private TodoItemRepository todoItemRepository;

    @POST   // POST: restapi/TodoItemsDto
    public Response postTodoItem(TodoItemDto dto) {
        if (dto == null) {
            throw new BadRequestException();
        }

        String errorMessage = JavaBeanValidator.validateBean(dto);
        if (errorMessage != null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(errorMessage).build();
        }

//        TodoItem newTodoItem = mapFromDto(dto);
        TodoItem newTodoItem = TodoItemMapper.INSTANCE.toEntity(dto);
        todoItemRepository.add(newTodoItem);

        URI todoItemsUri = uriInfo.getAbsolutePathBuilder().path(newTodoItem.getId().toString()).build();
        return Response.created(todoItemsUri).build();
    }

    @GET    // GET: restapi/TodoItemsDto/5
    @Path("{id}")
    public Response getTodoItem(@PathParam("id") Long id) {
        Optional<TodoItem> optionalTodoItem = todoItemRepository.findById(id);

        if (optionalTodoItem.isEmpty()) {
            throw new NotFoundException();
        }
        TodoItem existingTodoItem = optionalTodoItem.get();
//        TodoItemDto dto = mapToDto(existingTodoItem);
        TodoItemDto dto = TodoItemMapper.INSTANCE.toDto(existingTodoItem);

        return Response.ok(dto).build();
    }

    @GET    // GET: restapi/TodoItemsDto
    public Response getTodoItems() {
        return Response.ok(todoItemRepository.findAll()
                .stream()
//                .map(this::mapToDto)
                .map(TodoItemMapper.INSTANCE::toDto)
                .collect(Collectors.toList()))
                .build();
    }

    @PUT    // PUT: restapi/TodoItemsDto/5
    @Path("{id}")
    public Response updateTodoItem(@PathParam("id") Long id, TodoItemDto dto) {
        if (!id.equals(dto.getId())) {
            throw new BadRequestException();
        }

        Optional<TodoItem> optionalTodoItem = todoItemRepository.findById(id);
        if (optionalTodoItem.isEmpty()) {
            throw new NotFoundException();
        }

        String errorMessage = JavaBeanValidator.validateBean(dto);
        if (errorMessage != null) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(errorMessage).build();
        }

        TodoItem existingTodoItem = optionalTodoItem.orElseThrow();

        if (!Objects.equals(existingTodoItem.getVersion(), dto.getVersion())) {
            throw new ClientErrorException(
                    "The data you are trying to update has changed since your last read request.",
                    Response.Status.CONFLICT);
        }


        // Copy data from the updated entity to the existing entity
        existingTodoItem.setVersion(dto.getVersion());
        existingTodoItem.setTask(dto.getName());
        existingTodoItem.setDone(dto.isComplete());

        try {
            todoItemRepository.update(existingTodoItem);
        } catch (OptimisticLockException ex) {
            return Response
                    .status(Response.Status.CONFLICT)
                    .entity("The data was modified by another transaction.")
                    .build();
        } catch (Exception ex) {
            return Response
                    .serverError()
                    .entity("An unexpected error occurred while updating the Todo item.")
                    .build();
        }
//        todoItemRepository.update(mapFromDto(dto));

        return Response
                .ok(dto)
                .build();
    }

    @DELETE // DELETE: restapi/TodoItemsDto/5
    @Path("{id}")
    public Response deleteTodoItem(@PathParam("id") Long id) {
        Optional<TodoItem> optionalTodoItem = todoItemRepository.findById(id);

        if (optionalTodoItem.isEmpty()) {
            throw new NotFoundException();
        }

        todoItemRepository.deleteById(id);

        return Response.noContent().build();
    }

//    private TodoItemDto mapToDto(TodoItem todoItem) {
//        return new TodoItemDto(todoItem.getId(), todoItem.getName(), todoItem.isComplete(), todoItem.getVersion());
//    }
//
//    private TodoItem mapFromDto(TodoItemDto dto) {
//        return new TodoItem(dto.getId(), dto.getName(), dto.isComplete());
//    }

}