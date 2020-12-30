package com.udacity.jwdnd.course1.cloudstorage;

import com.udacity.jwdnd.course1.cloudstorage.controllers.HomeController;
import com.udacity.jwdnd.course1.cloudstorage.controllers.misc.Action;
import com.udacity.jwdnd.course1.cloudstorage.controllers.misc.Result;
import com.udacity.jwdnd.course1.cloudstorage.model.*;
import com.udacity.jwdnd.course1.cloudstorage.services.NoteService;
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
import static com.udacity.jwdnd.course1.cloudstorage.model.NoteTab.*;
import static org.junit.jupiter.api.Assertions.*;

public abstract class NoteTest extends AbstractHomeTest {

    private static final String TITLE = "u%d_title_n%d";
    private static final String DESCRIPTION = "u%d_description_n%d";

    private static final String ADD_SUCCESS_MSG = "Note successfully added.";
    private static final String UPDATE_SUCCESS_MSG = "Note successfully updated.";
    private static final String DELETE_SUCCESS_MSG = "Note successfully deleted.";

    private static final int USER0 = 0;
    private static final int USER1 = 1;
    private static final int NUM_USERS = 2;

    private static final int NUM_NOTES = 2;

    private static final Note[][] NOTES = new Note[NUM_USERS][NUM_NOTES];
    private static final Note[][] NOTES_EDIT = new Note[NUM_USERS][NUM_NOTES];


    @Autowired
    private NoteService noteService;

    static {
        String edit = "edit";

        for (int user = 0; user < NUM_USERS; user++) {
            for (int index = 0; index < NUM_NOTES; index++) {
                Note note = Note.of(String.format(TITLE, user, index), String.format(DESCRIPTION, user, index), 0);
                NOTES[user][index] = note;
                NOTES_EDIT[user][index] = Note.of(note.getNotetitle() + edit, note.getNotedescription() + edit, note.getNoteid());
            }
        }
    }

    @BeforeAll
    public static void beforeAll() {
        AbstractSeleniumTest.beforeAll();
    }

    @BeforeEach
    public void beforeEach() throws InterruptedException {
        clearTable(noteService);
        clearUsers();

        SignupTest.signupUsersAndTestSuccess(this, List.of(LOGIN_USER_0, LOGIN_USER_1));
    }

    @AfterEach
    public void afterEach() {
        clearTable(noteService);
        clearUsers();
    }

    private NoteTab selectTab() throws InterruptedException {
        // select notes tab
        NoteTab noteTab = NoteTab.getInstance(driver);
        noteTab.selectNotesTab();

        // check tab displayed
        pause("defaultTabChangeWait");
        testElements(this, getForefrontSuccessChecks(List.of(), defaultWaitTimeout));

        return noteTab;
    }

    @DisplayName("Modal dialog quit/close")
    @Test
    public void testModal() throws InterruptedException {

        // check modal no save close/quit
        loginModalLogout(LOGIN_USER_0, NOTES[USER0][0]);
    }

    @DisplayName("Multi-user notes add/edit/delete")
    @Test
    public void testNotes() throws InterruptedException {

        // add user 0 notes & edit 1st entry
        List<Note> user0Edited = List.of(NOTES_EDIT[USER0][0], NOTES[USER0][NUM_NOTES - 1]);
        loginAddEditLogout(LOGIN_USER_0, List.of(NOTES[USER0]), user0Edited);

        // add user 1 notes & edit last entry
        List<Note> user1Edited = List.of(NOTES[USER1][0], NOTES_EDIT[USER1][NUM_NOTES - 1]);
        loginAddEditLogout(LOGIN_USER_1, List.of(NOTES[USER1]), user1Edited);

        // check user notes
        loginCheckNotesLogout(LOGIN_USER_0, user0Edited);

        // check user notes
        loginCheckNotesLogout(LOGIN_USER_1, user1Edited);

        // login user 0 and delete last user note
        List<Note> user0PostDel = loginDeleteLogout(LOGIN_USER_0, -1);

        // login user 1 and delete first user note
        List<Note> user1PostDel = loginDeleteLogout(LOGIN_USER_1, 0);

        // check user notes
        loginCheckNotesLogout(LOGIN_USER_0, user0PostDel);

        // check user notes
        loginCheckNotesLogout(LOGIN_USER_1, user1PostDel);

        pause();
    }

    @DisplayName("Direct access read/edit/delete urls")
    @Test
    public void testEditDeleteDirectAccess() throws InterruptedException {

        // add user 0 notes & edit 1st entry
        List<Note> user0Notes = loginAdd(LOGIN_USER_0, List.of(NOTES[USER0]));

        Integer testId = user0Notes.get(0).getNoteid();

        selectTab().logout();
        pause("defaultLoadTimeout");

        // user 0 access while logged out
        for (Action action : Action.values()) {
            switch (action) {
                case read:
                case update:
                case delete:
                    getLinkAndCheckLoginRedirect(getNoteActionLink(testId, action));
                    break;
                default:
                    break;
            }
        }

        // login user 1 and test access to user 0's notes
        loginCheckEntries(LOGIN_USER_1, List.of());

        // check edit/delete
        NoteTab tab = null;
        for (Action action : Action.values()) {
            switch (action) {
                case read:
                case update:
                case delete:
                    getLinkAndCheckError(getNoteActionLink(testId, action), "unauthorisedAccess");

                    tab = selectTabCheckEntries(List.of());
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
        List<Note> user0Notes = loginAdd(LOGIN_USER_0, List.of(NOTES[USER0]));

        Integer testId = user0Notes.get(0).getNoteid();

        // test invalid id
        getLinkAndCheckError(getNoteActionLink(testId * 1000, Action.read), "badRequest");

        selectTab().logout();
        pause("defaultLoadTimeout");

        // login user 1 and test access to user 0's notes
        loginCheckEntries(LOGIN_USER_1, List.of());

        // check edit/delete
        NoteTab tab = null;
        for (String action : List.of(Action.none.name(), "unknown")) {
            getLinkAndCheckError(getNoteActionLink(testId, action), "unsupportedOperation");

            tab = selectTabCheckEntries(List.of());
        }

        // logout user 1
        Objects.requireNonNull(tab).logout();

        pause();
    }

    private URI getNoteActionLink(Integer id, Action action) {
        return getNoteActionLink(id, action.name());
    }

    private URI getNoteActionLink(Integer id, String action) {
        return getUri(HOME_URL, "tab=" + HomeController.Tabs.note_tab + "&action=" + action + "&id=" + id);
    }

    private void loginModalLogout(User user, Note noSave) throws InterruptedException {

        User dbUser = getUser(user.getUsername());

        // login & check no notes
        NoteTab tab = loginCheckEntries(user, List.of());

        for (int i = 0; i < 4; i++) {
            // select notes tab
            tab = selectTab();

            tab.newEntry();

            pause("defaultModalShowWait");
            testElements(this, getModalForefrontSuccessChecks(defaultWaitTimeout));

            if (i >= 2) {
                // fill fields
                tab.setNote(noSave);
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
            int count = noteService.countByUserId(dbUser.getUserid());
            assertEquals(0, count);
        }

        // logout
        tab.logout();
        waitForLoginAndTestLogoutSuccess(this);
    }

    private void loginAddEditLogout(User user, List<Note> add, List<Note> edit) throws InterruptedException {

        // login & add notes
        List<Note> addedNotes = loginAdd(user, add);

        // select notes tab
        NoteTab noteTab = selectTab();

        // check notes
        checkEntries(add);

        for (int i = 0; i < edit.size(); i++) {
            Note change = edit.get(i);
            if (!add.get(i).equalsExBase(change)) {
                // edit note
                noteTab.selectNotesTab();

                pause("defaultTabChangeWait");
                editNote(i);

                noteTab.setAndSaveNote(change);
                pause("defaultModalSaveWait");

                Note edited = noteService.getNote(change.getNotetitle());
                assertTrue(change.equalsExBase(edited), () -> "Edited note does not equal original");

                // check result
                ResultPage resultPage = getResultPageAndCheck(driver, Result.Success, UPDATE_SUCCESS_MSG);
                resultPage.successContinue();
                pause("defaultLoadTimeout");
            }
        }

        // select notes tab
        noteTab = selectTab();

        // check notes
        checkEntries(edit);

        // logout
        noteTab.logout();
        waitForLoginAndTestLogoutSuccess(this);
    }

    private List<Note> loginAdd(User user, List<Note> add) throws InterruptedException {

        List<Note> addedNotes = Lists.newArrayList();

        // login & check no notes
        NoteTab noteTab = loginCheckEntries(user, List.of());

        for (Note note : add) {
            // select notes tab
            noteTab = selectTab();

            // add note
            noteTab.newEntry();

            pause("defaultModalShowWait");
            testElements(this, getModalForefrontSuccessChecks(defaultWaitTimeout));

            noteTab.setAndSaveNote(note);
            pause("defaultModalSaveWait");

            // check what was saved in db
            Note added = noteService.getNote(note.getNotetitle());
            assertTrue(note.equalsExBase(added), () -> "Added note does not equal original");

            addedNotes.add(added);

            // check result
            ResultPage resultPage = getResultPageAndCheck(driver, Result.Success, ADD_SUCCESS_MSG);
            resultPage.successContinue();
            pause("defaultLoadTimeout");
        }

        return addedNotes;
    }

    private NoteTab loginCheckEntries(User user, List<Note> notes) throws InterruptedException {

        // login
        LoginTest.loginUserAndTestSuccess(this, user);

        // select notes tab
        NoteTab noteTab = selectTab();

        // check notes
        checkEntries(notes);

        return noteTab;
    }

    private void loginCheckNotesLogout(User user, List<Note> notes) throws InterruptedException {

        // login & check notes
        NoteTab noteTab = loginCheckEntries(user, notes);

        // logout
        noteTab.logout();
        waitForLoginAndTestLogoutSuccess(this);
    }

    private List<Note> loginDeleteLogout(User user, int delIndex) throws InterruptedException {

        User userDb = userService.getByUsername(user.getUsername());
        assertNotNull(user);

        assert userDb != null;
        List<Note> notes = noteService.getAllForUser(userDb.getUserid());
        assertFalse(notes.isEmpty());

        // login & check no notes
        NoteTab noteTab = loginCheckEntries(user, notes);

        // remove note
        if (delIndex < 0) {
            // negative del indices are from end, i.e. -1 is last index
            delIndex = notes.size() + delIndex;
        }
        deleteNote(delIndex);

        // check result
        ResultPage resultPage = getResultPageAndCheck(driver, Result.Success, DELETE_SUCCESS_MSG);
        resultPage.successContinue();
        pause("defaultLoadTimeout");

        // check number of notes in db
        List<Note> postDelNotes = noteService.getAllForUser(userDb.getUserid());
        assertEquals(notes.size() - 1, postDelNotes.size(), () -> "Expected post delete count does not match expected");

        // check deleted note not in database
        Note delNote = notes.get(delIndex);
        boolean match = postDelNotes.stream()
                .anyMatch(n -> n.equalsExBase(delNote));
        assertFalse(match, () -> "Deleted note found in database");

        // select notes tab
        noteTab = selectTab();

        // check notes
        checkEntries(postDelNotes);

        // logout
        noteTab.logout();
        waitForLoginAndTestLogoutSuccess(this);

        return postDelNotes;
    }

    private NoteTab selectTabCheckEntries(List<Note> entries) throws InterruptedException {

        // select credentials tab
        NoteTab tab = selectTab();

        // check tab displayed with notes
        checkEntries(entries);

        return tab;
    }

    private void checkEntries(List<Note> notes) {

        // check tab displayed with notes
        testElements(this, getForefrontSuccessChecks(notes, defaultWaitTimeout));

        // check no notes beyond expected
        testElementsNotFound(this, rowElements(IntStream.range(notes.size() + 1, notes.size() + 2), defaultNotThereWaitTimeout));
    }


    private void editNote(int index) throws InterruptedException {
        WebElement button = waitForElementById(getEditNoteButtonId(index), defaultWaitTimeout);
        button.click();
        pause("defaultModalShowWait");
    }

    private void deleteNote(int index) {
        WebElement button = waitForElementById(getDeleteNoteButtonId(index), defaultWaitTimeout);
        button.click();
    }


}
