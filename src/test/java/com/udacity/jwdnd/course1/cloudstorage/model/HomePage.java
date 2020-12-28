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

public class HomePage extends AbstractPage {

    public static final String TITLE_RESOURCE = "home";

    public static final String LOGOUT_BUTTON_ID = "logout-button";

    public static final String FILES_TAB_ID = "nav-files-tab";
    public static final String NOTES_TAB_ID = "nav-notes-tab";
    public static final String CREDENTIALS_TAB_ID = "nav-credentials-tab";


    public static final String FILE_BUTTON_ID = "file-button";



    public static final String CREDENTIAL_BUTTON_ID = "credentialSubmit";
    public static final String CREDENTIAL_QUIT_BUTTON_ID = "credential-quit-button";
    public static final String CREDENTIAL_SAVE_BUTTON_ID = "credential-save-button";


    @FindBy(id = LOGOUT_BUTTON_ID)
    protected WebElement logoutButton;

    @FindBy(id = FILES_TAB_ID)
    protected WebElement filesTab;

    @FindBy(id = NOTES_TAB_ID)
    protected WebElement notesTab;

    @FindBy(id = CREDENTIALS_TAB_ID)
    protected WebElement credentialsTab;

    @FindBy(id = FILE_BUTTON_ID)
    protected WebElement fileButton;


    @FindBy(id = CREDENTIAL_BUTTON_ID)
    protected WebElement credentialButton;


    public HomePage(WebDriver driver) {
        super(driver);
    }

    public static HomePage getInstance(WebDriver driver) {
        return new HomePage(driver);
    }

    public static String getPageDisplayedId() {
        return LOGOUT_BUTTON_ID;
    }

    public static List<ElementSpec> getDisplaySuccessChecks(int timeout) {
        return setListTimeouts(
                List.of(
                    // check for logout button
                    ElementSpec.ofResource(LOGOUT_BUTTON_ID, "logOut", AbstractSeleniumTest.TextCheck.EQUALS),
                    // check for tabs
                    ElementSpec.ofResource(FILES_TAB_ID, "files", AbstractSeleniumTest.TextCheck.EQUALS),
                    ElementSpec.ofResource(NOTES_TAB_ID, "notes", AbstractSeleniumTest.TextCheck.EQUALS),
                    ElementSpec.ofResource(CREDENTIALS_TAB_ID, "credentials", AbstractSeleniumTest.TextCheck.EQUALS)),
                timeout
        );
    }

    public static void testTitle(WebDriver driver) {
        String title = ResourceStore.getBundle().getString(TITLE_RESOURCE);
        assertEquals(title, driver.getTitle());
    }

    @Override
    public void testElements() {
        String title = ResourceStore.getBundle().getString(TITLE_RESOURCE);
        assertEquals(title, Objects.requireNonNull(webDriverWeakReference.get()).getTitle());
    }

    public void logout() {
        logoutButton.click();
    }

    public void selectFileTab() {
        filesTab.click();
    }

    public void selectNotesTab() {
        notesTab.click();
    }

    public void selectCredentialsTab() {
        credentialsTab.click();
    }

}
