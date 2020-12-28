package com.udacity.jwdnd.course1.cloudstorage.config;


import com.udacity.jwdnd.course1.cloudstorage.controllers.misc.ErrorCode;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class UrlFactory {

    public static final String LOGIN_URL = "/login";
    public static final String SIGNUP_URL = "/signup";
    public static final String LOGOUT_URL = "/logout";
    public static final String HOME_URL = "/home";
    public static final String ERROR_URL = "/error";
    public static final String GET_CREDENTIALS_PASSWORD_URL = HOME_URL + "/decode";
    public static final String FILE_URL = "/file";
    public static final String UPLOAD_URL = FILE_URL + "/upload";
    public static final String DOWNLOAD_URL = FILE_URL + "/download";

    public static final String CSS_ANT_PATTERN = "/css/**";
    public static final String JS_ANT_PATTERN = "/js/**";

    public static final String H2_CONSOLE_PATTERN = "/h2-console/**";



    private UrlFactory() {
        // no instantiation
    }

    public static String urlWithQuery(String url, String query) {
        return url + "?" + query;
    }

    public static String urlWithQuery(String url, Map<String, Object> queries) {
        StringBuilder sb = new StringBuilder(url);
        int count = queries.size();
        if (count > 0) {
            sb.append('?');
        }
        for (Map.Entry<String, Object> entry : queries.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue());
            if (count > 1) {
                sb.append('&');
            }
            --count;
        }
        return sb.toString();
    }

    public static URI errorRedirect(ErrorCode errorCode) {
        URI uri = null;
        try {
            uri = new URI(
                    urlWithQuery(ERROR_URL, "error=" + errorCode));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return uri;
    }

    public static URI getUri(String url, String query) {
        URI uri = null;
        try {
            uri = new URI(
                    urlWithQuery(ERROR_URL, query));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return uri;
    }
}
