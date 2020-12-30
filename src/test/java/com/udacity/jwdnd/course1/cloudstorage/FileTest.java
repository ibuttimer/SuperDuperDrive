package com.udacity.jwdnd.course1.cloudstorage;

import com.udacity.jwdnd.course1.cloudstorage.controllers.HomeController;
import com.udacity.jwdnd.course1.cloudstorage.controllers.misc.Action;
import com.udacity.jwdnd.course1.cloudstorage.controllers.misc.DownloadTarget;
import com.udacity.jwdnd.course1.cloudstorage.controllers.misc.Result;
import com.udacity.jwdnd.course1.cloudstorage.misc.MessageText;
import com.udacity.jwdnd.course1.cloudstorage.model.*;
import com.udacity.jwdnd.course1.cloudstorage.model.File;
import com.udacity.jwdnd.course1.cloudstorage.services.FileService;
import org.apache.commons.compress.utils.Lists;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.udacity.jwdnd.course1.cloudstorage.LoginTest.*;
import static com.udacity.jwdnd.course1.cloudstorage.config.UrlFactory.*;
import static com.udacity.jwdnd.course1.cloudstorage.model.FileTab.*;
import static org.junit.jupiter.api.Assertions.*;

public abstract class FileTest extends AbstractHomeTest {

    private static final String FILENAME_RESOURCE = "userFilename";
    private static final String CONTENTS_RESOURCE = "userFileContents";
    private static final String TOO_BIG_RESOURCE = "tooBigFilename";

    private static final String ADD_SUCCESS_MSG = "File successfully added.";
    private static final String DELETE_SUCCESS_MSG = "File successfully deleted.";
    private static final String ALREADY_EXISTS_MSG = "File add error. A file with that name already exists.";

    private static final int USER0 = 0;
    private static final int USER1 = 1;
    private static final int NUM_USERS = 2;

    private static final int NUM_FILES = 2;

    private final FileInfo[][] FILES = new FileInfo[NUM_USERS][NUM_FILES];
    private FileInfo TOO_BIG_FILE;

    protected final static String DOWNLOAD_FOLDER = System.getProperty("user.dir")+ java.io.File.separator + "externalFiles";

    protected enum Tests {
        ALL,
        ADD_VIEW_DOWNLOAD_DELETE_FILES,

        DOWNLOAD_FILES  // download of files test
    }
    protected List<Tests> skipTests;

    @Autowired
    private FileService fileService;

    public FileTest() throws FileNotFoundException {
        this(List.of());
    }

    public FileTest(List<Tests> skipTests) throws FileNotFoundException {
        setupFiles();
        this.skipTests = skipTests;
    }

    private void setupFiles() throws FileNotFoundException {
        Path filesFolder = Paths.get(getResourceString("filesFolder"));

        String filenameTemplate = getResourceString(FILENAME_RESOURCE);
        String contentsTemplate = getResourceString(CONTENTS_RESOURCE);
        Path filename;
        java.io.File file;


        for (int user = 0; user < NUM_USERS; user++) {
            for (int index = 0; index < NUM_FILES; index++) {

                String contents = String.format(contentsTemplate, user, index);

                filename = Paths.get(String.valueOf(filesFolder), String.format(filenameTemplate, user, index));

                file = ResourceUtils.getFile(
                        Objects.requireNonNull(this.getClass().getClassLoader().getResource(filename.toString())));

                // Note file contents should be only 1 line with NO new line (to avoid hassle of dealing with line endings on different os)
                byte[] contentBytes = contents.getBytes(StandardCharsets.UTF_8);
                byte[] fileBytes = loadFileBytes(filename.toString());

                // just check file contents are what is expected
                assertTrue(Arrays.equals(contentBytes, fileBytes), () -> "Expected contents and actual do not match, update test");

                FILES[user][index] = new FileInfo(
                        File.of(null, file.getName(), null,
                                Long.toString(file.length()), contentBytes, null),
                        contents,
                        file.getAbsolutePath()
                );
            }
        }

        filename = Paths.get(String.valueOf(filesFolder), getResourceString(TOO_BIG_RESOURCE));
        file = ResourceUtils.getFile(
                Objects.requireNonNull(this.getClass().getClassLoader().getResource(filename.toString())));

        TOO_BIG_FILE = new FileInfo(
                File.of(null, file.getName(), null,
                        Long.toString(file.length()), null, null),
                null,
                file.getAbsolutePath()
        );
    }

    @BeforeAll
    public static void beforeAll() {
        AbstractSeleniumTest.beforeAll();

        // clear download folder
        clearDownloadFolder();
    }

    protected static void clearDownloadFolder() {
        // clear download folder
        try {
            FileSystemUtils.deleteRecursively(Paths.get(DOWNLOAD_FOLDER));
        } catch (IOException e) {
            e.printStackTrace();
            fail("Unable to clear download folder");
        }
    }

    @BeforeEach
    public void beforeEach() throws InterruptedException {
        clearTable(fileService);
        clearUsers();

        SignupTest.signupUsersAndTestSuccess(this, List.of(LOGIN_USER_0, LOGIN_USER_1));
    }

    @AfterEach
    public void afterEach() {
        clearTable(fileService);
        clearUsers();
    }

    @AfterAll
    public static void afterAll() {
        // clear download folder
        clearDownloadFolder();

        AbstractSeleniumTest.afterAll();
    }

    private FileTab selectTab() throws InterruptedException {
        // select notes tab
        FileTab tab = FileTab.getInstance(driver);
        tab.selectFileTab();

        // check tab displayed
        pause("defaultTabChangeWait");
        testElements(this, getForefrontSuccessChecks(List.of(), defaultWaitTimeout));

        return tab;
    }

    @DisplayName("Multi-user files add/view/download/delete")
    @Test
    public void testFile() throws InterruptedException {

        // add user 0 files
        loginAddViewDownloadLogout(LOGIN_USER_0, List.of(FILES[USER0]));

        // add user 1 files
        loginAddViewDownloadLogout(LOGIN_USER_1, List.of(FILES[USER1]));

        // login user 0 and delete last user entry
        List<File> user0PostDel = loginDeleteLogout(LOGIN_USER_0, -1);

        // login user 1 and delete first user entry
        List<File> user1PostDel = loginDeleteLogout(LOGIN_USER_1, 0);

        // check user files
        loginCheckEntriesLogout(LOGIN_USER_0, user0PostDel);

        // check user files
        loginCheckEntriesLogout(LOGIN_USER_1, user1PostDel);

        pause();
    }

    @DisplayName("Direct access view/download/delete urls")
    @Test
    public void testViewDownloadDirectAccess() throws InterruptedException {

        // add user 0 files
        List<File> user0Files = loginAdd(LOGIN_USER_0, List.of(FILES[USER0][0]));
        assertTrue(user0Files.size() >= 1);

        Integer testId = user0Files.get(0).getFileid();

        selectTab().logout();
        pause("defaultLoadTimeout");

        // user 0 access while logged out
        for (DownloadTarget target : DownloadTarget.values()) {

            getLinkAndCheckLoginRedirect(getFileViewDownloadLink(testId, target));
        }

        // check delete
        getLinkAndCheckLoginRedirect(getFileActionLink(testId, Action.delete));

        // login user 1 and test access to user 0's files
        loginCheckEntries(LOGIN_USER_1, List.of());

        // check view/download
        FileTab tab;
        for (DownloadTarget target : DownloadTarget.values()) {

            getLinkAndCheckError(getFileViewDownloadLink(testId, target), "unauthorisedAccess");

            tab = selectTabCheckEntries(List.of());
        }

        // check delete
        getLinkAndCheckError(getFileActionLink(testId, Action.delete), "unauthorisedAccess");

        tab = selectTabCheckEntries(List.of());

        // logout user 1
        Objects.requireNonNull(tab).logout();

        pause();
    }

    @DisplayName("Direct access unsupported/erroneous urls")
    @Test
    public void testUnsupportedDirectAccess() throws InterruptedException {

        // add user 0 files
        List<File> user0Files = loginAdd(LOGIN_USER_0, List.of(FILES[USER0][0]));
        assertTrue(user0Files.size() >= 1);

        Integer testId = user0Files.get(0).getFileid();

        // test invalid id
        getLinkAndCheckError(getFileActionLink(testId * 1000, Action.read), "badRequest");

        selectTab().logout();
        pause("defaultLoadTimeout");

        // login user 1 and test access to user 0's files
        loginCheckEntries(LOGIN_USER_1, List.of());

        FileTab tab = null;
        for (String action : List.of(Action.none.name(), "unknown")) {

            getLinkAndCheckError(getFileActionLink(testId, action), "unsupportedOperation");

            tab = selectTabCheckEntries(List.of());
        }

        // logout user 1
        Objects.requireNonNull(tab).logout();

        pause();
    }

    @DisplayName("File already exists")
    @Test
    public void testAlreadyExists() throws InterruptedException {

        // add user 0 files
        FileInfo fileInfo = FILES[USER0][0];
        List<File> user0Files = loginAdd(LOGIN_USER_0, List.of(fileInfo));
        assertEquals(user0Files.size(), 1);

        FileTab tab = selectTab();
        tab.setAndSaveFile(fileInfo.path);
        pause("defaultModalSaveWait");

        // check page displayed
        testElements(this, ResultPage.getDisplayedMessageChecks(defaultWaitTimeout, MessageText.plainText(ALREADY_EXISTS_MSG)));
        ResultPage resultPage = ResultPage.getInstance(driver);

        resultPage.errorContinue();
        pause("defaultLoadTimeout");

        selectTab().logout();

        pause();
    }

    @DisplayName("File too large")
    @Test
    public void testTooLarge() throws InterruptedException {

        FileTab tab = loginCheckEntries(LOGIN_USER_0, List.of());

        // select files tab
        tab = selectTab();

        tab.setAndSaveFile(TOO_BIG_FILE.path);
        pause("defaultModalSaveWait");

        // check page displayed
        testElements(this, ErrorPage.getDisplayedMessageChecks(
                defaultWaitTimeout, MessageText.resourceText("fileTooLarge")));
        ErrorPage errorPage = ErrorPage.getInstance(driver);

        errorPage.errorContinue();
        pause("defaultLoadTimeout");

        selectTab().logout();

        pause();
    }

    private URI getFileViewDownloadLink(Integer id, DownloadTarget target) {
        return getUri(DOWNLOAD_URL, "id=" + id + "&target=" + target);
    }

    private URI getFileActionLink(Integer id, Action action) {
        return getFileActionLink(id, action.name());
    }

    private URI getFileActionLink(Integer id, String action) {
        return getUri(HOME_URL, "tab=" + HomeController.Tabs.file_tab + "&action=" + action + "&id=" + id);
    }

    private List<File> loginAddViewDownloadLogout(User user, List<FileInfo> add) throws InterruptedException {

        // login & check no credentials
        FileTab tab = loginCheckEntries(user, List.of());

        String mainWindow = driver.getWindowHandle();

        List<File> addedFiles = loginAdd(user, add);

        int index = 0;
        for (FileInfo fileInfo : add) {
            // select files tab
            tab = selectTab();

            // test view
            WebElement element = waitForElementByIdAndTest(getViewFileButtonId(index), defaultWaitTimeout, true);
            element.click();
            pause("defaultLoadTimeout");

            Set<String> allHandles = verifyNewWindow(mainWindow, "View window not found");

            String contents = driver.getPageSource();
            assertTrue(contents.contains(fileInfo.contents), () -> "File contents not found in window");

            driver.close();
            driver.switchTo().window(mainWindow);

            // test download
            if (skipTests.contains(Tests.DOWNLOAD_FILES)) {
                System.out.print("Skipping file download test");
            } else {
                element = waitForElementByIdAndTest(getDownloadFileButtonId(index), defaultWaitTimeout, true);
                element.click();
                pause("defaultLoadTimeout");

                try (InputStream is = new FileInputStream(Paths.get(DOWNLOAD_FOLDER, fileInfo.file.getFilename()).toString())) {
                    byte[] fileBytes = loadFileBytes(is);
                    assertTrue(Arrays.equals(fileBytes, fileInfo.file.getFiledata()), () -> "Downloaded file does not match");
                } catch (Exception e) {
                    e.printStackTrace();
                    fail("Unable to verify downloaded file");
                }
            }

            ++index;
        }

        // select credentials tab & check entries
        tab = selectTabCheckEntries(add.stream().map(fileInfo -> fileInfo.file).collect(Collectors.toList()));

        // logout
        tab.logout();
        waitForLoginAndTestLogoutSuccess(this);
        
        return addedFiles;
    }

    private List<File> loginAdd(User user, List<FileInfo> add) throws InterruptedException {

        List<File> addedFiles = Lists.newArrayList();

        // login & check no credentials
        FileTab tab = loginCheckEntries(user, List.of());

        for (FileInfo fileInfo : add) {
            // select files tab
            tab = selectTab();

            tab.setAndSaveFile(fileInfo.path);
            pause("defaultModalSaveWait");

            // check what was saved in db
            File added = fileService.getByFilename(fileInfo.file.getFilename());
            assertNotNull(added, () -> "Added file not in db");
            assertEquals(fileInfo.file.getFilename(), added.getFilename(), () -> "Filename mismatch");
            assertEquals(fileInfo.file.getFilesize(), added.getFilesize(), () -> "File size mismatch");
            assertTrue(Arrays.equals(fileInfo.file.getFiledata(), added.getFiledata()), () -> "File data mismatch");

            addedFiles.add(added);

            // check result
            ResultPage resultPage = getResultPageAndCheck(driver, Result.Success, ADD_SUCCESS_MSG);
            resultPage.successContinue();
            pause("defaultLoadTimeout");
        }

        return addedFiles;
    }

    private FileTab loginCheckEntries(User user, List<File> entries) throws InterruptedException {

        // login
        LoginTest.loginUserAndTestSuccess(this, user);

        // select credentials tab & check entries
        return selectTabCheckEntries(entries);
    }

    private void loginCheckEntriesLogout(User user, List<File> entries) throws InterruptedException {

        // login & check notes
        FileTab tab = loginCheckEntries(user, entries);

        // logout
        tab.logout();
        waitForLoginAndTestLogoutSuccess(this);
    }

    private List<File> loginDeleteLogout(User user, int delIndex) throws InterruptedException {

        User userDb = getUser(user.getUsername());

        List<File> entries = fileService.getAllForUser(userDb.getUserid());
        assertFalse(entries.isEmpty(), () -> "No entries for " + user.getUsername());

        // login & check no credentials
        FileTab tab = loginCheckEntries(user, entries);

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
        List<File> postDelEntries = fileService.getAllForUser(userDb.getUserid());
        assertEquals(entries.size() - 1, postDelEntries.size(), () -> "Expected post delete count does not match expected");

        // check deleted credentials not in database
        File delEntry = entries.get(delIndex);
        boolean match = postDelEntries.stream()
                .anyMatch(n -> n.equalsExBase(delEntry));
        assertFalse(match, () -> "Deleted file found in database");

        // select credentials tab & check entries
        tab = selectTabCheckEntries(postDelEntries);

        // logout
        tab.logout();
        waitForLoginAndTestLogoutSuccess(this);

        return postDelEntries;
    }

    private FileTab selectTabCheckEntries(List<File> entries) throws InterruptedException {

        // select credentials tab
        FileTab tab = selectTab();

        // check tab displayed with credentials
        testElements(this, getForefrontSuccessChecks(entries, defaultWaitTimeout));

        // check no credentials beyond expected
        testElementsNotFound(this, rowElements(IntStream.range(entries.size() + 1, entries.size() + 2), defaultNotThereWaitTimeout));

        return tab;
    }

    private void checkEntries(List<File> entries) {

        // check tab displayed with credentials
        testElements(this, getForefrontSuccessChecks(entries, defaultWaitTimeout));

        // check no credentials beyond expected
        testElementsNotFound(this, rowElements(IntStream.range(entries.size() + 1, entries.size() + 2), defaultNotThereWaitTimeout));
    }


    private void viewEntry(int index) throws InterruptedException {
        WebElement button = waitForElementById(getViewFileButtonId(index), defaultWaitTimeout);
        button.click();
        pause("defaultModalShowWait");
    }

    private void deleteEntry(int index) {
        WebElement button = waitForElementById(getDeleteFileButtonId(index), defaultWaitTimeout);
        button.click();
    }


    class FileInfo {
        File file;
        String contents;
        String path;

        public FileInfo(File file, String contents, String path) {
            this.file = file;
            this.contents = contents;
            this.path = path;
        }

        public File getFile() {
            return file;
        }

        public void setFile(File file) {
            this.file = file;
        }

        public String getContents() {
            return contents;
        }

        public void setContents(String contents) {
            this.contents = contents;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }
}
