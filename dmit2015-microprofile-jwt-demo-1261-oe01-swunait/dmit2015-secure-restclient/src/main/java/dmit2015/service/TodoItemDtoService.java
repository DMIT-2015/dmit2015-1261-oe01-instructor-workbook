package dmit2015.service;

import dmit2015.model.TodoItemDto;

import java.util.List;
import java.util.Optional;

public interface TodoItemDtoService {

    TodoItemDto createTodoItemDto(TodoItemDto todoItemDto);

    Optional<TodoItemDto> getTodoItemDtoById(Long id);

    List<TodoItemDto> getAllTodoItemDtos();

    TodoItemDto updateTodoItemDto(TodoItemDto todoItemDto);

    void deleteTodoItemDtoById(Long id);
}