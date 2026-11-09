package dmit2015.service;

import dmit2015.model.TodoItem;
import dmit2015.restclient.TodoItemMpRestClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;
import java.util.Optional;

@Named("currentMpRestClientTodoItemService")
@ApplicationScoped
public class MpRestClientTodoItemService implements TodoItemService {

    @Inject
    @RestClient
    private TodoItemMpRestClient mpRestClient;

    @Override
    public TodoItem createTodoItem(TodoItem todoItem) {
        return mpRestClient.create(todoItem);
    }

    @Override
    public Optional<TodoItem> getTodoItemById(Long id) {
        return mpRestClient.findById(id);
    }

    @Override
    public List<TodoItem> getAllTodoItems() {
        return mpRestClient.findAll();
    }

    @Override
    public TodoItem updateTodoItem(TodoItem todoItem) {
        return mpRestClient.update(todoItem.getId(), todoItem);
    }

    @Override
    public void deleteTodoItemById(Long id) {
        mpRestClient.delete(id);
    }
}
