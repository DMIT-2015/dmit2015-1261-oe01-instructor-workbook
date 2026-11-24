package dmit2015.repository;

import dmit2015.config.ApplicationConfig;
import dmit2015.config.TodoItemInitializer;
import dmit2015.entity.TodoItem;
import jakarta.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ArquillianTest
public class TodoItemRepositoryIT {

    @Inject
    private TodoItemRepository _todoRepository;

    static TodoItem currentTodoItem;  // the TodoItem that is currently being added, find, update, or delete

    @Deployment
    public static WebArchive createDeployment() {
        PomEquippedResolveStage pomFile = Maven.resolver().loadPomFromFile("pom.xml");

        return ShrinkWrap.create(WebArchive.class,"test.war")
//                .addAsLibraries(pomFile.resolve("groupId:artifactId:version").withTransitivity().asFile())
                .addAsLibraries(pomFile.resolve("com.h2database:h2:2.3.232").withTransitivity().asFile())
//                .addAsLibraries(pomFile.resolve("com.oracle.database.jdbc:ojdbc17:23.26.2.0.0").withTransitivity().asFile())
//                .addAsLibraries(pomFile.resolve("com.microsoft.sqlserver:mssql-jdbc:12.8.0.jre11").withTransitivity().asFile())
                .addAsLibraries(pomFile.resolve("org.assertj:assertj-core:3.27.7").withTransitivity().asFile())
                .addAsLibraries(pomFile.resolve("org.hamcrest:hamcrest:3.0").withTransitivity().asFile())
                .addClass(ApplicationConfig.class)
                .addClasses(TodoItem.class, TodoItemRepository.class, TodoItemInitializer.class)
               .addAsResource("META-INF/persistence.xml")
                // .addAsResource(new File("src/test/resources/META-INF/persistence-todoitem.xml"),"META-INF/persistence.xml")
                .addAsResource("META-INF/beans.xml");
    }

    @Order(2)
    @Test
    void shouldCreate() {
        currentTodoItem = new TodoItem();
        currentTodoItem.setTask("Create Arquillian IT");
        currentTodoItem.setDone(true);
        _todoRepository.add(currentTodoItem);

        Optional<TodoItem> optionalTodoItem = _todoRepository.findById(currentTodoItem.getId());
        assertThat(optionalTodoItem.isPresent())
                .isTrue();
        TodoItem existingTodoItem = optionalTodoItem.get();
        assertThat(currentTodoItem.getTask())
                .isEqualTo(existingTodoItem.getTask());
        assertThat(currentTodoItem.isDone())
                .isEqualTo(existingTodoItem.isDone());

    }

    @Order(3)
    @Test
    void shouldFindOne() {
        final Long todoId = currentTodoItem.getId();
        Optional<TodoItem> optionalTodoItem = _todoRepository.findById(todoId);
        assertThat(optionalTodoItem.isPresent())
                .isTrue();

        TodoItem existingTodoItem = optionalTodoItem.get();
        assertThat(existingTodoItem)
                .usingRecursiveComparison()
                .ignoringFields("createTime","updateTime")
                .isEqualTo(currentTodoItem);

    }

    @Order(1)
    @ParameterizedTest
    @CsvSource(value = {
            "Create JAX-RS demo project,true,Create DTO version of TodoResource,false"
    })
    void shouldFindAll(String firstName, boolean firstComplete, String lastName, boolean lastComplete) {
        List<TodoItem> queryResultList = _todoRepository.findAll();
        assertThat(queryResultList.size())
                .isEqualTo(3);

        TodoItem firstTodoItem = queryResultList.get(0);
        assertThat(firstTodoItem.getTask())
                .isEqualTo(firstName);
        assertThat(firstTodoItem.isDone())
                .isEqualTo(firstComplete);

        TodoItem lastTodoItem = queryResultList.get(queryResultList.size() - 1);
        assertThat(lastTodoItem.getTask())
                .isEqualTo(lastName);
        assertThat(lastTodoItem.isDone())
                .isEqualTo(lastComplete);
    }

    @Order(4)
    @Test
    void shouldUpdate() {
        currentTodoItem.setTask("Update JPA Arquillian IT");
        currentTodoItem.setDone(false);
        _todoRepository.update(currentTodoItem);

        Optional<TodoItem> optionalUpdatedTodoItem = _todoRepository.findById(currentTodoItem.getId());
        assertThat(optionalUpdatedTodoItem.isPresent())
                .isTrue();
        TodoItem updatedTodoItem = optionalUpdatedTodoItem.orElseThrow();
        assertThat(updatedTodoItem)
                .usingRecursiveComparison()
                .ignoringFields("createTime","updateTime","version")
                .isEqualTo(currentTodoItem);
        assertThat(updatedTodoItem.getUpdateTime())
                .isNotNull();
        assertThat(updatedTodoItem.getVersion())
                .isNotEqualTo(currentTodoItem.getVersion());

    }

    @Order(5)
    @Test
    void shouldDelete() {
        final Long todoId = currentTodoItem.getId();
        Optional<TodoItem> optionalTodoItem = _todoRepository.findById(todoId);
        assertThat(optionalTodoItem.isPresent())
                .isTrue();

        TodoItem existingTodoItem = optionalTodoItem.orElseThrow();
        _todoRepository.deleteById(existingTodoItem.getId());
        optionalTodoItem = _todoRepository.findById(todoId);
        assertThat(optionalTodoItem.isEmpty())
                .isTrue();
    }
}