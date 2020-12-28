package com.udacity.jwdnd.course1.cloudstorage.misc;

/**
 * Class representing a message
 */
public class MessageText {

    public enum Type { RESOURCE, PLAIN_TEXT }

    private Type type;
    private String message;

    private MessageText(String message, Type type) {
        this.message = message;
        this.type = type;
    }

    /**
     * Plain text object
     * @param message - text
     * @return
     */
    public static MessageText plainText(String message) {
        return new MessageText(message, Type.PLAIN_TEXT);
    }

    /**
     * Resource name object
     * @param message - name of resource
     * @return
     */
    public static MessageText resourceText(String message) {
        return new MessageText(message, Type.RESOURCE);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public boolean isPlainText() {
        return this.type == Type.PLAIN_TEXT;
    }

}
