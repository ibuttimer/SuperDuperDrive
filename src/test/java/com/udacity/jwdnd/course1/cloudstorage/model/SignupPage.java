package com.udacity.jwdnd.course1.cloudstorage.model;

import com.udacity.jwdnd.course1.cloudstorage.misc.ElementSpec;
import com.udacity.jwdnd.course1.cloudstorage.AbstractSeleniumTest.TextCheck;
import com.udacity.jwdnd.course1.cloudstorage.misc.ResourceStore;
import org.apache.commons.lang3.tuple.Pair;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SignupPage extends AbstractCredentialPage<SignupPage> {

    public static final String SIGNUP_SUCCESS_0_ID = "success-msg-0";
    public static final String LOGIN_LINK_ID = "login-link";
    public static final String LOGIN_BACKLINK_ID = "login-backlink";
    public static final String SIGNUP_SUCCESS_2_ID = "success-msg-2";

    @FindBy(id = "inputFirstName")
    private WebElement inputFirstName;

    @FindBy(id = "inputLastName")
    private WebElement inputLastName;

    @FindBy(id = LOGIN_BACKLINK_ID)
    protected WebElement loginBacklink;

    public SignupPage(WebDriver driver) {
        super(driver);
    }

    public static SignupPage getInstance(WebDriver driver) {
        return new SignupPage(driver);
    }


    @Override
    public void testElements() {
        super.testElements();

        String signUp = ResourceStore.getBundle().getString("signUp");
        assertEquals(signUp, Objects.requireNonNull(webDriverWeakReference.get()).getTitle());
        assertEquals(signUp, heading.getText());
        assertEquals(signUp, submitButton.getText());

        assertEquals(ResourceStore.getBundle().getString("backToLogin"), loginBacklink.getText());
//        assertEquals(ResourceStore.getBundle().getString("signupError"), errorMsgSpan.getText());
    }

    public static List<ElementSpec> getDisplaySuccessChecks(int timeout) {
        return setListTimeouts(
                List.of(
                    // check for back to login button
                    ElementSpec.ofResource(LOGIN_BACKLINK_ID, "backToLogin", TextCheck.EQUALS),
                    // check for signup button
                    ElementSpec.ofResource(SUBMIT_BUTTON_ID, "signUp", TextCheck.EQUALS)),
                timeout);
    }

    public void signup(String firstName, String lastName, String username, String password) {
        setFields(Arrays.asList(
                Pair.of(inputFirstName, firstName),
                Pair.of(inputLastName, lastName)));
        submitCredentials(username, password);
    }

    public void signup(User user) {
        signup(user.getFirstname(), user.getLastname(), user.getUsername(), user.getPassword());
    }

    public void backToLogin() {
        loginBacklink.click();
    }
}
