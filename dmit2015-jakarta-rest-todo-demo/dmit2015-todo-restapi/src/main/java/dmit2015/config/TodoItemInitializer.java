package dmit2015.config;

import dmit2015.entity.TodoItem;
import dmit2015.repository.TodoItemRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class TodoItemInitializer {
    private final Logger logger = Logger.getLogger(TodoItemInitializer.class.getName());

    @Inject
    private TodoItemRepository todoItemRepository;

    /**
     * Using the combination of `@Observes` and `@Initialized` annotations, you can
     * intercept and perform additional processing during the phase of beans or events
     * in a CDI container.
     * <p>
     * The @Observers is used to specify this method is in observer for an event
     * The @Initialized is used to specify the method should be invoked when a bean type of `ApplicationScoped` is being
     * initialized
     * <p>
     * Execute code to create the test data for the entity.
     * This is an alternative to using a @WebListener that implements a ServletContext listener.
     * <p>
     * ]    * @param event
     */
    public void initialize(@Observes @Initialized(ApplicationScoped.class) Object event) {
        logger.info("@PostConstruct init method");

        if (todoItemRepository.count() == 0) {
            try {
                TodoItem todo1 = new TodoItem();
                todo1.setTask("Create JAX-RS demo project");
                todo1.setDone(true);
                todo1.setCreateTime(LocalDateTime.now());
                todoItemRepository.add(todo1);

                TodoItem todo2 = new TodoItem();
                todo2.setTask("Run and verify all Integration Test pass");
                todo2.setDone(false);
                todo2.setCreateTime(LocalDateTime.now());
                todoItemRepository.add(todo2);

                TodoItem todo3 = new TodoItem();
                todo3.setTask("Create DTO version of TodoResource");
                todo3.setDone(false);
                todo3.setCreateTime(LocalDateTime.now());
                todoItemRepository.add(todo3);

            } catch (Exception ex) {
                logger.log(Level.SEVERE, "TodoItemInitializer intialized method failed to complete.", ex);
            }

            logger.info("Created " + todoItemRepository.count() + " records.");
        }
    }
}