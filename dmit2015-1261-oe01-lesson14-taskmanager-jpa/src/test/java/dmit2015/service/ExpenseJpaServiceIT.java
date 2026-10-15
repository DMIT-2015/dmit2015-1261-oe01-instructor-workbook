package dmit2015.service;

import dmit2015.config.ApplicationConfig;
import dmit2015.model.Expense;
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
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.junit.jupiter.api.*;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@Transactional(TransactionMode.ROLLBACK)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ArquillianTest
public class ExpenseJpaServiceIT { // The class must be declared as public

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
    private ExpenseJpaService expenseService;

    @Test
    @Order(1)
    void shouldInjectExpenseJpaService() {
        assertThat(expenseService).isNotNull();
    }

    @Order(2)
    @Test
    void givenNewExpense_whenAddExpense_thenExpenseIsAdded() {
        // Arrange
        Expense newExpense = Expense.of(faker);

        // Act
        expenseService.createExpense(newExpense);

        // Assert
//        assertThat(newExpense.getId())
//                .isNotNull();
        Expense createdExpense = expenseService.getExpenseById(newExpense.getId()).orElseThrow();
        assertThat(createdExpense).isNotNull();

    }

    @Order(3)
    @Test
    void givenExistingId_whenFindById_thenReturnEntity() {
        // Arrange
        Expense newExpense = Expense.of(faker);

        // Act
        newExpense = expenseService.createExpense(newExpense);

        // Assert
        Optional<Expense> optionalExpense = expenseService.getExpenseById(newExpense.getId());
        assertThat(optionalExpense.isPresent())
                .isTrue();
        // Assert
        var existingExpense = optionalExpense.orElseThrow();
        assertThat(existingExpense)
                .usingRecursiveComparison()
                // .ignoringFields("field1", "field2")
                .isEqualTo(newExpense);

    }

    @Transactional(TransactionMode.DISABLED)
    @Order(4)
    @Test
    void givenExistingEntity_whenUpdatedExpense_thenExpenseIsUpdated() {
        // Arrange
        Expense newExpense = Expense.of(faker);

        newExpense = expenseService.createExpense(newExpense);

        newExpense.setDescription("Expense update method test");
        newExpense.setAmount(BigDecimal.valueOf(99.99));
        newExpense.setDate(LocalDate.now().minusDays(7));

        // Act
        Expense updatedExpense = expenseService.updateExpense(newExpense);

        // Assert
        Optional<Expense> optionalExpense = expenseService.getExpenseById(updatedExpense.getId());
        assertThat(optionalExpense.isPresent())
                .isTrue();
        var existingExpense = optionalExpense.orElseThrow();
        assertThat(existingExpense)
                .usingRecursiveComparison()
                // .ignoringFields("field1", "field2")
                .isEqualTo(newExpense);

    }

    @Order(5)
    @Test
    void givenExistingId_whenDeleteExpense_thenExpenseIsDeleted() {
        // Arrange
        Expense newExpense = Expense.of(faker);
        newExpense = expenseService.createExpense(newExpense);
        // Act
        expenseService.deleteExpenseById(newExpense.getId());
        // Assert
        Optional<Expense> optionalExpense = expenseService.getExpenseById(newExpense.getId());
        assertThat(optionalExpense.isPresent())
                .isFalse();

    }

    @Order(6)
    @ParameterizedTest
    @CsvSource({"10"})
    void givenMultipleEntity_whenFindAll_thenReturnEntityList(int expectedRecordCount) {
        // Arrange: Set up the initial state

        // Delete all existing data
        assertThat(expenseService).isNotNull();
        expenseService.deleteAllExpenses();
        // Generate expectedRecordCount number of fake data
        Expense firstExpectedExpense = null;
        Expense lastExpectedExpense = null;
        for (int counter = 1; counter <= expectedRecordCount; counter++) {
            Expense currentExpense = Expense.of(faker);
            if (counter == 1) {
                firstExpectedExpense = currentExpense;
            } else if (counter == expectedRecordCount) {
                lastExpectedExpense = currentExpense;
            }

            expenseService.createExpense(currentExpense);
        }

        // Act: Perform the action to be tested
        List<Expense> expenseList = expenseService.getAllExpenses();

        // Assert: Verify the expected outcome
        assertThat(expenseList.size())
                .isEqualTo(expectedRecordCount);

        // Get the first entity and compare with expected results
        var firstActualExpense = expenseList.getFirst();
        assertThat(firstActualExpense)
                .usingRecursiveComparison()
                // .ignoringFields("field1", "field2")
                .isEqualTo(firstExpectedExpense);
        // Get the last entity and compare with expected results
        var lastActualExpense = expenseList.getLast();
        assertThat(lastActualExpense)
                .usingRecursiveComparison()
                // .ignoringFields("field1", "field2")
                .isEqualTo(lastExpectedExpense);

    }



}