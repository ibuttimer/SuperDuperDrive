package com.udacity.jwdnd.course1.cloudstorage.model;

import com.google.common.collect.Lists;
import com.udacity.jwdnd.course1.cloudstorage.AbstractSeleniumTest.TextCheck;
import com.udacity.jwdnd.course1.cloudstorage.misc.ElementSpec;
import com.udacity.jwdnd.course1.cloudstorage.misc.MessageText;
import com.udacity.jwdnd.course1.cloudstorage.misc.ResourceStore;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ErrorPage extends AbstractPage {

    public static final String ERROR_RESOURCE = "error";

    public static final String MSG_CONTAINER_ID = "msg-container";

    public static final String ERROR_TITLE_ID = "errorTitle";
    public static final String ERROR_TEXT_ID = "error-text";
    public static final String ERROR_MSG_0_ID = "error-msg-0";
    public static final String ERROR_LINK_ID = "error-continue-link";
    public static final String ERROR_MSG_2_ID = "error-msg-2";

    @FindBy(id = ERROR_LINK_ID)
    protected WebElement errorLink;

    public ErrorPage(WebDriver driver) {
        super(driver);
    }

    public static ErrorPage getInstance(WebDriver driver) {
        return new ErrorPage(driver);
    }

    public static String getPageDisplayedId() {
        return MSG_CONTAINER_ID;
    }

    public static List<ElementSpec> getDisplaySuccessChecks(int timeout) {
        return setListTimeouts(
                List.of(
                    // check for message container
                    ElementSpec.of(MSG_CONTAINER_ID)
                ),
                timeout
        );
    }

    static List<ElementSpec> getClickToContinueChecks(int timeout, String id0, String id1, String id2) {
        return setListTimeouts(
                List.of(
                    ElementSpec.ofResource(id0, "clickToContinue0", TextCheck.EQUALS),
                    ElementSpec.ofResource(id1, "clickToContinue1", TextCheck.EQUALS),
                    ElementSpec.ofResource(id2, "clickToContinue2", TextCheck.EQUALS)
                ),
                timeout
        );
    }

    public static List<ElementSpec> getDisplayedMessageChecks(int timeout, MessageText message) {
        List<ElementSpec> boxes = Lists.newArrayList(
                ElementSpec.of(ERROR_MSG_ID));

        return setListTimeouts(
                concatLists(getDisplaySuccessChecks(timeout),
                        getClickToContinueChecks(timeout, ERROR_MSG_0_ID, ERROR_LINK_ID, ERROR_MSG_2_ID),
                        boxes,
                        List.of(
                            // check result title
                            ElementSpec.ofResource(ERROR_TITLE_ID, "error", TextCheck.EQUALS),
                            // check texts
                            message.isPlainText() ? ElementSpec.ofText(ERROR_TEXT_ID, message.getMessage(), TextCheck.EQUALS)
                                    : ElementSpec.ofResource(ERROR_TEXT_ID, message.getMessage(), TextCheck.EQUALS)
                        )
                ),
                timeout
        );
    }

    public static void testTitle(WebDriver driver) {
        String title = ResourceStore.getBundle().getString(ERROR_RESOURCE);
        assertEquals(title, driver.getTitle());
    }

    @Override
    public void testElements() {
        String title = ResourceStore.getBundle().getString(ERROR_RESOURCE);
        assertEquals(title, Objects.requireNonNull(webDriverWeakReference.get()).getTitle());
    }

    public void errorContinue() {
        errorLink.click();
    }
}
