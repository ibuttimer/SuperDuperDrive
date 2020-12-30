package com.udacity.jwdnd.course1.cloudstorage;

import com.udacity.jwdnd.course1.cloudstorage.misc.ElementSpec;
import com.udacity.jwdnd.course1.cloudstorage.model.LoginPage;
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
import java.util.concurrent.atomic.AtomicReference;

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


    public static SignupPage getSignupPage(WebDriver driver, AbstractSeleniumTest test) throws InterruptedException {
        driver.get(test.getUrl(SIGNUP_URL));
        test.pause("defaultLoadTimeout");
        return new SignupPage(driver);
    }

    public static Map<String, WebElement> signupUserAndTest(SignupPage signupPage, AbstractSeleniumTest test,
                                                            List<ElementSpec> elementSpecs,
                                                            User user) throws InterruptedException {
        // sign up
        signupPage.signup(user);
        test.pause("defaultLoadTimeout", 2);

        return testElements(test, elementSpecs);
    }

    public static Map<String, WebElement> signupUserAndTestSuccess(SignupPage signupPage, AbstractSeleniumTest test, User user) throws InterruptedException {
        return signupUserAndTest(signupPage, test,
                SignupPage.setListTimeouts(
                    concatLists(
                            LoginPage.getDisplaySuccessChecks(test.defaultWaitTimeout),
                            List.of(
                                ElementSpec.ofResource(SUCCESS_MSG_ID, "signupSuccess", TextCheck.EQUALS)  // check for success message
                            )
                    ),
                    test.defaultWaitTimeout),
                user
        );
    }

    public static void signupUsersAndTestSuccess(AbstractSeleniumTest test, List<User> users) throws InterruptedException {
        for (User user : users) {
            SignupPage signupPage = SignupTest.getSignupPage(driver, test);
            signupUserAndTestSuccess(signupPage, test, user);
        }
    }

    public static Map<String, WebElement> signupUserAndTestFailure(SignupPage signupPage, AbstractSeleniumTest test, User user) throws InterruptedException {
        return signupUserAndTest(signupPage, test,
                SignupPage.setListTimeouts(
                    List.of(
                        ElementSpec.ofResource(ERROR_MSG_ID, "usernameExists", TextCheck.EQUALS)),  // check error message
                    test.defaultWaitTimeout),
                user
        );
    }

    @BeforeEach
    public void beforeEach() throws InterruptedException {
        signupPage = getSignupPage(driver, this);
    }

    @DisplayName("Signup success/failure")
    @Test
    public void testSignup() throws InterruptedException {

        signupPage.testElements();

        // sign up & check success
        List<User> existingUsers = userService.getAll();

        signupUserAndTestSuccess(signupPage, this, SIGNUP_USER);

        List<User> users = userService.getAll();
        assertEquals(existingUsers.size() + 1, users.size());

        AtomicReference<User> atomicUser = new AtomicReference<>(null);
        users.stream()
                .filter(u -> !existingUsers.contains(u))
                .findFirst()
                .ifPresent(atomicUser::set);

        User userDb = atomicUser.get();
        assertNotNull(userDb);
        assertNotNull(userDb.getUserid());
        assertTrue(StringUtils.isNotBlank(userDb.getSalt()));
        assertTrue(StringUtils.isNotBlank(userDb.getPassword()));

        User user = User.of(userDb.getUserid(), SIGNUP_USER.getUsername(), userDb.getSalt(), userDb.getPassword(),
                SIGNUP_USER.getFirstname(), SIGNUP_USER.getLastname());
        assertEquals(user, userDb);

        // sign up same user & check failure
        signupPage = getSignupPage(driver, this);
        signupUserAndTestFailure(signupPage, this, SIGNUP_USER);

        pause();
    }

}
