package com.udacity.jwdnd.course1.cloudstorage.controllers.misc;

import org.thymeleaf.util.StringUtils;

/**
 * Enum representing request actions
 */
public enum Action {
    /** No action */
    none,
    /** Create entry */
    create,
    /** Read entry */
    read,
    /** Update entry */
    update,
    /** Delete entry */
    delete,
    /** Select tab */
    select; // select tab

    public static Action from(String actionStr) {
        Action action = Action.none;
        if (!StringUtils.isEmptyOrWhitespace(actionStr)) {
            try {
                action = Action.valueOf(actionStr);
            } catch (IllegalArgumentException iae) {
                // no-op, return none
            }
        }
        return action;
    }
}
