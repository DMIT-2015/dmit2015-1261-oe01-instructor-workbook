package dmit2015.repository;

import dmit2015.config.ApplicationConfig;
import dmit2015.entity.Region;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@Transactional(TransactionMode.ROLLBACK)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ArquillianTest
public class RegionJpaServiceIT { // The class must be declared as public

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
//                .addAsLibraries(pomFile.resolve("com.h2database:h2").withTransitivity().asFile())
//                .addAsLibraries(pomFile.resolve("com.microsoft.sqlserver:mssql-jdbc").withTransitivity().asFile())
                .addAsLibraries(pomFile.resolve("com.oracle.database.jdbc:ojdbc17").withTransitivity().asFile())
//                .addAsLibraries(pomFile.resolve("org.postgresql:postgresql").withTransitivity().asFile())
//                .addAsLibraries(pomFile.resolve("com.mysql:mysql-connector-j").withTransitivity().asFile())
//                .addAsLibraries(pomFile.resolve("org.mariadb.jdbc:mariadb-java-client").withTransitivity().asFile())
                // .addAsLibraries(pomFile.resolve("org.hibernate.orm:hibernate-spatial").withTransitivity().asFile())
                // .addAsLibraries(pomFile.resolve("org.eclipse:yasson").withTransitivity().asFile())
                .addPackages(true,
                        "dmit2015.config",
                        "dmit2015.entity",
                        "dmit2015.repository"
                )
                .addAsResource("META-INF/persistence.xml")
                // .addAsResource(new File("src/test/resources/META-INF/persistence-entity.xml"),"META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Inject
    private HumanResourcesRepository hrRepository;

    @Test
    @Order(1)
    void shouldInjectRegionJpaService() {
        assertThat(hrRepository).isNotNull();
    }

    @Order(2)
    @Test
    void givenNewRegion_whenAddRegion_thenRegionIsAdded() {
        // Arrange
        final int expectedRegionCount = 5;

        // Act
        var regions = hrRepository.findAllRegions();

        // Assert
        assertThat(regions.size()).isEqualTo(expectedRegionCount);

    }

    @Order(3)
    @Test
    void givenExistingId_whenFindById_thenReturnEntity() {
        // Arrange
        final Long existingRegionId = 20L;
        final String expectedRegionName = "Americas";
        // Act
        var existingRegion = hrRepository.findRegionById(existingRegionId);

        // Assert
        assertThat(existingRegion).isNotNull();
        assertThat(existingRegion.getRegionName()).isEqualTo(expectedRegionName);


    }
//
//    @Order(4)
//    @Test
//    void givenExistingEntity_whenUpdatedRegion_thenRegionIsUpdated() {
//        // Arrange
//        Region newRegion = Region.of(faker);
//
//        newRegion = regionService.createRegion(newRegion);
//        // TODO: change the values of all properties
//        //newRegion.setProperty1(faker.providerName().methodName());
//        //newRegion.setProperty2(faker.providerName().methodName());
//        //newRegion.setProperty3(faker.providerName().methodName());
//
//        // Act
//        Region updatedRegion = regionService.updateRegion(newRegion);
//
//        // Assert
//        Optional<Region> optionalRegion = regionService.getRegionById(updatedRegion.getId());
//        assertThat(optionalRegion.isPresent())
//                .isTrue();
//        var existingRegion = optionalRegion.orElseThrow();
//        assertThat(existingRegion)
//                .usingRecursiveComparison()
//                // .ignoringFields("field1", "field2")
//                .isEqualTo(newRegion);
//
//    }
//
//    @Order(5)
//    @Test
//    void givenExistingId_whenDeleteRegion_thenRegionIsDeleted() {
//        // Arrange
//        Region newRegion = Region.of(faker);
//        newRegion = regionService.createRegion(newRegion);
//        // Act
//        regionService.deleteRegionById(newRegion.getId());
//        // Assert
//        Optional<Region> optionalRegion = regionService.getRegionById(newRegion.getId());
//        assertThat(optionalRegion.isPresent())
//                .isFalse();
//
//    }
//
//    @Order(6)
//    @ParameterizedTest
//    @CsvSource({"10"})
//    void givenMultipleEntity_whenFindAll_thenReturnEntityList(int expectedRecordCount) {
//        // Arrange: Set up the initial state
//
//        // Delete all existing data
//        assertThat(regionService).isNotNull();
//        regionService.deleteAllRegions();
//        // Generate expectedRecordCount number of fake data
//        Region firstExpectedRegion = null;
//        Region lastExpectedRegion = null;
//        for (int counter = 1; counter <= expectedRecordCount; counter++) {
//            Region currentRegion = Region.of(faker);
//            if (counter == 1) {
//                firstExpectedRegion = currentRegion;
//            } else if (counter == expectedRecordCount) {
//                lastExpectedRegion = currentRegion;
//            }
//
//            regionService.createRegion(currentRegion);
//        }
//
//        // Act: Perform the action to be tested
//        List<Region> regionList = regionService.getAllRegions();
//
//        // Assert: Verify the expected outcome
//        assertThat(regionList.size())
//                .isEqualTo(expectedRecordCount);
//
//        // Get the first entity and compare with expected results
//        var firstActualRegion = regionList.getFirst();
//        assertThat(firstActualRegion)
//                .usingRecursiveComparison()
//                // .ignoringFields("field1", "field2")
//                .isEqualTo(firstExpectedRegion);
//        // Get the last entity and compare with expected results
//        var lastActualRegion = regionList.getLast();
//        assertThat(lastActualRegion)
//                .usingRecursiveComparison()
//                // .ignoringFields("field1", "field2")
//                .isEqualTo(lastExpectedRegion);
//
//    }
//
//    @Order(7)
//    @ParameterizedTest
//    // TODO Change the value below
//    @CsvSource(value = {
//            "Invalid Property1Value, Property2Value, Property3Value, ExpectedExceptionMessage",
//            "Property1Value, Invalid Property2Value, Property3Value, ExpectedExceptionMessage",
//    }, nullValues = {"null"})
//    void givenEntityWithValidationErrors_whenAddRegion_thenThrowException(
//            String property1,
//            String property2,
//            String property3,
//            String expectedExceptionMessage
//    ) {
//        // Arrange
//        Region newRegion = new Region();
//        // TODO uncomment below and set each property of Region using parameter values
//        // newRegion.setProperty1(property1);
//        // newRegion.setProperty2(property2);
//        // newRegion.setProperty3(property3);
//
//        // Act + Assert using AssertJ to verify the exception message contains the expected text
//        assertThatThrownBy(() -> regionService.createRegion(newRegion))
//                .hasStackTraceContaining(expectedExceptionMessage);
//
//    }

}