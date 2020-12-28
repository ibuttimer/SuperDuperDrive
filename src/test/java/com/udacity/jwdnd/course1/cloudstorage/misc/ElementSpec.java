package com.udacity.jwdnd.course1.cloudstorage.misc;

import com.udacity.jwdnd.course1.cloudstorage.AbstractSeleniumTest.TextCheck;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import java.nio.charset.StandardCharsets;

public class ElementSpec {
    String id;
    String text;
    TextCheck textCheck;
    boolean isDisplayed;
    boolean isFound;
    int timeout;

    private ElementSpec(String id, String text, TextCheck textCheck) {
        this.id = id;
        this.text = text;
        this.textCheck = textCheck;
        this.isDisplayed = true;
        this.isFound = true;
        this.timeout = 0;
    }

    public static ElementSpec of(String id) {
        return new ElementSpec(id, null, TextCheck.NONE);
    }

    public static ElementSpec ofText(String id, String text, TextCheck textCheck) {
        return new ElementSpec(id, text, textCheck);
    }

    public static ElementSpec ofResource(String id, String resource, TextCheck textCheck) {
        if (StringUtils.isNotEmpty(resource)) {
            resource = ResourceStore.getBundle().getString(resource);
            if (resource.matches(".*&#x*[0-9a-fA-F]+;.*")) {
                // unescape any html
                resource = StringEscapeUtils.unescapeHtml4(resource);
            }
        } else {
            throw new IllegalArgumentException("Resource text required");
        }
        return new ElementSpec(id, resource, textCheck);
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public TextCheck getTextCheck() {
        return textCheck;
    }

    public void setTextCheck(TextCheck textCheck) {
        this.textCheck = textCheck;
    }

    public boolean isDisplayed() {
        return isDisplayed;
    }

    public ElementSpec setDisplayed(boolean displayed) {
        isDisplayed = displayed;
        return this;
    }

    public boolean isFound() {
        return isFound;
    }

    public ElementSpec setFound(boolean found) {
        isFound = found;
        return this;
    }

    public int getTimeout() {
        return timeout;
    }

    public ElementSpec setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public ElementSpec setTimeout(ITestResource resource, String key) {
        this.timeout = resource.getResourceInt(key);
        return this;
    }

    @Override
    public String toString() {
        return "ElementSpec{" +
                "id='" + id + '\'' +
                ", text='" + text + '\'' +
                ", textCheck=" + textCheck +
                ", isDisplayed=" + isDisplayed +
                ", isFound=" + isFound +
                ", timeout=" + timeout +
                '}';
    }
}
