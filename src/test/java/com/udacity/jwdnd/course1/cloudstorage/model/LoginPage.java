package com.udacity.jwdnd.course1.cloudstorage.model;

import com.udacity.jwdnd.course1.cloudstorage.AbstractSeleniumTest;
import com.udacity.jwdnd.course1.cloudstorage.misc.ElementSpec;
import com.udacity.jwdnd.course1.cloudstorage.misc.ResourceStore;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoginPage extends AbstractCredentialPage<LoginPage> {

    public static final String SIGNUP_LINK_ID = "signup-link";
    public static final String LOGOUT_MSG_ID = "logout-msg";

    @FindBy(id = SIGNUP_LINK_ID)
    protected WebElement signupLink;

    @FindBy(id = LOGOUT_MSG_ID)
    protected WebElement logoutMsg;

    @FindBy(id = ERROR_MSG_ID)
    protected WebElement errorMsg;

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public static LoginPage getInstance(WebDriver driver) {
        return new LoginPage(driver);
    }

    @Override
    public void testElements() {
        super.testElements();

        String logIn = ResourceStore.getBundle().getString("logIn");
        assertEquals(logIn, Objects.requireNonNull(webDriverWeakReference.get()).getTitle());
        assertEquals(logIn, heading.getText());
        assertEquals(logIn, submitButton.getText());

        assertEquals(ResourceStore.getBundle().getString("signUpHere"), signupLink.getText());
    }

    public static String getPageDisplayedId() {
        return SIGNUP_LINK_ID;
    }

    public static List<ElementSpec> getDisplaySuccessChecks(int timeout) {
        return setListTimeouts(
                List.of(
                    // check for sign in button
                    ElementSpec.ofResource(SUBMIT_BUTTON_ID, "logIn", AbstractSeleniumTest.TextCheck.EQUALS),
                    // check for signup button
                    ElementSpec.ofResource(SIGNUP_LINK_ID, "signUpHere", AbstractSeleniumTest.TextCheck.EQUALS)),
                timeout);
    }

    public static List<ElementSpec> getLoggedOutSuccessChecks(int timeout) {
        return setListTimeouts(
                concatLists(getDisplaySuccessChecks(timeout), List.of(
                    // check for logged out message
                    ElementSpec.ofResource(LOGOUT_MSG_ID, "loggedOut", AbstractSeleniumTest.TextCheck.EQUALS))),
                timeout);
    }

    public void login(String username, String password) {
        submitCredentials(username, password);
    }

    public void login(User user) {
        login(user.getUsername(), user.getPassword());
    }
}
