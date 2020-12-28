package com.udacity.jwdnd.course1.cloudstorage.model;

import com.google.common.collect.Lists;
import com.udacity.jwdnd.course1.cloudstorage.misc.ElementSpec;
import com.udacity.jwdnd.course1.cloudstorage.AbstractSeleniumTest.TextCheck;
import org.apache.commons.lang3.tuple.Pair;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.thymeleaf.util.StringUtils;

import java.util.List;
import java.util.stream.IntStream;

public class NoteTab extends HomePage {

    public static final String ADD_NEW_BUTTON_ID = "add_new_note";
    public static final String ENTRIES_TABLE_ID = "userTable";
    public static final String EDIT_ENTRY_BUTTON_ID = "editnote";
    public static final String DELETE_ENTRY_BUTTON_ID = "deletenote";

    public static final String NOTE_TITLE_ID = "notetitle";
    public static final String NOTE_DESCRIPTION_ID = "notedescription";

    // modal dialog ids
    public static final String MODAL_LABEL_ID = "noteModalLabel";
    public static final String MODAL_CLOSE_ID = "noteModalClose";
    public static final String MODAL_NOTE_ID = "note-id";
    public static final String MODAL_NOTE_TITLE_ID = "note-title";
    public static final String MODAL_NOTE_DESCRIPTION_ID = "note-description";
    public static final String SUBMIT_BUTTON_ID = "noteSubmit";
    public static final String QUIT_BUTTON_ID = "note-quit-button";
    public static final String SAVE_BUTTON_ID = "note-save-button";


    @FindBy(id = ADD_NEW_BUTTON_ID)
    protected WebElement addNewButton;

    @FindBy(id = ENTRIES_TABLE_ID)
    protected WebElement entriesTable;

    // modal elements
    @FindBy(id = MODAL_LABEL_ID)
    protected WebElement modalLabel;

    @FindBy(id = MODAL_CLOSE_ID)
    protected WebElement closeButton;

    @FindBy(id = MODAL_NOTE_ID)
    protected WebElement idText;

    @FindBy(id = MODAL_NOTE_TITLE_ID)
    protected WebElement titleText;

    @FindBy(id = MODAL_NOTE_DESCRIPTION_ID)
    protected WebElement descriptionText;

    @FindBy(id = SUBMIT_BUTTON_ID)
    protected WebElement noteButton;

    @FindBy(id = QUIT_BUTTON_ID)
    protected WebElement quitButton;

    @FindBy(id = SAVE_BUTTON_ID)
    protected WebElement saveButton;




    public NoteTab(WebDriver driver) {
        super(driver);
    }

    public static NoteTab getInstance(WebDriver driver) {
        return new NoteTab(driver);
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
                    // check for add note button
                    ElementSpec.ofResource(ADD_NEW_BUTTON_ID, "add_new_note", TextCheck.EQUALS)),
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
            list.add(ElementSpec.of(getEditNoteButtonId(i)));
            list.add(ElementSpec.of(getDeleteNoteButtonId(i)));
            list.add(ElementSpec.of(getNoteTitleId(i)));
            list.add(ElementSpec.of(getNoteDescriptionId(i)));
        });
        return setListTimeouts(list, timeout);
    }

    /**
     * Get tab and list displayed successfully tests
     * @param notes - Entries to test
     * @param timeout - Test timeout
     * @return
     */
    public static List<ElementSpec> getForefrontSuccessChecks(List<Note> notes, int timeout) {
        List<ElementSpec> list = Lists.newArrayList();
        list.addAll(getDisplaySuccessChecks(timeout));
        for (int i = 0; i < notes.size(); ++i) {
            Note note = notes.get(i);
            list.add(ElementSpec.of(getEditNoteButtonId(i)));
            list.add(ElementSpec.of(getDeleteNoteButtonId(i)));
            list.add(ElementSpec.ofText(getNoteTitleId(i), note.getNotetitle(), TextCheck.EQUALS));
            list.add(ElementSpec.ofText(getNoteDescriptionId(i), note.getNotedescription(), TextCheck.EQUALS));
        }
        return setListTimeouts(list, timeout);
    }


    /**
     * Get modal displayed successfully tests
     * @param id - database id of entry
     * @param title - url
     * @param description - description
     * @param timeout - Test timeout
     * @return
     */
    public static List<ElementSpec> getModalForefrontSuccessChecks(String id, String title, String description, int timeout) {
        return setListTimeouts(
                List.of(
                    ElementSpec.ofResource(MODAL_LABEL_ID, "note", TextCheck.EQUALS),
                    ElementSpec.ofText(MODAL_CLOSE_ID, Character.toString(0xd7), TextCheck.EQUALS),    // &times;	multiplication sign
                    ElementSpec.ofText(MODAL_NOTE_ID, id, StringUtils.isEmpty(id) ? TextCheck.NONE : TextCheck.EQUALS)
                        .setDisplayed(false),
                    ElementSpec.ofText(MODAL_NOTE_TITLE_ID, title, StringUtils.isEmpty(title) ? TextCheck.NONE : TextCheck.EQUALS),
                    ElementSpec.ofText(MODAL_NOTE_DESCRIPTION_ID, description, StringUtils.isEmpty(description) ? TextCheck.NONE : TextCheck.EQUALS),
                    ElementSpec.of(SUBMIT_BUTTON_ID)
                        .setDisplayed(false),
                    ElementSpec.ofResource(QUIT_BUTTON_ID, "close", TextCheck.EQUALS),
                    ElementSpec.ofResource(SAVE_BUTTON_ID, "save_changes", TextCheck.EQUALS)),
                timeout
        );
    }

    /**
     * Get empty modal displayed successfully tests
     * @param timeout - Test timeout
     * @return list of tests
     */
    public static List<ElementSpec> getModalForefrontSuccessChecks(int timeout) {
        return getModalForefrontSuccessChecks(null, null, null, timeout);
    }

    /**
     * Get empty modal not displayed tests
     * @param timeout - Test timeout
     * @return list of tests
     */
    public static List<ElementSpec> getModalNotDisplayedChecks(int timeout) {
        List<ElementSpec> list = getModalForefrontSuccessChecks(timeout);
        for (ElementSpec spec : list) {
            spec.setDisplayed(false);
            spec.setTextCheck(TextCheck.NONE);
        }
        return list;
    }

    @Override
    public void testElements() {
        super.testElements();
    }

    public void newEntry() {
        addNewButton.click();
    }

    public void quit() {
        quitButton.click();
    }

    public void close() {
        closeButton.click();
    }

    public void setAndSaveNote(Note note) {
        setNote(note);
        saveButton.click();
    }

    public static String getEditNoteButtonId(int index) {
        return EDIT_ENTRY_BUTTON_ID + index;
    }

    public static String getDeleteNoteButtonId(int index) {
        return DELETE_ENTRY_BUTTON_ID + index;
    }

    public static String getNoteTitleId(int index) {
        return NOTE_TITLE_ID + index;
    }

    public static String getNoteDescriptionId(int index) {
        return NOTE_DESCRIPTION_ID + index;
    }

    public void setIdText(String id) {
        idText.sendKeys(id);
    }

    public void setTitleText(String title) {
        titleText.sendKeys(title);
    }

    public void setDescriptionText(String description) {
        descriptionText.sendKeys(description);
    }

    public void setNote(Note note) {
        setFields(List.of(
                Pair.of(titleText, note.getNotetitle()),
                Pair.of(descriptionText, note.getNotedescription())
            ));
    }
}
