package dmit2015.service;

import dmit2015.model.Task;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Named("jakartaPersistenceTaskService")
@ApplicationScoped
public class TaskJpaService implements TaskService {

    // Assign a unitName if there are more than one persistence unit defined in persistence.xml
    @PersistenceContext (unitName="h2database-jpa-pu")
    private EntityManager entityManager;

    @Override
    @Transactional
    public Task createTask(Task task) {
        // 1) Generate a new primary key value
        String randomId = UUID.randomUUID().toString();
        // 2) Set the primary key value for the new entity
        task.setId(randomId);
        entityManager.persist(task);
        return task;
    }

    @Override
    public Optional<Task> getTaskById(String id) {
        try {
            Task querySingleResult = entityManager.find(Task.class, id);
            if (querySingleResult != null) {
                return Optional.of(querySingleResult);
            }
        } catch (Exception ex) {
            // id value not found
            throw new RuntimeException(ex);
        }
        return Optional.empty();
    }

    @Override
    public List<Task> getAllTasks() {
        return entityManager.createQuery("SELECT o FROM Task o ORDER BY o.createTime DESC", Task.class)
                .getResultList();
    }

    @Override
    @Transactional
    public Task updateTask(Task task) {

        Optional<Task> optionalTask = getTaskById(task.getId());
        if (optionalTask.isEmpty()) {
            String errorMessage = String.format("The id %s does not exists in the system.", task.getId());
            throw new RuntimeException(errorMessage);
        } else {
            var existingTask = optionalTask.orElseThrow();
            // Update only properties that is editable by the end user

//            existingTask.setDescription(task.getDescription());
//            existingTask.setPriority(task.getPriority());
//            existingTask.setDone(task.isDone());

//            task = entityManager.merge(existingTask);
        }
        return task;
    }

    @Override
    @Transactional
    public void deleteTaskById(String id) {
        Optional<Task> optionalTask = getTaskById(id);
        if (optionalTask.isPresent()) {
            Task task = optionalTask.orElseThrow();
            // Write code to throw a RuntimeException if this entity contains child records
            entityManager.remove(task);
        } else {
            throw new RuntimeException("Could not find Task with id: " + id);
        }
    }

    @Override
    public void deleteAllTasks() {
        entityManager.createQuery("DELETE FROM Task t").executeUpdate();
//        entityManager.createNativeQuery("DELETE FROM Task").executeUpdate();
    }

}