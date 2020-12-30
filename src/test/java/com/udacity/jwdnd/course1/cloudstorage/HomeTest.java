package com.udacity.jwdnd.course1.cloudstorage;

import org.junit.jupiter.api.*;

import java.net.URI;
import java.util.List;

import static com.udacity.jwdnd.course1.cloudstorage.LoginTest.*;
import static com.udacity.jwdnd.course1.cloudstorage.config.UrlFactory.*;
import static org.junit.jupiter.api.Assertions.*;

public abstract class HomeTest extends AbstractHomeTest {

    @BeforeEach
    public void beforeEach() throws InterruptedException {
        clearUsers();

        SignupTest.signupUsersAndTestSuccess(this, List.of(LOGIN_USER_0, LOGIN_USER_1));
    }

    @AfterEach
    public void afterEach() {
        clearUsers();
    }

    @DisplayName("Login/logout")
    @Test
    public void testHome() throws InterruptedException {

        loginUser(LOGIN_USER_0);

        waitForHomeAndTestSuccess(this);

        logoutUser();

        waitForLoginAndTestLogoutSuccess(this);

        pause();
    }

    @DisplayName("Erroneous url")
    @Test
    public void testUrls() throws InterruptedException {

        URI invalid = getUri("/invalid", "");

        getLinkAndCheckLoginRedirect(invalid);

        loginUser(LOGIN_USER_0);

        driver.get(invalid.toString());

        getLinkAndCheckError(invalid, "pageNotFound");

        logoutUser();

        pause();
    }

    @DisplayName("Logged out access limited")
    @Test
    public void testLoggedOutAccess() throws InterruptedException {

        for (String url : List.of(
                LOGOUT_URL, HOME_URL, ERROR_URL, GET_CREDENTIALS_PASSWORD_URL, UPLOAD_URL, DOWNLOAD_URL)) {

            gotoExternalUrl();

            URI uri = getUri(getUrl(url), "");

            getLinkAndCheckLoginRedirect(uri);
        }

        pause();
    }

    @DisplayName("Logged in home accessible, Logged out home inaccessible")
    @Test
    public void testHomeAccess() throws InterruptedException {

        loginUser(LOGIN_USER_0);

        waitForHomeAndTestSuccess(this);

        logoutUser();

        waitForLoginAndTestLogoutSuccess(this);

        gotoExternalUrl();

        URI uri = getUri(getUrl(HOME_URL), "");

        getLinkAndCheckLoginRedirect(uri);

        pause();
    }

    private void gotoExternalUrl() throws InterruptedException {

        final String externalUrl = "https://www.google.com/";
        URI uri = getExternalUri(externalUrl, "");
        driver.get(uri.toString());
        pause("defaultLoadTimeout");

        assertEquals(driver.getCurrentUrl(), externalUrl);
    }
}
