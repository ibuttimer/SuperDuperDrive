package com.udacity.jwdnd.course1.cloudstorage.exception;

import com.udacity.jwdnd.course1.cloudstorage.misc.ResourceStore;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.udacity.jwdnd.course1.cloudstorage.controllers.ErrorControllerImpl.addErrorAttributes;

/**
 * Max file size exception handler
 */
@ControllerAdvice
public class MaxUploadSizeExceededResponse extends ResponseEntityExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ModelAndView handleMaxSizeException(HttpServletRequest req, MaxUploadSizeExceededException exc) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("exception", exc);
        modelAndView.addObject("url", req.getRequestURL());

        modelAndView.setViewName(
                addErrorAttributes(modelAndView.getModelMap(), ResourceStore.getBundle().getString("fileTooLarge")));

        return modelAndView;
    }
}
