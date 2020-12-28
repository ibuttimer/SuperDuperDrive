package com.udacity.jwdnd.course1.cloudstorage.controllers;

import com.udacity.jwdnd.course1.cloudstorage.controllers.misc.ErrorCode;
import com.udacity.jwdnd.course1.cloudstorage.misc.ResourceStore;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.udacity.jwdnd.course1.cloudstorage.config.UrlFactory.ERROR_URL;

/**
 * Error controller
 */
@Controller
public class ErrorControllerImpl implements ErrorController {

    private static final List<String> RESULT_ATTRIBUTES = List.of(
        "error",
        "clickToContinue0", "clickToContinue1", "clickToContinue2"
    );

    private static final String ERROR_TEMPLATE = "error";
    private static final String ERROR_MESSAGE = "message";

    private static final Map<ErrorCode, String> errorMap;
    static {
        Map<ErrorCode, String> map = new HashMap<>();
        map.put(ErrorCode.notfound, "pageNotFound");
        map.put(ErrorCode.badrequest, "badRequest");
        map.put(ErrorCode.unsupported, "unsupportedOperation");
        map.put(ErrorCode.unauthorised, "unauthorisedAccess");
        errorMap = map;
    }

    @RequestMapping(ERROR_URL)
    public String handleError(HttpServletRequest request,
                              @RequestParam(required = false) String error,
                              Model model) {
        ErrorCode errorCode = ErrorCode.from(error);

        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        int statusCode = HttpStatus.OK.value();
        if (status != null) {
            statusCode = Integer.parseInt(status.toString());
        }
        String message = getErrorMessage(errorCode, statusCode);

        return addErrorAttributes(model, message);
    }

    /**
     * Get an error message corresponding to the specified error code or http status code
     * @param errorCode - error code
     * @param statusCode - http status code
     * @return
     */
    public static String getErrorMessage(ErrorCode errorCode, int statusCode) {
        String resourceName = null;

        if (errorMap.containsKey(errorCode)) {
            resourceName = errorMap.get(errorCode);
        }
        else {
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                resourceName = "pageNotFound";
            }
            else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                resourceName = "internalError";
            }
        }
        if (resourceName == null) {
            resourceName = "unknownError";
        }

        return ResourceStore.getBundle().getString(resourceName);
    }

    /**
     * Get an error message corresponding to the specified error code
     * @param errorCode - error code
     * @return
     */
    public static String getErrorMessage(ErrorCode errorCode) {
        return getErrorMessage(errorCode, HttpStatus.OK.value());
    }

    /**
     * Add the error attributes to the specified model
     * @param model - model to update
     * @param message - message to add
     * @return
     */
    public static String addErrorAttributes(Model model, String message) {
        for (String attribute : RESULT_ATTRIBUTES) {
            model.addAttribute(attribute, ResourceStore.getBundle().getString(attribute));
        }
        model.addAttribute(ERROR_MESSAGE, message);

        return ERROR_TEMPLATE;
    }

    /**
     * Add the error attributes to the specified model map
     * @param modelMap - model map to update
     * @param message - message to add
     * @return
     */
    public static String addErrorAttributes(ModelMap modelMap, String message) {
        for (String attribute : RESULT_ATTRIBUTES) {
            modelMap.addAttribute(attribute, ResourceStore.getBundle().getString(attribute));
        }
        modelMap.addAttribute(ERROR_MESSAGE, message);

        return ERROR_TEMPLATE;
    }


    @Override
    public String getErrorPath() {
        return null;
    }
}
