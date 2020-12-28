package com.udacity.jwdnd.course1.cloudstorage.controllers.misc;

import com.udacity.jwdnd.course1.cloudstorage.controllers.HomeController.Tabs;

import java.util.OptionalInt;

/**
 * Class representing an action result
 */
public class ActionResult {

    private Tabs tab;
    private Result result;
    private int affected;
    private OptionalInt userId;
    private String message;
    private ErrorCode errorCode;

    /**
     * Default constructor
     */
    public ActionResult() {
        this(Result.Error, 0, OptionalInt.empty(), "");
    }

    /**
     * Constructor
     * @param result - action result
     * @param affected - number of database rows affected
     * @param userId - user id
     * @param message - message
     */
    public ActionResult(Result result, int affected, OptionalInt userId, String message) {
        init(result, affected, userId, message, Tabs.no_tab, ErrorCode.none);
    }

    private void init(Result result, int affected, OptionalInt userId, String message, Tabs tab, ErrorCode errorCode) {
        this.result = result;
        this.affected = affected;
        this.userId = userId;
        this.message = message;
        this.tab = tab;
        this.errorCode = errorCode;
    }

    /**
     * Copy constructor
     * @param copy - object to copy
     */
    public ActionResult(ActionResult copy) {
        init(copy.result,
                copy.affected,
                copy.userId.isPresent() ? OptionalInt.of(copy.userId.getAsInt()) : OptionalInt.empty(),
                (copy.message == null ? null : new String(copy.message)),
                copy.tab,
                copy.errorCode);
    }

    /**
     * Success result constructor
     * @param affected - number of database rows affected
     * @param userId - user id
     * @return
     */
    public static ActionResult ofSuccess(int affected, OptionalInt userId) {
        return new ActionResult(Result.Success, affected, userId, "");
    }

    /**
     * Not saved result constructor
     * @param affected - number of database rows affected
     * @param userId - user id
     * @return
     */
    public static ActionResult ofNotSaved(int affected, OptionalInt userId) {
        return new ActionResult(Result.NotSaved, affected, userId, "");
    }

    /**
     * Error result constructor
     * @param affected - number of database rows affected
     * @param userId - user id
     * @return
     */
    public static ActionResult ofError(int affected, OptionalInt userId) {
        return new ActionResult(Result.Error, affected, userId, "");
    }

    /**
     * Error result constructor
     * @param affected - number of database rows affected
     * @param userId - user id
     * @param errorCode - error code
     * @return
     */
    public static ActionResult ofError(int affected, OptionalInt userId, ErrorCode errorCode) {
        ActionResult result = ofError(affected, userId);
        result.errorCode = errorCode;
        return result;
    }

    public boolean isSuccess() {
        return this.result == Result.Success;
    }

    public boolean isNotSaved() {
        return this.result == Result.NotSaved;
    }

    public boolean isError() {
        return this.result == Result.Error;
    }

    public int getAffected() {
        return affected;
    }

    public void setAffected(int affected) {
        this.affected = affected;
    }

    public OptionalInt getUserId() {
        return userId;
    }

    public void setUserId(OptionalInt userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public Tabs getTab() {
        return tab;
    }

    public void setTab(Tabs tab) {
        this.tab = tab;
    }

    public boolean isFileTab() {
        return this.tab == Tabs.file_tab;
    }

    public boolean isNoteTab() {
        return this.tab == Tabs.note_tab;
    }

    public boolean isCredentialsTab() {
        return this.tab == Tabs.credentials_tab;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public static Builder getBuilder() {
        return new Builder();
    }

    public static Builder getBuilder(ActionResult result) {
        return new Builder(result);
    }

    public static class Builder {

        private final ActionResult actionResult;

        public Builder() {
            this.actionResult = new ActionResult();
        }

        public Builder(ActionResult result) {
            this.actionResult = new ActionResult(result);
        }

        public Builder setAffected(int affected) {
            actionResult.affected = affected;
            return this;
        }

        public Builder setUserId(OptionalInt userId) {
            actionResult.userId = userId;
            return this;
        }

        public Builder setMessage(String message) {
            actionResult.message = message;
            return this;
        }

        public Builder setResult(Result result) {
            actionResult.result = result;
            return this;
        }

        public Builder setTab(Tabs tab) {
            actionResult.tab = tab;
            return this;
        }

        public Builder setErrorCode(ErrorCode errorCode) {
            actionResult.errorCode = errorCode;
            return this;
        }

        public ActionResult build() {
            return new ActionResult(actionResult);
        }
    }

}
