package com.udacity.jwdnd.course1.cloudstorage.model;

import com.google.common.collect.Lists;
import com.udacity.jwdnd.course1.cloudstorage.misc.ElementSpec;
import org.apache.commons.lang3.tuple.Pair;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.lang.ref.WeakReference;
import java.util.List;

public abstract class AbstractPage {

    public static final String HEADING_ID = "heading";
    public static final String SUCCESS_MSG_ID = "success-msg";
    public static final String ERROR_MSG_ID = "error-msg";

    @FindBy(id = HEADING_ID)
    protected WebElement heading;

    protected WeakReference<WebDriver> webDriverWeakReference;

    public AbstractPage(WebDriver driver) {
        PageFactory.initElements(driver, this);
        webDriverWeakReference = new WeakReference<>(driver);
    }

    public abstract void testElements();

    protected void setFields(List<Pair<WebElement, String>> elementValues) {
        for (Pair<WebElement, String> pair : elementValues) {
            pair.getLeft().clear();
            pair.getLeft().sendKeys(pair.getRight());
        }
    }

    @SafeVarargs
    public static List<ElementSpec> concatLists(List<ElementSpec> ...lists) {
        List<ElementSpec> list = Lists.newArrayList();
        for (List<ElementSpec> elementSpecs : lists) {
            list.addAll(elementSpecs);
        }
        return list;
    }

    public static List<ElementSpec> setListTimeouts(List<ElementSpec> list, int timeout) {
        for (ElementSpec spec : list) {
            spec.setTimeout(timeout);
        }
        return list;
    }

}
