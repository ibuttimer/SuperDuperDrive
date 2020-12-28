package com.udacity.jwdnd.course1.cloudstorage.controllers;

import com.udacity.jwdnd.course1.cloudstorage.misc.ResourceStore;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import static com.udacity.jwdnd.course1.cloudstorage.config.UrlFactory.SIGNUP_URL;

/**
 * Base controller class
 */
public abstract class AbstractController {

    protected void addModelAttributes(Model model, List<String> attributes) {
        for (String attribute : attributes) {
            model.addAttribute(attribute, ResourceStore.getBundle().getString(attribute));
        }
    }

}