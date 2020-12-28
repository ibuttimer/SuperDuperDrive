package com.udacity.jwdnd.course1.cloudstorage.misc;

import java.util.ResourceBundle;

/**
 * Class to retrieve resources
 */
public class ResourceStore {

    private static ResourceStore mInstance;
    private final ResourceBundle mBundle;

    private ResourceStore() {
        mBundle = ResourceBundle.getBundle("application");
    }

    public static ResourceStore getInstance() {
        if (mInstance == null) {
            mInstance = new ResourceStore();
        }
        return mInstance;
    }

    public static ResourceBundle getBundle() {
        return getInstance().mBundle;
    }
}
