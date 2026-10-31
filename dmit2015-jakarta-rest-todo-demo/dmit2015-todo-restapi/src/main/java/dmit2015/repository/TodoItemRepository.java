package dmit2015.repository;

import dmit2015.entity.TodoItem;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

/**
 * This Jakarta Persistence class contains methods for performing CRUD operations on a
 * Jakarta Persistence managed entity.
 */
@ApplicationScoped
public class TodoItemRepository {

    // Assign a unitName if there are more than one persistence unit defined in persistence.xml
    @PersistenceContext //(unitName="pu-name-in-persistence.xml")
    private EntityManager entityManager;

    @Transactional
    public void add(@Valid TodoItem newTodoItem) {
        // If the primary key is not an identity column then write code below here to
        // 1) Generate a new primary key value
        // 2) Set the primary key value for the new entity

        entityManager.persist(newTodoItem);
    }

    public Optional<TodoItem> findById(Long todoItemId) {
        try {
            TodoItem querySingleResult = entityManager.find(TodoItem.class, todoItemId);
            if (querySingleResult != null) {
                return Optional.of(querySingleResult);
            }
        } catch (Exception ex) {
            // todoItemId value not found
            throw new RuntimeException(ex);
        }
        return Optional.empty();
    }

    public List<TodoItem> findAll() {
        return entityManager.createQuery("SELECT o FROM TodoItem o ", TodoItem.class)
                .getResultList();
    }

    @Transactional
    public TodoItem update(@Valid TodoItem updatedTodoItem) {
       Optional<TodoItem> optionalTodoItem = findById(updatedTodoItem.getId());
        if (optionalTodoItem.isEmpty()) {
            String errorMessage = String.format("The id %s does not exists in the system.", updatedTodoItem.getId());
            throw new RuntimeException(errorMessage);
        } else {
            var existingTodoItem = optionalTodoItem.orElseThrow();
            // Update only properties that is editable by the end user
            existingTodoItem.setTask(updatedTodoItem.getTask());
            existingTodoItem.setDone(updatedTodoItem.isDone());

            updatedTodoItem = entityManager.merge(existingTodoItem);
        }
        return updatedTodoItem;
    }

    @Transactional
    public void delete(TodoItem existingTodoItem) {
        // Write code to throw a RuntimeException if this entity contains child records

        if (entityManager.contains(existingTodoItem)) {
            entityManager.remove(existingTodoItem);
        } else {
            entityManager.remove(entityManager.merge(existingTodoItem));
        }
    }

    @Transactional
    public void deleteById(Long todoItemId) {
        Optional<TodoItem> optionalTodoItem = findById(todoItemId);
        if (optionalTodoItem.isPresent()) {
            TodoItem existingTodoItem = optionalTodoItem.orElseThrow();
            // Write code to throw a RuntimeException if this entity contains child records

            entityManager.remove(existingTodoItem);
        }
    }

    public long count() {
        return entityManager.createQuery("SELECT COUNT(o) FROM TodoItem o", Long.class).getSingleResult();
    }

    @Transactional
    public void deleteAll() {
        entityManager.flush();
        entityManager.clear();
        entityManager.createQuery("DELETE FROM TodoItem").executeUpdate();
    }

}