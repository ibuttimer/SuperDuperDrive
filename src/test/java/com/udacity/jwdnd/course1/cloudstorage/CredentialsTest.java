package com.udacity.jwdnd.course1.cloudstorage;

import com.udacity.jwdnd.course1.cloudstorage.controllers.HomeController;
import com.udacity.jwdnd.course1.cloudstorage.controllers.misc.Action;
import com.udacity.jwdnd.course1.cloudstorage.controllers.misc.Result;
import com.udacity.jwdnd.course1.cloudstorage.model.*;
import com.udacity.jwdnd.course1.cloudstorage.services.CredentialsService;
import org.apache.commons.compress.utils.Lists;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static com.udacity.jwdnd.course1.cloudstorage.LoginTest.*;
import static com.udacity.jwdnd.course1.cloudstorage.config.UrlFactory.HOME_URL;
import static com.udacity.jwdnd.course1.cloudstorage.model.CredentialsTab.getDeleteCredentialsButtonId;
import static com.udacity.jwdnd.course1.cloudstorage.model.CredentialsTab.*;
import static org.junit.jupiter.api.Assertions.*;

public abstract class CredentialsTest extends AbstractHomeTest {

    private static final String URL = "http://www/u%d_c%d.com/";
    private static final String USERNAME = "username_u%d_n%d";
    private static final String PASSWORD = "password_u%d_n%d";

    private static final String ADD_SUCCESS_MSG = "Credentials successfully added.";
    private static final String UPDATE_SUCCESS_MSG = "Credentials successfully updated.";
    private static final String DELETE_SUCCESS_MSG = "Credentials successfully deleted.";

    private static final int USER0 = 0;
    private static final int USER1 = 1;
    private static final int NUM_USERS = 2;

    private static final int NUM_CREDENTIALS = 2;

    private static final Credentials[][] CREDENTIALS = new Credentials[NUM_USERS][NUM_CREDENTIALS];
    private static final Credentials[][] CREDENTIALS_EDIT = new Credentials[NUM_USERS][NUM_CREDENTIALS];


    @Autowired
    private CredentialsService credentialsService;

    static {
        String edit = "edit";

        for (int user = 0; user < NUM_USERS; user++) {
            for (int index = 0; index < NUM_CREDENTIALS; index++) {
                Credentials credentials = Credentials.of(String.format(URL, user, index), String.format(USERNAME, user, index),
                        null, String.format(PASSWORD, user, index), 0);
                CREDENTIALS[user][index] = credentials;
                CREDENTIALS_EDIT[user][index] = Credentials.of(credentials.getUrl() + edit, credentials.getUsername() + edit,
                        credentials.getKey(), credentials.getPassword() + edit, credentials.getId());
            }
        }
    }

    @BeforeAll
    public static void beforeAll() {
        AbstractSeleniumTest.beforeAll();
    }

    @BeforeEach
    public void beforeEach() throws InterruptedException {
        clearTable(credentialsService);
        clearUsers();

        SignupTest.signupUsersAndTestSuccess(this, List.of(LOGIN_USER_0, LOGIN_USER_1));
    }

    @AfterEach
    public void afterEach() {
        clearTable(credentialsService);
        clearUsers();
    }

    private CredentialsTab selectTab() throws InterruptedException {
        // select credentials tab
        CredentialsTab tab = CredentialsTab.getInstance(driver);
        tab.selectCredentialsTab();

        // check tab displayed
        pause("defaultTabChangeWait");
        testElements(this, getForefrontSuccessChecks(List.of(), defaultWaitTimeout, PasswordState.MODE_MATCH));

        return tab;
    }

    @DisplayName("Modal dialog quit/close")
    @Test
    public void testModal() throws InterruptedException {

        // check modal no save close/quit
        loginModalLogout(LOGIN_USER_0, CREDENTIALS[USER0][0]);
    }

    @DisplayName("Multi-user credentials add/edit/delete")
    @Test
    public void testCredentials() throws InterruptedException {

        // check modal no save close/quit
        loginModalLogout(LOGIN_USER_0, CREDENTIALS[USER0][0]);

        // add user 0 credentials & edit 1st entry
        List<Credentials> user0Edited = List.of(CREDENTIALS_EDIT[USER0][0], CREDENTIALS[USER0][NUM_CREDENTIALS - 1]);
        loginAddEditLogout(LOGIN_USER_0, List.of(CREDENTIALS[USER0]), user0Edited);

        // add user 1 credentials & edit last entry
        List<Credentials> user1Edited = List.of(CREDENTIALS[USER1][0], CREDENTIALS_EDIT[USER1][NUM_CREDENTIALS - 1]);
        loginAddEditLogout(LOGIN_USER_1, List.of(CREDENTIALS[USER1]), user1Edited);

        // check user 0 entries
        loginCheckEntriesLogout(LOGIN_USER_0, user0Edited, PasswordState.MODE_MISMATCH);

        // check user 1 entries
        loginCheckEntriesLogout(LOGIN_USER_1, user1Edited, PasswordState.MODE_MISMATCH);

        // login user 0 and delete last user entry
        List<Credentials> user0PostDel = loginDeleteLogout(LOGIN_USER_0, -1);

        // login user 1 and delete first user entry
        List<Credentials> user1PostDel = loginDeleteLogout(LOGIN_USER_1, 0);

        // check user credentials
        loginCheckEntriesLogout(LOGIN_USER_0, user0PostDel, PasswordState.MODE_MISMATCH);

        // check user credentials
        loginCheckEntriesLogout(LOGIN_USER_1, user1PostDel, PasswordState.MODE_MISMATCH);

        pause();
    }

    @DisplayName("Direct access read/edit/delete urls")
    @Test
    public void testEditDeleteDirectAccess() throws InterruptedException {

        // add user 0 credentials
        List<Credentials> user0Credentials = loginAdd(LOGIN_USER_0, List.of(CREDENTIALS[USER0]));

        Integer testId = user0Credentials.get(0).getCredentialid();

        selectTab().logout();
        pause("defaultLoadTimeout");

        // user 0 access while logged out
        for (Action action : Action.values()) {
            switch (action) {
                case read:
                case update:
                case delete:
                    getLinkAndCheckLoginRedirect(getCredentialsActionLink(testId, action));
                    break;
                default:
                    break;
            }
        }

        // login user 1 and test access to user 0's notes
        loginCheckEntries(LOGIN_USER_1, List.of(), PasswordState.MODE_MATCH);

        // check edit/delete
        CredentialsTab tab = null;
        for (Action action : Action.values()) {
            switch (action) {
                case read:
                case update:
                case delete:
                    getLinkAndCheckError(getCredentialsActionLink(testId, action), "unauthorisedAccess");

                    tab = selectTabCheckEntries(List.of(), PasswordState.MODE_MATCH);
                    break;
                default:
                    break;
            }
        }

        // logout user 1
        Objects.requireNonNull(tab).logout();

        pause();
    }

    @DisplayName("Direct access unsupported/erroneous urls")
    @Test
    public void testUnsupportedDirectAccess() throws InterruptedException {

        // add user 0 credentials
        List<Credentials> user0Credentials = loginAdd(LOGIN_USER_0, List.of(CREDENTIALS[USER0]));

        Integer testId = user0Credentials.get(0).getCredentialid();

        // test invalid id
        getLinkAndCheckError(getCredentialsActionLink(testId * 1000, Action.read), "badRequest");

        selectTab().logout();
        pause("defaultLoadTimeout");

        // login user 1 and test access to user 0's notes
        loginCheckEntries(LOGIN_USER_1, List.of(), PasswordState.MODE_MATCH);

        // check edit/delete
        CredentialsTab tab = null;
        for (String action : List.of(Action.none.name(), "unknown")) {
            getLinkAndCheckError(getCredentialsActionLink(testId, action), "unsupportedOperation");

            tab = selectTabCheckEntries(List.of(), PasswordState.MODE_MATCH);
        }

        // logout user 1
        Objects.requireNonNull(tab).logout();

        pause();
    }

    private URI getCredentialsActionLink(Integer id, Action action) {
        return getCredentialsActionLink(id, action.name());
    }

    private URI getCredentialsActionLink(Integer id, String action) {
        return getUri(HOME_URL, "tab=" + HomeController.Tabs.credentials_tab + "&action=" + action + "&id=" + id);
    }

    private void loginModalLogout(User user, Credentials noSave) throws InterruptedException {

        User dbUser = getUser(user.getUsername());

        // login & check no credentials
        CredentialsTab tab = loginCheckEntries(user, List.of(), PasswordState.MODE_MATCH);

        for (int i = 0; i < 4; i++) {
            // select credentials tab
            tab = selectTab();

            tab.newEntry();

            pause("defaultModalShowWait");
            testElements(this, getModalForefrontSuccessChecks(defaultWaitTimeout, PasswordState.MODE_MATCH));

            if (i >= 2) {
                // fill fields
                tab.setCredentials(noSave);
            }

            // check close/quit
            if (i % 2 == 0) {
                tab.close();
            } else {
                tab.quit();
            }

            pause("defaultModalSaveWait");
            testElements(this, getModalNotDisplayedChecks(defaultWaitTimeout));

            // make sure nothing in db
            int count = credentialsService.countByUserId(dbUser.getUserid());
            assertEquals(0, count);
        }

        // logout
        tab.logout();
        waitForLoginAndTestLogoutSuccess(this);
    }

    private void loginAddEditLogout(User user, List<Credentials> add, List<Credentials> edit) throws InterruptedException {

        List<Credentials> addedCredentials = loginAdd(user, add);

        // select credentials tab & check entries
        CredentialsTab tab = selectTabCheckEntries(add, PasswordState.MODE_MISMATCH);

        for (int i = 0; i < edit.size(); i++) {
            Credentials change = edit.get(i);
            if (!add.get(i).equalsExBase(change)) {
                // edit credentials
                tab.selectCredentialsTab();

                pause("defaultTabChangeWait");
                editEntry(i);

                tab.setAndSaveCredentials(change);
                pause("defaultModalSaveWait");

                Credentials edited = credentialsService.convertCredentialsPlainText(
                        credentialsService.getByUrl(change.getUrl())
                );
                edited.setKey(change.getKey()); // not way of verifying key
                assertTrue(change.equalsExBase(edited), () -> "Edited credentials do not equal original");

                // check result
                ResultPage resultPage = getResultPageAndCheck(driver, Result.Success, UPDATE_SUCCESS_MSG);
                resultPage.successContinue();
                pause("defaultLoadTimeout");
            }
        }

        // select credentials tab & check entries
        tab = selectTabCheckEntries(edit, PasswordState.MODE_MISMATCH);

        // logout
        tab.logout();
        waitForLoginAndTestLogoutSuccess(this);
    }

    private List<Credentials> loginAdd(User user, List<Credentials> add) throws InterruptedException {

        List<Credentials> addedCredentials = Lists.newArrayList();

        // login & check no credentials
        CredentialsTab tab = loginCheckEntries(user, List.of(), PasswordState.MODE_MATCH);

        for (Credentials credentials : add) {
            // select credentials tab
            tab = selectTab();

            // add credentials
            tab.newEntry();

            pause("defaultModalShowWait");
            testElements(this, getModalForefrontSuccessChecks(defaultWaitTimeout, PasswordState.MODE_MATCH));

            tab.setAndSaveCredentials(credentials);
            pause("defaultModalSaveWait");

            // check what was saved in db
            Credentials added = credentialsService.convertCredentialsPlainText(
                    credentialsService.getByUrl(credentials.getUrl())
            );
            added.setKey(credentials.getKey()); // not way of verifying key
            assertTrue(credentials.equalsExBase(added), () -> "Added credentials do not equal original");

            addedCredentials.add(added);

            // check result
            ResultPage resultPage = getResultPageAndCheck(driver, Result.Success, ADD_SUCCESS_MSG);
            resultPage.successContinue();
            pause("defaultLoadTimeout");
        }

        return addedCredentials;
    }

    private CredentialsTab loginCheckEntries(User user, List<Credentials> entries, PasswordState passwordState) throws InterruptedException {

        // login
        LoginTest.loginUserAndTestSuccess(this, user);

        // select credentials tab & check entries
        return selectTabCheckEntries(entries, passwordState);
    }

    private void loginCheckEntriesLogout(User user, List<Credentials> entries, PasswordState passwordState) throws InterruptedException {

        // login & check notes
        CredentialsTab tab = loginCheckEntries(user, entries, passwordState);

        // logout
        tab.logout();
        waitForLoginAndTestLogoutSuccess(this);
    }

    private List<Credentials> loginDeleteLogout(User user, int delIndex) throws InterruptedException {

        User userDb = getUser(user.getUsername());

        List<Credentials> entries = credentialsService.getAllForUser(userDb.getUserid());
        assertFalse(entries.isEmpty());

        // login & check no credentials
        CredentialsTab tab = loginCheckEntries(user, entries, PasswordState.MODE_MATCH);

        // remove note
        if (delIndex < 0) {
            // negative del indices are from end, i.e. -1 is last index
            delIndex = entries.size() + delIndex;
        }
        deleteEntry(delIndex);

        // check result
        ResultPage resultPage = getResultPageAndCheck(driver, Result.Success, DELETE_SUCCESS_MSG);
        resultPage.successContinue();
        pause("defaultLoadTimeout");

        // check number of credentials in db
        List<Credentials> postDelEntries = credentialsService.getAllForUser(userDb.getUserid());
        for (Credentials entry : postDelEntries) {
            credentialsService.convertCredentialsPlainText(entry);
        }
        assertEquals(entries.size() - 1, postDelEntries.size(), () -> "Expected post delete count does not match expected");

        // check deleted credentials not in database
        Credentials delEntry = entries.get(delIndex);
        boolean match = postDelEntries.stream()
                .anyMatch(n -> n.equalsExBase(delEntry));
        assertFalse(match, () -> "Deleted credentials found in database");

        // select credentials tab & check entries
        tab = selectTabCheckEntries(postDelEntries, PasswordState.MODE_MISMATCH);

        // logout
        tab.logout();
        waitForLoginAndTestLogoutSuccess(this);

        return postDelEntries;
    }

    private CredentialsTab selectTabCheckEntries(List<Credentials> entries, PasswordState passwordState) throws InterruptedException {

        // select credentials tab
        CredentialsTab tab = selectTab();

        // check tab displayed with credentials
        testElements(this, getForefrontSuccessChecks(entries, defaultWaitTimeout, passwordState));

        // check no credentials beyond expected
        testElementsNotFound(this, rowElements(IntStream.range(entries.size() + 1, entries.size() + 2), defaultNotThereWaitTimeout));

        return tab;
    }

    private void checkEntries(List<Credentials> entries, PasswordState passwordState) {

        // check tab displayed with credentials
        testElements(this, getForefrontSuccessChecks(entries, defaultWaitTimeout, passwordState));

        // check no credentials beyond expected
        testElementsNotFound(this, rowElements(IntStream.range(entries.size() + 1, entries.size() + 2), defaultNotThereWaitTimeout));
    }


    private void editEntry(int index) throws InterruptedException {
        WebElement button = waitForElementById(getEditCredentialsButtonId(index), defaultWaitTimeout);
        button.click();
        pause("defaultModalShowWait");
    }

    private void deleteEntry(int index) {
        WebElement button = waitForElementById(getDeleteCredentialsButtonId(index), defaultWaitTimeout);
        button.click();
    }


}
