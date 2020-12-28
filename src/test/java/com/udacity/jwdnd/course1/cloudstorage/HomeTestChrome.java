package com.udacity.jwdnd.course1.cloudstorage;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import static com.udacity.jwdnd.course1.cloudstorage.AbstractSeleniumTest.Browser.CHROME;

public class HomeTestChrome extends HomeTest {

    public HomeTestChrome() {
        super();
    }

    @BeforeAll
    public static void beforeAll() {
        setBrowser(Browser.CHROME, HomeTestChrome.class);
        HomeTest.beforeAll();
    }

    @AfterAll
    public static void afterAll() {
        HomeTest.afterAll();
    }

    public static Map<String, Object> browserOptions(Browser browser) {
        Map<String, Object> prefs = new HashMap<>();
        if (browser == CHROME) {
            // no-op
        }
        return prefs;
    }
}
