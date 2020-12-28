package com.udacity.jwdnd.course1.cloudstorage;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.util.HashMap;
import java.util.Map;

import static com.udacity.jwdnd.course1.cloudstorage.AbstractSeleniumTest.Browser.CHROME;

public class LoginTestChrome extends LoginTest {

    public LoginTestChrome() {
        super();
    }

    @BeforeAll
    public static void beforeAll() {
        setBrowser(Browser.CHROME, LoginTestChrome.class);
        LoginTest.beforeAll();
    }

    @AfterAll
    public static void afterAll() {
        LoginTest.afterAll();
    }

    public static Map<String, Object> browserOptions(Browser browser) {
        Map<String, Object> prefs = new HashMap<>();
        if (browser == CHROME) {
            // no-op
        }
        return prefs;
    }
}
