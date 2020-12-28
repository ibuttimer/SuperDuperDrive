package com.udacity.jwdnd.course1.cloudstorage.model;

import com.google.common.collect.Lists;
import com.udacity.jwdnd.course1.cloudstorage.AbstractSeleniumTest.TextCheck;
import com.udacity.jwdnd.course1.cloudstorage.misc.ElementSpec;
import org.apache.commons.lang3.tuple.Pair;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.stream.IntStream;

public class FileTab extends HomePage {

    public static final String ADD_NEW_BUTTON_ID = "add_new_file";
    public static final String FILE_INPUT_ID = "fileUpload";
    public static final String ENTRIES_TABLE_ID = "file_table";
    public static final String VIEW_ENTRY_BUTTON_ID = "viewfile";
    public static final String DOWNLOAD_ENTRY_BUTTON_ID = "downloadfile";
    public static final String DELETE_ENTRY_BUTTON_ID = "deletefile";

    public static final String FILENAME_ID = "filename";

    @FindBy(id = ADD_NEW_BUTTON_ID)
    protected WebElement addNewButton;

    @FindBy(id = FILE_INPUT_ID)
    protected WebElement fileInput;

    @FindBy(id = ENTRIES_TABLE_ID)
    protected WebElement entriesTable;



    public FileTab(WebDriver driver) {
        super(driver);
    }

    public static FileTab getInstance(WebDriver driver) {
        return new FileTab(driver);
    }

    public static String getPageDisplayedId() {
        return ADD_NEW_BUTTON_ID;
    }


    /**
     * Get tab displayed successfully tests
     * @param timeout - Test timeout
     * @return
     */
    public static List<ElementSpec> getDisplaySuccessChecks(int timeout) {
        return setListTimeouts(
                List.of(
                    // check for add file button
                    ElementSpec.ofResource(ADD_NEW_BUTTON_ID, "fileSubmit", TextCheck.EQUALS)),
                timeout);
    }

    /**
     * Get list of tests for a row(s)
     * @param intStream - Row indices to test
     * @param timeout - Test timeout
     * @return
     */
    public static List<ElementSpec> rowElements(IntStream intStream, int timeout) {
        List<ElementSpec> list = Lists.newArrayList();
        intStream.forEach(i -> {
            list.add(ElementSpec.of(getViewFileButtonId(i)));
            list.add(ElementSpec.of(getDownloadFileButtonId(i)));
            list.add(ElementSpec.of(getDeleteFileButtonId(i)));
            list.add(ElementSpec.of(getFilenameId(i)));
        });
        return setListTimeouts(list, timeout);
    }

    /**
     * Get tab and list displayed successfully tests
     * @param files - Entries to test
     * @param timeout - Test timeout
     * @return
     */
    public static List<ElementSpec> getForefrontSuccessChecks(List<File> files, int timeout) {
        List<ElementSpec> list = Lists.newArrayList();
        list.addAll(getDisplaySuccessChecks(timeout));
        for (int i = 0; i < files.size(); ++i) {
            File file = files.get(i);
            list.add(ElementSpec.of(getViewFileButtonId(i)));
            list.add(ElementSpec.of(getDownloadFileButtonId(i)));
            list.add(ElementSpec.of(getDeleteFileButtonId(i)));
            list.add(ElementSpec.ofText(getFilenameId(i), file.getFilename(), TextCheck.EQUALS));
        }
        return setListTimeouts(list, timeout);
    }


    @Override
    public void testElements() {
        super.testElements();
    }

    public void setAndSaveFile(String filePath) {
        setFile(filePath);
        addNewButton.click();
    }

    public static String getViewFileButtonId(int index) {
        return VIEW_ENTRY_BUTTON_ID + index;
    }

    public static String getDownloadFileButtonId(int index) {
        return DOWNLOAD_ENTRY_BUTTON_ID + index;
    }

    public static String getDeleteFileButtonId(int index) {
        return DELETE_ENTRY_BUTTON_ID + index;
    }

    public static String getFilenameId(int index) {
        return FILENAME_ID + index;
    }

    public void setFile(String filePath) {
        setFields(List.of(
                Pair.of(fileInput, filePath)
            ));
    }
}
