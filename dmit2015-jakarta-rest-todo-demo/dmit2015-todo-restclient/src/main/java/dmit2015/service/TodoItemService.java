package dmit2015.service;

import dmit2015.model.TodoItem;

import java.util.List;
import java.util.Optional;

public interface TodoItemService {

    TodoItem createTodoItem(TodoItem todoItem);

    Optional<TodoItem> getTodoItemById(Long id);

    List<TodoItem> getAllTodoItems();

    TodoItem updateTodoItem(TodoItem todoItem);

    void deleteTodoItemById(Long id);
}