package com.udacity.jwdnd.course1.cloudstorage.model;

import com.google.common.collect.Lists;
import com.udacity.jwdnd.course1.cloudstorage.AbstractSeleniumTest.TextCheck;
import com.udacity.jwdnd.course1.cloudstorage.controllers.misc.Result;
import com.udacity.jwdnd.course1.cloudstorage.misc.ElementSpec;
import com.udacity.jwdnd.course1.cloudstorage.misc.ResourceStore;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResultPage extends ErrorPage {

    public static final String RESULT_RESOURCE = "result";

    public static final String SUCCESS_TITLE_ID = "successTitle";
    public static final String SUCCESS_TEXT_ID = "success-text";
    public static final String CHANGE_MSG_0_ID = "change-msg-0";
    public static final String CHANGE_SUCCESS_LINK_ID = "change-success-link";
    public static final String CHANGE_MSG_2_ID = "change-msg-2";

    public static final String NOT_SAVED_MSG_ID = "not-saved-msg";
    public static final String NOT_SAVED_TITLE_ID = "notSavedTitle";
    public static final String NOT_SAVED_TEXT_ID = "notsaved-text";
    public static final String NOT_SAVED_MSG_0_ID = "notsaved-msg-0";
    public static final String NOT_SAVED_LINK_ID = "change-notsaved-link";
    public static final String NOT_SAVED_MSG_2_ID = "notsaved-msg-2";

    @FindBy(id = CHANGE_SUCCESS_LINK_ID)
    protected WebElement changeSuccessLink;

    @FindBy(id = NOT_SAVED_LINK_ID)
    protected WebElement notSavedLink;


    public ResultPage(WebDriver driver) {
        super(driver);
    }

    public static ResultPage getInstance(WebDriver driver) {
        return new ResultPage(driver);
    }

    public static List<ElementSpec> getDisplayedMessageChecks(int timeout, Result result, String message) {
        List<ElementSpec> continueChecks;
        List<ElementSpec> boxes = Lists.newArrayList();

        for (Result res : Result.values()) {
            String id;
            switch (res) {
                case Success:   id = SUCCESS_MSG_ID;    break;
                case NotSaved:  id = NOT_SAVED_MSG_ID;  break;
                default:        id = ERROR_MSG_ID;      break;
            }
            boxes.add(ElementSpec.of(id)
                        .setDisplayed(res == result)
                        .setFound(res == result));
        }

        String titleId;
        String titleResource;
        String textId;
        switch (result) {
            case Success:
                titleId = SUCCESS_TITLE_ID;
                titleResource = "success";
                textId = SUCCESS_TEXT_ID;
                continueChecks = getClickToContinueChecks(timeout, CHANGE_MSG_0_ID, CHANGE_SUCCESS_LINK_ID, CHANGE_MSG_2_ID);
                break;
            case NotSaved:
                titleId = NOT_SAVED_TITLE_ID;
                titleResource = "error";
                textId = NOT_SAVED_TEXT_ID;
                continueChecks = getClickToContinueChecks(timeout, NOT_SAVED_MSG_0_ID, NOT_SAVED_LINK_ID, NOT_SAVED_MSG_2_ID);
                break;
            default:
                titleId = ERROR_TITLE_ID;
                titleResource = "error";
                textId = ERROR_TEXT_ID;
                continueChecks = getClickToContinueChecks(timeout, ERROR_MSG_0_ID, ERROR_LINK_ID, ERROR_MSG_2_ID);
                break;
        }

        return setListTimeouts(
                concatLists(getDisplaySuccessChecks(timeout),
                        continueChecks,
                        boxes,
                        List.of(
                            // check result title
                            ElementSpec.ofResource(titleId, titleResource, TextCheck.EQUALS),
                            // check texts
                            ElementSpec.ofText(textId, message, TextCheck.EQUALS)
                        )
                ),
                timeout
        );
    }

    public static void testTitle(WebDriver driver) {
        String title = ResourceStore.getBundle().getString(RESULT_RESOURCE);
        assertEquals(title, driver.getTitle());
    }

    @Override
    public void testElements() {
        String title = ResourceStore.getBundle().getString(RESULT_RESOURCE);
        assertEquals(title, Objects.requireNonNull(webDriverWeakReference.get()).getTitle());
    }

    public void successContinue() {
        changeSuccessLink.click();
    }

    public void notSavedContinue() {
        notSavedLink.click();
    }
}
