package com.udacity.jwdnd.course1.cloudstorage.model;

import com.udacity.jwdnd.course1.cloudstorage.misc.ResourceStore;
import org.apache.commons.lang3.tuple.Pair;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class AbstractCredentialPage<T> extends AbstractPage {

    public static final String INPUT_USERNAME_ID = "inputUsername";
    public static final String INPUT_PASSWORD_ID = "inputPassword";
    public static final String SUBMIT_BUTTON_ID = "submit-button";


    @FindBy(id = INPUT_USERNAME_ID)
    protected WebElement inputUsername;

    @FindBy(id = INPUT_PASSWORD_ID)
    protected WebElement inputPassword;

    @FindBy(id = SUBMIT_BUTTON_ID)
    protected WebElement submitButton;

    public AbstractCredentialPage(WebDriver driver) {
        super(driver);
    }

    public void testElements() {
        assertEquals(ResourceStore.getBundle().getString("enterUsername"), inputUsername.getAttribute("placeholder"));
        assertEquals(ResourceStore.getBundle().getString("enterPassword"), inputPassword.getAttribute("placeholder"));
    }

    public void submitCredentials(String username, String password) {
        setFields(Arrays.asList(
                Pair.of(inputUsername, username),
                Pair.of(inputPassword, password)));
        submitButton.click();
    }
}
