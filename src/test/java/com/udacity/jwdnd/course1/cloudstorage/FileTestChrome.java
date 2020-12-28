package com.udacity.jwdnd.course1.cloudstorage;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.udacity.jwdnd.course1.cloudstorage.AbstractSeleniumTest.Browser.CHROME;
import static com.udacity.jwdnd.course1.cloudstorage.AbstractSeleniumTest.Browser.EDGE;

public class FileTestChrome extends FileTest {

    public FileTestChrome() throws FileNotFoundException {
        super();
    }

    @BeforeAll
    public static void beforeAll() {
        setBrowser(Browser.CHROME, FileTestChrome.class);
        FileTest.beforeAll();
    }

    @AfterAll
    public static void afterAll() {
        FileTest.afterAll();
    }

    public static Map<String, Object> browserOptions(Browser browser) {
        Map<String, Object> prefs = new HashMap<>();
        if (browser == CHROME) {
            prefs.put("download.default_directory", DOWNLOAD_FOLDER);
        }
        return prefs;
    }
}
