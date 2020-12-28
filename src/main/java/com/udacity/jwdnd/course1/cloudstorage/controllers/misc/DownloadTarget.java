package com.udacity.jwdnd.course1.cloudstorage.controllers.misc;

import org.thymeleaf.util.StringUtils;

/**
 * Enum representing download targets
 */
public enum DownloadTarget {
    inline,
    attachment;

    public static DownloadTarget from(String targetStr) {
        DownloadTarget action = DownloadTarget.attachment;
        if (!StringUtils.isEmptyOrWhitespace(targetStr)) {
            try {
                action = DownloadTarget.valueOf(targetStr);
            } catch (IllegalArgumentException iae) {
                // no-op, return default
            }
        }
        return action;
    }

}
