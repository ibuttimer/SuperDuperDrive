package com.udacity.jwdnd.course1.cloudstorage;

import com.udacity.jwdnd.course1.cloudstorage.misc.ElementSpec;
import com.udacity.jwdnd.course1.cloudstorage.model.SignupPage;
import com.udacity.jwdnd.course1.cloudstorage.model.User;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static com.udacity.jwdnd.course1.cloudstorage.model.AbstractPage.ERROR_MSG_ID;
import static com.udacity.jwdnd.course1.cloudstorage.model.AbstractPage.SUCCESS_MSG_ID;
import static com.udacity.jwdnd.course1.cloudstorage.config.UrlFactory.SIGNUP_URL;
import static com.udacity.jwdnd.course1.cloudstorage.model.SignupPage.*;
import static org.junit.jupiter.api.Assertions.*;

public abstract class SignupTest extends AbstractSeleniumTest {

    private static final String FIRSTNAME = "signupUserFirst";
    private static final String LASTNAME = "signupUserLast";
    private static final String USERNAME = "signupuser";
    private static final String PASSWORD = "signup";

    public static final User SIGNUP_USER = User.of(USERNAME, null, PASSWORD, FIRSTNAME, LASTNAME);

    private SignupPage signupPage;

    @Autowired
    private UserService userService;


    public static SignupPage getSignupPage(WebDriver driver, AbstractSeleniumTest test) {
        driver.get(test.getUrl(SIGNUP_URL));
        return new SignupPage(driver);
    }

    public static Map<String, WebElement> signupUserAndTest(SignupPage signupPage, AbstractSeleniumTest test,
                                                            List<ElementSpec> elementSpecs,
                                                            User user) {
        // sign up
        signupPage.signup(user);

        return testElements(test, elementSpecs);
    }

    public static Map<String, WebElement> signupUserAndTestSuccess(SignupPage signupPage, AbstractSeleniumTest test, User user) {
        return signupUserAndTest(signupPage, test,
                SignupPage.setListTimeouts(
                    List.of(
                        ElementSpec.of(SUCCESS_MSG_ID),          // check for success message
                        ElementSpec.ofResource(SIGNUP_SUCCESS_0_ID, "signupSuccess0", TextCheck.STARTS_WITH),  // check for continue to login
                        ElementSpec.ofResource(LOGIN_LINK_ID, "signupSuccess1", TextCheck.EQUALS),
                        ElementSpec.ofResource(SIGNUP_SUCCESS_2_ID, "signupSuccess2", TextCheck.ENDS_WITH)),
                    test.defaultWaitTimeout),
                user
        );
    }

    public static void signupUsersAndTestSuccess(SignupPage signupPage, AbstractSeleniumTest test, List<User> users) {
        for (User user : users) {
            signupUserAndTestSuccess(signupPage, test, user);
        }
    }

    public static Map<String, WebElement> signupUserAndTestFailure(SignupPage signupPage, AbstractSeleniumTest test, User user) {
        return signupUserAndTest(signupPage, test,
                SignupPage.setListTimeouts(
                    List.of(
                        ElementSpec.ofResource(ERROR_MSG_ID, "usernameExists", TextCheck.EQUALS)),  // check error message
                    test.defaultWaitTimeout),
                user
        );
    }

    @BeforeEach
    public void beforeEach() {
        signupPage = getSignupPage(driver, this);
    }

    @DisplayName("Signup success/failure")
    @Test
    public void testSignup() throws InterruptedException {

        signupPage.testElements();

        // sign up & check success
        signupUserAndTestSuccess(signupPage, this, SIGNUP_USER);

        List<User> users = userService.getAll();
        assertEquals(1, users.size());
        User userDb = users.get(0);

        assertNotNull(userDb.getUserid());
        assertTrue(StringUtils.isNotBlank(userDb.getSalt()));
        assertTrue(StringUtils.isNotBlank(userDb.getPassword()));

        User user = User.of(userDb.getUserid(), SIGNUP_USER.getUsername(), userDb.getSalt(), userDb.getPassword(), SIGNUP_USER.getFirstname(), SIGNUP_USER.getLastname());
        assertEquals(user, userDb);

        // sign up same user & check failure
        signupUserAndTestFailure(signupPage, this, SIGNUP_USER);

        pause();
    }

}
