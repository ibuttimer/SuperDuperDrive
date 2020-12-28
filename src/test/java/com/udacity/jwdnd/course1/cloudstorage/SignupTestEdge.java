package com.udacity.jwdnd.course1.cloudstorage;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.util.HashMap;
import java.util.Map;

import static com.udacity.jwdnd.course1.cloudstorage.AbstractSeleniumTest.Browser.EDGE;

public class SignupTestEdge extends SignupTest {

    public SignupTestEdge() {
        super();
    }

    @BeforeAll
    public static void beforeAll() {
        setBrowser(EDGE, SignupTestEdge.class);
        SignupTest.beforeAll();
    }

    @AfterAll
    public static void afterAll() {
        SignupTest.afterAll();
    }

    public static Map<String, Object> browserOptions(Browser browser) {
        return new HashMap<>();
    }
}
