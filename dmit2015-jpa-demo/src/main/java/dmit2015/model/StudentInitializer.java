package dmit2015.model;

import dmit2015.model.Student;
import dmit2015.service.StudentJpaService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import net.datafaker.Faker;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

@ApplicationScoped
public class StudentInitializer {
    private final Logger logger = Logger.getLogger(StudentInitializer.class.getName());

    @Inject
    private StudentJpaService studentJpaService;


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
        logger.info("Initializing students");

        if (studentJpaService.getAllStudents().isEmpty()) {


            try {
                var faker = new Faker();
              for (int count = 1; count <= 22; count++) {
                  Student currentStudent = Student.of(faker);
                  studentJpaService.createStudent(currentStudent);
              }


            } catch (Exception ex) {
                logger.warning(ex.getMessage());
            }

            logger.info("Created " + studentJpaService.getAllStudents().size() + " records.");
        }
    }
}