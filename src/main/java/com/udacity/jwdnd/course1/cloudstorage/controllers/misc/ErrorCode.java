package com.udacity.jwdnd.course1.cloudstorage.controllers.misc;

import org.thymeleaf.util.StringUtils;

/**
 * Enum representing error codes
 */
public enum ErrorCode {
    none,
    notfound,
    badrequest,
    unsupported,
    unauthorised;

    public static ErrorCode from(String errorStr) {
        ErrorCode errorCode = ErrorCode.none;
        if (!StringUtils.isEmptyOrWhitespace(errorStr)) {
            try {
                errorCode = ErrorCode.valueOf(errorStr);
            } catch (IllegalArgumentException iae) {
                // no-op, return none
            }
        }
        return errorCode;
    }

}
