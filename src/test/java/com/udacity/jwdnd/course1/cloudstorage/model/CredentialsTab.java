package com.udacity.jwdnd.course1.cloudstorage.model;

import com.google.common.collect.Lists;
import com.udacity.jwdnd.course1.cloudstorage.AbstractSeleniumTest.TextCheck;
import com.udacity.jwdnd.course1.cloudstorage.misc.ElementSpec;
import org.apache.commons.lang3.tuple.Pair;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.thymeleaf.util.StringUtils;

import java.util.List;
import java.util.stream.IntStream;

public class CredentialsTab extends HomePage {

    public static final String ADD_NEW_BUTTON_ID = "add_new_credentials";
    public static final String ENTRIES_TABLE_ID = "credentials_table";
    public static final String EDIT_ENTRY_BUTTON_ID = "editcredential";
    public static final String DELETE_ENTRY_BUTTON_ID = "deletecredential";
    public static final String URL_ID = "url";
    public static final String USERNAME_ID = "username";
    public static final String PASSWORD_ID = "password";

    // modal dialog ids
    public static final String MODAL_LABEL_ID = "credentialModalLabel";
    public static final String MODAL_CLOSE_ID = "credentialModalClose";
    public static final String MODAL_CREDENTIAL_ID = "credential-id";
    public static final String MODAL_CREDENTIAL_URL_ID = "credential-url";
    public static final String MODAL_CREDENTIAL_USERNAME_ID = "credential-username";
    public static final String MODAL_CREDENTIAL_PASSWORD_ID = "credential-password";
    public static final String SUBMIT_BUTTON_ID = "credentialSubmit";
    public static final String QUIT_BUTTON_ID = "credential-quit-button";
    public static final String SAVE_BUTTON_ID = "credential-save-button";


    @FindBy(id = ADD_NEW_BUTTON_ID)
    protected WebElement addNewButton;

    @FindBy(id = ENTRIES_TABLE_ID)
    protected WebElement entriesTable;

    // modal elements
    @FindBy(id = MODAL_LABEL_ID)
    protected WebElement modalLabel;

    @FindBy(id = MODAL_CLOSE_ID)
    protected WebElement closeButton;

    @FindBy(id = MODAL_CREDENTIAL_ID)
    protected WebElement idText;

    @FindBy(id = MODAL_CREDENTIAL_URL_ID)
    protected WebElement urlText;

    @FindBy(id = MODAL_CREDENTIAL_USERNAME_ID)
    protected WebElement usernameText;

    @FindBy(id = MODAL_CREDENTIAL_PASSWORD_ID)
    protected WebElement passwordText;

    @FindBy(id = SUBMIT_BUTTON_ID)
    protected WebElement noteButton;

    @FindBy(id = QUIT_BUTTON_ID)
    protected WebElement quitButton;

    @FindBy(id = SAVE_BUTTON_ID)
    protected WebElement saveButton;




    public CredentialsTab(WebDriver driver) {
        super(driver);
    }

    public static CredentialsTab getInstance(WebDriver driver) {
        return new CredentialsTab(driver);
    }

    public static String getPageDisplayedId() {
        return ADD_NEW_BUTTON_ID;
    }


    /**
     * Get tab displayed successfully tests
     * @param timeout - Test timeout
     * @return
     */
    public static List<ElementSpec> getDisplaySuccessChecks(int timeout) {
        return setListTimeouts(
                List.of(
                    // check for add note button
                    ElementSpec.ofResource(ADD_NEW_BUTTON_ID, "add_new_credentials", TextCheck.EQUALS)),
                timeout);
    }


    /**
     * Get list of tests for a row(s)
     * @param intStream - Row indices to test
     * @param timeout - Test timeout
     * @return
     */
    public static List<ElementSpec> rowElements(IntStream intStream, int timeout) {
        List<ElementSpec> list = Lists.newArrayList();
        intStream.forEach(i -> {
            list.add(ElementSpec.of(getEditCredentialsButtonId(i)));
            list.add(ElementSpec.of(getDeleteCredentialsButtonId(i)));
            list.add(ElementSpec.of(getCredentialsUrlId(i)));
            list.add(ElementSpec.of(getCredentialsUsernameId(i)));
            list.add(ElementSpec.of(getCredentialsPasswordId(i)));
        });
        return setListTimeouts(list, timeout);
    }

    public enum PasswordState {
        MODE_MATCH,     // both encrypted or both plain
        MODE_MISMATCH   // one encrypted and one plain
    }

    /**
     * Get tab and list displayed successfully tests
     * @param credentials - Entries to test
     * @param timeout - Test timeout
     * @return
     */
    public static List<ElementSpec> getForefrontSuccessChecks(List<Credentials> credentials, int timeout, PasswordState passwordState) {
        List<ElementSpec> list = Lists.newArrayList();
        list.addAll(getDisplaySuccessChecks(timeout));
        for (int i = 0; i < credentials.size(); ++i) {
            Credentials credential = credentials.get(i);
            list.add(ElementSpec.of(getEditCredentialsButtonId(i)));
            list.add(ElementSpec.of(getDeleteCredentialsButtonId(i)));
            list.add(ElementSpec.ofText(getCredentialsUrlId(i), credential.getUrl(), TextCheck.EQUALS));
            list.add(ElementSpec.ofText(getCredentialsUsernameId(i), credential.getUsername(), TextCheck.EQUALS));
            list.add(ElementSpec.ofText(getCredentialsPasswordId(i), credential.getPassword(),
                    passwordState == PasswordState.MODE_MATCH ? TextCheck.EQUALS : TextCheck.NOT_EQUALS));
        }
        return setListTimeouts(list, timeout);
    }


    /**
     * Get modal displayed successfully tests
     * @param id - database id of entry
     * @param url - url
     * @param username - username
     * @param password - password
     * @param timeout - Test timeout
     * @param passwordState - state of password in credentials
     * @return list of tests
     */
    public static List<ElementSpec> getModalForefrontSuccessChecks(String id, String url, String username, String password, int timeout, PasswordState passwordState) {
        return setListTimeouts(
                List.of(
                    ElementSpec.ofResource(MODAL_LABEL_ID, "credential", TextCheck.EQUALS),
                    ElementSpec.ofText(MODAL_CLOSE_ID, Character.toString(0xd7), TextCheck.EQUALS),    // &times;	multiplication sign
                    ElementSpec.ofText(MODAL_CREDENTIAL_ID, id, StringUtils.isEmpty(id) ? TextCheck.NONE : TextCheck.EQUALS)
                        .setDisplayed(false),
                    ElementSpec.ofText(MODAL_CREDENTIAL_URL_ID, url, StringUtils.isEmpty(url) ? TextCheck.NONE : TextCheck.EQUALS),
                    ElementSpec.ofText(MODAL_CREDENTIAL_USERNAME_ID, username, StringUtils.isEmpty(username) ? TextCheck.NONE : TextCheck.EQUALS),
                    ElementSpec.ofText(MODAL_CREDENTIAL_PASSWORD_ID, password,
                            StringUtils.isEmpty(password) ? TextCheck.NONE :
                                    (passwordState == PasswordState.MODE_MATCH ? TextCheck.EQUALS : TextCheck.NOT_EQUALS)),
                    ElementSpec.of(SUBMIT_BUTTON_ID)
                        .setDisplayed(false),
                    ElementSpec.ofResource(QUIT_BUTTON_ID, "close", TextCheck.EQUALS),
                    ElementSpec.ofResource(SAVE_BUTTON_ID, "save_changes", TextCheck.EQUALS)),
                timeout
        );
    }

    /**
     * Get empty modal displayed successfully tests
     * @param timeout - Test timeout
     * @param passwordState - state of password in credentials
     * @return list of tests
     */
    public static List<ElementSpec> getModalForefrontSuccessChecks(int timeout, PasswordState passwordState) {
        return getModalForefrontSuccessChecks(null, null, null, null, timeout, passwordState);
    }

    /**
     * Get empty modal not displayed tests
     * @param timeout - Test timeout
     * @return list of tests
     */
    public static List<ElementSpec> getModalNotDisplayedChecks(int timeout) {
        List<ElementSpec> list = getModalForefrontSuccessChecks(timeout, PasswordState.MODE_MATCH);
        for (ElementSpec spec : list) {
            spec.setDisplayed(false);
            spec.setTextCheck(TextCheck.NONE);
        }
        return list;
    }


    @Override
    public void testElements() {
        super.testElements();
    }

    public void newEntry() {
        addNewButton.click();
    }

    public void quit() {
        quitButton.click();
    }

    public void close() {
        closeButton.click();
    }

    public void setAndSaveCredentials(Credentials credentials) {
        setCredentials(credentials);
        saveButton.click();
    }

    public static String getEditCredentialsButtonId(int index) {
        return EDIT_ENTRY_BUTTON_ID + index;
    }

    public static String getDeleteCredentialsButtonId(int index) {
        return DELETE_ENTRY_BUTTON_ID + index;
    }

    public static String getCredentialsUrlId(int index) {
        return URL_ID + index;
    }

    public static String getCredentialsUsernameId(int index) {
        return USERNAME_ID + index;
    }

    public static String getCredentialsPasswordId(int index) {
        return PASSWORD_ID + index;
    }

    public void setIdText(String id) {
        idText.sendKeys(id);
    }

    public void setUrlText(String url) {
        urlText.sendKeys(url);
    }

    public void setUsernameText(String username) {
        usernameText.sendKeys(username);
    }

    public void setPasswordText(String password) {
        passwordText.sendKeys(password);
    }

    public void setCredentials(Credentials credentials) {
        setFields(List.of(
                Pair.of(urlText, credentials.getUrl()),
                Pair.of(usernameText, credentials.getUsername()),
                Pair.of(passwordText, credentials.getPassword())
            ));
    }
}
