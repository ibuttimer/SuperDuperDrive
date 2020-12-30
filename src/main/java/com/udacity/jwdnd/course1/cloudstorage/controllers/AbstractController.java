package com.udacity.jwdnd.course1.cloudstorage.controllers;

import com.udacity.jwdnd.course1.cloudstorage.misc.ResourceStore;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;

import java.util.List;

/**
 * Base controller class
 */
public abstract class AbstractController {

    protected void addModelAttributes(Model model, List<String> attributes) {
        for (String attribute : attributes) {
            model.addAttribute(attribute, ResourceStore.getBundle().getString(attribute));
        }
    }

    protected void addModelAttributes(ModelMap modelMap, List<String> attributes) {
        for (String attribute : attributes) {
            modelMap.addAttribute(attribute, ResourceStore.getBundle().getString(attribute));
        }
    }
}