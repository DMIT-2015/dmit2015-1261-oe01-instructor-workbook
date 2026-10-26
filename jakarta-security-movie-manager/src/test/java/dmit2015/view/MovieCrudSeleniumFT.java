package dmit2015.view;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MovieCrudSeleniumFT {

    private static final String BASE_URL = "http://localhost:8080/movies";
    private static final Duration SHORT_WAIT = Duration.ofSeconds(3);
    private static final Duration MEDIUM_WAIT = Duration.ofSeconds(10);

    private WebDriver driver;

    @BeforeAll
    void setUpSuite() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("ignore-certificate-errors");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
    }

    @AfterAll
    void tearDownSuite() {
        if (driver != null) {
            driver.quit();
        }
    }

    @AfterEach
    void afterEachTestMethod() throws InterruptedException {
        Thread.sleep(1000);
    }

    // Helper methods

    private void open(String relativePath, String expectedTitle) {
        driver.get(BASE_URL + relativePath);
        assertEquals(expectedTitle, driver.getTitle());
    }

    private WebElement findById(String id) {
        return driver.findElement(By.id(id));
    }

    private void setInputValue(String id, String value) throws InterruptedException {
        WebElement element = findById(id);
        element.clear();
        element.sendKeys(value);
        Thread.sleep(500);
    }

    private void setDateInputValue(String id, String value) {
        WebElement element = findById(id);
        element.sendKeys(Keys.ESCAPE);
        element.sendKeys(Keys.ESCAPE);
        element.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        element.sendKeys(Keys.BACK_SPACE);
        element.sendKeys(value);
        element.sendKeys(Keys.TAB);
    }

    private WebElement waitForVisible(By locator, Duration timeout) {
        return new WebDriverWait(driver, timeout)
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    private String waitForFacesInfoMessage(Duration timeout) {
        By infoSummary = By.className("ui-messages-info-summary");
        return waitForVisible(infoSummary, timeout).getText();
    }

    private List<WebElement> tableRows() {
        return driver.findElements(By.xpath("//table[@role='grid']/tbody/tr"));
    }

    private int lastRowIndex() {
        int rowCount = tableRows().size();
        assertTrue(rowCount > 0, "Expected at least 1 row in the table.");
        return rowCount - 1;
    }

    private String cellText(int oneBasedRow, int oneBasedCol) {
        String xpath = String.format("//table[@role='grid']/tbody/tr[%d]/td[%d]", oneBasedRow, oneBasedCol);
        return driver.findElement(By.xpath(xpath)).getText();
    }

    private void clickRowActionLink(String linkIdContains, int zeroBasedRowIndex) {
        List<WebElement> links = driver.findElements(By.xpath("//a[contains(@id,'" + linkIdContains + "')]"));
        links.get(zeroBasedRowIndex).click();
    }

    // Test to run

    @Order(1)
    @ParameterizedTest(name = "createMovie_validInput_persistsAndShowsSuccessMessage")
    @CsvSource({
            "Java 25 Release Party,Action,G,9.14,2025-09-16"
    })
    void createMovie_validInput_persistsAndShowsSuccessMessage(
            String title, String genre, String rating, String price, String releaseDate) throws InterruptedException {

        open("/create.xhtml", "Create Movie");

        setInputValue("title", title);
        setInputValue("genre", genre);
        setInputValue("rating", rating);
        setInputValue("price", price);
        setDateInputValue("releaseDate_input", releaseDate);

        findById("createButton").click();

        // Wait for 3 seconds and verify navigate has been redirected to the listing page
        var wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("ui-messages-info-summary")));
        assertEquals("Movie - List", driver.getTitle());
        String message = waitForFacesInfoMessage(SHORT_WAIT);
        assertThat(message, Matchers.containsString("Create was successful."));
    }

    @Order(2)
    @ParameterizedTest(name = "listMovies_afterCreate_showsNewMovieInLastRow")
    @CsvSource({
            "Java 25 Release Party,Action,G,$9.14,'Sep 16, 2025'"
    })
    void listMovies_afterCreate_showsNewMovieInLastRow(
            String expectedTitle,
            String expectedGenre,
            String expectedRating,
            String expectedPrice,
            String expectedReleaseDate) {

        open("/index.xhtml", "Movie - List");

        int lastRow = tableRows().size(); // 1-based row number for XPath cell lookup
        assertAll("last row values",
                () -> assertEquals(expectedTitle, cellText(lastRow, 1)),
                () -> assertEquals(expectedGenre, cellText(lastRow, 2)),
                () -> assertEquals(expectedRating, cellText(lastRow, 3)),
                () -> assertEquals(expectedPrice, cellText(lastRow, 4)),
                () -> assertEquals(expectedReleaseDate, cellText(lastRow, 5))
        );

        // Sanity-check navigation links on the first row (kept as in your original, but cleaner)
        clickRowActionLink("editLink", 0);
        assertEquals("Edit Movie", driver.getTitle());
        driver.navigate().back();

        clickRowActionLink("detailsLink", 0);
        assertEquals("Movie Details", driver.getTitle());
        driver.navigate().back();

        clickRowActionLink("deleteLink", 0);
        assertEquals("Delete Movie", driver.getTitle());
        driver.navigate().back();
    }

    @Order(3)
    @ParameterizedTest(name = "details_lastRow_showsExpectedMovieValues")
    @CsvSource({
            "Java 25 Release Party,Action,G,9.14,'2025-09-16'"
    })
    void details_lastRow_showsExpectedMovieValues(
            String expectedTitle,
            String expectedGenre,
            String expectedRating,
            String expectedPrice,
            String expectedReleaseDate) {

        open("/index.xhtml", "Movie - List");

        int rowIndex = lastRowIndex();
        clickRowActionLink("detailsLink", rowIndex);
        assertEquals("Movie Details", driver.getTitle());

        assertAll("details page values",
                () -> assertEquals(expectedTitle, findById("title").getText()),
                () -> assertEquals(expectedGenre, findById("genre").getText()),
                () -> assertEquals(expectedRating, findById("rating").getText()),
                () -> assertEquals(expectedPrice, findById("price").getText()),
                () -> assertEquals(expectedReleaseDate, findById("releaseDate").getText())
        );
    }

    @Order(4)
    @ParameterizedTest(name = "edit_lastRow_updatesAndShowsSuccessMessage")
    @CsvSource({
            "Java 27 Release Party,Action,G,9.20,'Sep 15, 2026'"
    })
    void edit_lastRow_updatesAndShowsSuccessMessage(
            String updatedTitle,
            String updatedGenre,
            String updatedRating,
            String updatedPrice,
            String updatedReleaseDate) throws InterruptedException {

        open("/index.xhtml", "Movie - List");

        int rowIndex = lastRowIndex();
        clickRowActionLink("editLink", rowIndex);
        assertEquals("Edit Movie", driver.getTitle());

        setInputValue("title", updatedTitle);
        setInputValue("genre", updatedGenre);
        setInputValue("rating", updatedRating);
        setInputValue("price", updatedPrice);
        setDateInputValue("releaseDate_input", updatedReleaseDate);

        findById("updateButton").click();

        // Wait for 3 seconds and verify navigate has been redirected to the listing page
        var wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("ui-messages-info-summary")));
        assertEquals("Movie - List", driver.getTitle());
        String message = waitForFacesInfoMessage(MEDIUM_WAIT);
        assertThat(message, Matchers.containsString("Update was successful."));
    }

    @Order(5)
    @Test
    void delete_lastRow_removesMovieAndShowsSuccessMessage() {
        open("/index.xhtml", "Movie - List");

        int rowIndex = lastRowIndex();
        clickRowActionLink("deleteLink", rowIndex);
        assertEquals("Delete Movie", driver.getTitle());

        findById("deleteButton").click();

        // PrimeFaces confirm dialog
        By yesButton = By.className("ui-confirmdialog-yes");
        waitForVisible(yesButton, SHORT_WAIT).click();

        // Wait for 3 seconds and verify navigate has been redirected to the listing page
        var wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("ui-messages-info-summary")));
        assertEquals("Movie - List", driver.getTitle());
        String message = waitForFacesInfoMessage(SHORT_WAIT);
        assertThat(message, Matchers.containsString("Delete was successful."));
    }
}