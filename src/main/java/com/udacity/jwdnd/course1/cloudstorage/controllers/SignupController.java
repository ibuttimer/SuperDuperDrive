package com.udacity.jwdnd.course1.cloudstorage.controllers;

import com.udacity.jwdnd.course1.cloudstorage.misc.ResourceStore;
import com.udacity.jwdnd.course1.cloudstorage.model.User;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

import static com.udacity.jwdnd.course1.cloudstorage.config.UrlFactory.SIGNUP_URL;

/**
 * Signup controller
 */
@Controller()
@RequestMapping(SIGNUP_URL)
public class SignupController extends AbstractController {

    private static final List<String> MODEL_ATTRIBUTES = List.of(
            "enterFirstName", "enterLastName", "enterUsername", "enterPassword", "signUp", "backToLogin",
            "usernameMaxLen", "passwordMaxLen", "firstnameMaxLen", "lastnameMaxLen"
    );

    private static final String SIGNUP_VIEW = "signup";

    private final UserService userService;

    public SignupController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public String signupView(Model model) {
        return signupViewSetup(model);
    }

    private String signupViewSetup(Model model) {
        addModelAttributes(model, MODEL_ATTRIBUTES);
        return SIGNUP_VIEW;
    }

    private String signupViewSetup(ModelMap model) {
        addModelAttributes(model, MODEL_ATTRIBUTES);
        return SIGNUP_VIEW;
    }

    /**
     * Sign up the specified user
     * @param user - user details
     * @param modelMap - model
     * @return
     */
    @PostMapping()
    public ModelAndView signupUser(@ModelAttribute User user, ModelMap modelMap) {
        String signupError = null;

        if (!userService.isUsernameAvailable(user.getUsername())) {
            signupError = "usernameExists";
        } else {
            int rowsAdded = userService.createUser(user);
            if (rowsAdded < 0) {
                signupError = "signupError";
            }
        }

        ModelAndView modelAndView;
        if (signupError == null) {
            modelMap.addAttribute("success", true);

            modelAndView = new ModelAndView("redirect:/"+LoginController.loginViewSetup(this, modelMap), modelMap);
        } else {
            signupError = ResourceStore.getBundle().getString(signupError);
            modelMap.addAttribute("signupError", signupError);

            modelAndView = new ModelAndView(signupViewSetup(modelMap), modelMap);
        }

        return modelAndView;
    }
}