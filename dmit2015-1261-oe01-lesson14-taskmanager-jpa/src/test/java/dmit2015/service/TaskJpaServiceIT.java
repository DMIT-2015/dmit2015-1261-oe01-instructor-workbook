package dmit2015.service;

import dmit2015.model.Task;
import dmit2015.model.TaskPriority;
import jakarta.annotation.Resource;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.transaction.NotSupportedException;
import jakarta.transaction.SystemException;
import jakarta.transaction.UserTransaction;
import net.datafaker.Faker;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional(TransactionMode.ROLLBACK)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ArquillianTest
public class TaskJpaServiceIT { // The class must be declared as public

    static Faker faker = new Faker();

    static String mavenArtifactIdId;

    @Deployment
    public static WebArchive createDeployment() throws IOException, XmlPullParserException {
        PomEquippedResolveStage pomFile = Maven.resolver().loadPomFromFile("pom.xml");
        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model = reader.read(new FileReader("pom.xml"));
        mavenArtifactIdId = model.getArtifactId();
        final String archiveName = model.getArtifactId() + ".war";
        return ShrinkWrap.create(WebArchive.class, archiveName)
                .addAsLibraries(pomFile.resolve("org.codehaus.plexus:plexus-utils:3.4.2").withTransitivity().asFile())
                .addAsLibraries(pomFile.resolve("org.hamcrest:hamcrest").withTransitivity().asFile())
                .addAsLibraries(pomFile.resolve("org.assertj:assertj-core").withTransitivity().asFile())
                .addAsLibraries(pomFile.resolve("net.datafaker:datafaker").withTransitivity().asFile())
                .addAsLibraries(pomFile.resolve("com.h2database:h2").withTransitivity().asFile())
                .addAsLibraries(pomFile.resolve("com.microsoft.sqlserver:mssql-jdbc").withTransitivity().asFile())
                .addAsLibraries(pomFile.resolve("com.oracle.database.jdbc:ojdbc17").withTransitivity().asFile())
                .addAsLibraries(pomFile.resolve("org.postgresql:postgresql").withTransitivity().asFile())
//                .addAsLibraries(pomFile.resolve("com.mysql:mysql-connector-j").withTransitivity().asFile())
//                .addAsLibraries(pomFile.resolve("org.mariadb.jdbc:mariadb-java-client").withTransitivity().asFile())
                // .addAsLibraries(pomFile.resolve("org.hibernate.orm:hibernate-spatial").withTransitivity().asFile())
                // .addAsLibraries(pomFile.resolve("org.eclipse:yasson").withTransitivity().asFile())
                .addPackages(true,
                        "dmit2015.config",
                        "dmit2015.model",
                        "dmit2015.service"
                )
                .addAsResource("META-INF/persistence.xml")
                // .addAsResource(new File("src/test/resources/META-INF/persistence-entity.xml"),"META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Inject
    @Named("jakartaPersistenceTaskService")
    private TaskService taskService;

    @Test
    @Order(1)
    void shouldInjectTaskJpaService() {
        assertThat(taskService).isNotNull();
    }

    @Order(2)
    @Test
    void givenNewTask_whenAddTask_thenTaskIsAdded() {
        // Arrange
        Task newTask = Task.of(faker);

        // Act
        taskService.createTask(newTask);

        // Assert
//        assertThat(newTask.getId())
//                .isNotNull();
        Task createdTask = taskService.getTaskById(newTask.getId()).orElseThrow();
        assertThat(createdTask).isNotNull();


    }

    @Order(3)
    @Test
    void givenExistingId_whenFindById_thenReturnEntity() {
        // Arrange
        Task newTask = Task.of(faker);

        // Act
        newTask = taskService.createTask(newTask);

        // Assert
        Optional<Task> optionalTask = taskService.getTaskById(newTask.getId());
        assertThat(optionalTask.isPresent())
                .isTrue();
        // Assert
        var existingTask = optionalTask.orElseThrow();
        assertThat(existingTask)
                .usingRecursiveComparison()
                // .ignoringFields("field1", "field2")
                .isEqualTo(newTask);

    }

    @Transactional(TransactionMode.DISABLED)
    @Order(4)
    @Test
    void givenExistingEntity_whenUpdatedTask_thenTaskIsUpdated() {
        // Arrange
        Task newTask = Task.of(faker);

        newTask = taskService.createTask(newTask);
        newTask.setDescription("Test update method");
        newTask.setDone(!newTask.isDone());
        newTask.setPriority(TaskPriority.High);

        // Act
        Task updatedTask = taskService.updateTask(newTask);

        // Assert
        Optional<Task> optionalTask = taskService.getTaskById(updatedTask.getId());
        assertThat(optionalTask.isPresent())
                .isTrue();
        var existingTask = optionalTask.orElseThrow();
        assertThat(existingTask)
                .usingRecursiveComparison()
                 .ignoringFields("updateTime")
                .isEqualTo(updatedTask);

    }

    @Order(5)
    @Test
    void givenExistingId_whenDeleteTask_thenTaskIsDeleted() {
        // Arrange
        Task newTask = Task.of(faker);
        newTask = taskService.createTask(newTask);
        // Act
        taskService.deleteTaskById(newTask.getId());
        // Assert
        Optional<Task> optionalTask = taskService.getTaskById(newTask.getId());
        assertThat(optionalTask.isPresent())
                .isFalse();

    }

    @Order(6)
    @ParameterizedTest
    @CsvSource({"10"})
    void givenMultipleEntity_whenFindAll_thenReturnEntityList(int expectedRecordCount) {
        // Arrange: Set up the initial state

        // Delete all existing data
        assertThat(taskService).isNotNull();
        taskService.deleteAllTasks();
        // Generate expectedRecordCount number of fake data
        Task firstExpectedTask = null;
        Task lastExpectedTask = null;
        for (int counter = 1; counter <= expectedRecordCount; counter++) {
            Task currentTask = Task.of(faker);
            if (counter == 1) {
                firstExpectedTask = currentTask;
            } else if (counter == expectedRecordCount) {
                lastExpectedTask = currentTask;
            }

            taskService.createTask(currentTask);
        }

        // Act: Perform the action to be tested
        List<Task> taskList = taskService.getAllTasks();

        // Assert: Verify the expected outcome
        assertThat(taskList.size())
                .isEqualTo(expectedRecordCount);

        // Get the first entity and compare with expected results
        var firstActualTask = taskList.getFirst();
        assertThat(firstActualTask)
                .usingRecursiveComparison()
                .ignoringFields("createTime", "updateTime")
                .isEqualTo(firstExpectedTask);
        // Get the last entity and compare with expected results
        var lastActualTask = taskList.getLast();
        assertThat(lastActualTask)
                .usingRecursiveComparison()
                 .ignoringFields("createTime", "updateTime")
                .isEqualTo(lastExpectedTask);

    }


}