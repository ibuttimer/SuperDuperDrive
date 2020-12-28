package com.udacity.jwdnd.course1.cloudstorage;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.udacity.jwdnd.course1.cloudstorage.AbstractSeleniumTest.Browser.EDGE;

public class FileTestEdge extends FileTest {

    public FileTestEdge() throws FileNotFoundException {
        // skip download files test as there doesn't seem to be a way to set the download folder via selenium
        super(List.of(Tests.DOWNLOAD_FILES));
    }

    @BeforeAll
    public static void beforeAll() {
        setBrowser(EDGE, FileTestEdge.class);
        FileTest.beforeAll();
    }

    @AfterAll
    public static void afterAll() {
        FileTest.afterAll();
    }

    public static Map<String, Object> browserOptions(Browser browser) {
        Map<String, Object> prefs = new HashMap<>();
        // no specific options, there doesn't seem to be a way to set the download folder via selenium at the moment
        return prefs;
    }
}
