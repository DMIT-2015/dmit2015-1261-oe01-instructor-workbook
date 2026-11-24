package dmit2015.service;

import dmit2015.model.TodoItemDto;
import dmit2015.restclient.TodoItemDtoMpRestClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;
import java.util.Optional;

@Named("currentMpRestClientTodoItemDtoService")
@ApplicationScoped
public class MpRestClientTodoItemDtoService implements TodoItemDtoService{

    @Inject
    @RestClient
    private TodoItemDtoMpRestClient mpRestClient;

    @Override
    public TodoItemDto createTodoItemDto(TodoItemDto todoItemDto) {
        try (Response response = mpRestClient.create(todoItemDto)) {
            if (response.getStatus() != Response.Status.CREATED.getStatusCode()) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            } else {
                String location = response.getHeaderString("Location");
                int resourceIdIndex = location.lastIndexOf("/") + 1;
                Long resourceId = Long.parseLong(location.substring(resourceIdIndex));
                todoItemDto.setId(resourceId);
            }
        }
        return todoItemDto;
    }

    @Override
    public Optional<TodoItemDto> getTodoItemDtoById(Long id) {
        return mpRestClient.findById(id);
    }

    @Override
    public List<TodoItemDto> getAllTodoItemDtos() {
        return mpRestClient.findAll();
    }

    @Override
    public TodoItemDto updateTodoItemDto(TodoItemDto todoItemDto) {
        return mpRestClient.update(todoItemDto.getId(), todoItemDto);
    }

    @Override
    public void deleteTodoItemDtoById(Long id) {
        mpRestClient.delete(id);
    }
}