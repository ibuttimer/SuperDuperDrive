package com.udacity.jwdnd.course1.cloudstorage;

import com.udacity.jwdnd.course1.cloudstorage.misc.ElementSpec;
import com.udacity.jwdnd.course1.cloudstorage.model.HomePage;
import com.udacity.jwdnd.course1.cloudstorage.model.LoginPage;
import com.udacity.jwdnd.course1.cloudstorage.model.SignupPage;
import com.udacity.jwdnd.course1.cloudstorage.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

import static com.udacity.jwdnd.course1.cloudstorage.HomeTest.waitForHomeAndTestSuccess;
import static com.udacity.jwdnd.course1.cloudstorage.SignupTest.getSignupPage;
import static com.udacity.jwdnd.course1.cloudstorage.SignupTest.signupUserAndTestSuccess;
import static com.udacity.jwdnd.course1.cloudstorage.config.UrlFactory.LOGIN_URL;
import static com.udacity.jwdnd.course1.cloudstorage.model.AbstractPage.ERROR_MSG_ID;
import static com.udacity.jwdnd.course1.cloudstorage.model.SignupPage.LOGIN_LINK_ID;

public abstract class LoginTest extends AbstractSeleniumTest {

    private static final String FIRSTNAME = "loginUserFirst";
    private static final String LASTNAME = "loginUserLast";
    private static final String USERNAME = "loginuser";
    private static final String PASSWORD = "login";

    public static User LOGIN_USER_0;
    public static User LOGIN_USER_1;

    private static final int NUM_USERS = 2;

    static {
        for (int i = 0; i < NUM_USERS; i++) {
            User user = User.of(USERNAME+i, null, PASSWORD+i, FIRSTNAME+i, LASTNAME+i);
            switch (i) {
                case 0: LOGIN_USER_0 = user;    break;
                case 1: LOGIN_USER_1 = user;    break;
            }
        }
    }

    private LoginPage loginPage;

    public static LoginPage getLoginPage(WebDriver driver, AbstractSeleniumTest test) {
        driver.get(test.getUrl(LOGIN_URL));
        return new LoginPage(driver);
    }

    public static Map<String, WebElement> loginUserAndTest(LoginPage loginPage, AbstractSeleniumTest test,
                                                           List<ElementSpec> elementSpecs,
                                                           User user) {
        loginPage.login(user);
        return testElements(test, elementSpecs);
    }

    public static Map<String, WebElement> loginUserAndTestSuccess(LoginPage loginPage, AbstractSeleniumTest test,
                                                                  User user) {
        return loginUserAndTest(loginPage, test, HomePage.getDisplaySuccessChecks(test.defaultWaitTimeout), user);
    }

    public static Map<String, WebElement> loginUserAndTestSuccess(AbstractSeleniumTest test, User user) {
        LoginPage loginPage = getLoginPage(driver, test);
        loginPage.testElements();

        return loginUserAndTest(loginPage, test, HomePage.getDisplaySuccessChecks(test.defaultWaitTimeout), user);
    }

    public static Map<String, WebElement> loginUserAndTestFailure(LoginPage loginPage, AbstractSeleniumTest test,
                                                                  User user) {
        return loginUserAndTest(loginPage, test,
                LoginPage.setListTimeouts(
                        List.of(
                            ElementSpec.ofResource(ERROR_MSG_ID, "invalidCredentials", TextCheck.EQUALS)),
                        test.defaultWaitTimeout),  // check error message
                user
        );
    }

    public static Map<String, WebElement> waitForLoginAndTestSuccess(AbstractSeleniumTest test) {
        test.waitForElementByIdAndTest(LoginPage.getPageDisplayedId(), test.defaultWaitTimeout, true);
        return testElements(test, LoginPage.getDisplaySuccessChecks(test.defaultWaitTimeout));
    }

    public static Map<String, WebElement> waitForLoginAndTestLogoutSuccess(AbstractSeleniumTest test) {
        test.waitForElementByIdAndTest(LoginPage.getPageDisplayedId(), test.defaultWaitTimeout, true);
        return testElements(test, LoginPage.getLoggedOutSuccessChecks(test.defaultWaitTimeout));
    }

    @BeforeEach
    public void beforeEach() {
        loginPage = getLoginPage(driver, this);
    }

    @DisplayName("Login non-existent user fail/existent user success")
    @Test
    public void testLogin() throws InterruptedException {

        loginPage.testElements();

        // login non-existent user and check failure
        loginUserAndTestFailure(loginPage, this, LOGIN_USER_0);

        // signup standard user
        Map<String, WebElement> elements = signupUserAndTestSuccess(getSignupPage(driver, this), this, LOGIN_USER_0);

        loginUserAndTestSuccess(loginPage, this, LOGIN_USER_0);

        pause();
    }

    @DisplayName("Login/out")
    @Test
    public void testLoginOut() throws InterruptedException {

        // signup standard user
        SignupPage signupPage = getSignupPage(driver, this);
        Map<String, WebElement> elements = signupUserAndTestSuccess(signupPage, this, LOGIN_USER_1);

        loginUserAndTestSuccess(loginPage, this, LOGIN_USER_1);

        // make sure home is displayed
        waitForHomeAndTestSuccess(this);

        // logout
        HomePage homePage = new HomePage(driver);
        homePage.logout();

        // make sure login is displayed
        waitForLoginAndTestLogoutSuccess(this);

        LoginPage.getInstance(driver).testElements();

        pause();
    }

}
