package com.udacity.jwdnd.course1.cloudstorage;

import com.udacity.jwdnd.course1.cloudstorage.controllers.misc.Result;
import com.udacity.jwdnd.course1.cloudstorage.model.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Map;

import static com.udacity.jwdnd.course1.cloudstorage.config.UrlFactory.HOME_URL;
import static com.udacity.jwdnd.course1.cloudstorage.model.ErrorPage.getDisplayedMessageChecks;

public abstract class AbstractHomeTest extends AbstractSeleniumTest {

    public static HomePage getHomePage(WebDriver driver, AbstractSeleniumTest test) {
        driver.get(test.getUrl(HOME_URL));
        return HomePage.getInstance(driver);
    }

    public static Map<String, WebElement> waitForHomeAndTestSuccess(AbstractSeleniumTest test) {
        test.waitForElementByIdAndTest(HomePage.getPageDisplayedId(), test.defaultWaitTimeout, true);
        return testElements(test, HomePage.getDisplaySuccessChecks(test.defaultWaitTimeout));
    }

    public void loginUser(User user) {
        LoginTest.loginUserAndTestSuccess(this, user);
    }

    public void logoutUser() {
        HomePage.getInstance(driver).logout();
    }

    public ResultPage getResultPageAndCheck(WebDriver driver, Result result, String message) throws InterruptedException {
        // select notes tab
        ResultPage resultPage = ResultPage.getInstance(driver);

        // check page displayed
        pause("defaultModalShowWait");
        testElements(this, ResultPage.getDisplayedMessageChecks(defaultWaitTimeout, result, message));

        return resultPage;
    }

}
