package com.udacity.jwdnd.course1.cloudstorage;

import com.udacity.jwdnd.course1.cloudstorage.misc.ElementSpec;
import com.udacity.jwdnd.course1.cloudstorage.misc.ITestResource;
import com.udacity.jwdnd.course1.cloudstorage.misc.MessageText;
import com.udacity.jwdnd.course1.cloudstorage.misc.Utils;
import com.udacity.jwdnd.course1.cloudstorage.model.ErrorPage;
import com.udacity.jwdnd.course1.cloudstorage.model.LoginPage;
import com.udacity.jwdnd.course1.cloudstorage.model.User;
import com.udacity.jwdnd.course1.cloudstorage.services.BaseService;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.compress.utils.Lists;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static com.udacity.jwdnd.course1.cloudstorage.config.UrlFactory.urlWithQuery;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AbstractSeleniumTest implements ITestResource {

    enum Browser { CHROME, EDGE }

    @LocalServerPort
    private Integer port;

    @Autowired
    protected UserService userService;

    protected static WebDriver driver;

    protected ResourceBundle bundle;

    protected int defaultWaitTimeout;
    protected int defaultNotThereWaitTimeout;

    public AbstractSeleniumTest() {
        bundle = getResourceBundle("test");
        defaultWaitTimeout = getResourceInt("defaultWaitTimeout");
        defaultNotThereWaitTimeout = getResourceInt("defaultNotThereWaitTimeout");
    }

    @Override
    public ResourceBundle getResourceBundle() {
        return bundle;
    }

    /**
     * Load a resource file
     * @param filename - resource file name
     * @return file contents as byte array
     */
    protected byte[] loadFileBytes(String filename) {
        ClassLoader classLoader = getClass().getClassLoader();
        byte[] fileBytes = new byte[0];

        try (InputStream inputStream = classLoader.getResourceAsStream(filename)) {
            fileBytes = Objects.requireNonNull(inputStream).readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileBytes;
    }

    /**
     * Load a resource file
     * @param inputStream - file input stream
     * @return file contents as byte array
     */
    protected byte[] loadFileBytes(InputStream inputStream) {
        byte[] fileBytes = new byte[0];

        try {
            if (inputStream != null) {
                fileBytes = inputStream.readAllBytes();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileBytes;
    }

    /**
     * Load a resource file
     * @param filename - resource file name
     * @return file contents as list of strings
     */
    protected List<String> loadFileLines(String filename) {
        ClassLoader classLoader = getClass().getClassLoader();
        List<String> lines = Lists.newArrayList();

        try (InputStream inputStream = classLoader.getResourceAsStream(filename)) {
            lines = loadFileLines(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    /**
     * Load a resource file
     * @param inputStream - file input stream
     * @return file contents as list of strings
     */
    protected List<String> loadFileLines(InputStream inputStream) {
        List<String> lines = Lists.newArrayList();

        try (InputStreamReader streamReader = new InputStreamReader(Objects.requireNonNull(inputStream), StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(streamReader)) {

            lines = reader.lines().collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }


    @BeforeAll
    public static void beforeAll() {
        // no-op
    }

    /**
     * Set the browser for the test
     * @param browser - browser to use
     * @param cls - class of test
     */
    protected static void setBrowser(Browser browser, Class<? extends AbstractSeleniumTest> cls) {
        Object prefs = null;
        if (cls != null) {
            // get browser options
            try {
                prefs = Utils.executeMethod(cls, "browserOptions", List.of(Browser.class), null, List.of(browser));
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                fail("browserOptions error");
            }
        }

        switch (browser) {
            case CHROME:
                WebDriverManager.chromedriver().setup();

                ChromeOptions chromeOptions = new ChromeOptions();
                if (prefs != null) {
                    if (prefs instanceof Map<?, ?>) {
                        chromeOptions.setExperimentalOption("prefs", prefs);
                    }
                }
                driver = new ChromeDriver(chromeOptions);
                break;
            case EDGE:
                WebDriverManager.edgedriver().setup();

                EdgeOptions edgeOptions = new EdgeOptions();
                if (prefs != null) {
                    if (prefs instanceof Map<?, ?>) {
                        for (Object key : ((Map<?, ?>) prefs).keySet()) {
                            edgeOptions.setCapability((String) key, ((Map<?, ?>) prefs).get(key));
                        }
                    }
                }
                driver = new EdgeDriver(edgeOptions);
                break;
            default:
                fail(browser + " not implemented");
        }
    }

    @AfterAll
    public static void afterAll() {
        driver.quit();
    }

    protected String getUrl(String path,
                            String query,
                            String fragment) {
        URI uri = null;
        try {
            uri = new URI("http", null, "localhost", port, path, query, fragment);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            fail();
        }
        return uri.toString();
    }

    protected String getUrl(String path) {
        return getUrl(path, null, null);
    }

    protected WebElement waitForElementById(String elementId, long timeOutInSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
        return wait.until(webDriver -> webDriver.findElement(By.id(elementId)));
    }

    protected WebElement waitForElementById(String elementId, int timeOutInSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
        WebElement element = null;
        try {
            element = wait.until(webDriver -> webDriver.findElement(By.id(elementId)));
        } catch (TimeoutException e) {
            e.printStackTrace();
            fail("Timeout waiting for element with id '" + elementId + "'");
        }
        return element;
    }

    protected WebElement waitForElementNotFoundById(ElementSpec spec) {
        WebDriverWait wait = new WebDriverWait(driver, spec.getTimeout());
        WebElement element = null;
        try {
            element = wait.until(webDriver -> webDriver.findElement(By.id(spec.getId())));
            fail("Element with id '" + spec.getId() + "' found");
        } catch (TimeoutException e) {
            // no-op ignore
        }
        return element;
    }

    protected WebElement waitForElementByIdAndTest(String elementId, int timeout, boolean isDisplayed) {
        WebElement marker = waitForElementById(elementId, timeout);
        assertEquals(isDisplayed, marker.isDisplayed(), () ->
                elementId + ": expected display state [" + isDisplayed + "] does not equal actual [" + marker.isDisplayed() + "]");
        return marker;
    }

    public enum TextCheck { NONE, EQUALS, STARTS_WITH, ENDS_WITH, CONTAINS,
        NOT_EQUALS, NOT_STARTS_WITH, NOT_ENDS_WITH, NOT_CONTAINS }

    protected WebElement waitForElementByIdAndTest(String elementId, int timeout, boolean isDisplayed, String text, TextCheck textCheck) {
        WebElement marker = waitForElementByIdAndTest(elementId, timeout, isDisplayed);
        String msg = elementId + ": '" + marker.getText() +"' ";
        switch (textCheck) {
            case STARTS_WITH:
                assertTrue(marker.getText().startsWith(text), () -> msg + "does not start with '" + text + "'");
                break;
            case ENDS_WITH:
                assertTrue(marker.getText().endsWith(text), () -> msg + "does not end with '" + text + "'");
                break;
            case CONTAINS:
                assertTrue(marker.getText().contains(text), () -> msg + "does not contain '" + text + "'");
                break;
            case EQUALS:
                assertEquals(text, marker.getText(), () -> msg + "does not equal '" + text + "'");
                break;
            case NOT_STARTS_WITH:
                assertFalse(marker.getText().startsWith(text), () -> msg + "starts with '" + text + "'");
                break;
            case NOT_ENDS_WITH:
                assertFalse(marker.getText().endsWith(text), () -> msg + "ends with '" + text + "'");
                break;
            case NOT_CONTAINS:
                assertFalse(marker.getText().contains(text), () -> msg + "does contain '" + text + "'");
                break;
            case NOT_EQUALS:
                assertNotEquals(text, marker.getText(), () -> msg + "does equal '" + text + "'");
                break;
            default:
                break;
        }
        return marker;
    }

    public static Map<String, WebElement> testElements(AbstractSeleniumTest test, List<ElementSpec> elementSpecs) {
        Map<String, WebElement> elements = new HashMap<>();

        for (ElementSpec spec : elementSpecs) {
            String id = spec.getId();
            if (spec.isFound()) {
                elements.put(id, test.waitForElementByIdAndTest(id, spec.getTimeout(), spec.isDisplayed(), spec.getText(), spec.getTextCheck()));
            } else {
                test.waitForElementNotFoundById(spec);
            }
        }
        return elements;
    }

    public static void testElementsNotFound(AbstractSeleniumTest test, List<ElementSpec> elementSpecs) {
        for (ElementSpec spec : elementSpecs) {
            test.waitForElementNotFoundById(spec);
        }
    }

    /**
     * Pause test for specified time
     * @param timeout – the length of time to sleep in milliseconds
     * @throws InterruptedException – see {@link Thread()#pause(int)}
     */
    protected void pause(int timeout) throws InterruptedException {
        Thread.sleep(timeout);
    }

    /**
     * Pause test for specified time
     * @param timeout – resource key for the length of time to sleep in milliseconds
     * @throws InterruptedException – see {@link Thread()#pause(int)}
     */
    protected void pause(String timeout) throws InterruptedException {
        pause(getResourceInt(timeout));
    }

    /**
     * Pause test
     * @throws InterruptedException – see {@link Thread()#pause(int)}
     */
    protected void pause() throws InterruptedException {
        pause("defaultEoTTimeout");
    }

    protected void clearTable(BaseService<?> baseService) {
        baseService.deleteAll();
        assertTrue(baseService.getAll().isEmpty(), () -> "Table not empty for service: " + baseService.getClass().getSimpleName());
    }

    protected void clearUsers() {
        clearTable(userService);
    }

    protected User getUser(String username) {
        User user = userService.getByUsername(username);
        assertNotNull(user, ()-> "User with username '" + username + "' not found");
        return user;
    }

    protected Set<String> verifyNewWindow(String mainWindow, String errorMessage) {
        Set<String> allHandles = driver.getWindowHandles();
        assertTrue(allHandles.size() > 1);

        allHandles.stream()
                .filter(h -> !h.equals(mainWindow))
                .findFirst()
                .ifPresentOrElse(h -> driver.switchTo().window(h), () -> {
                    fail(errorMessage);
                });

        return allHandles;
    }

    protected URI getUri(String url, String query) {
        URI uri = null;
        try {
            uri = new URI(driver.getCurrentUrl())
                    .resolve(urlWithQuery(url, query));
        } catch (URISyntaxException e) {
            e.printStackTrace();
            fail("Invalid test Uri for " + url + ", " + query);
        }
        return uri;
    }

    protected void getLinkAndCheckError(URI uri, String errorResource) throws InterruptedException {
        driver.get(uri.toString());
        pause("defaultLoadTimeout");

        // check page displayed
        testElements(this, ErrorPage.getDisplayedMessageChecks(
                defaultWaitTimeout, MessageText.resourceText(errorResource)));
        ErrorPage errorPage = ErrorPage.getInstance(driver);

        errorPage.errorContinue();
        pause("defaultLoadTimeout");
    }

    protected void getLinkAndCheckLoginRedirect(URI uri) throws InterruptedException {
        driver.get(uri.toString());
        pause("defaultLoadTimeout");

        // check page displayed
        testElements(this, LoginPage.getDisplaySuccessChecks(defaultWaitTimeout));
    }


}
