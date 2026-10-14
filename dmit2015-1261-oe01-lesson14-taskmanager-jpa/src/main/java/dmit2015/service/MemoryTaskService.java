package dmit2015.service;

import dmit2015.model.Task;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import net.datafaker.Faker;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Named("memoryTaskService")
@ApplicationScoped
public class MemoryTaskService implements TaskService {

    private final List<Task> tasks = new CopyOnWriteArrayList<>();

    @PostConstruct
    public void init() {

        var faker = new Faker();
        for (int counter = 1; counter <= 5; counter++) {
            var currentTask = Task.of(faker);
            tasks.add(currentTask);
        }

    }

    @Override
    public Task createTask(Task task) {
        Objects.requireNonNull(task, "Task to create must not be null");

        // Assign a fresh id on create to ensure uniqueness (ignore any incoming id)
        Task stored = Task.copyOf(task);
        stored.setId(UUID.randomUUID().toString());
        tasks.add(stored);

        // Return a defensive copy
        return Task.copyOf(stored);
    }

    @Override
    public Optional<Task> getTaskById(String id) {
        Objects.requireNonNull(id, "id must not be null");

        return tasks.stream()
                .filter(currentTask -> currentTask.getId().equals(id))
                .findFirst()
                .map(Task::copyOf); // return a copy to avoid external mutation

    }

    @Override
    public List<Task> getAllTasks() {
        // Unmodifiable snapshot of copies
        return tasks.stream().map(Task::copyOf).toList();
    }

    @Override
    public Task updateTask(Task task) {
        Objects.requireNonNull(task, "Task to update must not be null");
        Objects.requireNonNull(task.getId(), "Task id must not be null");

        // Find index of existing task by id
        int index = -1;
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId().equals(task.getId())) {
                index = i;
                break;
            }
        }
        if (index < 0) {
            throw new NoSuchElementException("Could not find Task with id: " + task.getId());
        }

        // Replace stored item with a copy (preserve id)
        Task stored = Task.copyOf(task);
        tasks.set(index, stored);

        return Task.copyOf(stored);
    }

    @Override
    public void deleteTaskById(String id) {
        Objects.requireNonNull(id, "id must not be null");

        boolean removed = tasks.removeIf(currentTask -> id.equals(currentTask.getId()));
        if (!removed) {
            throw new NoSuchElementException("Could not find Task with id: " + id);
        }
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }
}